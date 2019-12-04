package org.wctf.fdesigner.common;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A type implemented by the Consumer Role.
 * 
 * <p>
 * A non-binary compatible change to a consumer type normally requires
 * incrementing the major version of the type's package. This change will
 * require all providers and all consumers to be updated to handle the change
 * since consumers implement the consumer type and all providers must understand
 * the change in the consumer type.
 * 
 * <p>
 * A type can be marked {@link ConsumerType} or {@link ProviderType} but not
 * both. A type is assumed to be {@link ConsumerType} if it is not marked either
 * {@link ConsumerType} or {@link ProviderType}.
 * 
 * <p>
 * This annotation is not retained at runtime. It is for use by tools to
 * understand the semantic version of a package. When a bundle implements a
 * consumer type from an imported package, then the bundle's import range for
 * that package must require the exact major version and a minor version greater
 * than or equal to the package's version.
 * 
 * @see <a href="http://www.osgi.org/wiki/uploads/Links/SemanticVersioning.pdf"
 *      >Semantic Versioning</a>
 * @author $Id: 319ac9d62b568a8cde1523e0059aa3e44c7e86af $
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface ConsumerType {
	// marker annotation
}

