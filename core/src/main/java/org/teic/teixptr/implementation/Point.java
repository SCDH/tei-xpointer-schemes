package org.teic.teixptr.implementation;

import java.util.Iterator;
import java.util.ArrayList;

import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;
import net.sf.saxon.s9api.XdmExternalObject;



/**
 * A {@link Point} is a wrapper around an {@link XdmNode} and adds a
 * position relative to the node. The position should be one of
 * <code>LEFT</code>, <code>RIGHT</code>.<P>
 *
 * See {@link #makePointLeft(XdmValue) makePointLeft(XdmValue)} and
 * {@link #makePointRight(XdmValue) makePointRight(XdmValue)} for a
 * convenient methods to wrap {@link XdmValue}.<P>
 *
 * Implementation notes:<P> Cf. {@link XdmItem}: "Users must not
 * attempt to create additional subclasses." We wrap the Point into
 * {@link XdmExternalObject}. Not subclassing from {@link XdmNode} but
 * storing it to a field also enables us to keep the node's identity.
 *
 */
public class Point {

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

    private final XdmNode node;

    private final int position;

    public Point(XdmNode node, int position) {
	this.node = node;
	this.position = position;
    }

    /**
     * Get the node the point is described (located) relative to. This
     * is the identical node.
     */
    public XdmNode getNode() {
	return node;
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
     * @return {@link XdmValue}
     *
     * @throws {@link SaxonApiException} if the items in the <code>xdmValue</code> cannot be ordered to document order.
     */
    public static XdmValue makePointLeft(XdmValue xdmValue) throws SaxonApiException {
	// get the first node from the xdmValue and wrap it into a point
	ArrayList<XdmItem> wrappedItems = new ArrayList<XdmItem>();
	Iterator<XdmItem> iter = xdmValue.documentOrder().iterator();
	while (iter.hasNext()) {
	    XdmItem item = iter.next();
	    if (item instanceof XdmNode) {
		// wrap the node into a Point object
		Point point = new Point((XdmNode) item, LEFT);
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
		point = new Point((XdmNode) item, RIGHT);
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
