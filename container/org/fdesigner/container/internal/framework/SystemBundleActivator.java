/*******************************************************************************
 * Copyright (c) 2003, 2016 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.fdesigner.container.internal.framework;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.fdesigner.container.internal.debug.Debug;
import org.fdesigner.container.internal.debug.FrameworkDebugOptions;
import org.fdesigner.container.internal.location.EquinoxLocations;
import org.fdesigner.container.internal.permadmin.EquinoxSecurityManager;
import org.fdesigner.container.internal.permadmin.SecurityAdmin;
import org.fdesigner.container.internal.url.EquinoxFactoryManager;
import org.fdesigner.container.storage.BundleLocalizationImpl;
import org.fdesigner.container.storage.url.BundleResourceHandler;
import org.fdesigner.container.storage.url.BundleURLConverter;
import org.fdesigner.felix.Logger;
import org.fdesigner.felix.ResolverImpl;
import org.fdesigner.framework.framework.BundleActivator;
import org.fdesigner.framework.framework.BundleContext;
import org.fdesigner.framework.framework.BundleException;
import org.fdesigner.framework.framework.Constants;
import org.fdesigner.framework.framework.ServiceRegistration;
import org.fdesigner.framework.service.condpermadmin.ConditionalPermissionAdmin;
import org.fdesigner.framework.service.packageadmin.PackageAdmin;
import org.fdesigner.framework.service.permissionadmin.PermissionAdmin;
import org.fdesigner.framework.service.resolver.Resolver;
import org.fdesigner.framework.service.startlevel.StartLevel;
import org.fdesigner.supplement.service.datalocation.Location;
import org.fdesigner.supplement.service.debug.DebugOptions;
import org.fdesigner.supplement.service.debug.DebugOptionsListener;
import org.fdesigner.supplement.service.environment.EnvironmentInfo;
import org.fdesigner.supplement.service.localization.BundleLocalization;
import org.fdesigner.supplement.service.urlconversion.URLConverter;

/**
 * This class activates the System Bundle.
 */

public class SystemBundleActivator implements BundleActivator {
	private EquinoxFactoryManager urlFactoryManager;
	private List<ServiceRegistration<?>> registrations = new ArrayList<>(10);
	private SecurityManager setSecurityManagner;

