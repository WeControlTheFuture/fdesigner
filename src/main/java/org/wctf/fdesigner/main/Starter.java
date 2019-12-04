package org.wctf.fdesigner.main;

import java.util.HashMap;
import java.util.Map;

import org.wctf.fdesigner.config.EquinoxConfiguration;
import org.wctf.fdesigner.config.Msg;
import org.wctf.fdesigner.config.SettingConstants;
import org.wctf.fdesigner.framework.Framework;
import org.wctf.fdesigner.framework.bundle.BundleContext;
import org.wctf.fdesigner.log.FrameworkLogEntry;
import org.wctf.fdesigner.util.NLS;

public class Starter {
	private static boolean running = false;
	private static EquinoxConfiguration equinoxConfig;
	private static BundleContext context;
	private static Map<String, String> configuration = null;
	private static Framework framework = null;

	public static void main(String[] args) throws Exception {
		if (getProperty("eclipse.startTime") == null) //$NON-NLS-1$
			setProperty("eclipse.startTime", Long.toString(System.currentTimeMillis())); //$NON-NLS-1$
		if (getProperty(SettingConstants.PROP_NOSHUTDOWN) == null)
			setProperty(SettingConstants.PROP_NOSHUTDOWN, "true"); //$NON-NLS-1$
		// set the compatibility boot delegation flag to false to get "standard" OSGi
		// behavior WRT boot delegation (bug 178477)
		if (getProperty(SettingConstants.PROP_COMPATIBILITY_BOOTDELEGATION) == null)
			setProperty(SettingConstants.PROP_COMPATIBILITY_BOOTDELEGATION, "false"); //$NON-NLS-1$
		Object result = run(args, null);
		if (result instanceof Integer && !Boolean.valueOf(getProperty(SettingConstants.PROP_NOSHUTDOWN)).booleanValue())
			System.exit(((Integer) result).intValue());
	}

	public static Object run(String[] args, Runnable endSplashHandler) throws Exception {
		if (running)
			throw new IllegalStateException(Msg.ECLIPSE_STARTUP_ALREADY_RUNNING);
		boolean startupFailed = true;
		try {
			startup(args, endSplashHandler);
			startupFailed = false;
			if (Boolean.valueOf(getProperty(SettingConstants.PROP_IGNOREAPP)).booleanValue() || isForcedRestart())
				return null;
			return run(null);
		} catch (Throwable e) {
			// ensure the splash screen is down
			if (endSplashHandler != null)
				endSplashHandler.run();
			// may use startupFailed to understand where the error happened
			FrameworkLogEntry logEntry = new FrameworkLogEntry(EquinoxContainer.NAME, FrameworkLogEntry.ERROR, 0, startupFailed ? Msg.ECLIPSE_STARTUP_STARTUP_ERROR : Msg.ECLIPSE_STARTUP_APP_ERROR, 1, e, null);
			if (log != null)
				log.log(logEntry);
			else
				// TODO desperate measure - ideally, we should write this to disk (a la
				// Main.log)
				e.printStackTrace();
		} finally {
			try {
				// The application typically sets the exit code however the framework can
				// request that
				// it be re-started. We need to check for this and potentially override the exit
				// code.
				if (isForcedRestart())
					setProperty(SettingConstants.PROP_EXITCODE, "23"); //$NON-NLS-1$
				if (!Boolean.valueOf(getProperty(SettingConstants.PROP_NOSHUTDOWN)).booleanValue())
					shutdown();
			} catch (Throwable e) {
				FrameworkLogEntry logEntry = new FrameworkLogEntry(EquinoxContainer.NAME, FrameworkLogEntry.ERROR, 0, Msg.ECLIPSE_STARTUP_SHUTDOWN_ERROR, 1, e, null);
				if (log != null)
					log.log(logEntry);
				else
					// TODO desperate measure - ideally, we should write this to disk (a la
					// Main.log)
					e.printStackTrace();
			}
		}
		// we only get here if an error happened
		if (getProperty(SettingConstants.PROP_EXITCODE) == null) {
			setProperty(SettingConstants.PROP_EXITCODE, "13"); //$NON-NLS-1$
			setProperty(SettingConstants.PROP_EXITDATA, NLS.bind(Msg.ECLIPSE_STARTUP_ERROR_CHECK_LOG, log == null ? null : log.getFile().getPath()));
		}
		return null;
	}
	
