/*******************************************************************************
 * Copyright (c) 2003, 2014 IBM Corporation and others.
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
 *     Julian Chen - fix for bug #92572, jclRM
 *******************************************************************************/
package org.fdesigner.runtime.core.internal.runtime;

import org.fdesigner.framework.framework.BundleActivator;
import org.fdesigner.framework.framework.BundleContext;
import org.fdesigner.runtime.app.internal.app.CommandLineArgs;
import org.fdesigner.runtime.core.Plugin;

/**
 * Activator for the Eclipse runtime.
 */
public class PlatformActivator extends Plugin implements BundleActivator {
	private static BundleContext context;

	public static BundleContext getContext() {
		return context;
	}

	@Override
	public void start(BundleContext runtimeContext) throws Exception {
		PlatformActivator.context = runtimeContext;
		InternalPlatform.getDefault().start(runtimeContext);
		startAppContainer();
		InternalPlatform.getDefault().setRuntimeInstance(this);
		super.start(runtimeContext);
	}

	@Override
	public void stop(BundleContext runtimeContext) {
		// Stop the platform orderly.
		InternalPlatform.getDefault().stop(runtimeContext);
		InternalPlatform.getDefault().setRuntimeInstance(null);
	}

	private void startAppContainer() {
		// just using a class out of app admin to force it to lazy-start
		CommandLineArgs.getApplicationArgs();
	}
}
