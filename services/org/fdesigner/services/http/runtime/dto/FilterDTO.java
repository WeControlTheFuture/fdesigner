/*
 * Copyright (c) OSGi Alliance (2012, 2017). All Rights Reserved.
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

package org.fdesigner.services.http.runtime.dto;

import java.util.Map;

import org.fdesigner.framework.dto.DTO;

/**
 * Represents a servlet {@code javax.servlet.Filter} service currently being
 * used for by a servlet context.
 * 
 * @NotThreadSafe
 * @author $Id$
 */
public class FilterDTO extends DTO {
	/**
	 * The name of the servlet filter. This field is never {@code null}.
	 */
	public String				name;

	/**
	 * The request mappings for the servlet filter.
	 * 
	 * <p>
	 * The specified patterns are used to determine whether a request is mapped
	 * to the servlet filter. This array might be empty.
	 */
	public String[]				patterns;

	/**
	 * The servlet names for the servlet filter.
	 * 
	 * <p>
	 * The specified names are used to determine the servlets whose requests are
	 * mapped to the servlet filter. This array might be empty.
	 */
	public String[]				servletNames;

	/**
	 * The request mappings for the servlet filter.
	 * 
	 * <p>
	 * The specified regular expressions are used to determine whether a request
	 * is mapped to the servlet filter. This array might be empty.
	 */
	public String[]				regexs;

	/**
	 * Specifies whether the servlet filter supports asynchronous processing.
	 */
	public boolean				asyncSupported;

	/**
	 * The dispatcher associations for the servlet filter.
	 * 
	 * <p>
	 * The specified names are used to determine in what occasions the servlet
	 * filter is called. This array is never {@code null}.
	 */
	public String[]				dispatcher;

	/**
	 * The servlet filter initialization parameters as provided during
	 * registration of the servlet filter. Additional parameters like the Http
	 * Service Runtime attributes are not included. If the servlet filter has
	 * not initialization parameters, this map is empty.
	 */
	public Map<String, String>	initParams;

	/**
	 * Service property identifying the servlet filter. In the case of a servlet
	 * filter registered in the service registry and picked up by a Http
	 * Whiteboard Implementation, this value is not negative and corresponds to
	 * the service id in the registry. If the servlet filter has not been
	 * registered in the service registry, the value is negative and a unique
	 * negative value is generated by the Http Service Runtime in this case.
	 */
	public long					serviceId;

	/**
	 * The service id of the servlet context for the servlet filter represented
	 * by this DTO.
	 */
	public long		servletContextId;
}
