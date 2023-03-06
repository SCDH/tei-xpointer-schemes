package org.teic.teixptr.implementation;

import java.util.Iterator;

import net.sf.saxon.s9api.XdmValue;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmExternalObject;

class TestUtils {

    public static XdmNode getFirstNode(XdmValue xdmValue) {
	Iterator<XdmItem> iter = xdmValue.iterator();
	if (iter.hasNext()) {
	    XdmItem item = iter.next();
	    if (item instanceof XdmNode) {
		return (XdmNode) item;
	    }
	}
	return null;
    }

}
