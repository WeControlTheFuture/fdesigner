/*
 * Copyright (c) OSGi Alliance (2002, 2017). All Rights Reserved.
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

package org.fdesigner.utils.xml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.SAXParserFactory;

import org.fdesigner.framework.framework.Bundle;
import org.fdesigner.framework.framework.BundleActivator;
import org.fdesigner.framework.framework.BundleContext;
import org.fdesigner.framework.framework.Constants;
import org.fdesigner.framework.framework.ServiceFactory;
import org.fdesigner.framework.framework.ServiceReference;
import org.fdesigner.framework.framework.ServiceRegistration;

/**
 * A BundleActivator class that allows any JAXP compliant XML Parser to register
 * itself as an OSGi parser service.
 * 
 * Multiple JAXP compliant parsers can concurrently register by using this
 * BundleActivator class. Bundles who wish to use an XML parser can then use the
 * framework's service registry to locate available XML Parsers with the desired
 * characteristics such as validating and namespace-aware.
 * 
 * <p>
 * The services that this bundle activator enables a bundle to provide are:
 * <ul>
 * <li>{@code javax.xml.parsers.SAXParserFactory}({@link #SAXFACTORYNAME})</li>
 * <li>{@code javax.xml.parsers.DocumentBuilderFactory}( {@link #DOMFACTORYNAME}
 * )</li>
 * </ul>
 * 
 * <p>
 * The algorithm to find the implementations of the abstract parsers is derived
 * from the JAR file specifications, specifically the Services API.
 * <p>
 * An XMLParserActivator assumes that it can find the class file names of the
 * factory classes in the following files:
 * <ul>
 * <li>{@code /META-INF/services/javax.xml.parsers.SAXParserFactory} is a file
 * contained in a jar available to the runtime which contains the implementation
 * class name(s) of the SAXParserFactory.</li>
 * <li>{@code /META-INF/services/javax.xml.parsers.DocumentBuilderFactory} is a
 * file contained in a jar available to the runtime which contains the
 * implementation class name(s) of the {@code DocumentBuilderFactory}</li>
 * </ul>
 * <p>
 * If either of the files does not exist, {@code XMLParserActivator} assumes
 * that the parser does not support that parser type.
 * 
 * <p>
 * {@code XMLParserActivator} attempts to instantiate both the
 * {@code SAXParserFactory} and the {@code DocumentBuilderFactory}. It registers
 * each factory with the framework along with service properties:
 * <ul>
 * <li>{@link #PARSER_VALIDATING}- indicates if this factory supports validating
 * parsers. It's value is a {@code Boolean}.</li>
 * <li>{@link #PARSER_NAMESPACEAWARE}- indicates if this factory supports
 * namespace aware parsers It's value is a {@code Boolean}.</li>
 * </ul>
 * <p>
 * Individual parser implementations may have additional features, properties,
 * or attributes which could be used to select a parser with a filter. These can
 * be added by extending this class and overriding the {@code setSAXProperties}
 * and {@code setDOMProperties} methods.
 * 
 * @ThreadSafe
 * @author $Id$
 */
public class XMLParserActivator implements BundleActivator, ServiceFactory<Object> {
	/** Context of this bundle */
	private volatile BundleContext	bundleContext;
	/**
	 * Filename containing the SAX Parser Factory Class name. Also used as the
	 * basis for the {@code SERVICE_PID} registration property.
	 */
	public static final String		SAXFACTORYNAME			= "javax.xml.parsers.SAXParserFactory";
	/**
	 * Filename containing the DOM Parser Factory Class name. Also used as the
	 * basis for the {@code SERVICE_PID} registration property.
	 */
	public static final String		DOMFACTORYNAME			= "javax.xml.parsers.DocumentBuilderFactory";
	/** Path to the factory class name files */
	private static final String		PARSERCLASSFILEPATH		= "/META-INF/services/";
	/** Fully qualified path name of SAX Parser Factory Class Name file */
	public static final String		SAXCLASSFILE			= PARSERCLASSFILEPATH + SAXFACTORYNAME;
	/** Fully qualified path name of DOM Parser Factory Class Name file */
	public static final String		DOMCLASSFILE			= PARSERCLASSFILEPATH + DOMFACTORYNAME;
	/** SAX Factory Service Description */
	private static final String		SAXFACTORYDESCRIPTION	= "A JAXP Compliant SAX Parser";
	/** DOM Factory Service Description */
	private static final String		DOMFACTORYDESCRIPTION	= "A JAXP Compliant DOM Parser";
	/**
	 * Service property specifying if factory is configured to support
	 * validating parsers. The value is of type {@code Boolean}.
	 */
	public static final String		PARSER_VALIDATING		= "parser.validating";
	/**
	 * Service property specifying if factory is configured to support namespace
	 * aware parsers. The value is of type {@code Boolean}.
	 */
	public static final String		PARSER_NAMESPACEAWARE	= "parser.namespaceAware";
	/**
	 * Key for parser factory name property - this must be saved in the parsers
	 * properties hashtable so that the parser factory can be instantiated from
	 * a ServiceReference
	 */
	private static final String		FACTORYNAMEKEY			= "parser.factoryname";