	@SuppressWarnings("deprecation")
	@Override
	public void start(BundleContext bc) throws Exception {
		registrations.clear();
		EquinoxBundle bundle = (EquinoxBundle) bc.getBundle();

		bundle.getEquinoxContainer().systemStart(bc);

		EquinoxConfiguration configuration = bundle.getEquinoxContainer().getConfiguration();
		installSecurityManager(configuration);
		bundle.getEquinoxContainer().getLogServices().start(bc);

		urlFactoryManager = new EquinoxFactoryManager(bundle.getEquinoxContainer());
		urlFactoryManager.installHandlerFactories(bc);

		FrameworkDebugOptions dbgOptions = (FrameworkDebugOptions) configuration.getDebugOptions();
		dbgOptions.start(bc);

		SecurityAdmin sa = bundle.getEquinoxContainer().getStorage().getSecurityAdmin();
		ClassLoader tccl = bundle.getEquinoxContainer().getContextFinder();

		registerLocations(bc, bundle.getEquinoxContainer().getLocations());
		register(bc, EnvironmentInfo.class, bundle.getEquinoxContainer().getConfiguration(), null);
		register(bc, PackageAdmin.class, bundle.getEquinoxContainer().getPackageAdmin(), null);
		register(bc, StartLevel.class, bundle.getEquinoxContainer().getStartLevel(), null);

		register(bc, PermissionAdmin.class, sa, null);
		register(bc, ConditionalPermissionAdmin.class, sa, null);

		Hashtable<String, Object> props = new Hashtable<>(7);
		props.clear();
		props.put(Constants.SERVICE_RANKING, Integer.MIN_VALUE);
		register(bc, Resolver.class, new ResolverImpl(new Logger(0), null), false, props);

		register(bc, DebugOptions.class, dbgOptions, null);

		if (tccl != null) {
			props.clear();
			props.put("equinox.classloader.type", "contextClassLoader"); //$NON-NLS-1$ //$NON-NLS-2$
			register(bc, ClassLoader.class, tccl, props);
		}

		props.clear();
		props.put("protocol", new String[] {BundleResourceHandler.OSGI_ENTRY_URL_PROTOCOL, BundleResourceHandler.OSGI_RESOURCE_URL_PROTOCOL}); //$NON-NLS-1$
		register(bc, URLConverter.class, new BundleURLConverter(), props);

		register(bc, BundleLocalization.class, new BundleLocalizationImpl(), null);

		boolean setTccl = "true".equals(bundle.getEquinoxContainer().getConfiguration().getConfiguration("eclipse.parsers.setTCCL", "true")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		try {
			register(bc, "javax.xml.parsers.SAXParserFactory", new XMLParsingServiceFactory(true, setTccl), false, null); //$NON-NLS-1$
			register(bc, "javax.xml.parsers.DocumentBuilderFactory", new XMLParsingServiceFactory(false, setTccl), false, null); //$NON-NLS-1$
		} catch (NoClassDefFoundError e) {
			// ignore; on a platform with no javax.xml (Java 8 SE compact1 profile)
		}
		bundle.getEquinoxContainer().getStorage().getExtensionInstaller().startExtensionActivators(bc);

		// Add an options listener; we already read the options on initialization.
		// Here we are just allowing the options to change
		props.clear();
		props.put(DebugOptions.LISTENER_SYMBOLICNAME, EquinoxContainer.NAME);
		register(bc, DebugOptionsListener.class, bundle.getEquinoxContainer().getConfiguration().getDebug(), props);
		register(bc, DebugOptionsListener.class, bundle.getModule().getContainer(), props);
	}

	private void installSecurityManager(EquinoxConfiguration configuration) throws BundleException {
		String securityManager = configuration.getConfiguration(Constants.FRAMEWORK_SECURITY);
		if (System.getSecurityManager() != null && securityManager != null) {
			throw new BundleException("Cannot specify the \"" + Constants.FRAMEWORK_SECURITY + "\" configuration property when a security manager is already installed."); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if (securityManager == null) {
			securityManager = configuration.getConfiguration(EquinoxConfiguration.PROP_EQUINOX_SECURITY, configuration.getProperty("java.security.manager")); //$NON-NLS-1$
		}
		if (securityManager != null) {
			SecurityManager sm = System.getSecurityManager();
			if (sm == null) {
				if (securityManager.length() == 0)
					sm = new SecurityManager(); // use the default one from java
				else if (securityManager.equals(Constants.FRAMEWORK_SECURITY_OSGI))
					sm = new EquinoxSecurityManager(); // use an OSGi enabled manager that understands postponed conditions
				else {
					// try to use a specific classloader by classname
					try {
						Class<?> clazz = Class.forName(securityManager);
						sm = (SecurityManager) clazz.getConstructor().newInstance();
					} catch (Throwable t) {
						throw new BundleException("Failed to create security manager", t); //$NON-NLS-1$
					}
				}
				if (configuration.getDebug().DEBUG_SECURITY)
					Debug.println("Setting SecurityManager to: " + sm); //$NON-NLS-1$
				System.setSecurityManager(sm);
				setSecurityManagner = sm;
				return;
			}
		}
	}

	private void registerLocations(BundleContext bc, EquinoxLocations equinoxLocations) {
		Dictionary<String, Object> locationProperties = new Hashtable<>(1);
		Location location = equinoxLocations.getUserLocation();
		if (location != null) {
			locationProperties.put("type", EquinoxLocations.PROP_USER_AREA); //$NON-NLS-1$
			register(bc, Location.class, location, locationProperties);
		}
		location = equinoxLocations.getInstanceLocation();
		if (location != null) {
			locationProperties.put("type", EquinoxLocations.PROP_INSTANCE_AREA); //$NON-NLS-1$
			register(bc, Location.class, location, locationProperties);
		}
		location = equinoxLocations.getConfigurationLocation();
		if (location != null) {
			locationProperties.put("type", EquinoxLocations.PROP_CONFIG_AREA); //$NON-NLS-1$
			register(bc, Location.class, location, locationProperties);
		}
		location = equinoxLocations.getInstallLocation();
		if (location != null) {
			locationProperties.put("type", EquinoxLocations.PROP_INSTALL_AREA); //$NON-NLS-1$
			register(bc, Location.class, location, locationProperties);
		}

		location = equinoxLocations.getEclipseHomeLocation();
		if (location != null) {
			locationProperties.put("type", EquinoxLocations.PROP_HOME_LOCATION_AREA); //$NON-NLS-1$
			register(bc, Location.class, location, locationProperties);
		}
	}

	@Override
	public void stop(BundleContext bc) throws Exception {
		EquinoxBundle bundle = (EquinoxBundle) bc.getBundle();

		bundle.getEquinoxContainer().getStorage().getExtensionInstaller().stopExtensionActivators(bc);

		FrameworkDebugOptions dbgOptions = (FrameworkDebugOptions) bundle.getEquinoxContainer().getConfiguration().getDebugOptions();
		dbgOptions.stop(bc);

		urlFactoryManager.uninstallHandlerFactories();

		// unregister services
		for (ServiceRegistration<?> registration : registrations)
			registration.unregister();
		registrations.clear();
		bundle.getEquinoxContainer().getLogServices().stop(bc);
		unintallSecurityManager();
		bundle.getEquinoxContainer().systemStop(bc);
	}

	private void unintallSecurityManager() {
		if (setSecurityManagner != null && System.getSecurityManager() == setSecurityManagner)
			System.setSecurityManager(null);
		setSecurityManagner = null;
	}

	private void register(BundleContext context, Class<?> serviceClass, Object service, Dictionary<String, Object> properties) {
		register(context, serviceClass.getName(), service, true, properties);
	}

	private void register(BundleContext context, Class<?> serviceClass, Object service, boolean setRanking, Dictionary<String, Object> properties) {
		register(context, serviceClass.getName(), service, setRanking, properties);
	}

	private void register(BundleContext context, String serviceClass, Object service, boolean setRanking, Dictionary<String, Object> properties) {
		if (properties == null)
			properties = new Hashtable<>();
		if (setRanking) {
			properties.put(Constants.SERVICE_RANKING, Integer.valueOf(Integer.MAX_VALUE));
		}
		properties.put(Constants.SERVICE_PID, context.getBundle().getBundleId() + "." + service.getClass().getName()); //$NON-NLS-1$
		registrations.add(context.registerService(serviceClass, service, properties));
	}
}
