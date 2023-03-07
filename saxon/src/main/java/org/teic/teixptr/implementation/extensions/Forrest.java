package org.teic.teixptr.implementation.extensions;

import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.teic.teixptr.implementation.Utils;
import org.teic.teixptr.extension.xpath.AbstractForrest;

/**
 * This class defines and implements the XPath function
 *
 * <code>forrest(nodes as node()*) as node()*</code>
 *
 */
public class Forrest extends AbstractForrest {

    private static final Logger LOG = LoggerFactory.getLogger(GetSequence.class);

    /**
     * {@inheritDoc}
     */
    @Override
	public ExtensionFunctionCall makeCallExpression() {
	return new ExtensionFunctionCall() {
	    @Override
	    public Sequence call(XPathContext context, Sequence[] arguments)
		throws XPathException {

		LOG.debug("{} called", NAME);

		try {
		    // get sequence form argument
		    XdmValue sequence = XdmValue.wrap(arguments[0]);

		    // make forrest
		    XdmValue forrest = Utils.forrest(sequence);

		    // return the selected nodes
		    return forrest.getUnderlyingValue();
		} catch (SaxonApiException e) {
		    throw new XPathException(e);
		}
	    }
	};
    }

}