	/**
	 * Called when this bundle is started so the Framework can perform the
	 * bundle-specific activities necessary to start this bundle. This method
	 * can be used to register services or to allocate any resources that this
	 * bundle needs.
	 * 
	 * <p>
	 * This method must complete and return to its caller in a timely manner.
	 * 
	 * <p>
	 * This method attempts to register a SAX and DOM parser with the
	 * Framework's service registry.
	 * 
	 * @param context The execution context of the bundle being started.
	 * @throws java.lang.Exception If this method throws an exception, this
	 *         bundle is marked as stopped and the Framework will remove this
	 *         bundle's listeners, unregister all services registered by this
	 *         bundle, and release all services used by this bundle.
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		this.bundleContext = context;
		Bundle parserBundle = context.getBundle();
		// check for sax parsers
		registerSAXParsers(getParserFactoryClassNames(parserBundle.getResource(SAXCLASSFILE)));
		// check for dom parsers
		registerDOMParsers(getParserFactoryClassNames(parserBundle.getResource(DOMCLASSFILE)));
	}

	/**
	 * This method has nothing to do as all active service registrations will
	 * automatically get unregistered when the bundle stops.
	 * 
	 * @param context The execution context of the bundle being stopped.
	 * @throws java.lang.Exception If this method throws an exception, the
	 *         bundle is still marked as stopped, and the Framework will remove
	 *         the bundle's listeners, unregister all services registered by the
	 *         bundle, and release all services used by the bundle.
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		// framework will automatically unregister the parser services
	}

	/**
	 * Given the URL for a file, reads and returns the parser class names. There
	 * may be multiple classes specified in this file, one per line. There may
	 * also be comment lines in the file, which begin with "#".
	 * 
	 * @param parserUrl The URL of the service file containing the parser class
	 *        names
	 * @return A List of strings containing the parser class names.
	 * @throws IOException if there is a problem reading the URL input stream
	 */
	private List<String> getParserFactoryClassNames(URL parserUrl) throws IOException {
		if (parserUrl == null) {
			return Collections.emptyList();
		}
		List<String> v = new ArrayList<String>(1);
		String parserFactoryClassName = null;
		InputStream is = parserUrl.openStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		while (true) {
			parserFactoryClassName = br.readLine();
			if (parserFactoryClassName == null) {
				break; // end of file reached
			}
			String pfcName = parserFactoryClassName.trim();
			if (pfcName.length() == 0) {
				continue; // blank line
			}
			int commentIdx = pfcName.indexOf("#");
			if (commentIdx == 0) { // comment line
				continue;
			} else
				if (commentIdx < 0) { // no comment on this line
					v.add(pfcName);
				} else {
					v.add(pfcName.substring(0, commentIdx).trim());
				}
		}
		return v;
	}

	/**
	 * Register SAX Parser Factory Services with the framework.
	 * 
	 * @param parserFactoryClassNames - a {@code List} of {@code String} objects
	 *        containing the names of the parser Factory Classes
	 * @throws FactoryConfigurationError if thrown from {@code getFactory}
	 */
	private void registerSAXParsers(List<String> parserFactoryClassNames) throws FactoryConfigurationError {
		Iterator<String> e = parserFactoryClassNames.iterator();
		int index = 0;
		while (e.hasNext()) {
			String parserFactoryClassName = e.next();
			// create a sax parser factory just to get it's default
			// properties. It will never be used since
			// this class will operate as a service factory and give each
			// service requestor it's own SaxParserFactory
			SAXParserFactory factory = (SAXParserFactory) getFactory(parserFactoryClassName);
			Hashtable<String, Object> properties = new Hashtable<String, Object>(7);
			// figure out the default properties of the parser
			setDefaultSAXProperties(factory, properties, index);
			// store the parser factory class name in the properties so that
			// it can be retrieved when getService is called
			// to return a parser factory
			properties.put(FACTORYNAMEKEY, parserFactoryClassName);
			// register the factory as a service
			bundleContext.registerService(SAXFACTORYNAME, this, properties);
			index++;
		}
	}

