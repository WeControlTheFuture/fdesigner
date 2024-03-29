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

import org.fdesigner.framework.service.log.LogListener;
import org.fdesigner.framework.service.log.LogReaderService;

/**
 * Extends the OSGi Log Service's LogReaderService to allow better control of log listeners.
 * @ThreadSafe
 * @see LogListener
 * @since 3.7
 */
public interface ExtendedLogReaderService extends LogReaderService {
	/**
	 * Subscribes to <code>LogEntry</code> objects.
	 * 
	 * <p>
	 * This method registers a <code>LogListener</code> object with the Log Reader
	 * Service with a <code>LogFilter</code> to allow pre-filtering of interesting log entries.
	 * The <code>LogListener.logged(LogEntry)</code> method will be
	 * called for each <code>LogEntry</code> object placed into the log that matches the filter.
	 * 
	 * @param listener A <code>LogListener</code> object to register; the
	 *        <code>LogListener</code> object is used to receive <code>LogEntry</code>
	 *        objects.
	 * @param filter A <code>LogFilter</code> object to register; the
	 *        <code>LogFilter</code> object is used to filter <code>LogEntry</code>
	 *        objects before sending them to the associated <code>LogListener</code>.
	 * @see LogListener
	 * @see LogFilter
	 * @see LogEntry
	 * @see LogReaderService#addLogListener(LogListener)
	 */
	public void addLogListener(LogListener listener, LogFilter filter);
}
