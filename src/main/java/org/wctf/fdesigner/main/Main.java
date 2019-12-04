package org.wctf.fdesigner.main;

import java.net.URL;
import java.util.Date;

public final class Main {
	private boolean splashDown = false;
	private JNIBridge bridge = null;
	private String exitData = null;
	private final Thread splashHandler = new SplashHandler();

	public static void main(String[] args) {
		int result = 0;
		try {
			result = new Main().run(args);
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			if (!Boolean.getBoolean(Contants.PROP_NOSHUTDOWN) || result == 23)
				System.exit(result);
		}
	}

	public int run(String[] args) {
		int result = 0;
		try {
			basicRun(args);
			String exitCode = System.getProperty(Contants.PROP_EXITCODE);
			try {
				result = exitCode == null ? 0 : Integer.parseInt(exitCode);
			} catch (NumberFormatException e) {
				result = 17;
			}
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			takeDownSplash();
			if (bridge != null)
				bridge.uninitialize();
		}
		System.setProperty(Contants.PROP_EXITCODE, Integer.toString(result));
		setExitData();
		return result;
	}

	private void basicRun(String[] args) throws Exception {
//		System.setProperty("eclipse.startTime", Long.toString(System.currentTimeMillis())); //$NON-NLS-1$
//		commands = args;
//		String[] passThruArgs = processCommandLine(args);
//
//		if (!debug)
//			// debug can be specified as system property as well
//			debug = System.getProperty(PROP_DEBUG) != null;
//		setupVMProperties();
//		processConfiguration();
//
//		if (protectBase && (System.getProperty(PROP_SHARED_CONFIG_AREA) == null)) {
//			System.err.println("This application is configured to run in a cascaded mode only."); //$NON-NLS-1$
//			System.setProperty(PROP_EXITCODE, Integer.toString(14));
//			return;
//		}
//		// need to ensure that getInstallLocation is called at least once to initialize the value.
//		// Do this AFTER processing the configuration to allow the configuration to set
//		// the install location.  
//		getInstallLocation();
//
//		// locate boot plugin (may return -dev mode variations)
//		URL[] bootPath = getBootPath(bootLocation);
//
//		//Set up the JNI bridge.  We need to know the install location to find the shared library
//		setupJNI(bootPath);
//
//		//ensure minimum Java version, do this after JNI is set up so that we can write an error message 
//		//with exitdata if we fail.
//		if (!checkVersion(System.getProperty("java.version"), System.getProperty(PROP_REQUIRED_JAVA_VERSION))) //$NON-NLS-1$
//			return;
//
//		// verify configuration location is writable
//		if (!checkConfigurationLocation(configurationLocation))
//			return;
//
//		setSecurityPolicy(bootPath);
//		// splash handling is done here, because the default case needs to know
//		// the location of the boot plugin we are going to use
//		handleSplash(bootPath);
//
//		beforeFwkInvocation();
//		invokeFramework(passThruArgs, bootPath);
//	
	}

	protected void takeDownSplash() {
		if (splashDown || bridge == null) // splash is already down
			return;

		splashDown = bridge.takeDownSplash();
		System.clearProperty(Contants.SPLASH_HANDLE);

		try {
			Runtime.getRuntime().removeShutdownHook(splashHandler);
		} catch (Throwable e) {
			// OK to ignore this, happens when the VM is already shutting down
		}
	}

	private void setExitData() {
		String data = System.getProperty(Contants.PROP_EXITDATA);
		if (data == null)
			return;
		if (bridge == null || (bridge.isLibraryLoadedByJava() && exitData == null))
			System.out.println(data);
		else
			bridge.setExitData(exitData, data);
	}

	public final class SplashHandler extends Thread {
		@Override
		public void run() {
			takeDownSplash();
		}

		public void updateSplash() {
			if (bridge != null && !splashDown) {
				bridge.updateSplash();
			}
		}
	}
}
