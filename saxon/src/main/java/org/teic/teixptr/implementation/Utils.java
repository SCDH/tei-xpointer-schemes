package org.teic.teixptr.implementation;

import java.util.Iterator;

import net.sf.saxon.s9api.XdmEmptySequence;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Axis;
import net.sf.saxon.s9api.XdmNodeKind;

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
	Iterator<XdmItem> iter = docOrderWithVirtualNodes(nodes).iterator();
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

    /**
     * Like {@link XdmValue.documentOrder()}, but does not fail when
     * there are items that are not nodes or do not have a tree
     * information. Remember, that {@link
     * net.sf.saxon.tree.wrapper.VirtualCopy} does not have a {@link
     * net.sf.saxon.om.TreeInfo}.
     */
    protected static XdmValue docOrderWithVirtualNodes(XdmValue nodes) throws SaxonApiException {
	// documentOrder calls NodeInfo.getTreeInfo().getDocumentNumber() This not work on a VirtualCopy
	if (nodes.stream().allMatch(item -> item.isNode() && ((XdmNode) item).getUnderlyingNode().getTreeInfo() != null)) {
	    return nodes.documentOrder();
	} else {
	    // TODO: maybe we should assert order in subsequences?
	    return nodes;
	}
    }

    /**
     * This returns all the text nodes on the descendant axis of the
     * given {@link XdmNode}.
     *
     * @param node  the not the descendant axis is to be iterated
     * @return a sequence of text nodes as {@link XdmValue}
     */
    public static XdmValue descendantTextNodes(XdmNode node) {
	XdmValue nodes = XdmEmptySequence.getInstance();
	Iterator<XdmNode> descendants = node.axisIterator(Axis.DESCENDANT);
	while (descendants.hasNext()) {
	    XdmNode descendant = descendants.next();
	    if (descendant.getNodeKind() == XdmNodeKind.TEXT) {
		nodes = nodes.append(descendant);
	    }
	}
	return nodes;
    }

    /**
     * This returns the string concatenation of all text nodes on the
     * descendant axis of the given {@link XdmNode}.
     *
     * @param node  the not the descendant axis is to be iterated
     * @return the String concatenation of text nodes on the descendant axis
     */
    public static String descendantTextNodesAsString(XdmNode node) {
	String text = "";
	Iterator<XdmNode> descendants = node.axisIterator(Axis.DESCENDANT);
	while (descendants.hasNext()) {
	    XdmNode descendant = descendants.next();
	    if (descendant.getNodeKind() == XdmNodeKind.TEXT) {
		text = text + descendant.toString();
	    }
	}
	return text;
    }

}
