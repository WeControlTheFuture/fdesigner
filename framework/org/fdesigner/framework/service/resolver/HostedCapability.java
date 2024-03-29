/*
 * Copyright (c) OSGi Alliance (2012, 2015). All Rights Reserved.
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

package org.fdesigner.framework.service.resolver;

import org.fdesigner.common.ProviderType;
import org.fdesigner.framework.resource.Capability;
import org.fdesigner.framework.resource.Resource;

/**
 * A capability hosted by a resource.
 * 
 * <p>
 * A HostedCapability is a Capability where the {@link #getResource()} method
 * returns a Resource that hosts this Capability instead of declaring it. This
 * is necessary for cases where the declaring Resource of a Capability does not
 * match the runtime state. For example, this is the case for fragments attached
 * to a host. Most fragment declared capabilities and requirements become hosted
 * by the host resource. Since a fragment can attach to multiple hosts, a single
 * capability can actually be hosted multiple times.
 * 
 * @ThreadSafe
 * @author $Id$
 */
@ProviderType
public interface HostedCapability extends Capability {

	/**
	 * Return the Resource that hosts this Capability.
	 * 
	 * @return The Resource that hosts this Capability.
	 */
	@Override
	Resource getResource();

	/**
	 * Return the Capability hosted by the Resource.
	 * 
	 * @return The Capability hosted by the Resource.
	 */
	Capability getDeclaredCapability();
}
