package org.teic.teixptr.implementation;

import java.util.Iterator;
import java.util.ArrayList;

import net.sf.saxon.s9api.Axis;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmEmptySequence;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmNodeKind;
import net.sf.saxon.s9api.XdmValue;
import net.sf.saxon.s9api.XdmExternalObject;
import net.sf.saxon.om.NodeInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A {@link Point} is a wrapper around an {@link XdmNode} and adds a
 * position relative to the node. The position is a code specific to
 * the XPointer scheme the point is described with. It is one of
 * {@link LEFT}, {@link RIGHT}, or {@link STRING_INDEX}.<P>
 *
 * A {@link Point} object can be created through one of the static
 * methods of this class.
 *
 * See {@link #makePointLeft(XdmValue) makePointLeft(XdmValue)} and
 * {@link #makePointRight(XdmValue) makePointRight(XdmValue)} for a
 * convenient methods to wrap {@link XdmValue} using a {@link
 * XdmExternalObject}.<P>
 *
 * Implementation notes:<P> Cf. {@link XdmItem}: "Users must not
 * attempt to create additional subclasses." We wrap the Point into
 * {@link XdmExternalObject}. Not subclassing from {@link XdmNode} but
 * storing it to a field also enables us to keep the node's identity.
 *
 */
public class Point {

    private static final Logger LOG = LoggerFactory.getLogger(Point.class);

    /**
     * Point located next left to the node.
     */
    public static final int LEFT = 1;

    /**
     * Point located next right to the node.
     */
    public static final int RIGHT = 2;

    /**
     * Point located at an offset in the text nodes following the node.
     */
    public static final int STRING_INDEX = 3;

    /**
     * Point located at an offset in the text nodes following the node.
     */
    public static final int STRING_RANGE = 4;

    /**
     * By convention, the offset of a left() pointer is set to this
     * constant value.
     */
    public static final int LEFT_OFFSET = -1;

    public static final int INTER_CHARACTER = 0;

    public static final int START_TO_OFFSET = 1;

    public static final int OFFSET_TO_END = 2;

    public static final int LENGTH_FROM_OFFSET = 3;

    private final int fragmentPosition;

    private final XdmNode node;

    private final int position;

    private final int offset;

    private final int length;

    /**
     * Create a new Point that stores an {@link XdmNode} as reference
     * node.<P>
     *
     * Use one of the static methods of this class to create a Point.
     *
     * @param node     the {@link XdmNode} reference node
     * @param position a code specific to the XPointer scheme
     * @param offset   an integer that describes the point relative in the text, if node is a text node
     * @param fragmentPosition  where the text fragment is located
     *
     */
    protected Point(XdmNode node, int position, int offset, int fragmentPosition) {
	this.node = node;
	this.position = position;
	this.offset = offset;
	this.fragmentPosition = fragmentPosition;
	this.length = -1;
    }

    /**
     * Create a new Point that stores an {@link XdmNode} as reference
     * node.<P>
     *
     * Use one of the static methods of this class to create a Point.
     *
     * @param node     the {@link XdmNode} reference node
     * @param position a code specific to the XPointer scheme
     * @param offset   an integer that describes the point relative in the text, if node is a text node
     * @param length   length of the text fragment from offset
     * @param fragmentPosition  where the text fragment is located
     *
     */
    protected Point(XdmNode node, int position, int offset, int length, int fragmentPosition) {
	this.node = node;
	this.position = position;
	this.offset = offset;
	this.fragmentPosition = fragmentPosition;
	this.length = length;
    }

    /**
     * Get the node the point is described (located) relative to. This
     * is the identical node.
     */
    public XdmNode getNode() {
	return node;
    }

    /**
     * Get the position code of the point relative to the node.
     */
    public int getPosition() {
	return position;
    }

    /**
     * Get the fragment position code.
     */
    public int getFragmentPosition() {
	return fragmentPosition;
    }

    /**
     * Get the type of the pointer that described the point.
     */
    public String getPointerType() {
	if (position == LEFT) {
	    return "left";
	} else if (position == RIGHT) {
	    return "right";
	} else if (position == STRING_INDEX) {
	    return "string-index";
	} else {
	    return null;
	}
    }

    /**
     * Get the offset of the point relative to the node.
     */
    public int getOffset() {
	return offset;
    }

    /**
     * Get the length of the text fragment.
     */
    public int getLength() {
	return length;
    }

    public static XdmValue makeXdmPointValue(Point point) {
	ArrayList<XdmItem> wrappedItems = new ArrayList<XdmItem>();
	wrappedItems.add(new XdmExternalObject(point));
	return new XdmValue(wrappedItems);
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
		Point point = new Point((XdmNode) item, LEFT, LEFT_OFFSET, INTER_CHARACTER);
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
		point = new Point((XdmNode) item, RIGHT, 0, INTER_CHARACTER);
	    }
	}
	if (point != null) {
	    XdmExternalObject wrappedPoint = new XdmExternalObject(point);
	    wrappedItems.add(wrappedPoint);
	}
	return new XdmValue(wrappedItems);
    }

    /**
     * A static method that makes a {@link Point} for a string-index()
     * scheme. The first (left-most) node of the reference nodes
     * passed in as {@link XdmValue} is used as a reference from which
     * the offset in the text stream is searched.
     *
     * @param xdmValue  the item or node (list) to wrap into point
     * @return {@link XdmValue}
     *
     * @see makeStringIndexPoint(XdmValue, int)
     */
    public static XdmValue makeStringIndex(XdmValue xdmValue, int offset, int fragmentPosition) throws SaxonApiException {
	Point point = Point.makeStringIndexPoint(xdmValue, offset, fragmentPosition);
	if (point != null) {
	    XdmExternalObject wrappedPoint = new XdmExternalObject(point);
	    return wrappedPoint;
	} else {
	    return XdmEmptySequence.getInstance();
	}
    }

    /**
     * A static method that makes a Point from a
     * <code>string-index()</code> pointer from a given XdmValue with
     * reference node(s) and an offset.<P>
     *
     * The {@link Point} will store a reference to a text node. This
     * text node is either on the descendant axis or or on the
     * following axis of the of the left-most node of the given
     * nodes. It is the text node, in which the offset points to (or
     * immediately before). The {@link Point} will store the offset to
     * the referenced point within this text node. It is not
     * necessarily the offset given as parameter to this method.<P>
     *
     * Spec: "An offset of 0 represents the position immediately
     * before the first character in either the first text node
     * descendant of the node addressed in the first parameter or
     * the first following text node, if the addressed element
     * contains no text node descendants."<p>
     *
     * @param referenceNodes  the node(s) to get the string point at offset from
     * @param offset          the offset
     */
    public static Point makeStringIndexPoint(XdmValue referenceNodes, int offset, int fragmentPosition) throws SaxonApiException {

	// get the first (left most) node of the reference nodes
	Iterator<XdmItem> iter = referenceNodes.documentOrder().iterator();
	XdmNode referenceNode = null;
	while (iter.hasNext()) {
	    XdmItem item = iter.next();
	    if (item instanceof XdmNode) {
		referenceNode = (XdmNode) item;
		break;
	    }
	}

	// stop, if we do not have a reference node
	if (referenceNode == null) {
	    return null;
	}
	// else

	XdmNode startNode = null;
	Iterator<XdmNode> startIterator;
	// initialize integers for navigation
	int currentOffset = 0;
	int startTextOffset;
	boolean offsetReached = false;

	// navigate to the starting text node and take a part of it
	if (offset >= 0) {
	    // search the first text node on the descendant axis
	    startIterator = referenceNode.axisIterator(Axis.DESCENDANT);
	    while (startIterator.hasNext() && !offsetReached) {
		LOG.debug("searching starting text node on the descendant axis");
		XdmNode node = startIterator.next();
		if (node.getNodeKind() != XdmNodeKind.TEXT) {
		    continue;
		} else {
		    String text = node.toString();
		    int len = text.length();
		    if (offset < currentOffset + len) {
			// reached
			offsetReached = true;
			startNode = node;
		    } else {
			currentOffset = currentOffset + len;
		    }
		}
	    }
	    // search on the following axis if not found on the descendant axis
	    startIterator = referenceNode.axisIterator(Axis.FOLLOWING);
	    while (startIterator.hasNext() && !offsetReached) {
		LOG.debug("searching starting text node on the following axis");
		XdmNode node = startIterator.next();
		if (node.getNodeKind() != XdmNodeKind.TEXT) {
		    continue;
		} else {
		    String text = node.toString();
		    int len = text.length();
		    if (offset < currentOffset + len) {
			// reached
			offsetReached = true;
			startNode = node;
		    } else {
			currentOffset = currentOffset + len;
		    }
		}
	    }
	    startTextOffset = offset - currentOffset;
	} else {
	    // preceding axis
	    startIterator = referenceNode.axisIterator(Axis.PRECEDING);
	    while (startIterator.hasNext() && !offsetReached) {
		LOG.debug("searching starting text node on the preceding axis");
		XdmNode node = startIterator.next();
		if (node.getNodeKind() != XdmNodeKind.TEXT) {
		    continue;
		} else {
		    String text = node.toString();
		    int len = text.length();
		    if (offset <= currentOffset - len) {
			// reached
			offsetReached = true;
			currentOffset = currentOffset - len; // to start of node
			startNode = node;
		    } else {
			currentOffset = currentOffset - len;
		    }
		}
	    }
	    startTextOffset = currentOffset - offset;
	}

	//
	if (startNode == null) {
	    return null;
	} else {
	    return new Point(startNode, Point.STRING_INDEX, startTextOffset, fragmentPosition);
	}
    }



    /**
     * Test if the supplied {@link XdmValue} is a {@link Point}.
     *
     * @param selection  an {@link XdmValue} containing a selection
     * @return <code>boolean</code>
     */
    public static boolean isPoint(XdmValue selection) {
	Iterator<XdmItem> iter = selection.iterator();
	if (iter.hasNext()) {
	    XdmItem item = iter.next();
	    if (item instanceof XdmExternalObject) {
		return ((XdmExternalObject) item).getExternalObject() instanceof Point;
	    }
	}
	return false;
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

    /**
     * Wrap a point into a text node using {@link TextNodeFragment},
     * which makes a virtual copy of the node stored as the reference
     * node of the point. The resulting text node does not serialize
     * to the whole text of the original node, but only to a portion
     * of it, determined by the offset and the fragment position.
     *
     * @param point  a {@link Point} object to be wrapped
     * @return  an {@link XdmNode}
     */
    public static XdmNode makeTextNodeFragment(Point point) {
	if (point.getNode().getNodeKind() == XdmNodeKind.TEXT) {
	    NodeInfo nodeInfo = new TextNodeFragment(point);
	    return new XdmNode(nodeInfo);
	} else {
	    return point.getNode();
	}
    }

    /**
     * Wrap an {@link XdmItem} into an text node, if and only if it is
     * a {@link XdmExternalObject} around a {@link Point}. Otherwise,
     * the item is returned. This static method can be mapped over a
     * stream of items.
     *
     * @param node  an {@link XdmItem}
     * @return an {@link XdmItem}
     *
     * @see Point.makeTextNodeFragment(Point)
     */
    public static XdmItem wrapPointIntoTextNode(XdmItem node) {
	if (Point.isPoint(node)) {
	    return makeTextNodeFragment(Point.getPoint(node));
	} else {
	    return node;
	}
    }

}
