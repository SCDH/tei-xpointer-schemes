package org.teic.teixptr.extension.xpath;

import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.value.SequenceType;


/**
 * This class defines the XPath function
 *
 * <code>xptr:to-oa-selector(uri as xs:string, pointer as xs:string) as node()*</code>
 *
 * The serialization format is RDF/XML.
 */
public abstract class AbstractToOASelector extends ExtensionFunctionDefinition {

    public final String NAME = "to-oa-selector";

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
     * This function may have a side effects. This allows us to offer
     * implementations, that pull in the document and evaluate the
     * XPointer and to generate Selectors from the resulting
     * sequences.
     */
    @Override
    public final boolean hasSideEffects() {
	return true;
    }

}
