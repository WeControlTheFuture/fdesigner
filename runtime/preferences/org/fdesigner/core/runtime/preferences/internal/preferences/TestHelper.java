/*******************************************************************************
 * Copyright (c) 2011, 2015 IBM Corporation and others.
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
package org.fdesigner.core.runtime.preferences.internal.preferences;

import java.util.Properties;

import org.fdesigner.core.runtime.preferences.service.prefs.BackingStoreException;
import org.fdesigner.runtime.common.runtime.IPath;

public class TestHelper {

	public static Properties convertToProperties(EclipsePreferences node, String prefix) throws BackingStoreException {
		return node.convertToProperties(new Properties(), prefix);
	}

	public static IPath getInstanceBaseLocation() {
		return InstancePreferences.getBaseLocation();
	}
}
