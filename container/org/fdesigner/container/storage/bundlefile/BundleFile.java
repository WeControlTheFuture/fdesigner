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

package org.fdesigner.container.storage.bundlefile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.util.Enumeration;

import org.fdesigner.container.Module;
import org.fdesigner.container.framework.util.SecureAction;
import org.fdesigner.container.storage.url.BundleResourceHandler;
import org.fdesigner.container.storage.url.bundleentry.Handler;

/**
 * The BundleFile API is used by Adaptors to read resources out of an 
 * installed Bundle in the Framework.
 * <p/>
 * Clients wishing to modify or extend the functionality of this class at
 * runtime should extend the associated {@link BundleFileWrapper decorator}
 * instead.
 */
abstract public class BundleFile {
	static final SecureAction secureAction = AccessController.doPrivileged(SecureAction.createSecureAction());
	/**
	 * The File object for this BundleFile.
	 */
	protected File basefile;
	private int mruIndex = -1;

	/**
	 * BundleFile constructor
	 * @param basefile The File object where this BundleFile is 
	 * persistently stored.
	 */
	public BundleFile(File basefile) {
		this.basefile = basefile;
	}

	/**
	 * Returns a File for the bundle entry specified by the path.
	 * If required the content of the bundle entry is extracted into a file
	 * on the file system.
	 * @param path The path to the entry to locate a File for.
	 * @param nativeCode true if the path is native code.
	 * @return A File object to access the contents of the bundle entry.
	 */
	abstract public File getFile(String path, boolean nativeCode);

	/**
	 * Locates a file name in this bundle and returns a BundleEntry object
	 *
	 * @param path path of the entry to locate in the bundle
	 * @return BundleEntry object or null if the file name
	 *         does not exist in the bundle
	 */
	abstract public BundleEntry getEntry(String path);

	/** 
	 * Performs the same function as calling 
	 * {@link #getEntryPaths(String, boolean)} with <code>recurse</code> equal
	 * to <code>false</code>.
	 * @param path path of the entry to locate in the bundle
	 * @return an Enumeration of Strings that indicate the paths found or
	 * null if the path does not exist. 
	 */
	public Enumeration<String> getEntryPaths(String path) {
		return getEntryPaths(path, false);
	}

	/** 
	 * Allows to access the entries of the bundle. 
	 * Since the bundle content is usually a jar, this 
	 * allows to access the jar contents.
	 * 
	 * GetEntryPaths allows to enumerate the content of "path".
	 * If path is a directory, it is equivalent to listing the directory
	 * contents. The returned names are either files or directories 
	 * themselves. If a returned name is a directory, it finishes with a 
	 * slash. If a returned name is a file, it does not finish with a slash.
	 * @param path path of the entry to locate in the bundle
	 * @param recurse - If <code>true</code>, provide entries for the files and 
	 *        directories within the directory denoted by <code>path</code> plus
	 *        all sub-directories and files; otherwise, provide only the entries
	 *        within the immediate directory.
	 * @return an Enumeration of Strings that indicate the paths found or
	 * null if the path does not exist. 
	 */
	abstract public Enumeration<String> getEntryPaths(String path, boolean recurse);

	/**
	 * Closes the BundleFile.
	 * @throws IOException if any error occurs.
	 */
	abstract public void close() throws IOException;

	/**
	 * Opens the BundleFiles.
	 * @throws IOException if any error occurs.
	 */
	abstract public void open() throws IOException;

	/**
	 * Determines if any BundleEntries exist in the given directory path.
	 * @param dir The directory path to check existence of.
	 * @return true if the BundleFile contains entries under the given directory path;
	 * false otherwise.
	 */
	abstract public boolean containsDir(String dir);

	/**
	 * Returns a URL to access the contents of the entry specified by the path.
	 * This method first calls {@link #getEntry(String)} to locate the entry
	 * at the specified path.  If no entry is found {@code null} is returned;
	 * otherwise {@link #createResourceURL(BundleEntry, Module, int, String)}
	 * is called in order to create the URL.  Subclasses should not override
	 * this method.  Instead the methods {@link #getEntry(String)} and/or
	 * {@link #createResourceURL(BundleEntry, Module, int, String)} may be
	 * overriden to augment the behavior.
	 * @param path the path to the resource
	 * @param hostModule the host module
	 * @param index the resource index
	 * @return a URL to access the contents of the entry specified by the path
	 */
	public URL getResourceURL(String path, Module hostModule, int index) {
		BundleEntry bundleEntry = getEntry(path);
		if (bundleEntry == null)
			return null;
		return createResourceURL(bundleEntry, hostModule, index, path);
	}

	/**
	 * Creates a URL to access the content of the specified entry
	 * @param bundleEntry the bundle entry
	 * @param hostModule the host module
	 * @param index the resource index
	 * @param path
	 * @return a URL to access the contents of the specified entry
	 */
	protected URL createResourceURL(BundleEntry bundleEntry, Module hostModule, int index, String path) {
		long hostBundleID = hostModule.getId();
		path = fixTrailingSlash(path, bundleEntry);
		try {
			//use the constant string for the protocol to prevent duplication
			return secureAction.getURL(BundleResourceHandler.OSGI_RESOURCE_URL_PROTOCOL, Long.toString(hostBundleID) + BundleResourceHandler.BID_FWKID_SEPARATOR + Integer.toString(hostModule.getContainer().hashCode()), index, path, new Handler(hostModule.getContainer(), bundleEntry));
		} catch (MalformedURLException e) {
			return null;
		}
	}

	/**
	 * Returns the base file for this BundleFile
	 * @return the base file for this BundleFile
	 */
	public File getBaseFile() {
		return basefile;
	}

	void setMruIndex(int index) {
		mruIndex = index;
	}

	int getMruIndex() {
		return mruIndex;
	}

	@Override
	public String toString() {
		return String.valueOf(basefile);
	}

	public static String fixTrailingSlash(String path, BundleEntry entry) {
		if (path.length() == 0)
			return "/"; //$NON-NLS-1$
		if (path.charAt(0) != '/')
			path = '/' + path;
		String name = entry.getName();
		if (name.length() == 0)
			return path;
		boolean pathSlash = path.charAt(path.length() - 1) == '/';
		boolean entrySlash = name.length() > 0 && name.charAt(name.length() - 1) == '/';
		if (entrySlash != pathSlash) {
			if (entrySlash)
				path = path + '/';
			else
				path = path.substring(0, path.length() - 1);
		}
		return path;
	}

}
