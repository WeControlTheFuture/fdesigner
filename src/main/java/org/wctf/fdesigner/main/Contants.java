package org.wctf.fdesigner.main;

public class Contants {
	// splash screen system properties
	public static final String SPLASH_HANDLE = "org.eclipse.equinox.launcher.splash.handle"; //$NON-NLS-1$
	public static final String SPLASH_LOCATION = "org.eclipse.equinox.launcher.splash.location"; //$NON-NLS-1$

	// command line args
	public static final String FRAMEWORK = "-framework"; //$NON-NLS-1$
	public static final String INSTALL = "-install"; //$NON-NLS-1$
	public static final String INITIALIZE = "-initialize"; //$NON-NLS-1$
	public static final String VM = "-vm"; //$NON-NLS-1$
	public static final String VMARGS = "-vmargs"; //$NON-NLS-1$
	public static final String DEBUG = "-debug"; //$NON-NLS-1$
	public static final String DEV = "-dev"; //$NON-NLS-1$
	public static final String CONFIGURATION = "-configuration"; //$NON-NLS-1$
	public static final String NOSPLASH = "-nosplash"; //$NON-NLS-1$
	public static final String SHOWSPLASH = "-showsplash"; //$NON-NLS-1$
	public static final String EXITDATA = "-exitdata"; //$NON-NLS-1$
	public static final String NAME = "-name"; //$NON-NLS-1$
	public static final String LAUNCHER = "-launcher"; //$NON-NLS-1$

	public static final String PROTECT = "-protect"; //$NON-NLS-1$
	// currently the only level of protection we care about.
	public static final String PROTECT_MASTER = "master"; //$NON-NLS-1$
	public static final String PROTECT_BASE = "base"; //$NON-NLS-1$

	public static final String LIBRARY = "--launcher.library"; //$NON-NLS-1$
	public static final String APPEND_VMARGS = "--launcher.appendVmargs"; //$NON-NLS-1$
	public static final String OVERRIDE_VMARGS = "--launcher.overrideVmargs"; //$NON-NLS-1$
	public static final String NL = "-nl"; //$NON-NLS-1$
	public static final String ENDSPLASH = "-endsplash"; //$NON-NLS-1$
	public static final String SPLASH_IMAGE = "splash.bmp"; //$NON-NLS-1$
	public static final String CLEAN = "-clean"; //$NON-NLS-1$
	public static final String NOEXIT = "-noExit"; //$NON-NLS-1$
	public static final String OS = "-os"; //$NON-NLS-1$
	public static final String WS = "-ws"; //$NON-NLS-1$
	public static final String ARCH = "-arch"; //$NON-NLS-1$
	public static final String STARTUP = "-startup"; //$NON-NLS-1$

	public static final String OSGI = "org.eclipse.osgi"; //$NON-NLS-1$
	public static final String STARTER = "org.eclipse.core.runtime.adaptor.EclipseStarter"; //$NON-NLS-1$
	public static final String PLATFORM_URL = "platform:/base/"; //$NON-NLS-1$
	public static final String ECLIPSE_PROPERTIES = "eclipse.properties"; //$NON-NLS-1$
	public static final String FILE_SCHEME = "file:"; //$NON-NLS-1$
	public static final String REFERENCE_SCHEME = "reference:"; //$NON-NLS-1$
	public static final String JAR_SCHEME = "jar:"; //$NON-NLS-1$

	// constants: configuration file location
	public static final String CONFIG_DIR = "configuration/"; //$NON-NLS-1$
	public static final String CONFIG_FILE = "config.ini"; //$NON-NLS-1$
	public static final String CONFIG_FILE_TEMP_SUFFIX = ".tmp"; //$NON-NLS-1$
	public static final String CONFIG_FILE_BAK_SUFFIX = ".bak"; //$NON-NLS-1$
	public static final String ECLIPSE = "eclipse"; //$NON-NLS-1$
	public static final String PRODUCT_SITE_MARKER = ".eclipseproduct"; //$NON-NLS-1$
	public static final String PRODUCT_SITE_ID = "id"; //$NON-NLS-1$
	public static final String PRODUCT_SITE_VERSION = "version"; //$NON-NLS-1$

