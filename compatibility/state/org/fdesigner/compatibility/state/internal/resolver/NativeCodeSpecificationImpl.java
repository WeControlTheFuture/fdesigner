/*******************************************************************************
 * Copyright (c) 2007, 2014 IBM Corporation and others.
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

import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Map;

import org.fdesigner.container.internal.framework.AliasMapper;
import org.fdesigner.container.service.resolver.BaseDescription;
import org.fdesigner.container.service.resolver.NativeCodeDescription;
import org.fdesigner.container.service.resolver.NativeCodeSpecification;
import org.fdesigner.container.service.resolver.State;
import org.fdesigner.container.service.resolver.VersionRange;
import org.fdesigner.framework.framework.Constants;
import org.fdesigner.framework.framework.Filter;
import org.fdesigner.framework.framework.Version;

public class NativeCodeSpecificationImpl extends VersionConstraintImpl implements NativeCodeSpecification {
	private static final NativeCodeDescription[] EMPTY_NATIVECODEDESCRIPTIONS = new NativeCodeDescription[0];
	private static AliasMapper aliasMapper = new AliasMapper();
	private NativeCodeDescription[] possibleSuppliers;
	private boolean optional;

	public NativeCodeDescription[] getPossibleSuppliers() {
		synchronized (this.monitor) {
			if (possibleSuppliers == null)
				return EMPTY_NATIVECODEDESCRIPTIONS;
			return possibleSuppliers;
		}
	}

	void setPossibleSuppliers(NativeCodeDescription[] possibleSuppliers) {
		synchronized (this.monitor) {
			this.possibleSuppliers = possibleSuppliers;
		}
	}

	public boolean isOptional() {
		synchronized (this.monitor) {
			return optional;
		}
	}

	void setOptional(boolean optional) {
		synchronized (this.monitor) {
			this.optional = optional;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isSatisfiedBy(BaseDescription supplier) {
		if (!(supplier instanceof NativeCodeDescription))
			return false;
		State containingState = getBundle().getContainingState();
		if (containingState == null)
			return false;
		Dictionary<Object, Object>[] platformProps = containingState.getPlatformProperties();
		NativeCodeDescription nativeSupplier = (NativeCodeDescription) supplier;
		Filter filter = nativeSupplier.getFilter();
		boolean match = false;
		for (int i = 0; i < platformProps.length && !match; i++) {
			@SuppressWarnings("rawtypes")
			Dictionary props = platformProps[i];
			if (filter != null && !filter.matchCase(props))
				continue;
			String[] osNames = nativeSupplier.getOSNames();
			if (osNames.length == 0)
				match = true;
			else {
				Collection<?> platformOSAliases;
				Object platformOS = platformProps[i].get(Constants.FRAMEWORK_OS_NAME);
				if (platformOS instanceof Collection) {
					platformOSAliases = (Collection<?>) platformOS;
				} else if (platformOS instanceof String) {
					platformOS = aliasMapper.getCanonicalOSName((String) platformOS);
					platformOSAliases = aliasMapper.getOSNameAliases((String) platformOS);
				} else {
					platformOSAliases = platformOS == null ? Collections.emptyList() : Collections.singleton(platformOS);
				}
				osNamesLoop: for (String osName : osNames) {
					String canonicalOSName = aliasMapper.getCanonicalOSName(osName);
					for (Object osAlias : platformOSAliases) {
						if (osAlias instanceof String) {
							match = (((String) osAlias).equalsIgnoreCase(canonicalOSName));
						} else {
							match = osAlias.equals(canonicalOSName);
						}
						if (match) {
							break osNamesLoop;
						}
					}
				}
			}
			if (!match)
				continue;
			match = false;

			String[] processors = nativeSupplier.getProcessors();
			if (processors.length == 0)
				match = true;
			else {
				Collection<?> platformProcessorAliases;
				Object platformProcessor = platformProps[i].get(Constants.FRAMEWORK_PROCESSOR);
				if (platformProcessor instanceof Collection) {
					platformProcessorAliases = (Collection<?>) platformProcessor;
				} else if (platformProcessor instanceof String) {
					platformProcessor = aliasMapper.getCanonicalProcessor((String) platformProcessor);
					platformProcessorAliases = aliasMapper.getProcessorAliases((String) platformProcessor);
				} else {
					platformProcessorAliases = platformProcessor == null ? Collections.emptyList() : Collections.singleton(platformProcessor);
				}
				processorLoop: for (String processor : processors) {
					String canonicalProcessor = aliasMapper.getCanonicalProcessor(processor);
					for (Object processorAlias : platformProcessorAliases) {
						if (processorAlias instanceof String) {
							match = ((String) processorAlias).equalsIgnoreCase(canonicalProcessor);
						} else {
							match = processorAlias.equals(canonicalProcessor);
						}
						if (match) {
							break processorLoop;
						}
					}
				}
			}
			if (!match)
				continue;
			match = false;

			String[] languages = nativeSupplier.getLanguages();
			if (languages.length == 0)
				match = true;
			else {
				Object platformLanguage = platformProps[i].get(Constants.FRAMEWORK_LANGUAGE);
				if (platformLanguage != null)
					for (int j = 0; j < languages.length && !match; j++) {
						if ((platformLanguage instanceof String) ? ((String) platformLanguage).equalsIgnoreCase(languages[j]) : platformLanguage.equals(languages[j]))
							match = true;
					}
			}
			if (!match)
				continue;
			match = false;

			VersionRange[] osVersions = nativeSupplier.getOSVersions();
			if (osVersions.length == 0 || platformProps[i].get(Constants.FRAMEWORK_OS_VERSION) == null)
				match = true;
			else {
				Version osversion;
				try {
					osversion = Version.parseVersion((String) platformProps[i].get(Constants.FRAMEWORK_OS_VERSION));
				} catch (Exception e) {
					osversion = Version.emptyVersion;
				}
				for (int j = 0; j < osVersions.length && !match; j++) {
					if (osVersions[j].isIncluded(osversion))
						match = true;
				}
			}
		}
		return match;
	}

	@Override
	protected boolean hasMandatoryAttributes(String[] mandatory) {
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		NativeCodeDescription[] suppliers = getPossibleSuppliers();
		for (int i = 0; i < suppliers.length; i++) {
			if (i > 0)
				sb.append(", "); //$NON-NLS-1$
			sb.append(suppliers[i].toString());
		}

		return sb.toString();
	}

	@Override
	protected Map<String, String> getInternalDirectives() {
		return Collections.<String, String> emptyMap();
	}

	@Override
	protected Map<String, Object> getInteralAttributes() {
		return Collections.<String, Object> emptyMap();
	}

	@Override
	protected String getInternalNameSpace() {
		return null;
	}
}
