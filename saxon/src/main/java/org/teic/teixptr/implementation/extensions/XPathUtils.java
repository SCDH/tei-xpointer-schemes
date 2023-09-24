package org.teic.teixptr.implementation.extensions;

import net.sf.saxon.om.Sequence;
import net.sf.saxon.value.StringValue;
import net.sf.saxon.trans.XPathException;


/**
 * Utilities used throughout the binding library.
 */
public class XPathUtils {

    /**
     * Cast a argument of type <code>xs:string</code> to a {@link String}.
     *
     * @param arg  the argument as {@link Sequence}
     * @return a {@link String}
     */
    public static String getStringArgument(Sequence arg) throws XPathException {
        StringValue inputValue = (StringValue) arg.materialize();
	return inputValue.getStringValue();
    }

    /**
     * Cast a argument of type <code>xs:boolean</code> to a boolean value.
     *
     * @param arg  the argument as {@link Sequence}
     * @return a boolean value
     */
    public static boolean getBooleanArgument(Sequence arg) throws XPathException {
        boolean inputValue = arg.materialize().effectiveBooleanValue();
	return inputValue;
    }

}