	/**
	 * <p>
	 * Set the SAX Parser Service Properties. By default, the following
	 * properties are set:
	 * <ul>
	 * <li>{@code SERVICE_DESCRIPTION}</li>
	 * <li>{@code SERVICE_PID}</li>
	 * <li>{@code PARSER_VALIDATING}- instantiates a parser and queries it to
	 * find out whether it is validating or not</li>
	 * <li>{@code PARSER_NAMESPACEAWARE}- instantiates a parser and queries it
	 * to find out whether it is namespace aware or not</li>
	 * <ul>
	 * 
	 * @param factory The {@code SAXParserFactory} object
	 * @param props {@code Hashtable} of service properties.
	 */
	private void setDefaultSAXProperties(SAXParserFactory factory, Hashtable<String, Object> props, int index) {
		props.put(Constants.SERVICE_DESCRIPTION, SAXFACTORYDESCRIPTION);
		props.put(Constants.SERVICE_PID, SAXFACTORYNAME + "." + bundleContext.getBundle().getBundleId() + "." + index);
		setSAXProperties(factory, props);
	}

	/**
	 * <p>
	 * Set the customizable SAX Parser Service Properties.
	 * 
	 * <p>
	 * This method attempts to instantiate a validating parser and a namespace
	 * aware parser to determine if the parser can support those features. The
	 * appropriate properties are then set in the specified properties object.
	 * 
	 * <p>
	 * This method can be overridden to add additional SAX2 features and
	 * properties. If you want to be able to filter searches of the OSGi service
	 * registry, this method must put a key, value pair into the properties
	 * object for each feature or property. For example,
	 * 
	 * properties.put("http://www.acme.com/features/foo", Boolean.TRUE);
	 * 
	 * @param factory - the SAXParserFactory object
	 * @param properties - the properties object for the service
	 */
	public void setSAXProperties(SAXParserFactory factory, Hashtable<String, Object> properties) {
		// check if this parser can be configured to validate
		boolean validating = true;
		factory.setValidating(true);
		factory.setNamespaceAware(false);
		try {
			factory.newSAXParser();
		} catch (Exception pce_val) {
			validating = false;
		}
		// check if this parser can be configured to be namespaceaware
		boolean namespaceaware = true;
		factory.setValidating(false);
		factory.setNamespaceAware(true);
		try {
			factory.newSAXParser();
		} catch (Exception pce_nsa) {
			namespaceaware = false;
		}
		// set the factory values
		factory.setValidating(validating);
		factory.setNamespaceAware(namespaceaware);
		// set the OSGi service properties
		properties.put(PARSER_NAMESPACEAWARE, Boolean.valueOf(namespaceaware));
		properties.put(PARSER_VALIDATING, Boolean.valueOf(validating));
	}

	/**
	 * Register DOM Parser Factory Services with the framework.
	 * 
	 * @param parserFactoryClassNames - a {@code List} of {@code String} objects
	 *        containing the names of the parser Factory Classes
	 * @throws FactoryConfigurationError if thrown from {@code getFactory}
	 */
	private void registerDOMParsers(List<String> parserFactoryClassNames) throws FactoryConfigurationError {
		Iterator<String> e = parserFactoryClassNames.iterator();
		int index = 0;
		while (e.hasNext()) {
			String parserFactoryClassName = e.next();
			// create a dom parser factory just to get it's default
			// properties. It will never be used since
			// this class will operate as a service factory and give each
			// service requestor it's own DocumentBuilderFactory
			DocumentBuilderFactory factory = (DocumentBuilderFactory) getFactory(parserFactoryClassName);
			Hashtable<String, Object> properties = new Hashtable<String, Object>(7);
			// figure out the default properties of the parser
			setDefaultDOMProperties(factory, properties, index);
			// store the parser factory class name in the properties so that
			// it can be retrieved when getService is called
			// to return a parser factory
			properties.put(FACTORYNAMEKEY, parserFactoryClassName);
			// register the factory as a service
			bundleContext.registerService(DOMFACTORYNAME, this, properties);
			index++;
		}
	}

	/**
	 * Set the DOM parser service properties.
	 * 
	 * By default, the following properties are set:
	 * <ul>
	 * <li>{@code SERVICE_DESCRIPTION}</li>
	 * <li>{@code SERVICE_PID}</li>
	 * <li>{@code PARSER_VALIDATING}</li>
	 * <li>{@code PARSER_NAMESPACEAWARE}</li>
	 * <ul>
	 * 
	 * @param factory The {@code DocumentBuilderFactory} object
	 * @param props {@code Hashtable} of service properties.
	 */
	private void setDefaultDOMProperties(DocumentBuilderFactory factory, Hashtable<String, Object> props, int index) {
		props.put(Constants.SERVICE_DESCRIPTION, DOMFACTORYDESCRIPTION);
		props.put(Constants.SERVICE_PID, DOMFACTORYNAME + "." + bundleContext.getBundle().getBundleId() + "." + index);
		setDOMProperties(factory, props);
	}

