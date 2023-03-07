package org.teic.teixptr.extension.xpath;

import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmValue;


/**
 * This class defines the XPath function
 *
 * <code>xptr:forrest(nodes as node()*) as node()*</code>
 *
 */
public abstract class AbstractForrest extends ExtensionFunctionDefinition {

    public final String NAME = "forrest";

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
            SequenceType.NODE_SEQUENCE
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SequenceType getResultType(SequenceType[] suppliedArgumentTypes) {
        return SequenceType.NODE_SEQUENCE;
    }

}
