/*******************************************************************************
 * Copyright (c) 2007, 2013 IBM Corporation and others.
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
 *     Rob Harrop - SpringSource Inc. (bug 247522)
 *******************************************************************************/
package org.fdesigner.compatibility.state.internal.resolver;

import java.util.Collections;
import java.util.Dictionary;
import java.util.Map;

import org.fdesigner.container.internal.framework.FilterImpl;
import org.fdesigner.container.service.resolver.BundleDescription;
import org.fdesigner.container.service.resolver.NativeCodeDescription;
import org.fdesigner.container.service.resolver.State;
import org.fdesigner.container.service.resolver.VersionRange;
import org.fdesigner.framework.framework.Constants;
import org.fdesigner.framework.framework.Filter;
import org.fdesigner.framework.framework.InvalidSyntaxException;
import org.fdesigner.framework.framework.Version;

public class NativeCodeDescriptionImpl extends BaseDescriptionImpl implements NativeCodeDescription {
	private static final VersionRange[] EMPTY_VERSIONRANGES = new VersionRange[0];

	private volatile Filter filter;
	private String[] languages;
	private String[] nativePaths;
	private String[] osNames;
	private VersionRange[] osVersions;
	private String[] processors;
	private BundleDescription supplier;
	private volatile boolean invalidNativePaths = false;

	public Filter getFilter() {
		return filter;
	}

	public String[] getLanguages() {
		synchronized (this.monitor) {
			if (languages == null)
				return BundleDescriptionImpl.EMPTY_STRING;
			return languages;
		}
	}

	public String[] getNativePaths() {
		synchronized (this.monitor) {
			if (nativePaths == null)
				return BundleDescriptionImpl.EMPTY_STRING;
			return nativePaths;
		}
	}

	public String[] getOSNames() {
		synchronized (this.monitor) {
			if (osNames == null)
				return BundleDescriptionImpl.EMPTY_STRING;
			return osNames;
		}
	}

	public VersionRange[] getOSVersions() {
		synchronized (this.monitor) {
			if (osVersions == null)
				return EMPTY_VERSIONRANGES;
			return osVersions;
		}
	}

	public String[] getProcessors() {
		synchronized (this.monitor) {
			if (processors == null)
				return BundleDescriptionImpl.EMPTY_STRING;
			return processors;
		}
	}

	public BundleDescription getSupplier() {
		return supplier;
	}

	public int compareTo(NativeCodeDescription otherDesc) {
		State containingState = getSupplier().getContainingState();
		if (containingState == null)
			return 0;
		Dictionary<Object, Object>[] platformProps = containingState.getPlatformProperties();
		Version osversion;
		try {
			osversion = Version.parseVersion((String) platformProps[0].get(Constants.FRAMEWORK_OS_VERSION));
		} catch (Exception e) {
			osversion = Version.emptyVersion;
		}
		VersionRange[] thisRanges = getOSVersions();
		VersionRange[] otherRanges = otherDesc.getOSVersions();
		Version thisHighest = getHighestVersionMatch(osversion, thisRanges);
		Version otherHighest = getHighestVersionMatch(osversion, otherRanges);
		if (thisHighest.compareTo(otherHighest) < 0)
			return -1;
		return (getLanguages().length == 0 ? 0 : 1) - (otherDesc.getLanguages().length == 0 ? 0 : 1);
	}

	public boolean hasInvalidNativePaths() {
		return invalidNativePaths;
	}

	private Version getHighestVersionMatch(Version version, VersionRange[] ranges) {
		Version highest = Version.emptyVersion;
		for (VersionRange range : ranges) {
			if (range.isIncluded(version) && highest.compareTo(range.getMinimum()) < 0) {
				highest = range.getMinimum();
			}
		}
		return highest;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		String[] paths = getNativePaths();
		for (int i = 0; i < paths.length; i++) {
			if (i > 0) {
				sb.append("; "); //$NON-NLS-1$
			}
			sb.append(paths[i]);
		}

		String[] procs = getProcessors();
		for (String proc : procs) {
			sb.append("; "); //$NON-NLS-1$
			sb.append(Constants.BUNDLE_NATIVECODE_PROCESSOR);
			sb.append('=');
			sb.append(proc);
		}

		String[] oses = getOSNames();
		for (String os : oses) {
			sb.append("; "); //$NON-NLS-1$
			sb.append(Constants.BUNDLE_NATIVECODE_OSNAME);
			sb.append('=');
			sb.append(os);
		}

		VersionRange[] osRanges = getOSVersions();
		for (VersionRange osRange : osRanges) {
			sb.append("; "); //$NON-NLS-1$
			sb.append(Constants.BUNDLE_NATIVECODE_OSVERSION);
			sb.append("=\""); //$NON-NLS-1$
			sb.append(osRange.toString());
			sb.append('"');
		}

		String[] langs = getLanguages();
		for (String lang : langs) {
			sb.append("; "); //$NON-NLS-1$
			sb.append(Constants.BUNDLE_NATIVECODE_LANGUAGE);
			sb.append('=');
			sb.append(lang);
		}

		Filter f = getFilter();
		if (f != null) {
			sb.append("; "); //$NON-NLS-1$
			sb.append(Constants.SELECTION_FILTER_ATTRIBUTE);
			sb.append("=\""); //$NON-NLS-1$
			sb.append(f.toString());
			sb.append('"');
		}
		return (sb.toString());
	}

	void setInvalidNativePaths(boolean invalidNativePaths) {
		this.invalidNativePaths = invalidNativePaths;
	}

	void setOSNames(String[] osNames) {
		synchronized (this.monitor) {
			this.osNames = osNames;
		}
	}

	void setOSVersions(VersionRange[] osVersions) {
		synchronized (this.monitor) {
			this.osVersions = osVersions;
		}
	}

	void setFilter(String filter) throws InvalidSyntaxException {
		this.filter = filter == null ? null : FilterImpl.newInstance(filter);
	}

	void setLanguages(String[] languages) {
		synchronized (this.monitor) {
			this.languages = languages;
		}
	}

	void setNativePaths(String[] nativePaths) {
		synchronized (this.monitor) {
			this.nativePaths = nativePaths;
		}
	}

	void setProcessors(String[] processors) {
		synchronized (this.monitor) {
			this.processors = processors;
		}
	}

	void setSupplier(BundleDescription supplier) {
		this.supplier = supplier;
	}

	public Map<String, String> getDeclaredDirectives() {
		return Collections.<String, String> emptyMap();
	}

	public Map<String, Object> getDeclaredAttributes() {
		return Collections.<String, Object> emptyMap();
	}
}
