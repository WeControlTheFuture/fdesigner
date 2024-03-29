/*******************************************************************************
 * Copyright (c) 2012, 2017 IBM Corporation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which accompanies this distribution,
 * and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/
package org.fdesigner.compatibility.state;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.fdesigner.compatibility.state.internal.module.ResolverImpl;
import org.fdesigner.compatibility.state.internal.resolver.StateHelperImpl;
import org.fdesigner.compatibility.state.internal.resolver.StateObjectFactoryImpl;
import org.fdesigner.container.Module;
import org.fdesigner.container.ModuleContainer;
import org.fdesigner.container.ModuleDatabase;
import org.fdesigner.container.ModuleRevision;
import org.fdesigner.container.internal.framework.BundleContextImpl;
import org.fdesigner.container.internal.framework.EquinoxContainer;
import org.fdesigner.container.service.resolver.BundleDescription;
import org.fdesigner.container.service.resolver.DisabledInfo;
import org.fdesigner.container.service.resolver.PlatformAdmin;
import org.fdesigner.container.service.resolver.Resolver;
import org.fdesigner.container.service.resolver.State;
import org.fdesigner.container.service.resolver.StateHelper;
import org.fdesigner.container.service.resolver.StateObjectFactory;
import org.fdesigner.framework.framework.BundleContext;
import org.fdesigner.framework.framework.BundleException;
import org.fdesigner.framework.framework.ServiceRegistration;

public class PlatformAdminImpl implements PlatformAdmin {
	private final StateObjectFactory factory = new StateObjectFactoryImpl();
	private final Object monitor = new Object();
	private EquinoxContainer equinoxContainer;
	private BundleContext bc;
	private State systemState;
	private PlatformBundleListener synchronizer;
	private ServiceRegistration<PlatformAdmin> reg;

	void start(BundleContext context) {
		synchronized (this.monitor) {
			equinoxContainer = ((BundleContextImpl) context).getContainer();
			this.bc = context;
		}
		this.reg = context.registerService(PlatformAdmin.class, this, null);
	}

	void stop(BundleContext context) {
		synchronized (this.monitor) {
			if (synchronizer != null) {
				context.removeBundleListener(synchronizer);
				context.removeFrameworkListener(synchronizer);
			}
			synchronizer = null;
			systemState = null;
		}
		this.reg.unregister();
	}

	@Override
	public State getState() {
		return getState(true);
	}

	@Override
	public State getState(boolean mutable) {
		if (mutable) {
			return factory.createState(getSystemState());
		}
		return new ReadOnlyState(this);
	}

	State getSystemState() {
		synchronized (this.monitor) {
			if (systemState == null) {
				systemState = createSystemState();
			}
			return systemState;
		}
	}

	long getTimeStamp() {
		synchronized (this.monitor) {
			return equinoxContainer.getStorage().getModuleDatabase().getRevisionsTimestamp();
		}
	}

	private State createSystemState() {
		State state = factory.createState(true);
		StateConverter converter = new StateConverter(state);
		ModuleDatabase database = equinoxContainer.getStorage().getModuleDatabase();
		database.readLock();
		try {
			ModuleContainer container = equinoxContainer.getStorage().getModuleContainer();
			List<Module> modules = equinoxContainer.getStorage().getModuleContainer().getModules();
			for (Module module : modules) {
				ModuleRevision current = module.getCurrentRevision();
				BundleDescription description = converter.createDescription(current);
				state.addBundle(description);
			}
			state.setPlatformProperties(asDictionary(equinoxContainer.getConfiguration().getInitialConfig()));
			synchronizer = new PlatformBundleListener(state, converter, database, container);
			state.setResolverHookFactory(synchronizer);
			bc.addBundleListener(synchronizer);
			bc.addFrameworkListener(synchronizer);
			state.resolve();
			state.setTimeStamp(database.getRevisionsTimestamp());
		} finally {
			database.readUnlock();
		}
		return state;
	}

	private Dictionary<String, Object> asDictionary(Map<String, ?> map) {
		return new Hashtable<>(map);
	}

	@Override
	public StateHelper getStateHelper() {
		return StateHelperImpl.getInstance();
	}

	/**
	 * @throws BundleException  
	 */
	@Override
	public void commit(State state) throws BundleException {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public Resolver getResolver() {
		return createResolver();
	}

	@Override
	public Resolver createResolver() {
		return new ResolverImpl(false);
	}

	@Override
	public StateObjectFactory getFactory() {
		return factory;
	}

	@Override
	public void addDisabledInfo(DisabledInfo disabledInfo) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeDisabledInfo(DisabledInfo disabledInfo) {
		throw new UnsupportedOperationException();
	}

}
