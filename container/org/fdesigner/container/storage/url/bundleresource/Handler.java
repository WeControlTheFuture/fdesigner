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

package org.fdesigner.container.storage.url.bundleresource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import org.fdesigner.container.Module;
import org.fdesigner.container.ModuleContainer;
import org.fdesigner.container.ModuleRevision;
import org.fdesigner.container.ModuleWiring;
import org.fdesigner.container.internal.loader.ModuleClassLoader;
import org.fdesigner.container.storage.bundlefile.BundleEntry;
import org.fdesigner.container.storage.url.BundleResourceHandler;

/**
 * URLStreamHandler the bundleresource protocol.
 */

public class Handler extends BundleResourceHandler {

	public Handler(ModuleContainer container, BundleEntry bundleEntry) {
		super(container, bundleEntry);
	}

	@Override
	protected BundleEntry findBundleEntry(URL url, Module module) throws IOException {
		ModuleRevision current = module.getCurrentRevision();
		ModuleWiring wiring = current == null ? null : current.getWiring();
		ModuleClassLoader classloader = (ModuleClassLoader) (current == null ? null : wiring.getClassLoader());
		if (classloader == null)
			throw new FileNotFoundException(url.getPath());
		BundleEntry entry = classloader.getClasspathManager().findLocalEntry(url.getPath(), url.getPort());
		if (entry == null) {
			// this isn't strictly needed but is kept to maintain compatibility
			entry = classloader.getClasspathManager().findLocalEntry(url.getPath());
		}
		if (entry == null) {
			throw new FileNotFoundException(url.getPath());
		}
		return entry;
	}

}
