/*******************************************************************************
 * Copyright (c) 2003, 2016 IBM Corporation and others.
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

package org.fdesigner.container.internal.url;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import org.fdesigner.framework.framework.BundleContext;
import org.fdesigner.framework.framework.Constants;
import org.fdesigner.framework.framework.ServiceReference;
import org.fdesigner.framework.service.url.URLConstants;
import org.fdesigner.framework.service.url.URLStreamHandlerService;
import org.fdesigner.framework.util.tracker.ServiceTracker;
import org.fdesigner.framework.util.tracker.ServiceTrackerCustomizer;

/**
 * The URLStreamHandlerProxy is a URLStreamHandler that acts as a proxy for registered 
 * URLStreamHandlerServices.  When a URLStreamHandler is requested from the URLStreamHandlerFactory 
 * and it exists in the service registry, a URLStreamHandlerProxy is created which will pass all the 
 * requests from the requestor to the real URLStreamHandlerService.  We can't return the real 
 * URLStreamHandlerService from the URLStreamHandlerFactory because the JVM caches URLStreamHandlers 
 * and therefore would not support a dynamic environment of URLStreamHandlerServices being registered 
 * and unregistered.
 */

public class URLStreamHandlerProxy extends URLStreamHandler implements ServiceTrackerCustomizer<URLStreamHandlerService, ServiceReference<URLStreamHandlerService>> {
	// TODO lots of type-based names 
	protected URLStreamHandlerService realHandlerService;

	protected URLStreamHandlerSetter urlSetter;

	protected ServiceTracker<URLStreamHandlerService, ServiceReference<URLStreamHandlerService>> urlStreamHandlerServiceTracker;

	protected BundleContext context;
	protected ServiceReference<URLStreamHandlerService> urlStreamServiceReference;

	protected String protocol;

	protected int ranking = Integer.MIN_VALUE;

	public URLStreamHandlerProxy(String protocol, ServiceReference<URLStreamHandlerService> reference, BundleContext context) {
		this.context = context;
		this.protocol = protocol;

		urlSetter = new URLStreamHandlerSetter(this);

		//set the handler and ranking
		setNewHandler(reference, getRank(reference));

		urlStreamHandlerServiceTracker = new ServiceTracker<>(context, URLStreamHandlerFactoryImpl.URLSTREAMHANDLERCLASS, this);
		URLStreamHandlerFactoryImpl.secureAction.open(urlStreamHandlerServiceTracker);
	}

	private void setNewHandler(ServiceReference<URLStreamHandlerService> reference, int rank) {
		if (urlStreamServiceReference != null)
			context.ungetService(urlStreamServiceReference);

		urlStreamServiceReference = reference;
		ranking = rank;

		if (reference == null)
			realHandlerService = new NullURLStreamHandlerService();
		else
			realHandlerService = URLStreamHandlerFactoryImpl.secureAction.getService(reference, context);
	}

	/**
	 * @see java.net.URLStreamHandler#equals(URL, URL)
	 */
	@Override
	protected boolean equals(URL url1, URL url2) {
		return realHandlerService.equals(url1, url2);
	}

	/**
	 * @see java.net.URLStreamHandler#getDefaultPort()
	 */
	@Override
	protected int getDefaultPort() {
		return realHandlerService.getDefaultPort();
	}

	/**
	 * @see java.net.URLStreamHandler#getHostAddress(URL)
	 */
	@Override
	protected InetAddress getHostAddress(URL url) {
		return realHandlerService.getHostAddress(url);
	}

	/**
	 * @see java.net.URLStreamHandler#hashCode(URL)
	 */
	@Override
	protected int hashCode(URL url) {
		return realHandlerService.hashCode(url);
	}

	/**
	 * @see java.net.URLStreamHandler#hostsEqual(URL, URL)
	 */
	@Override
	protected boolean hostsEqual(URL url1, URL url2) {
		return realHandlerService.hostsEqual(url1, url2);
	}

	/**
	 * @see java.net.URLStreamHandler#openConnection(URL)
	 */
	@Override
	protected URLConnection openConnection(URL url) throws IOException {
		return realHandlerService.openConnection(url);
	}

	/**
	 * @see java.net.URLStreamHandler#parseURL(URL, String, int, int)
	 */
	@Override
	protected void parseURL(URL url, String str, int start, int end) {
		realHandlerService.parseURL(urlSetter, url, str, start, end);
	}

	/**
	 * @see java.net.URLStreamHandler#sameFile(URL, URL)
	 */
	@Override
	protected boolean sameFile(URL url1, URL url2) {
		return realHandlerService.sameFile(url1, url2);
	}

