package org.teic.teixptr.implementation.extensions;

import java.io.File;

import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.teic.teixptr.implementation.TEIXPointer;


/**
 * This class defines and implements the XPath function
 *
 * <code>get-sequence(uri as xs:string, pointer as xs:string) as node()*</code>
 *
 */
public class GetSequence extends ExtensionFunctionDefinition {

    private static final Logger LOG = LoggerFactory.getLogger(GetSequence.class);

    public static final String NAME = "get-sequence";

    /**
     * {@inheritDoc}
     */
    @Override
    public StructuredQName getFunctionQName() {
        return new StructuredQName(XPathFunctionRegistry.PREFIX,
                                   XPathFunctionRegistry.NAMESPACE,
                                   NAME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SequenceType[] getArgumentTypes() {
        return new SequenceType[] {
            SequenceType.SINGLE_STRING,
            SequenceType.SINGLE_STRING
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SequenceType getResultType(SequenceType[] suppliedArgumentTypes) {
        return SequenceType.NODE_SEQUENCE;
    }

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
		    // we can get the processor from context
		    Processor processor = new Processor(context.getConfiguration());

		    // get the arguments
		    String uriArg = XPathUtils.getStringArgument(arguments[0]);
		    String pointerArg = XPathUtils.getStringArgument(arguments[1]);

		    // process the pointer
		    TEIXPointer pointer = TEIXPointer.parseTEIXPointer(pointerArg, uriArg, processor);

		    // return the selected nodes
		    return pointer.getSelectedNodes().getUnderlyingValue();
		} catch (SaxonApiException e) {
		    throw new XPathException(e);
		} catch (Exception e) {
		    throw new XPathException(e);
		}
	    }
	};
    }

}
