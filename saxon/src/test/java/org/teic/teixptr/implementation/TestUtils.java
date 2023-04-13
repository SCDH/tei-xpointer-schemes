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

    public static XdmItem getFirstItem(XdmValue xdmValue) {
	Iterator<XdmItem> iter = xdmValue.iterator();
	if (iter.hasNext()) {
	    return iter.next();
	}
	return null;
    }

    public static XdmItem getLastItem(XdmValue xdmValue) {
	XdmItem last = null;
	Iterator<XdmItem> iter = xdmValue.iterator();
	while (iter.hasNext()) {
	    last = iter.next();
	}
	return last;
    }

}