	/**
	 * @see java.net.URLStreamHandler#toExternalForm(URL)
	 */
	@Override
	protected String toExternalForm(URL url) {
		return realHandlerService.toExternalForm(url);
	}

	/**
	 * @see java.net.URLStreamHandler#setURL(URL, String, String, int, String, String, String, String, String)
	 */
	@Override
	public void setURL(URL u, String protocol, String host, int port, String authority, String userInfo, String file, String query, String ref) {
		super.setURL(u, protocol, host, port, authority, userInfo, file, query, ref);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setURL(URL url, String protocol, String host, int port, String file, String ref) {

		//using non-deprecated URLStreamHandler.setURL method. 
		//setURL(URL u, String protocol, String host, int port, String authority, String userInfo, String file, String query, String ref) 
		super.setURL(url, protocol, host, port, null, null, file, null, ref);
	}

	/**
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#addingService(ServiceReference)
	 */
	@Override
	public ServiceReference<URLStreamHandlerService> addingService(ServiceReference<URLStreamHandlerService> reference) {
		//check to see if our protocol is being registered by another service
		Object prop = reference.getProperty(URLConstants.URL_HANDLER_PROTOCOL);
		if (prop instanceof String) {
			prop = new String[] {(String) prop};
		}
		if (!(prop instanceof String[])) {
			return null;
		}
		String[] protocols = (String[]) prop;
		for (String candidateProtocol : protocols) {
			if (candidateProtocol.equals(protocol)) {
				//If our protocol is registered by another service, check the service ranking and switch URLStreamHandlers if nessecary.
				int newServiceRanking = getRank(reference);
				if (newServiceRanking > ranking || urlStreamServiceReference == null)
					setNewHandler(reference, newServiceRanking);
				return reference;
			}
		}

		//we don't want to continue hearing events about a URLStreamHandlerService not registered under our protocol
		return null;
	}

	/**
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#modifiedService(ServiceReference, Object)
	 */
	// check to see if the ranking has changed.  If so, re-select a new URLHandler
	@Override
	public void modifiedService(ServiceReference<URLStreamHandlerService> reference, ServiceReference<URLStreamHandlerService> service) {
		int newRank = getRank(reference);
		if (reference == urlStreamServiceReference) {
			if (newRank < ranking) {
				// The URLHandler we are currently using has dropped it's ranking below a URLHandler registered 
				// for the same protocol. We need to swap out URLHandlers.
				// this should get us the highest ranked service, if available
				ServiceReference<URLStreamHandlerService> newReference = urlStreamHandlerServiceTracker.getServiceReference();
				if (newReference != urlStreamServiceReference && newReference != null) {
					setNewHandler(newReference, ((Integer) newReference.getProperty(Constants.SERVICE_RANKING)).intValue());
				}
			}
		} else if (newRank > ranking) {
			// the service changed is another URLHandler that we are not currently using
			// If it's ranking is higher, we must swap it in.
			setNewHandler(reference, newRank);
		}
	}

	/**
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#removedService(ServiceReference, Object)
	 */
	@Override
	public void removedService(ServiceReference<URLStreamHandlerService> reference, ServiceReference<URLStreamHandlerService> service) {
		// check to see if our URLStreamHandler was unregistered.
		if (reference != urlStreamServiceReference)
			return;
		// If so, look for a lower ranking URLHandler
		// this should get us the highest ranking service left, if available
		ServiceReference<URLStreamHandlerService> newReference = urlStreamHandlerServiceTracker.getServiceReference();
		// if newReference == null then we will use the NullURLStreamHandlerService here
		setNewHandler(newReference, getRank(newReference));
	}

	private int getRank(ServiceReference<?> reference) {
		if (reference == null)
			return Integer.MIN_VALUE;
		Object property = reference.getProperty(Constants.SERVICE_RANKING);
		return (property instanceof Integer) ? ((Integer) property).intValue() : 0;
	}

	@Override
	protected URLConnection openConnection(URL u, Proxy p) throws IOException {
		try {
			Method openConn = realHandlerService.getClass().getMethod("openConnection", new Class[] {URL.class, Proxy.class}); //$NON-NLS-1$
			openConn.setAccessible(true);
			return (URLConnection) openConn.invoke(realHandlerService, new Object[] {u, p});
		} catch (InvocationTargetException e) {
			if (e.getTargetException() instanceof IOException)
				throw (IOException) e.getTargetException();
			throw (RuntimeException) e.getTargetException();
		} catch (Exception e) {
			// expected on JRE < 1.5
			throw new UnsupportedOperationException(e);
		}
	}
}
