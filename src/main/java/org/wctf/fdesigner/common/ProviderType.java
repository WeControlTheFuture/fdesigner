package org.wctf.fdesigner.common;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A type implemented by the Provider Role.
 * 
 * <p>
 * A non-binary compatible change to a provider type normally requires
 * incrementing the minor version of the type's package. This change will
 * require all providers to be updated to handle the change, but consumers of
 * that package will not require changes since they only use, and do not
 * implement, the provider type.
 * 
 * <p>
 * A type can be marked {@link ConsumerType} or {@link ProviderType} but not
 * both. A type is assumed to be {@link ConsumerType} if it is not marked either
 * {@link ConsumerType} or {@link ProviderType}.
 * 
 * <p>
 * This annotation is not retained at runtime. It is for use by tools to
 * understand the semantic version of a package. When a bundle implements a
 * provider type from an imported package, then the bundle's import range for
 * that package must require the package's exact major and minor version.
 * 
 * @see <a href="http://www.osgi.org/wiki/uploads/Links/SemanticVersioning.pdf"
 *      >Semantic Versioning</a>
 * @author $Id: 46ccfd7aa446f79451d090e0c23e257c3c5c3cf0 $
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface ProviderType {
	// marker annotation
}