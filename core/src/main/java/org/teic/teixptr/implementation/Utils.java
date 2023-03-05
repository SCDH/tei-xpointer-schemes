package org.teic.teixptr.implementation;

import java.util.Iterator;

import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;
import net.sf.saxon.s9api.SaxonApiException;

/**
 * Some utility methods.
 */
public class Utils {

    /**
     * Get the first node from a {@link XdmValue} sequence.
     *
     * @param xdmValue the sequence to get the node from
     * @returns {@link XdmNode} or <code>null</code>
     * @throws SaxonApiException may occur when getting the document order
     */
    public static XdmNode getFirstNode(XdmValue xdmValue) throws SaxonApiException {
	Iterator<XdmItem> iter = xdmValue.documentOrder().iterator();
	while (iter.hasNext()) {
	    XdmItem item = iter.next();
	    if (item instanceof XdmNode) {
		return (XdmNode) item;
	    }
	}
	return null;
    }

    /**
     * Get the last node from a {@link XdmValue} sequence.
     *
     * @param xdmValue the sequence to get the node from
     * @returns {@link XdmNode} or <code>null</code>
     * @throws SaxonApiException may occur when getting the document order
     */
    public static XdmNode getLastNode(XdmValue xdmValue) throws SaxonApiException {
	Iterator<XdmItem> iter = xdmValue.documentOrder().iterator();
	XdmNode node = null;
	while (iter.hasNext()) {
	    XdmItem item = iter.next();
	    if (item instanceof XdmNode) {
		node = (XdmNode) item;
	    }
	}
	return node;
    }

}