	// constants: System property keys and/or configuration file elements
	public static final String PROP_USER_HOME = "user.home"; //$NON-NLS-1$
	public static final String PROP_USER_DIR = "user.dir"; //$NON-NLS-1$
	public static final String PROP_INSTALL_AREA = "osgi.install.area"; //$NON-NLS-1$
	public static final String PROP_CONFIG_AREA = "osgi.configuration.area"; //$NON-NLS-1$
	public static final String PROP_CONFIG_AREA_DEFAULT = "osgi.configuration.area.default"; //$NON-NLS-1$
	public static final String PROP_BASE_CONFIG_AREA = "osgi.baseConfiguration.area"; //$NON-NLS-1$
	public static final String PROP_SHARED_CONFIG_AREA = "osgi.sharedConfiguration.area"; //$NON-NLS-1$
	public static final String PROP_CONFIG_CASCADED = "osgi.configuration.cascaded"; //$NON-NLS-1$
	public static final String PROP_FRAMEWORK = "osgi.framework"; //$NON-NLS-1$
	public static final String PROP_SPLASHPATH = "osgi.splashPath"; //$NON-NLS-1$
	public static final String PROP_SPLASHLOCATION = "osgi.splashLocation"; //$NON-NLS-1$
	public static final String PROP_CLASSPATH = "osgi.frameworkClassPath"; //$NON-NLS-1$
	public static final String PROP_EXTENSIONS = "osgi.framework.extensions"; //$NON-NLS-1$
	public static final String PROP_FRAMEWORK_SYSPATH = "osgi.syspath"; //$NON-NLS-1$
	public static final String PROP_FRAMEWORK_SHAPE = "osgi.framework.shape"; //$NON-NLS-1$
	public static final String PROP_LOGFILE = "osgi.logfile"; //$NON-NLS-1$
	public static final String PROP_REQUIRED_JAVA_VERSION = "osgi.requiredJavaVersion"; //$NON-NLS-1$
	public static final String PROP_PARENT_CLASSLOADER = "osgi.parentClassloader"; //$NON-NLS-1$
	public static final String PROP_FRAMEWORK_PARENT_CLASSLOADER = "osgi.frameworkParentClassloader"; //$NON-NLS-1$
	public static final String PROP_NL = "osgi.nl"; //$NON-NLS-1$
	public static final String PROP_NOSHUTDOWN = "osgi.noShutdown"; //$NON-NLS-1$
	public static final String PROP_DEBUG = "osgi.debug"; //$NON-NLS-1$
	public static final String PROP_OS = "osgi.os"; //$NON-NLS-1$
	public static final String PROP_WS = "osgi.ws"; //$NON-NLS-1$
	public static final String PROP_ARCH = "osgi.arch"; //$NON-NLS-1$

	public static final String PROP_EXITCODE = "eclipse.exitcode"; //$NON-NLS-1$
	public static final String PROP_EXITDATA = "eclipse.exitdata"; //$NON-NLS-1$
	public static final String PROP_LAUNCHER = "eclipse.launcher"; //$NON-NLS-1$
	public static final String PROP_LAUNCHER_NAME = "eclipse.launcher.name"; //$NON-NLS-1$

	public static final String PROP_VM = "eclipse.vm"; //$NON-NLS-1$
	public static final String PROP_VMARGS = "eclipse.vmargs"; //$NON-NLS-1$
	public static final String PROP_COMMANDS = "eclipse.commands"; //$NON-NLS-1$
	public static final String PROP_ECLIPSESECURITY = "eclipse.security"; //$NON-NLS-1$

	// Suffix for location properties - see LocationManager.
	public static final String READ_ONLY_AREA_SUFFIX = ".readOnly"; //$NON-NLS-1$

	// Data mode constants for user, configuration and data locations.
	public static final String NONE = "@none"; //$NON-NLS-1$
	public static final String NO_DEFAULT = "@noDefault"; //$NON-NLS-1$
	public static final String USER_HOME = "@user.home"; //$NON-NLS-1$
	public static final String USER_DIR = "@user.dir"; //$NON-NLS-1$
	// Placeholder for hashcode of installation directory
	public static final String INSTALL_HASH_PLACEHOLDER = "@install.hash"; //$NON-NLS-1$

	// types of parent classloaders the framework can have
	public static final String PARENT_CLASSLOADER_APP = "app"; //$NON-NLS-1$
	public static final String PARENT_CLASSLOADER_EXT = "ext"; //$NON-NLS-1$
	public static final String PARENT_CLASSLOADER_BOOT = "boot"; //$NON-NLS-1$
	public static final String PARENT_CLASSLOADER_CURRENT = "current"; //$NON-NLS-1$

	// log file handling
	public static final String SESSION = "!SESSION"; //$NON-NLS-1$
	public static final String ENTRY = "!ENTRY"; //$NON-NLS-1$
	public static final String MESSAGE = "!MESSAGE"; //$NON-NLS-1$
	public static final String STACK = "!STACK"; //$NON-NLS-1$
	public static final int ERROR = 4;
	public static final String PLUGIN_ID = "org.eclipse.equinox.launcher"; //$NON-NLS-1$
}
