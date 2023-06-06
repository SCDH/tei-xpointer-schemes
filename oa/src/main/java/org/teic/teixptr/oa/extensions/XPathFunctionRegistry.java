package org.teic.teixptr.oa.extensions;

import net.sf.saxon.s9api.Processor;

/**
 * A registry of the XPath function libray extension provided in this module.
 *
 */
public class XPathFunctionRegistry {

    /**
     * Registers the XPath function from this library to the given
     * {@link Processor}.
     *
     * @param processor  a Saxon {@link Processor}
     */
    static public void register(Processor processor) {
	processor.registerExtensionFunction(new org.teic.teixptr.oa.extensions.ToOASelectorXPath());
    }

}