	/**
	 * <p>
	 * Set the customizable DOM Parser Service Properties.
	 * 
	 * <p>
	 * This method attempts to instantiate a validating parser and a namespace
	 * aware parser to determine if the parser can support those features. The
	 * appropriate properties are then set in the specified props object.
	 * 
	 * <p>
	 * This method can be overridden to add additional DOM2 features and
	 * properties. If you want to be able to filter searches of the OSGi service
	 * registry, this method must put a key, value pair into the properties
	 * object for each feature or property. For example,
	 * 
	 * properties.put("http://www.acme.com/features/foo", Boolean.TRUE);
	 * 
	 * @param factory - the DocumentBuilderFactory object
	 * @param props - Hashtable of service properties.
	 */
	public void setDOMProperties(DocumentBuilderFactory factory, Hashtable<String, Object> props) {
		// check if this parser can be configured to validate
		boolean validating = true;
		factory.setValidating(true);
		factory.setNamespaceAware(false);
		try {
			factory.newDocumentBuilder();
		} catch (Exception pce_val) {
			validating = false;
		}
		// check if this parser can be configured to be namespaceaware
		boolean namespaceaware = true;
		factory.setValidating(false);
		factory.setNamespaceAware(true);
		try {
			factory.newDocumentBuilder();
		} catch (Exception pce_nsa) {
			namespaceaware = false;
		}
		// set the factory values
		factory.setValidating(validating);
		factory.setNamespaceAware(namespaceaware);
		// set the OSGi service properties
		props.put(PARSER_VALIDATING, Boolean.valueOf(validating));
		props.put(PARSER_NAMESPACEAWARE, Boolean.valueOf(namespaceaware));
	}

	/**
	 * Given a parser factory class name, instantiate that class.
	 * 
	 * @param parserFactoryClassName A {@code String} object containing the name
	 *        of the parser factory class
	 * @return a parserFactoryClass Object
	 * @pre parserFactoryClassName!=null
	 */
	private Object getFactory(String parserFactoryClassName) throws FactoryConfigurationError {
		try {
			return bundleContext.getBundle()
					.loadClass(parserFactoryClassName)
					.getConstructor()
					.newInstance();
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new FactoryConfigurationError(e);
		}
	}

	/**
	 * Creates a new XML Parser Factory object.
	 * 
	 * <p>
	 * A unique XML Parser Factory object is returned for each call to this
	 * method.
	 * 
	 * <p>
	 * The returned XML Parser Factory object will be configured for validating
	 * and namespace aware support as specified in the service properties of the
	 * specified ServiceRegistration object.
	 * 
	 * This method can be overridden to configure additional features in the
	 * returned XML Parser Factory object.
	 * 
	 * @param bundle The bundle using the service.
	 * @param registration The {@code ServiceRegistration} object for the
	 *        service.
	 * @return A new, configured XML Parser Factory object or null if a
	 *         configuration error was encountered
	 */
	@Override
	public Object getService(Bundle bundle, ServiceRegistration<Object> registration) {
		ServiceReference<Object> sref = registration.getReference();
		String parserFactoryClassName = (String) sref.getProperty(FACTORYNAMEKEY);
		// need to set factory properties
		Object factory = getFactory(parserFactoryClassName);
		if (factory instanceof SAXParserFactory) {
			((SAXParserFactory) factory).setValidating(((Boolean) sref.getProperty(PARSER_VALIDATING)).booleanValue());
			((SAXParserFactory) factory).setNamespaceAware(((Boolean) sref.getProperty(PARSER_NAMESPACEAWARE)).booleanValue());
		} else {
			if (factory instanceof DocumentBuilderFactory) {
				((DocumentBuilderFactory) factory).setValidating(((Boolean) sref.getProperty(PARSER_VALIDATING)).booleanValue());
				((DocumentBuilderFactory) factory).setNamespaceAware(((Boolean) sref.getProperty(PARSER_NAMESPACEAWARE)).booleanValue());
			}
		}
		return factory;
	}

	/**
	 * Releases a XML Parser Factory object.
	 * 
	 * @param bundle The bundle releasing the service.
	 * @param registration The {@code ServiceRegistration} object for the
	 *        service.
	 * @param service The XML Parser Factory object returned by a previous call
	 *        to the {@code getService} method.
	 */
	@Override
	public void ungetService(Bundle bundle, ServiceRegistration<Object> registration, Object service) {
		// nothing to do
	}
}
