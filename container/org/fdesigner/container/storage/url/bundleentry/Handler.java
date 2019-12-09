/*******************************************************************************
 * Copyright (c) 2004, 2017 IBM Corporation and others.
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

package org.fdesigner.container.storage.url.bundleentry;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import org.fdesigner.container.Module;
import org.fdesigner.container.ModuleContainer;
import org.fdesigner.container.ModuleRevision;
import org.fdesigner.container.storage.BundleInfo;
import org.fdesigner.container.storage.bundlefile.BundleEntry;
import org.fdesigner.container.storage.url.BundleResourceHandler;
import org.fdesigner.supplement.internal.location.LocationHelper;

/**
 * URLStreamHandler the bundleentry protocol.
 */

public class Handler extends BundleResourceHandler {

	public Handler(ModuleContainer container, BundleEntry bundleEntry) {
		super(container, bundleEntry);
	}

	@Override
	protected BundleEntry findBundleEntry(URL url, Module module) throws IOException {
		ModuleRevision revision = module.getCurrentRevision();
		BundleInfo.Generation revisionInfo = (BundleInfo.Generation) revision.getRevisionInfo();
		BundleEntry entry = revisionInfo == null ? null : revisionInfo.getBundleFile().getEntry(url.getPath());
		if (entry == null) {
			String path = url.getPath();
			if (revisionInfo != null && (path.indexOf('%') >= 0 || path.indexOf('+') >= 0)) {
				entry = revisionInfo.getBundleFile().getEntry(LocationHelper.decode(path, true));
				if (entry != null) {
					return entry;
				}
				entry = revisionInfo.getBundleFile().getEntry(LocationHelper.decode(path, false));
				if (entry != null) {
					return entry;
				}
			}
			throw new FileNotFoundException(url.getPath());
		}
		return entry;
	}
}
