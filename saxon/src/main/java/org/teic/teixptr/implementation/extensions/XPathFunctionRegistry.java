package org.teic.teixptr.implementation.extensions;

import net.sf.saxon.s9api.Processor;

/**
 * A registry of the XPath function libray extension provided in this module.
 *
 */
public class XPathFunctionRegistry {

    /**
     * The default prefix of the XPath function library.
     */
    public static final String PREFIX = "xptr";

    /**
     * The namespace of the XPath function library.
     */
    public static final String NAMESPACE = "http://www.tei-c.org/ns/xptr";

    /**
     * Registers the XPath function from this library to the given
     * {@link Processor}.
     *
     * @param processor  a Saxon {@link Processor}
     */
    static public void register(Processor processor) {
	processor.registerExtensionFunction(new org.teic.teixptr.implementation.extensions.GetSequence());
	processor.registerExtensionFunction(new org.teic.teixptr.implementation.extensions.Forrest());
    }

}
