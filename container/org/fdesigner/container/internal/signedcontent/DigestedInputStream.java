/*******************************************************************************
 * Copyright (c) 2006, 2012 IBM Corporation and others.
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

package org.fdesigner.container.internal.signedcontent;

import java.io.FilterInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.fdesigner.container.signedcontent.InvalidContentException;
import org.fdesigner.container.signedcontent.SignerInfo;
import org.fdesigner.container.storage.bundlefile.BundleEntry;
import org.fdesigner.container.storage.bundlefile.BundleFile;
import org.fdesigner.supplement.util.NLS;

/**
 * This InputStream will calculate the digest of bytes as they are read. At the
 * end of the InputStream, it will calculate the digests and throw an exception
 * if the calculated digest do not match the expected digests.
 */
class DigestedInputStream extends FilterInputStream {
	private final MessageDigest digests[];
	private final byte result[][];
	private final BundleEntry entry;
	private final BundleFile bundleFile;
	private long remaining;

	/**
	 * Constructs an InputStream that uses another InputStream as a source and
	 * calculates the digest. At the end of the stream an exception will be
	 * thrown if the calculated digest doesn't match the passed digest.
	 * 
	 * @param in the stream to use as an input source.
	 * @param signerInfos the signers.
	 * @param results the expected digest.
	 * @throws IOException 
	 * @throws NoSuchAlgorithmException 
	 */
	DigestedInputStream(BundleEntry entry, BundleFile bundleFile, SignerInfo[] signerInfos, byte results[][], long size) throws IOException, NoSuchAlgorithmException {
		super(entry.getInputStream());
		this.entry = entry;
		this.bundleFile = bundleFile;
		this.remaining = size;
		this.digests = new MessageDigest[signerInfos.length];
		for (int i = 0; i < signerInfos.length; i++)
			this.digests[i] = MessageDigest.getInstance(signerInfos[i].getMessageDigestAlgorithm());
		this.result = results;
	}

	/**
	 * Not supported.
	 */
	@Override
	public synchronized void mark(int readlimit) {
		// Noop, we don't want to support this
	}

	/**
	 * Always returns false.
	 */
	@Override
	public boolean markSupported() {
		return false;
	}

	/**
	 * Read a byte from the InputStream. Digests are calculated on reads. At the
	 * end of the stream the calculated digests must match the expected digests.
	 * 
	 * @return the character read or -1 at end of stream.
	 * @throws IOException if there was an problem reading the byte or at the
	 *         end of the stream the calculated digests do not match the
	 *         expected digests.
	 * @see java.io.InputStream#read()
	 */
	@Override
	public int read() throws IOException {
		if (remaining <= 0)
			return -1;
		int c = super.read();
		if (c != -1) {
			for (MessageDigest digest : digests) {
				digest.update((byte) c);
			}
			remaining--;
		} else {
			// We hit eof so set remaining to zero
			remaining = 0;
		}
		if (remaining == 0)
			verifyDigests();
		return c;
	}

	private void verifyDigests() throws InvalidContentException {
		// Check the digest at end of file
		for (int i = 0; i < digests.length; i++) {
			byte rc[] = digests[i].digest();
			if (!MessageDigest.isEqual(result[i], rc))
				throw new InvalidContentException(NLS.bind(SignedContentMessages.File_In_Jar_Is_Tampered, entry.getName(), bundleFile.getBaseFile()), null);
		}
	}

	/**
	 * Read bytes from the InputStream. Digests are calculated on reads. At the
	 * end of the stream the calculated digests must match the expected digests.
	 * 
	 * @return the number of characters read or -1 at end of stream.
	 * @throws IOException if there was an problem reading or at the
	 *         end of the stream the calculated digests do not match the
	 *         expected digests.
	 * @see java.io.InputStream#read()
	 */
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if (remaining <= 0)
			return -1;
		int rc = super.read(b, off, len);
		if (rc != -1) {
			for (MessageDigest digest : digests) {
				digest.update(b, off, rc);
			}
			remaining -= rc;
		} else {
			// We hit eof so set remaining to zero
			remaining = 0;
		}
		if (remaining <= 0)
			verifyDigests();
		return rc;
	}

	/**
	 * Not supported.
	 * 
	 * @throws IOException always thrown if this method is called since mark/reset is not supported.
	 * @see java.io.InputStream#reset()
	 */
	@Override
	public synchronized void reset() throws IOException {
		// Throw IOException, we don't want to support this
		throw new IOException("Reset not supported"); //$NON-NLS-1$
	}

	/**
	 * This method is implemented as a read into a bitbucket.
	 */
	@Override
	public long skip(long n) throws IOException {
		byte buffer[] = new byte[4096];
		long count = 0;
		while (n - count > 0) {
			int rc = (n - count) > buffer.length ? buffer.length : (int) (n - count);
			rc = read(buffer, 0, rc);
			if (rc == -1)
				break;
			count += rc;
			n -= rc;
		}
		return count;
	}
}
