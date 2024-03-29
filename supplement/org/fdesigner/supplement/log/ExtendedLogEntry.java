/*******************************************************************************
 * Copyright (c) 2006, 2011 IBM Corporation and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0 which
 * accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.fdesigner.supplement.log;

import org.fdesigner.framework.service.log.LogEntry;

/**
 * Extends the OSGi Log Services <code>LogEntry</code> object to provide additional context information.
 * Otherwise similarly accessible by registering a <code>LogListener</code> object.
 * 
 * @ThreadSafe
 * @see LogListener
 * @since 3.7
 */
public interface ExtendedLogEntry extends LogEntry {

	/**
	 * Returns the logger name associated with this <code>LogEntry</code>
	 * object.
	 * 
	 * @return <code>String</code> containing the logger name associated with this
	 *         <code>LogEntry</code> object;<code>null</code> if no logger name is
	 *         associated with this <code>LogEntry</code> object.
	 */
	@Override
	String getLoggerName();

	/**
	 * Returns the context associated with this <code>LogEntry</code>
	 * object.
	 * 
	 * @return <code>Object</code> containing the context associated with this
	 *         <code>LogEntry</code> object;<code>null</code> if no context is
	 *         associated with this <code>LogEntry</code> object.
	 */
	Object getContext();

	/**
	 * Returns the thread id of the logging thread associated with this <code>LogEntry</code>
	 * object.
	 * 
	 * @return <code>long</code> containing the thread id associated with this
	 *         <code>LogEntry</code> object.
	 */
	long getThreadId();

	/**
	 * Returns the thread name of the logging thread associated with this <code>LogEntry</code>
	 * object.
	 * 
	 * @return <code>String</code> containing the message associated with this
	 *         <code>LogEntry</code> object.
	 */
	String getThreadName();

	/**
	 * Returns the log sequence number associated with this <code>LogEntry</code>
	 * object. 
	 * 
	 * @return <code>long</code> containing the sequence number associated with this
	 *         <code>LogEntry</code> object.
	 */
	long getSequenceNumber();
}
