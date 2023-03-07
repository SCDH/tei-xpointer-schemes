package org.teic.teixptr.extension.xpath;

import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;


/**
 * This class defines the XPath function
 *
 * <code>xptr:get-sequence(uri as xs:string, pointer as xs:string) as node()*</code>
 *
 */
public abstract class AbstractGetSequence extends ExtensionFunctionDefinition {

    public static final String NAME = "get-sequence";

    /**
     * {@inheritDoc}
     */
    @Override
    public StructuredQName getFunctionQName() {
        return new StructuredQName(Namespace.PREFIX,
                                   Namespace.NAMESPACE,
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
     * This function has side effects: It pulls in a document.
     */
    @Override
    public boolean hasSideEffects() {
	return true;
    }

}
