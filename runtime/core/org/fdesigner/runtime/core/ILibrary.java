/*******************************************************************************
 * Copyright (c) 2000, 2014 IBM Corporation and others.
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
package org.fdesigner.runtime.core;

import org.fdesigner.framework.framework.Constants;
import org.fdesigner.runtime.common.runtime.IPath;
import org.fdesigner.supplement.util.ManifestElement;

/**
 * A runtime library declared in a plug-in. Libraries contribute elements to the
 * search path. These contributions are specified as a path to a directory or
 * Jar file. This path is always considered to be relative to the containing
 * plug-in.
 * <p>
 * Libraries are typed. The type is used to determine to which search path the
 * library's contribution should be added. The valid types are:
 * <code>CODE</code> and <code>RESOURCE</code>.
 * </p>
 *
 * @deprecated In Eclipse 3.0 the plug-in classpath representation was changed.
 *             Clients of <code>ILibrary</code> are directed to the headers
 *             associated with the relevant bundle. In particular, the
 *             <code>Bundle-Classpath</code> header contains all available
 *             information about the classpath of a plug-in. Having retrieved
 *             the header, the {@link ManifestElement} helper class can be used
 *             to parse the value and discover the individual class path
 *             entries. The various header attributes are defined in
 *             {@link Constants}.
 *             <p>
 *             For example,
 *             </p>
 *
 *             <pre>
 *     String header = bundle.getHeaders().get(Constants.BUNDLE_CLASSPATH);
 *     ManifestElement[] elements = ManifestElement.parseHeader(
 *         Constants.BUNDLE_CLASSPATH, header);
 *     if (elements == null)
 *         return;
 *     elements[0].getValue();   // the jar/dir containing the code
 *     ...
 *             </pre>
 *             <p>
 *             Note that this new structure does not include information on
 *             which packages are exported or present in the listed classpath
 *             entries. This information is no longer relevant.
 *             </p>
 *             <p>
 *             This interface must only be used by plug-ins which explicitly
 *             require the org.eclipse.core.runtime.compatibility plug-in.
 *             </p>
 * @noextend This interface is not intended to be extended by clients.
 * @noreference This interface is not intended to be referenced by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 *
 *              This interface is planned to be deleted, see
 *              https://bugs.eclipse.org/bugs/show_bug.cgi?id=544339
 *
 */
@Deprecated
public interface ILibrary {
	/**
	 * Constant string (value "code") indicating the code library type.
	 * @deprecated As of Eclipse 3.0 library types are obsolete.
	 * There is no replacement.
	 */
	@Deprecated
	public static final String CODE = "code"; //$NON-NLS-1$

	/**
	 * Constant string (value "resource") indicating the resource library type.
	 * @deprecated As of Eclipse 3.0 library types are obsolete.
	 * There is no replacement.
	 */
	@Deprecated
	public static final String RESOURCE = "resource"; //$NON-NLS-1$

	/**
	 * Returns the content filters, or <code>null</code>.
	 * Each content filter identifies a specific class, or
	 * a group of classes, using a notation and matching rules
	 * equivalent to Java <code>import</code> declarations
	 * (e.g., "java.io.File", or "java.io.*"). Returns <code>null</code>
	 * if the library is not exported, or it is fully exported
	 * (no filtering).
	 *
	 * @return the content filters, or <code>null</code> if none
	 * @deprecated As of Eclipse 3.0 content filters are obsolete.
	 * There is no replacement.
	 */
	@Deprecated
	public String[] getContentFilters();

	/**
	 * Returns the path of this runtime library, relative to the
	 * installation location.
	 *
	 * @return the path of the library
	 * @deprecated
	 * Given a manifest element corresponding to a classpath entry, the path
	 * for the entry can be accessed by getting the value of the manifest element.
	 * For example,
	 * <pre>
	 *     element.getValue();   // the jar/dir containing the code
	 * </pre>
	 */
	@Deprecated
	public IPath getPath();

	/**
	 * Returns this library's type.
	 *
	 * @return the type of this library.   The valid types are: <code>CODE</code> and <code>RESOURCE</code>.
	 * @see #CODE
	 * @see #RESOURCE
	 * @deprecated As of Eclipse 3.0 library types are obsolete.
	 * There is no replacement.
	 */
	@Deprecated
	public String getType();

	/**
	 * Returns whether the library is exported. The contents of an exported
	 * library may be visible to other plug-ins that declare a dependency
	 * on the plug-in containing this library, subject to content filtering.
	 * Libraries that are not exported are entirely private to the declaring
	 * plug-in.
	 *
	 * @return <code>true</code> if the library is exported, <code>false</code>
	 *    if it is private
	 * @deprecated As of Eclipse 3.0 exporting an individual library is obsolete.
	 * There is no replacement.
	 */
	@Deprecated
	public boolean isExported();

	/**
	 * Returns whether this library is fully exported. A library is considered
	 * fully exported iff it is exported and has no content filters.
	 *
	 * @return <code>true</code> if the library is fully exported, and
	 *    <code>false</code> if it is private or has filtered content
	 * @deprecated As of Eclipse 3.0 exporting an individual library is obsolete.
	 * There is no replacement.
	 */
	@Deprecated
	public boolean isFullyExported();

	/**
	 * Returns the array of package prefixes that this library declares. This
	 * is used in classloader enhancements and is an optional entry in the plugin.xml.
	 *
	 * @return the array of package prefixes or <code>null</code>
	 * @since 2.1
	 * @deprecated As of Eclipse 3.0 package prefix filtering is obsolete.
	 * There is no replacement.
	 */
	@Deprecated
	public String[] getPackagePrefixes();
}
