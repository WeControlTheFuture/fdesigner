/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
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

package org.fdesigner.runtime.registry.internal.registry;

import org.fdesigner.runtime.common.runtime.CoreException;
import org.fdesigner.runtime.common.runtime.IStatus;
import org.fdesigner.runtime.common.runtime.Status;
import org.fdesigner.runtime.core.*;
import org.fdesigner.runtime.registry.runtime.spi.IRegistryProvider;

/**
 * @since org.eclipse.equinox.registry 3.2
 */
public final class RegistryProviderFactory {

	private static IRegistryProvider defaultRegistryProvider;

	public static IRegistryProvider getDefault() {
		return defaultRegistryProvider;
	}

	public static void setDefault(IRegistryProvider provider) throws CoreException {
		if (defaultRegistryProvider != null) {
			Status status = new Status(IStatus.ERROR, RegistryMessages.OWNER_NAME, IRegistryConstants.PLUGIN_ERROR, RegistryMessages.registry_default_exists, null);
			throw new CoreException(status);
		}
		defaultRegistryProvider = provider;
	}

	public static void releaseDefault() {
		defaultRegistryProvider = null;
	}
}