	public static BundleContext startup(String[] args, Runnable endSplashHandler) throws Exception {
		if (running)
			throw new IllegalStateException(Msg.ECLIPSE_STARTUP_ALREADY_RUNNING);
		processCommandLine(args);
		framework = new Equinox(getConfiguration());
		framework.init();
		context = framework.getBundleContext();
		ServiceReference<FrameworkLog> logRef = context.getServiceReference(FrameworkLog.class);
		log = context.getService(logRef);
		ServiceReference<EnvironmentInfo> configRef = context.getServiceReference(EnvironmentInfo.class);
		equinoxConfig = (EquinoxConfiguration) context.getService(configRef);

		equinoxConfig.setAllArgs(allArgs);
		equinoxConfig.setFrameworkArgs(frameworkArgs);
		equinoxConfig.setAppArgs(appArgs);

		registerFrameworkShutdownHandlers();
		publishSplashScreen(endSplashHandler);
		consoleMgr = ConsoleManager.startConsole(context, equinoxConfig);

		Bundle[] startBundles = loadBasicBundles();

		if (startBundles == null || ("true".equals(getProperty(PROP_REFRESH_BUNDLES)) && refreshPackages(getCurrentBundles(false)))) { //$NON-NLS-1$
			waitForShutdown();
			return context; // cannot continue; loadBasicBundles caused refreshPackages to shutdown the framework
		}

		framework.start();

		if (isForcedRestart()) {
			return context;
		}
		// set the framework start level to the ultimate value.  This will actually start things
		// running if they are persistently active.
		setStartLevel(getStartLevel());
		// they should all be active by this time
		ensureBundlesActive(startBundles);

		// in the case where the built-in console is disabled we should try to start the console bundle
		try {
			consoleMgr.checkForConsoleBundle();
		} catch (BundleException e) {
			FrameworkLogEntry entry = new FrameworkLogEntry(EquinoxContainer.NAME, FrameworkLogEntry.ERROR, 0, e.getMessage(), 0, e, null);
			log.log(entry);
		}
		// TODO should log unresolved bundles if in debug or dev mode
		running = true;
		return context;
	}
	
	private static void processCommandLine(String[] args) throws Exception {
		allArgs = args;
		if (args.length == 0) {
			frameworkArgs = args;
			return;
		}
		int[] configArgs = new int[args.length];
		configArgs[0] = -1; // need to initialize the first element to something that could not be an index.
		int configArgIndex = 0;
		for (int i = 0; i < args.length; i++) {
			boolean found = false;
			// check for args without parameters (i.e., a flag arg)

			// check if debug should be enabled for the entire platform
			// If this is the last arg or there is a following arg (i.e., arg+1 has a leading -), 
			// simply enable debug.  Otherwise, assume that that the following arg is
			// actually the filename of an options file.  This will be processed below.
			if (args[i].equalsIgnoreCase(DEBUG) && ((i + 1 == args.length) || ((i + 1 < args.length) && (args[i + 1].startsWith("-"))))) { //$NON-NLS-1$
				setProperty(PROP_DEBUG, ""); //$NON-NLS-1$
				debug = true;
				found = true;
			}

			// check if development mode should be enabled for the entire platform
			// If this is the last arg or there is a following arg (i.e., arg+1 has a leading -), 
			// simply enable development mode.  Otherwise, assume that that the following arg is
			// actually some additional development time class path entries.  This will be processed below.
			if (args[i].equalsIgnoreCase(DEV) && ((i + 1 == args.length) || ((i + 1 < args.length) && (args[i + 1].startsWith("-"))))) { //$NON-NLS-1$
				setProperty(PROP_DEV, ""); //$NON-NLS-1$
				found = true;
			}

			// look for the initialization arg
			if (args[i].equalsIgnoreCase(INITIALIZE)) {
				initialize = true;
				found = true;
			}

			// look for the clean flag.
			if (args[i].equalsIgnoreCase(CLEAN)) {
				setProperty(PROP_CLEAN, "true"); //$NON-NLS-1$
				found = true;
			}

			// look for the consoleLog flag
			if (args[i].equalsIgnoreCase(CONSOLE_LOG)) {
				setProperty(PROP_CONSOLE_LOG, "true"); //$NON-NLS-1$
				found = true;
			}

			// look for the console with no port.  
			if (args[i].equalsIgnoreCase(CONSOLE) && ((i + 1 == args.length) || ((i + 1 < args.length) && (args[i + 1].startsWith("-"))))) { //$NON-NLS-1$
				setProperty(PROP_CONSOLE, ""); //$NON-NLS-1$
				found = true;
			}

			if (args[i].equalsIgnoreCase(NOEXIT)) {
				setProperty(PROP_NOSHUTDOWN, "true"); //$NON-NLS-1$
				found = true;
			}

			if (found) {
				configArgs[configArgIndex++] = i;
				continue;
			}
			// check for args with parameters. If we are at the last argument or if the next one
			// has a '-' as the first character, then we can't have an arg with a parm so continue.
			if (i == args.length - 1 || args[i + 1].startsWith("-")) { //$NON-NLS-1$
				continue;
			}
			String arg = args[++i];

			// look for the console and port.  
			if (args[i - 1].equalsIgnoreCase(CONSOLE)) {
				setProperty(PROP_CONSOLE, arg);
				found = true;
			}

			// look for the configuration location .  
			if (args[i - 1].equalsIgnoreCase(CONFIGURATION)) {
				setProperty(EquinoxLocations.PROP_CONFIG_AREA, arg);
				found = true;
			}

			// look for the data location for this instance.  
			if (args[i - 1].equalsIgnoreCase(DATA)) {
				setProperty(EquinoxLocations.PROP_INSTANCE_AREA, arg);
				found = true;
			}

			// look for the user location for this instance.  
			if (args[i - 1].equalsIgnoreCase(USER)) {
				setProperty(EquinoxLocations.PROP_USER_AREA, arg);
				found = true;
			}

			// look for the launcher location
			if (args[i - 1].equalsIgnoreCase(LAUNCHER)) {
				setProperty(EquinoxLocations.PROP_LAUNCHER, arg);
				found = true;
			}
			// look for the development mode and class path entries.  
			if (args[i - 1].equalsIgnoreCase(DEV)) {
				setProperty(PROP_DEV, arg);
				found = true;
			}

			// look for the debug mode and option file location.  
			if (args[i - 1].equalsIgnoreCase(DEBUG)) {
				setProperty(PROP_DEBUG, arg);
				debug = true;
				found = true;
			}

			// look for the window system.  
			if (args[i - 1].equalsIgnoreCase(WS)) {
				setProperty(PROP_WS, arg);
				found = true;
			}

			// look for the operating system
			if (args[i - 1].equalsIgnoreCase(OS)) {
				setProperty(PROP_OS, arg);
				found = true;
			}

			// look for the system architecture
			if (args[i - 1].equalsIgnoreCase(ARCH)) {
				setProperty(PROP_ARCH, arg);
				found = true;
			}

			// look for the nationality/language
			if (args[i - 1].equalsIgnoreCase(NL)) {
				setProperty(PROP_NL, arg);
				found = true;
			}

			// look for the locale extensions
			if (args[i - 1].equalsIgnoreCase(NL_EXTENSIONS)) {
				setProperty(PROP_NL_EXTENSIONS, arg);
				found = true;
			}

			// done checking for args.  Remember where an arg was found 
			if (found) {
				configArgs[configArgIndex++] = i - 1;
				configArgs[configArgIndex++] = i;
			}
		}

		// remove all the arguments consumed by this argument parsing
		if (configArgIndex == 0) {
			frameworkArgs = new String[0];
			appArgs = args;
			return;
		}
		appArgs = new String[args.length - configArgIndex];
		frameworkArgs = new String[configArgIndex];
		configArgIndex = 0;
		int j = 0;
		int k = 0;
		for (int i = 0; i < args.length; i++) {
			if (i == configArgs[configArgIndex]) {
				frameworkArgs[k++] = args[i];
				configArgIndex++;
			} else
				appArgs[j++] = args[i];
		}
		return;
	}

