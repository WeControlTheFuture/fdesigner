/*
 * Copyright (c) OSGi Alliance (2016, 2017). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fdesigner.services.cm;

import org.fdesigner.services.cm.Configuration.ConfigurationAttribute;

/**
 * An {@code Exception} class to inform the client of a {@code Configuration}
 * about the {@link ConfigurationAttribute#READ_ONLY read only} state of a
 * configuration object.
 *
 * @author $Id$
 * @since 1.6
 */
public class ReadOnlyConfigurationException extends RuntimeException {
	private static final long serialVersionUID = 1898442024230518832L;

    /**
	 * Create a {@code ReadOnlyConfigurationException} object.
	 *
	 * @param reason reason for failure
	 */
	public ReadOnlyConfigurationException(String reason) {
		super(reason);
	}
}
