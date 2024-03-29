/*******************************************************************************
 * Copyright (c) 2007, 2016 IBM Corporation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which accompanies this distribution,
 * and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors: IBM Corporation - initial API and implementation
 ******************************************************************************/
package org.fdesigner.container.internal.signedcontent;

import java.security.cert.Certificate;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.fdesigner.container.internal.framework.EquinoxBundle;
import org.fdesigner.container.internal.signedcontent.SignedStorageHook.StorageHookImpl;
import org.fdesigner.container.signedcontent.SignerInfo;
import org.fdesigner.container.storage.BundleInfo.Generation;
import org.fdesigner.framework.framework.Bundle;
import org.fdesigner.framework.framework.BundleContext;

public class TrustEngineListener {
	private final BundleContext context;
	private final SignedBundleHook signedBundleHook;

	TrustEngineListener(BundleContext context, SignedBundleHook signedBundleHook) {
		this.context = context;
		this.signedBundleHook = signedBundleHook;
	}

	public void addedTrustAnchor(Certificate anchor) {
		// find any SignedContent with SignerInfos that do not have an anchor;
		// re-evaluate trust and check authorization for these SignedContents
		Bundle[] bundles = context.getBundles();
		for (Bundle bundle : bundles) {
			SignedContentImpl signedContent = getSignedContent(bundle);
			if (signedContent != null && signedContent.isSigned()) {
				// check the SignerInfos for this content
				SignerInfo[] infos = signedContent.getSignerInfos();
				for (SignerInfo info : infos) {
					if (info.getTrustAnchor() == null) {
						// one of the signers is not trusted
						signedBundleHook.determineTrust(signedContent, SignedBundleHook.VERIFY_TRUST);
					} else {
						SignerInfo tsa = signedContent.getTSASignerInfo(info);
						if (tsa != null && tsa.getTrustAnchor() == null)
							// one of the tsa signers is not trusted
							signedBundleHook.determineTrust(signedContent, SignedBundleHook.VERIFY_TRUST);
					}
				}
			}
		}
	}

	public void removedTrustAnchor(Certificate anchor) {
		// find any signed content that has signerinfos with the supplied anchor
		// re-evaluate trust and check authorization again.
		Bundle[] bundles = context.getBundles();
		Set<Bundle> usingAnchor = new HashSet<>();
		Set<SignerInfo> untrustedSigners = new HashSet<>();
		for (Bundle bundle : bundles) {
			SignedContentImpl signedContent = getSignedContent(bundle);
			if (signedContent != null && signedContent.isSigned()) {
				// check signer infos for this content
				SignerInfo[] infos = signedContent.getSignerInfos();
				for (SignerInfo info : infos) {
					if (anchor.equals(info.getTrustAnchor())) {
						// one of the signers uses this anchor
						untrustedSigners.add(info);
						usingAnchor.add(bundle);
					}
					SignerInfo tsa = signedContent.getTSASignerInfo(info);
					if (tsa != null && anchor.equals(tsa.getTrustAnchor())) {
						// one of the tsa signers uses this anchor
						usingAnchor.add(bundle);
						untrustedSigners.add(tsa);
					}
				}
			}
		}
		// remove trust anchors from untrusted signers
		for (Iterator<SignerInfo> untrusted = untrustedSigners.iterator(); untrusted.hasNext();)
			((SignerInfoImpl) untrusted.next()).setTrustAnchor(null);
		// re-establish trust
		for (Bundle bundle : usingAnchor) {
			SignedContentImpl signedContent = getSignedContent(bundle);
			// found an signer using the anchor for this bundle re-evaluate trust
			signedBundleHook.determineTrust(signedContent, SignedBundleHook.VERIFY_TRUST);
		}
	}

	private SignedContentImpl getSignedContent(Bundle bundle) {
		Generation generation = (Generation) ((EquinoxBundle) bundle).getModule().getCurrentRevision().getRevisionInfo();
		StorageHookImpl hook = generation.getStorageHook(SignedStorageHook.class);
		return hook != null ? hook.signedContent : null;
	}
}
