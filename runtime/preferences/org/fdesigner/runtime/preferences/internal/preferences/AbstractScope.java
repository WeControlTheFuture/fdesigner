/*******************************************************************************
 * Copyright (c) 2004, 2015 IBM Corporation and others.
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
package org.fdesigner.runtime.preferences.internal.preferences;

import org.fdesigner.runtime.common.runtime.IPath;
import org.fdesigner.runtime.preferences.runtime.preferences.IEclipsePreferences;
import org.fdesigner.runtime.preferences.runtime.preferences.IScopeContext;

/**
 * Abstract super-class for scope context object contributed
 * by the Platform.
 *
 * @since 3.0
 */
public abstract class AbstractScope implements IScopeContext {


	@Override
	public abstract String getName();

	/*
	 * Default path hierarchy for nodes is /<scope>/<qualifier>.
	 *
	 * @see org.eclipse.core.runtime.preferences.IScopeContext#getNode(java.lang.String)
	 */
	@Override
	public IEclipsePreferences getNode(String qualifier) {
		if (qualifier == null)
			throw new IllegalArgumentException();
		return (IEclipsePreferences) PreferencesService.getDefault().getRootNode().node(getName()).node(qualifier);
	}


	@Override
	public abstract IPath getLocation();


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof IScopeContext))
			return false;
		IScopeContext other = (IScopeContext) obj;
		if (!getName().equals(other.getName()))
			return false;
		IPath location = getLocation();
		return location == null ? other.getLocation() == null : location.equals(other.getLocation());
	}


	@Override
	public int hashCode() {
		return getName().hashCode();
	}
}