	private synchronized static String getProperty(String key) {
		if (equinoxConfig != null) {
			return equinoxConfig.getConfiguration(key);
		}
		return getConfiguration().get(key);
	}

	private synchronized static Object setProperty(String key, String value) {
		if (equinoxConfig != null) {
			return equinoxConfig.setProperty(key, value);
		}
		if ("true".equals(getConfiguration().get(SettingConstants.PROP_USE_SYSTEM_PROPERTIES))) { //$NON-NLS-1$
			System.setProperty(key, value);
		}
		return getConfiguration().put(key, value);
	}

	private synchronized static Map<String, String> getConfiguration() {
		if (configuration == null) {
			configuration = new HashMap<>();
			// TODO hack to set these to defaults for EclipseStarter
			// Note that this hack does not allow this property to be specified in
			// config.ini
			configuration.put(SettingConstants.PROP_USE_SYSTEM_PROPERTIES, System.getProperty(SettingConstants.PROP_USE_SYSTEM_PROPERTIES, "true")); //$NON-NLS-1$
			// we handle this compatibility setting special for EclipseStarter
			String systemCompatibilityBoot = System.getProperty(SettingConstants.PROP_COMPATIBILITY_BOOTDELEGATION);
			if (systemCompatibilityBoot != null) {
				// The system properties have a specific setting; use it
				configuration.put(SettingConstants.PROP_COMPATIBILITY_BOOTDELEGATION, systemCompatibilityBoot);
			} else {
				// set a default value; but this value can be overriden by the config.ini
				configuration.put(SettingConstants.PROP_COMPATIBILITY_BOOTDELEGATION + SettingConstants.PROP_DEFAULT_SUFFIX, "true"); //$NON-NLS-1$
			}

			String dsDelayedKeepInstances = System.getProperty(SettingConstants.PROP_DS_DELAYED_KEEPINSTANCES);
			if (dsDelayedKeepInstances != null) {
				// The system properties have a specific setting; use it
				configuration.put(SettingConstants.PROP_DS_DELAYED_KEEPINSTANCES, dsDelayedKeepInstances);
			} else {
				// set a default value; but this value can be overriden by the config.ini
				configuration.put(SettingConstants.PROP_DS_DELAYED_KEEPINSTANCES + SettingConstants.PROP_DEFAULT_SUFFIX, "true"); //$NON-NLS-1$
			}
		}
		return configuration;
	}

}
