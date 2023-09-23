package org.teic.teixptr.implementation;

import net.sf.saxon.tree.wrapper.VirtualCopy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A {@link TextNodeFragment} can wrap a {@link Point} into a virtual
 * copy of the text node related to it. The virtual copy differs from
 * the original node, in that it does only serialize to a portion of
 * the text node and in that it has no parent node. It is not
 * identical to the original node.
 *
 * @see {@link VirtualCopy}
 */
public class TextNodeFragment extends VirtualCopy {

    private final Point point;

    private static final Logger LOG = LoggerFactory.getLogger(TextNodeFragment.class);

    /**
     * Make a virtual copy from a {@link Point} and its related {@link
     * net.sf.saxon.s9api.XdmNode}.
     *
     * @param point  a {@link Point} to be wrapped into a virtual copy of its related text node
     */
    public TextNodeFragment(Point point) {
	super(point.getNode().getUnderlyingNode(), point.getNode().getUnderlyingNode().getRoot());
	this.point = point;
	LOG.debug("wrapped Point into a new TextNodeFragment object");
    }

    // FIXME: implement escaping to &lt; etc. as defined in the Saxon API

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStringValue() {
	LOG.debug("preparing string value for Point, {}, from {} to {}",
		  point.getFragmentPosition(), point.getOffset(), point.getLength());
	if (point.getFragmentPosition() == Point.INTER_CHARACTER) {
	    LOG.debug("inter character point: empty string");
	    return "";
	} else if (point.getFragmentPosition() == Point.START_TO_OFFSET) {
	    return getOriginalNode().getStringValue().substring(0, point.getOffset());
	} else if (point.getFragmentPosition() == Point.OFFSET_TO_END) {
	    return getOriginalNode().getStringValue().substring(point.getOffset());
	} else if (point.getFragmentPosition() == Point.LENGTH_FROM_OFFSET) {
	    return getOriginalNode().getStringValue().substring(point.getOffset(), point.getOffset() + point.getLength());
	} else {
	    LOG.debug("full original text node");
	    return getOriginalNode().getStringValue();
	}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStringValueCS() {
	return getStringValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
	return getStringValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toShortString() {
	return getStringValue();
    }

    // @Override
    // public static net.sf.saxon.om.GroundedValue toGroundedValue(net.sf.saxon.om.Item item) {
    // 	LOG.info("static");
    // 	return null;
    // }

}
