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
package org.fdesigner.core.runtime.internal.runtime;

import org.fdesigner.core.runtime.IProduct;
import org.fdesigner.framework.framework.Bundle;
import org.fdesigner.runtime.app.internal.app.IBranding;

public class Product implements IProduct {
	IBranding branding;
	public Product(IBranding branding) {
		this.branding = branding;
	}

	@Override
	public String getApplication() {
		return branding.getApplication();
	}

	@Override
	public Bundle getDefiningBundle() {
		return branding.getDefiningBundle();
	}

	@Override
	public String getDescription() {
		return branding.getDescription();
	}

	@Override
	public String getId() {
		return branding.getId();
	}

	@Override
	public String getName() {
		return branding.getName();
	}

	@Override
	public String getProperty(String key) {
		return branding.getProperty(key);
	}
}
