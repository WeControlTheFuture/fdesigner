package org.wctf.fdesigner.config;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class SettingConstants {
	public static final int BSN_VERSION_SINGLE = 1;
	public static final int BSN_VERSION_MULTIPLE = 2;
	public static final int BSN_VERSION_MANAGED = 3;

	// JVM os.arch property name
	public static final String PROP_JVM_OS_ARCH = "os.arch"; //$NON-NLS-1$
	// JVM os.name property name
	public static final String PROP_JVM_OS_NAME = "os.name"; //$NON-NLS-1$
	// JVM os.version property name
	public static final String PROP_JVM_OS_VERSION = "os.version"; //$NON-NLS-1$
	public static final String PROP_JVM_SPEC_VERSION = "java.specification.version"; //$NON-NLS-1$
	public static final String PROP_JVM_SPEC_NAME = "java.specification.name"; //$NON-NLS-1$

	public static final String PROP_SETPERMS_CMD = "osgi.filepermissions.command"; //$NON-NLS-1$
	public static final String PROP_DEBUG = "osgi.debug"; //$NON-NLS-1$
	public static final String PROP_DEBUG_VERBOSE = "osgi.debug.verbose"; //$NON-NLS-1$
	public static final String PROP_DEV = "osgi.dev"; //$NON-NLS-1$
	public static final String PROP_CLEAN = "osgi.clean"; //$NON-NLS-1$
	public static final String PROP_USE_SYSTEM_PROPERTIES = "osgi.framework.useSystemProperties"; //$NON-NLS-1$
	public static final String PROP_FRAMEWORK = "osgi.framework"; //$NON-NLS-1$

	public static final String ECLIPSE_FRAMEWORK_VENDOR = "Eclipse"; //$NON-NLS-1$

	public static final String PROP_OSGI_JAVA_PROFILE = "osgi.java.profile"; //$NON-NLS-1$
	public static final String PROP_OSGI_JAVA_PROFILE_NAME = "osgi.java.profile.name"; //$NON-NLS-1$
	// OSGi java profile bootdelegation; used to indicate how the
	// org.osgi.framework.bootdelegation
	// property defined in the java profile should be processed, (ingnore, override,
	// none). default is ignore
	public static final String PROP_OSGI_JAVA_PROFILE_BOOTDELEGATION = "osgi.java.profile.bootdelegation"; //$NON-NLS-1$
	// indicates that the org.osgi.framework.bootdelegation in the java profile
	// should be ingored
	public static final String PROP_OSGI_BOOTDELEGATION_IGNORE = "ignore"; //$NON-NLS-1$
	// indicates that the org.osgi.framework.bootdelegation in the java profile
	// should override the system property
	public static final String PROP_OSGI_BOOTDELEGATION_OVERRIDE = "override"; //$NON-NLS-1$
	// indicates that the org.osgi.framework.bootdelegation in the java profile AND
	// the system properties should be ignored
	public static final String PROP_OSGI_BOOTDELEGATION_NONE = "none"; //$NON-NLS-1$

	public static final String PROP_CONTEXT_BOOTDELEGATION = "osgi.context.bootdelegation"; //$NON-NLS-1$
	public static final String PROP_COMPATIBILITY_BOOTDELEGATION = "osgi.compatibility.bootdelegation"; //$NON-NLS-1$
	public static final String PROP_DS_DELAYED_KEEPINSTANCES = "ds.delayed.keepInstances"; //$NON-NLS-1$
	public static final String PROP_COMPATIBILITY_ERROR_FAILED_START = "osgi.compatibility.errorOnFailedStart"; //$NON-NLS-1$
	public static final String PROP_COMPATIBILITY_START_LAZY = "osgi.compatibility.eagerStart.LazyActivation"; //$NON-NLS-1$
	public static final String PROP_COMPATIBILITY_START_LAZY_ON_FAIL_CLASSLOAD = "osgi.compatibility.trigger.lazyActivation.onFailedClassLoad"; //$NON-NLS-1$

	public static final String PROP_OSGI_OS = "osgi.os"; //$NON-NLS-1$
	public static final String PROP_OSGI_WS = "osgi.ws"; //$NON-NLS-1$
	public static final String PROP_OSGI_ARCH = "osgi.arch"; //$NON-NLS-1$
	public static final String PROP_OSGI_NL = "osgi.nl"; //$NON-NLS-1$
	public static final String PROP_OSGI_NL_USER = "osgi.nl.user"; //$NON-NLS-1$

	public static final String PROP_ROOT_LOCALE = "equinox.root.locale"; //$NON-NLS-1$

	public static final String PROP_PARENT_CLASSLOADER = "osgi.parentClassloader"; //$NON-NLS-1$
	// A parent classloader type that specifies the framework classlaoder
	public static final String PARENT_CLASSLOADER_FWK = "fwk"; //$NON-NLS-1$
	// System property used to set the context classloader parent classloader type
	// (ccl is the default)
	public static final String PROP_CONTEXTCLASSLOADER_PARENT = "osgi.contextClassLoaderParent"; //$NON-NLS-1$
	public static final String CONTEXTCLASSLOADER_PARENT_APP = "app"; //$NON-NLS-1$
	public static final String CONTEXTCLASSLOADER_PARENT_EXT = "ext"; //$NON-NLS-1$
	public static final String CONTEXTCLASSLOADER_PARENT_BOOT = "boot"; //$NON-NLS-1$
	public static final String CONTEXTCLASSLOADER_PARENT_FWK = "fwk"; //$NON-NLS-1$

	public static final String PROP_FRAMEWORK_LIBRARY_EXTENSIONS = "osgi.framework.library.extensions"; //$NON-NLS-1$
	public static final String PROP_COPY_NATIVES = "osgi.classloader.copy.natives"; //$NON-NLS-1$
	public static final String PROP_DEFINE_PACKAGES = "osgi.classloader.define.packages"; //$NON-NLS-1$
	public static final String PROP_BUNDLE_SETTCCL = "eclipse.bundle.setTCCL"; //$NON-NLS-1$

	public static final String PROP_EQUINOX_SECURITY = "eclipse.security"; //$NON-NLS-1$
	public static final String PROP_FILE_LIMIT = "osgi.bundlefile.limit"; //$NON-NLS-1$

	public final static String PROP_CLASS_CERTIFICATE_SUPPORT = "osgi.support.class.certificate"; //$NON-NLS-1$
	public final static String PROP_CLASS_LOADER_TYPE = "osgi.classloader.type"; //$NON-NLS-1$
	public final static String CLASS_LOADER_TYPE_PARALLEL = "parallel"; //$NON-NLS-1$

	public static final String PROP_FORCED_RESTART = "osgi.forcedRestart"; //$NON-NLS-1$
	public static final String PROP_IGNORE_USER_CONFIGURATION = "eclipse.ignoreUserConfiguration"; //$NON-NLS-1$

	public static final String PROPERTY_STRICT_BUNDLE_ENTRY_PATH = "osgi.strictBundleEntryPath";//$NON-NLS-1$

	public static final String PROP_CHECK_CONFIGURATION = "osgi.checkConfiguration"; //$NON-NLS-1$

	public static final String DEFAULT_STATE_SAVE_DELAY_INTERVAL = "30000"; //$NON-NLS-1$
	public static final String PROP_STATE_SAVE_DELAY_INTERVAL = "eclipse.stateSaveDelayInterval"; //$NON-NLS-1$

	public static final String PROP_MODULE_LOCK_TIMEOUT = "osgi.module.lock.timeout"; //$NON-NLS-1$
	public static final String PROP_MODULE_AUTO_START_ON_RESOLVE = "osgi.module.auto.start.on.resolve"; //$NON-NLS-1$
	public static final String PROP_ALLOW_RESTRICTED_PROVIDES = "osgi.equinox.allow.restricted.provides"; //$NON-NLS-1$
	public static final String PROP_LOG_HISTORY_MAX = "equinox.log.history.max"; //$NON-NLS-1$

	@Deprecated
	public static final String PROP_RESOLVER_THREAD_COUNT = "equinox.resolver.thead.count"; //$NON-NLS-1$
	public static final String PROP_EQUINOX_RESOLVER_THREAD_COUNT = "equinox.resolver.thread.count"; //$NON-NLS-1$
	public static final String PROP_EQUINOX_START_LEVEL_THREAD_COUNT = "equinox.start.level.thread.count"; //$NON-NLS-1$
	public static final String PROP_EQUINOX_START_LEVEL_RESTRICT_PARALLEL = "equinox.start.level.restrict.parallel"; //$NON-NLS-1$
	public static final String PROP_RESOLVER_REVISION_BATCH_SIZE = "equinox.resolver.revision.batch.size"; //$NON-NLS-1$
	public static final String PROP_RESOLVER_BATCH_TIMEOUT = "equinox.resolver.batch.timeout"; //$NON-NLS-1$

	public static final String PROP_SYSTEM_PROVIDE_HEADER = "equinox.system.provide.header"; //$NON-NLS-1$
	public static final String SYSTEM_PROVIDE_HEADER_ORIGINAL = "original"; //$NON-NLS-1$
	public static final String SYSTEM_PROVIDE_HEADER_SYSTEM = "system"; //$NON-NLS-1$
	public static final String SYSTEM_PROVIDE_HEADER_SYSTEM_EXTRA = "system.extra"; //$NON-NLS-1$

	public static final String PROP_DEFAULT_SUFFIX = ".default"; //$NON-NLS-1$
	public static final Collection<String> PROP_WITH_ECLIPSE_STARTER_DEFAULTS = Collections.unmodifiableList(Arrays.asList(PROP_COMPATIBILITY_BOOTDELEGATION, PROP_DS_DELAYED_KEEPINSTANCES));
	public static final String PROP_INIT_UUID = "equinox.init.uuid"; //$NON-NLS-1$

	public static final String PROP_ACTIVE_THREAD_TYPE = "osgi.framework.activeThreadType"; //$NON-NLS-1$
	public static final String ACTIVE_THREAD_TYPE_NORMAL = "normal"; //$NON-NLS-1$

	public static final String PROP_GOSH_ARGS = "gosh.args"; //$NON-NLS-1$

	// System properties
	public static final String PROP_BUNDLES = "osgi.bundles"; //$NON-NLS-1$
	public static final String PROP_BUNDLES_STARTLEVEL = "osgi.bundles.defaultStartLevel"; //$NON-NLS-1$ //The start
																							// level used to install the
																							// bundles
	public static final String PROP_EXTENSIONS = "osgi.framework.extensions"; //$NON-NLS-1$
	public static final String PROP_INITIAL_STARTLEVEL = "osgi.startLevel"; //$NON-NLS-1$ //The start level when the
																			// fwl start
	public static final String PROP_CONSOLE = "osgi.console"; //$NON-NLS-1$
	public static final String PROP_CONSOLE_CLASS = "osgi.consoleClass"; //$NON-NLS-1$
	public static final String PROP_CHECK_CONFIG = "osgi.checkConfiguration"; //$NON-NLS-1$
	public static final String PROP_OS = "osgi.os"; //$NON-NLS-1$
	public static final String PROP_WS = "osgi.ws"; //$NON-NLS-1$
	public static final String PROP_NL = "osgi.nl"; //$NON-NLS-1$
	public static final String PROP_NL_EXTENSIONS = "osgi.nl.extensions"; //$NON-NLS-1$
	public static final String PROP_ARCH = "osgi.arch"; //$NON-NLS-1$
	public static final String PROP_ADAPTOR = "osgi.adaptor"; //$NON-NLS-1$
	public static final String PROP_SYSPATH = "osgi.syspath"; //$NON-NLS-1$
	public static final String PROP_LOGFILE = "osgi.logfile"; //$NON-NLS-1$
	public static final String PROP_INSTALL_AREA = "osgi.install.area"; //$NON-NLS-1$
	public static final String PROP_FRAMEWORK_SHAPE = "osgi.framework.shape"; //$NON-NLS-1$ //the shape of the fwk
																				// (jar, or folder)
	public static final String PROP_NOSHUTDOWN = "osgi.noShutdown"; //$NON-NLS-1$

	public static final String PROP_EXITCODE = "eclipse.exitcode"; //$NON-NLS-1$
	public static final String PROP_EXITDATA = "eclipse.exitdata"; //$NON-NLS-1$
	public static final String PROP_CONSOLE_LOG = "eclipse.consoleLog"; //$NON-NLS-1$
	public static final String PROP_IGNOREAPP = "eclipse.ignoreApp"; //$NON-NLS-1$
	public static final String PROP_REFRESH_BUNDLES = "eclipse.refreshBundles"; //$NON-NLS-1$
	public static final String PROP_ALLOW_APPRELAUNCH = "eclipse.allowAppRelaunch"; //$NON-NLS-1$
	public static final String PROP_APPLICATION_LAUNCHDEFAULT = "eclipse.application.launchDefault"; //$NON-NLS-1$

	public static final String FILE_SCHEME = "file:"; //$NON-NLS-1$
	public static final String REFERENCE_SCHEME = "reference:"; //$NON-NLS-1$
	public static final String REFERENCE_PROTOCOL = "reference"; //$NON-NLS-1$
	public static final String INITIAL_LOCATION = "initial@"; //$NON-NLS-1$

	public static final int DEFAULT_INITIAL_STARTLEVEL = 6; // default value for legacy purposes
	public static final String DEFAULT_BUNDLES_STARTLEVEL = "4"; //$NON-NLS-1$
}
