/*******************************************************************************
 * Copyright (c) 2007, 2010 IBM Corporation and others.
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
package org.fdesigner.simpleconfigurator.simpleconfigurator.console;

import java.io.IOException;
import java.net.URL;

import org.fdesigner.framework.framework.BundleContext;
import org.fdesigner.framework.util.tracker.ServiceTracker;
import org.fdesigner.simpleconfigurator.provisional.configurator.Configurator;
import org.fdesigner.supplement.framework.console.CommandInterpreter;

/**
 * An OSGi console command to apply a configuration
 */
public class ApplyCommand {

	private URL configURL;
	private CommandInterpreter interpreter;
	private BundleContext context;

	public ApplyCommand(CommandInterpreter interpreter, BundleContext context, URL configURL) {
		this.interpreter = interpreter;
		this.context = context;
		this.configURL = configURL;
	}

	/**
	 * Runs the apply console command
	 */
	public void run() {
		@SuppressWarnings({"rawtypes", "unchecked"})
		ServiceTracker tracker = new ServiceTracker(context, Configurator.class.getName(), null);
		tracker.open();
		Configurator configurator = (Configurator) tracker.getService();
		if (configurator != null) {
			try {
				if (configURL != null)
					configurator.applyConfiguration(configURL);
				else
					configurator.applyConfiguration();

				if (configurator.getUrlInUse() == null)
					interpreter.println("Config URL not set.");
			} catch (IOException e) {
				interpreter.println(e.getMessage());
			}
		} else {
			interpreter.println("No configurator registered"); //$NON-NLS-1$
		}
		tracker.close();
	}
}
