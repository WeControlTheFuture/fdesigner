/*
 * Copyright (c) OSGi Alliance (2004, 2017). All Rights Reserved.
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

import org.fdesigner.common.ConsumerType;

/**
 * Listener for Configuration Events. When a {@code ConfigurationEvent} is
 * fired, it is asynchronously delivered to all {@code ConfigurationListener}s.
 * <p>
 * {@code ConfigurationListener} objects are registered with the Framework
 * service registry and are notified with a {@code ConfigurationEvent} object
 * when an event is fired.
 * <p>
 * {@code ConfigurationListener} objects can inspect the received
 * {@code ConfigurationEvent} object to determine its type, the pid of the
 * {@code Configuration} object with which it is associated, and the
 * Configuration Admin service that fired the event.
 * <p>
 * Security Considerations. Bundles wishing to monitor configuration events will
 * require {@code ServicePermission[ConfigurationListener,REGISTER]} to register
 * a {@code ConfigurationListener} service.
 *
 * @author $Id$
 * @since 1.2
 * @ThreadSafe
 */
@ConsumerType
public interface ConfigurationListener {
	/**
	 * Receives notification of a Configuration that has changed.
	 *
	 * @param event The {@code ConfigurationEvent}.
	 */
	public void configurationEvent(ConfigurationEvent event);
}
