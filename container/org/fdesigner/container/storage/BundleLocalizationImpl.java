/*******************************************************************************
 * Copyright (c) 2004, 2012 IBM Corporation and others.
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
package org.fdesigner.container.storage;

import java.util.ResourceBundle;

import org.fdesigner.container.Module;
import org.fdesigner.container.ModuleRevision;
import org.fdesigner.container.internal.framework.EquinoxBundle;
import org.fdesigner.container.storage.BundleInfo.Generation;
import org.fdesigner.framework.framework.Bundle;
import org.fdesigner.supplement.service.localization.BundleLocalization;

/**
 * The implementation of the service that gets ResourceBundle objects from a given 
 * bundle with a given locale. 
 * 
 * <p>Internal class.</p>
 */

public class BundleLocalizationImpl implements BundleLocalization {
	/**
	 * The getLocalization method gets a ResourceBundle object for the given
	 * locale and bundle.
	 * 
	 * @return A <code>ResourceBundle</code> object for the given bundle and locale.
	 * If null is passed for the locale parameter, the default locale is used.
	 */
	@Override
	public ResourceBundle getLocalization(Bundle bundle, String locale) {
		Module m = ((EquinoxBundle) bundle).getModule();
		ModuleRevision r = m.getCurrentRevision();
		Generation g = (Generation) r.getRevisionInfo();
		return g.getResourceBundle(locale);
	}
}
