package org.teic.teixptr.implementation;

import java.util.Iterator;

import net.sf.saxon.s9api.XdmEmptySequence;
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

    /**
     * Make a forrest of a sequence of nodes. I.E. drop all nodes that
     * are on the child axis of any other node in the sequence.
     *
     * @param nodes  a sequence of nodes as {@link XdmValue}
     * @returns {@link XdmValue}
     * @throws SaxonApiException may occur when getting the document order
     */
    public static XdmValue forrest(XdmValue nodes) throws SaxonApiException {
	Iterator<XdmItem> iter = nodes.documentOrder().iterator();
	// start with an empty forrest
	XdmValue forrest = XdmEmptySequence.getInstance();
	// For every node we test, if it is on the child axis of any
	// other node. If it is not a child, we append it to the
	// forrest.
	while (iter.hasNext()) {
	    XdmItem item = iter.next();
	    if (item instanceof XdmNode) {
		XdmNode node = (XdmNode) item;
		boolean isChild = false;
		Iterator<XdmItem> otherIter = nodes.iterator();
		while (otherIter.hasNext()) {
		    XdmItem otherItem = otherIter.next();
		    if (otherItem instanceof XdmNode) {
			XdmNode otherNode = (XdmNode) otherItem;
			for (XdmNode child : otherNode.children()) {
			    if (node.equals(child)) {
				isChild = true;
				break;
			    }
			}
		    }
		}
		if (!isChild) {
		    forrest = forrest.append(node);
		}
	    }
	}
	return forrest;
    }

}
