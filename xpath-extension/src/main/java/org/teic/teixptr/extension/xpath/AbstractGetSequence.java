package org.teic.teixptr.extension.xpath;

import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.value.SequenceType;


/**
 * This class defines the XPath function
 *
 * <code>xptr:get-sequence(uri as xs:string, pointer as xs:string) as node()*</code>
 *
 */
public abstract class AbstractGetSequence extends ExtensionFunctionDefinition {

    public final String NAME = "get-sequence";

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
            SequenceType.SINGLE_STRING
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
