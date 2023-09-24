package org.teic.teixptr.extension.xpath;

import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.value.SequenceType;


/**
 * This class defines the XPath function
 *
 * <code>xptr:get-nodes(uri as xs:string, pointer as xs:string, wrap-points as xs:boolean) as node()*</code>
 *
 */
public abstract class AbstractGetNodes3 extends ExtensionFunctionDefinition {

    public final String NAME = "get-nodes";

    /**
     * {@inheritDoc}
     */
    @Override
    public final StructuredQName getFunctionQName() {
        return new StructuredQName(Namespace.PREFIX,
                                   Namespace.NAMESPACE,
                                   NAME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final SequenceType[] getArgumentTypes() {
        return new SequenceType[] {
            SequenceType.SINGLE_STRING,
            SequenceType.SINGLE_STRING,
	    SequenceType.SINGLE_BOOLEAN
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final SequenceType getResultType(SequenceType[] suppliedArgumentTypes) {
        return SequenceType.NODE_SEQUENCE;
    }

    /**
     * This function has side effects: It pulls in a document.
     */
    @Override
    public final boolean hasSideEffects() {
	return true;
    }

}
