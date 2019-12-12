/*
 * Copyright (c) OSGi Alliance (2010, 2016). All Rights Reserved.
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

/**
 * User Admin Package Version 1.1.
 * 
 * <p>
 * Bundles wishing to use this package must list the package in the
 * Import-Package header of the bundle's manifest. This package has two types of
 * users: the consumers that use the API in this package and the providers that
 * implement the API in this package.
 * 
 * <p>
 * Example import for consumers using the API in this package:
 * <p>
 * {@code  Import-Package: org.osgi.service.useradmin; version="[1.1,2.0)"}
 * <p>
 * Example import for providers implementing the API in this package:
 * <p>
 * {@code  Import-Package: org.osgi.service.useradmin; version="[1.1,1.2)"}
 * 
 * @author $Id$
 */

@Version("1.1")
package org.fdesigner.services.useradmin;

import org.fdesigner.common.Version;
