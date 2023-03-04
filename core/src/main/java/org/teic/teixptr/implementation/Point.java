package org.teic.teixptr.implementation;

import java.util.Iterator;
import java.util.ArrayList;

import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;
import net.sf.saxon.s9api.XdmExternalObject;

import net.sf.saxon.om.NodeInfo;


/**
 * A {@link Point} is a wrapper around {@link XdmNode} and adds a
 * position relative to the node. The position should be one of
 * <code>NOTHING</code>, <code>LEFT</code>, <code>RIGHT</code>.
 *
 * See {@link Point#makePoint(XdmValue, int)} for a convenient method
 * to wrap {@link XdmValue}.
 *
 * See {@link XdmItem}: "Users must not attempt to create additional
 * subclasses." So we wrap the Point into {@link XdmExternalObject}.
 *
 */
public class Point extends XdmNode {

    /**
     * No point available.
     */
    public static final int NOTHING = 0;

    /**
     * Point located next left to the node.
     */
    public static final int LEFT = 1;

    /**
     * Point located next right to the node.
     */
    public static final int RIGHT = 2;

    private final int position;

    public Point(NodeInfo nodeInfo) {
	super(nodeInfo);
	this.position = NOTHING;
    }

    public Point(NodeInfo nodeInfo, int position) {
	super(nodeInfo);
	this.position = position;
    }

    /**
     * Get the position of the point relative to the node.
     */
    public int getPosition() {
	return position;
    }

    /**
     * A static method that makes a {@link Point} left of the first
     * node in the {@link XdmValue} passed in.
     *
     * @param xdmValue  the item or node (list) to wrap into point
     * @param position  an integer describing the point's position relative to the node
     * @return {@link XdmValue}
     */
    public static XdmValue makePointLeft(XdmValue xdmValue) throws SaxonApiException {
	// get the first node from the xdmValue and wrap it into a point
	ArrayList<XdmItem> wrappedItems = new ArrayList<XdmItem>();
	Iterator<XdmItem> iter = xdmValue.documentOrder().iterator();
	while (iter.hasNext()) {
	    XdmItem item = iter.next();
	    if (item instanceof XdmNode) {
		// wrap the node into a Point object
		Point point = new Point(((XdmNode) item).getUnderlyingNode(), LEFT);
		XdmExternalObject wrappedPoint = new XdmExternalObject(point);
		wrappedItems.add(wrappedPoint);
		break;
	    }
	}
	return new XdmValue(wrappedItems);
    }

    /**
     * A static method that makes a {@link Point} right of the last
     * node in the {@link XdmValue} passed in.
     *
     * @param xdmValue  the item or node (list) to wrap into point
     * @param position  an integer describing the point's position relative to the node
     * @return {@link XdmValue}
     */
    public static XdmValue makePointRight(XdmValue xdmValue) throws SaxonApiException {
	// get the last node from the xdmValue and wrap it into a point
	ArrayList<XdmItem> wrappedItems = new ArrayList<XdmItem>();
	Point point = null;
	Iterator<XdmItem> iter = xdmValue.documentOrder().iterator();
	while (iter.hasNext()) {
	    XdmItem item = iter.next();
	    if (item instanceof XdmNode) {
		// wrap the node into a Point object
		point = new Point(((XdmNode) item).getUnderlyingNode(), RIGHT);
	    }
	}
	if (point != null) {
	    XdmExternalObject wrappedPoint = new XdmExternalObject(point);
	    wrappedItems.add(wrappedPoint);
	}
	return new XdmValue(wrappedItems);
    }

    /**
     * Unwrap the {@link Point} from a pointer selection.
     *
     * @param selection  an {@link XdmValue} containing a selection
     * @return {@link Point}
     */
    public static Point getPoint(XdmValue selection) {
	Iterator<XdmItem> iter = selection.iterator();
	if (iter.hasNext()) {
	    XdmItem item = iter.next();
	    if (item instanceof XdmExternalObject) {
		Object obj = ((XdmExternalObject) item).getExternalObject();
		return (Point) obj;
	    }
	}
	return null;
    }

}
