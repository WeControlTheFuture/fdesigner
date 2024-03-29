/*******************************************************************************
 * Copyright (c) 2004, 2011 IBM Corporation and others.
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
package org.fdesigner.compatibility.state.internal.module;

import org.fdesigner.container.service.resolver.BaseDescription;
import org.fdesigner.container.service.resolver.BundleDescription;
import org.fdesigner.framework.framework.Version;
import org.fdesigner.framework.framework.wiring.BundleCapability;

/*
 * A companion to BaseDescription from the state used while resolving.
 */
public abstract class VersionSupplier {
	final protected BaseDescription base;
	final private BundleCapability capability;
	private VersionSupplier substitute;

	VersionSupplier(BaseDescription base) {
		this.base = base;
		this.capability = base.getCapability();
	}

	public Version getVersion() {
		return base.getVersion();
	}

	public String getName() {
		return base.getName();
	}

	public BaseDescription getBaseDescription() {
		return base;
	}

	// returns the version supplier that has been substituted for this version supplier
	VersionSupplier getSubstitute() {
		return substitute;
	}

	// sets the dropped status.  This should only be called by the VersionHashMap 
	// when VersionSuppliers are removed
	void setSubstitute(VersionSupplier substitute) {
		this.substitute = substitute;
	}

	/*
	 * returns the BundleDescription which supplies this VersionSupplier
	 */
	abstract public BundleDescription getBundleDescription();

	/*
	 * returns the ResolverBundle which supplies this VersionSupplier 
	 */
	abstract ResolverBundle getResolverBundle();

	@Override
	public String toString() {
		return base.toString();
	}

	BundleCapability getCapability() {
		return capability;
	}
}
