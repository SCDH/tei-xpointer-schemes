package org.teic.teixptr.extension.xpath;

import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.value.SequenceType;


/**
 * This class defines the XPath function
 *
 * <code>xptr:to-oa-selector(uri as xs:string, pointer as xs:string, format as xs:string) as xs:string*</code>
 *
 */
public abstract class AbstractToOASelector3 extends ExtensionFunctionDefinition {

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
            SequenceType.SINGLE_STRING,
            SequenceType.SINGLE_STRING
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final SequenceType getResultType(SequenceType[] suppliedArgumentTypes) {
        return SequenceType.SINGLE_STRING;
    }

    /**
     * This function may have a side effects.
     */
    @Override
    public final boolean hasSideEffects() {
	return true;
    }

}
