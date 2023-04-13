package org.teic.teixptr.implementation;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Iterator;
import java.net.URI;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.InputSource;
import org.xml.sax.helpers.NamespaceSupport;
import javax.xml.transform.Source;

import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmEmptySequence;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmNodeKind;
import net.sf.saxon.s9api.XdmValue;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XPathExecutable;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.Axis;
import net.sf.saxon.value.StringValue;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.teic.teixptr.grammar.*;

/**
 * The implementation of the TEI XPointer scheme.
 *
 */
public class TEIXPointer extends TEIXPointerBaseListener {

    private static final Logger LOG = LoggerFactory.getLogger(TEIXPointer.class);

    protected final XdmNode docNode;

    protected final Processor processor;

    public static final String TEINS = "http://www.tei-c.org/ns/1.0";


    private String pointerType;

    private XdmValue selectedNodes = null;

    protected boolean assertNonEmpty = true;

    // state variables
    private boolean errorSeen = false;
    private List<Exception> errorStack = new ArrayList<Exception>();
    private List<XdmValue> selectedNodesStack = new ArrayList<XdmValue>();
    private String xpath = null;
    private NamespaceSupport namespaces = new NamespaceSupport();

    /**
     * Create a {@link TEIXPointer} to a file.
     *
     * @param file      the {@link File} the pointer points into
     * @param processor a Saxon {@link Processor}
     * @throws {@link SaxonApiException}
     */
    public TEIXPointer(File file, Processor processor) throws SaxonApiException {
	this.processor = processor;
	DocumentBuilder documentBuilder = this.processor.newDocumentBuilder();
	docNode = documentBuilder.build(file);
    }

    /**
     * Create a {@link TEIXPointer} to a SAX source.
     *
     * @param source    the {@link Source} the pointer points into
     * @param processor a Saxon {@link Processor}
     * @throws {@link SaxonApiException}
     */
    public TEIXPointer(Source source, Processor processor) throws SaxonApiException {
	this.processor = processor;
	DocumentBuilder documentBuilder = this.processor.newDocumentBuilder();
	docNode = documentBuilder.build(source);
    }

    /**
     * Create a {@link TEIXPointer} to a parsed document.
     *
     * @param document  the root {@link XdmNode} the pointer points into
     * @param processor a Saxon {@link Processor}
     * @throws {@link SaxonApiException}
     */
    public TEIXPointer(XdmNode node, Processor processor) throws SaxonApiException {
	this.processor = processor;
	this.docNode = node;
    }



    /**
     * A static method that parses a pointer to a {@link TEIXPointer}
     * object.
     *
     */
    public static TEIXPointer parseTEIXPointer(String pointer, String systemId, Processor processor)
	throws SaxonApiException, Exception {
	ParseTree tree = parse(pointer);
	ParseTreeWalker walker = new ParseTreeWalker();
	Source source = new StreamSource(systemId);
	TEIXPointer listener = new TEIXPointer(source, processor);
	walker.walk(listener, tree);
	listener.check();
	return listener;
    }

    /**
     * Parse a pointer to a ANTLR {@link ParseTree}.
     *
     * @param pointer  the pointer
     * @return {@link ParseTree}
     */
    public static ParseTree parse(String pointer) {
	CharStream input = CharStreams.fromString(pointer);
	TEIXPointerLexer lexer = new TEIXPointerLexer(input);
	CommonTokenStream tokens = new CommonTokenStream(lexer);
	TEIXPointerParser parser = new TEIXPointerParser(tokens);
	ParseTree tree;
	if (pointer.startsWith("#", 0)) {
	    tree = parser.fragm();
	} else {
	    tree = parser.pointer();
	}
	return tree;
    }

    /**
     * Check for errors during the parsing and processing of the
     * pointer. This method should be called before accessing the
     * getter methods.
     */
    public void check() throws Exception {
	if (errorStack.size() > 0) {
	    LOG.error("found {} errors in the pointer", errorStack.size());
	    for (Exception err : errorStack) {
		LOG.error(err.getMessage());
	    }
	    throw errorStack.get(0);
	}
    }

    /**
     * A method that encapsulates code for evaluating an XPath
     * expression on the document. This method catches errors and puts
     * them on the error stack and sets <code>errorSeen</code>.
     *
     * @param xpath  the XPath expression
     * @return {@link XdmValue}
     */
    protected XdmValue evaluateXPath(String xpath, String pointerType) {
	try {
	    XPathCompiler xpathCompiler = processor.newXPathCompiler();
	    // set the namespace binding
	    Enumeration<String> ns = this.namespaces.getPrefixes();
	    while (ns.hasMoreElements()) {
		String prefix = ns.nextElement();
		xpathCompiler.declareNamespace(prefix, namespaces.getURI(prefix));
	    }
	    if (this.namespaces.getURI("") != null) {
		xpathCompiler.declareNamespace("", this.namespaces.getURI(""));
	    }
	    LOG.debug("evaluating XPath {} with namespace binding {}", xpath, namespaces.toString());
	    // compile and evaluate
	    XPathExecutable xpathExecutable = xpathCompiler.compile(xpath);
	    XPathSelector xpathSelector = xpathExecutable.load();
	    xpathSelector.setContextItem(docNode);
	    return xpathSelector.evaluate();
	} catch (SaxonApiException e) {
	    LOG.error("error evaluating XPath from {} pointer: {}", pointerType, xpath);
	    errorSeen = true;
	    errorStack.add(e);
	    return null;
	}
    }

    /**
     * A method that encapsulates code for evaluating an XPath
     * expression on the document. This method catches errors and puts
     * them on the error stack and sets <code>errorSeen</code>.
     *
     * @param xpath  the XPath expression
     * @return {@link XdmValue}
     */
    protected XdmValue evaluateXPathWithContext(String xpath, XdmNode context, String pointerType) {
	try {
	    XPathCompiler xpathCompiler = processor.newXPathCompiler();
	    // set the namespace binding
	    Enumeration<String> ns = this.namespaces.getPrefixes();
	    while (ns.hasMoreElements()) {
		String prefix = ns.nextElement();
		xpathCompiler.declareNamespace(prefix, namespaces.getURI(prefix));
	    }
	    if (this.namespaces.getURI("") != null) {
		xpathCompiler.declareNamespace("", this.namespaces.getURI(""));
	    }
	    LOG.debug("evaluating XPath {} with namespace binding {}", xpath, namespaces.toString());
	    // compile and evaluate
	    XPathExecutable xpathExecutable = xpathCompiler.compile(xpath);
	    XPathSelector xpathSelector = xpathExecutable.load();
	    xpathSelector.setContextItem(context);
	    return xpathSelector.evaluate();
	} catch (SaxonApiException e) {
	    LOG.error("error evaluating XPath from {} pointer: {}", pointerType, xpath);
	    errorSeen = true;
	    errorStack.add(e);
	    return null;
	}
    }

    /**
     * Set the default namespace to the TEI namespace. This implements
     * a passage from the TEI guidelines: "TEI Pointer schemes assume
     * that un-prefixed element names in TEI Pointer XPaths are in the
     * TEI namespace, http://www.tei-c.org/ns/1.0."<P>
     *
     * This method is intended for
     * <code>enter&lt;TEIXPOINTER&gt;</code> methods.
     */
    protected void bindDefaultNamespaceTEI() {
	this.namespaces.pushContext();
	this.namespaces.declarePrefix("", TEINS);
    }

    /**
     * If the policy for handling non-empty pointers determines to
     * raise errors on empty pointers, then check the pointer value
     * passed in.
     *
     */
    protected void enforceNonEmptyPolicy(XdmValue xdmValue, String pointerString) {
	if (assertNonEmpty) {
	    Iterator<XdmItem> iter = xdmValue.iterator();
	    if (!iter.hasNext()) {
		errorSeen = true;
		errorStack.add(new EmptyPointerException("Empty pointer " + pointerString));
	    }
	}
    }

    /**
     * Returns the basic pointer type.
     */
    public String getPointerType() {
	return pointerType;
    }

    /**
     * Returns the sequence of nodes (items) selected by the pointer.
     */
    public XdmValue getSelectedNodes() {
	return selectedNodes;
    }

    /**
     * Internal.
     */
    @Override
    public void enterPointer(TEIXPointerParser.PointerContext ctx) {
	pointerType = ctx.getStart().getText();
	LOG.debug("found base pointer: {}", pointerType);
	if (ctx.xpathPointer() != null) {
	    assertNonEmpty = false;
	}
    }

    @Override
    public void exitPointer(TEIXPointerParser.PointerContext ctx) {
	if (errorSeen) {
	    LOG.error("errors occurred while processing the pointer");
	}
	// set selectedNodes according to the base pointer type
	else if (ctx.xpathPointer() != null) {
	    selectedNodes = selectedNodesStack.get(0);
	} else if (ctx.leftPointer() != null) {
	    selectedNodes = selectedNodesStack.get(0);
	} else if (ctx.rightPointer() != null) {
	    selectedNodes = selectedNodesStack.get(0);
	} else if (ctx.rangePointer() != null) {
	    selectedNodes = selectedNodesStack.get(0);
	} else if (ctx.stringIndexPointer() != null) {
	    selectedNodes = selectedNodesStack.get(0);
	} else if (ctx.stringRangePointer() != null) {
	    selectedNodes = selectedNodesStack.get(0);
	} else if (ctx.idref() != null) {
	    // handle IDREF
	    pointerType = "IDREF";
	    LOG.debug("found IDREF, evaluating: {}", xpath);
	    selectedNodes = evaluateXPath(xpath, "IDREF");
	    // reset the xpath state variable
	    xpath = null;
	} else {
	    // TODO
	    errorSeen = true;
	    errorStack.add(new UnsupportedOperationException("pointer type not implemented"));
	}
	LOG.debug("finished processing the pointer");
    }

    /**
     * Internal.
     */
    @Override
    public void enterIdref(TEIXPointerParser.IdrefContext ctx) {
	// we make an XPath, save it to the state and let the pointer
	// evaluate it on the exit<POINTER> event
	LOG.debug("found IDREF {}", ctx.getText());
	// xpath = "id(" + ctx.getText() + ")";
	// IDness is not implemented by Xerces! In a TEI context we
	// can check the @xml:id instead of calling id(...)
	xpath = "//*[@xml:id eq '" + ctx.getText() + "']";
    }

    /**
     * Internal.
     */
    @Override
    public void enterPathexpr(TEIXPointerParser.PathexprContext ctx) {
	// we save the XPATH to the state and let the pointer evaluate
	// it on the exit<POINTER> event
	//
	// We just store the first pathexpr in the state variable.
	// Followups may contain only parts.
	if (xpath == null) {
	    LOG.debug("found XPATH {}", ctx.getText());
	    xpath = ctx.getText();
	}
    }

    /**
     * Internal.
     */
    @Override
    public void enterXpathPointer(TEIXPointerParser.XpathPointerContext ctx) {
	bindDefaultNamespaceTEI();
    }

    /**
     * Internal.
     */
    @Override
    public void exitXpathPointer(TEIXPointerParser.XpathPointerContext ctx) {
	if (!errorSeen) {
	    // we get the XPath from the xpath state variable on the
	    // exit event
	    LOG.debug("found XPATH pointer, evaluating: {}", xpath);
	    XdmValue nodes = evaluateXPath(xpath, "xpath()");
	    enforceNonEmptyPolicy(nodes, ctx.getText());
	    selectedNodesStack.add(nodes);
	    // reset the xpath state variable
	    xpath = null;
	}
    }

    /**
     * Internal.
     */
    @Override
    public void enterLeftPointer(TEIXPointerParser.LeftPointerContext ctx) {
	bindDefaultNamespaceTEI();
    }

    /**
     * Internal.
     */
    @Override
    public void exitLeftPointer(TEIXPointerParser.LeftPointerContext ctx) {
	if (!errorSeen) {
	    // we get the XPath from the xpath state variable on the
	    // exit event
	    LOG.debug("found left() pointer, evaluating: {}", xpath);
	    // wrap node(s) into a point
	    try {
		XdmValue point = Point.makePointLeft(evaluateXPath(xpath, "left()"));
		enforceNonEmptyPolicy(point, ctx.getText());
		selectedNodesStack.add(point);
		// reset the xpath state variable
		xpath = null;
	    } catch (Exception e) {
		errorSeen = true;
		errorStack.add(e);
	    }
	}
    }

    /**
     * Internal.
     */
    @Override
    public void enterRightPointer(TEIXPointerParser.RightPointerContext ctx) {
	bindDefaultNamespaceTEI();
    }

    /**
     * Internal.
     */
    @Override
    public void exitRightPointer(TEIXPointerParser.RightPointerContext ctx) {
	if (!errorSeen) {
	    // we get the XPath from the xpath state variable on the
	    // exit event
	    LOG.debug("found right() pointer, evaluating: {}", xpath);
	    try {
		XdmValue point = Point.makePointRight(evaluateXPath(xpath, "right()"));
		enforceNonEmptyPolicy(point, ctx.getText());
		selectedNodesStack.add(point);
		// reset the xpath state variable
		xpath = null;
	    } catch (Exception e) {
		errorSeen = true;
		errorStack.add(e);
	    }
	}
    }

    /**
     * Internal.
     */
    @Override
    public void enterStringIndexPointer(TEIXPointerParser.StringIndexPointerContext ctx) {
	bindDefaultNamespaceTEI();
    }

    /**
     * Internal.
     */
    @Override
    public void exitStringIndexPointer(TEIXPointerParser.StringIndexPointerContext ctx) {
	if (!errorSeen) {
	    // we get the XPath from the xpath state variable on the
	    // exit event
	    LOG.debug("found string-index() pointer, evaluating: {}", xpath);
	    try {
		int offset = Integer.parseInt(ctx.offset().getText());
		XdmValue point = Point.makeStringIndex(evaluateXPath(xpath, "string-index()"), offset);
		enforceNonEmptyPolicy(point, ctx.getText());
		selectedNodesStack.add(point);
		// reset the xpath state variable
		xpath = null;
	    } catch (Exception e) {
		errorSeen = true;
		errorStack.add(e);
	    }
	}
    }

    /**
     * Internal.
     */
    @Override
    public void exitRangeArgument(TEIXPointerParser.RangeArgumentContext ctx) {
	// We only have to check if there was a argument, that is not a pointer: an IDREF
	// Then we have to evaluate it and push the selection on the stack.
	if (!errorSeen) {
	    if (ctx.idref() != null && xpath != null) {
		if (!ctx.idref().getText().isEmpty()) {
		    LOG.debug("found IDREF argument to range(), evaluating: {}", xpath);
		    XdmValue node = evaluateXPath(xpath, "IDREF");
		    selectedNodesStack.add(node);
		    // reset the xpath state variable
		    xpath = null;
		}
	    } else if (ctx.pathexpr() != null && xpath != null) {
		if (!ctx.pathexpr().getText().isEmpty()) {
		    LOG.debug("found XPATH argument to range(), evaluating: {}", xpath);
		    XdmValue node = evaluateXPath(xpath, "XPATH");
		    selectedNodesStack.add(node);
		    // reset the xpath state variable
		    xpath = null;
		}
	    }
	}
    }

    /**
     * Internal.
     */
    @Override
    public void enterRangePointer(TEIXPointerParser.RangePointerContext ctx) {
	bindDefaultNamespaceTEI();
    }

    /**
     * Internal.
     */
    @Override
    public void exitRangePointer(TEIXPointerParser.RangePointerContext ctx) {
	if (!errorSeen) {
	    // we get the XPath from the xpath state variable on the
	    // exit event
	    int pairsCount = ctx.rangePointerPair().size();
	    LOG.debug("found range() pointer with {} pair(s)", pairsCount);
	    if (pairsCount*2 != selectedNodesStack.size()) {
		LOG.error("error processing range pointers: {} range pairs mismatch {} processed pointers",
			  pairsCount, selectedNodesStack.size());
		errorSeen = true;
		errorStack.add(new Exception("error processing range pointer: number of range pairs and processed pointers differ"));
	    } else {
		// algorithm: The overall range is a set union of the
		// nodes of each range described by a pair of
		// pointers, the start pointer and the end
		// pointer. Each such range is the intersection of the
		// nodes following the start pointer and the nodes
		// preceding the end pointer. Depending on the pointer
		// type, the start or end node identified by the
		// pointer is part of the intersection. E.g. when the
		// range starts with a left() pointer, the node
		// identified by this left pointer also belongs to the
		// range.

		// implementation: 1) iterate of pointer pairs. 2) for
		// each pointer pair: start with an empty range. Get
		// the starting pointer's first node. Append it to the
		// range if the pointer type does require so (e.g. for
		// a left() pointer). Get an iterator for the
		// following:: axis of this node. Get the end
		// pointer's node and set a predicate that indicates,
		// if the node must be appended to the range based on
		// the pointer type (e.g. true for a right() pointer).
		// Do the intersection: Iterate over the following::
		// axis as long as there are nodes AND the end node
		// has not been reached. Append each node seen to the
		// range.  If the end node has been reached, then
		// append the range to the ranges.

		// start with ranges empty
		XdmValue ranges = XdmEmptySequence.getInstance();

		try {
		    for (int i = 0; i < pairsCount; i++) {

			// we start with an empty intersection
			XdmValue range = XdmEmptySequence.getInstance();

			XdmValue startPointer = selectedNodesStack.get(2*i);
			// initialize axis iterator with an empty iterator
			XdmNode startNode = null;
			Iterator<XdmNode> followingIterator = new ArrayList<XdmNode>().iterator();
			// handle pointer types
			if (Point.isPoint(startPointer)) {
			    // Point pointer
			    Point startPoint = Point.getPoint(startPointer);
			    if (startPoint.getPosition() == Point.LEFT) {
				// we have a start node not on the axis iterator
				startNode = startPoint.getNode();
				// append it to the range
				range = range.append(startNode);
				followingIterator = startPoint.getNode().axisIterator(Axis.FOLLOWING);
			    } else if (startPoint.getPosition() == Point.RIGHT) {
				followingIterator = startPoint.getNode().axisIterator(Axis.FOLLOWING);
			    } else if (startPoint.getPosition() == Point.STRING_INDEX) {
				// FIXME: get text node fragment
				followingIterator = startPoint.getNode().axisIterator(Axis.FOLLOWING);
			    } else {
				// FIXME
			    }
			} else {
			    // Sequence pointer
			    startNode = Utils.getFirstNode(startPointer);
			    range = range.append(startNode);
			    followingIterator = startNode.axisIterator(Axis.FOLLOWING);
			}


			XdmValue endPointer = selectedNodesStack.get(2*i+1);
			XdmNode endNode = null;
			// handle pointer types
			boolean appendEndNode = false;
			if (Point.isPoint(endPointer)) {
			    // Point pointer
			    Point endPoint = Point.getPoint(endPointer);
			    endNode = endPoint.getNode();
			    if (endPoint.getPosition() == Point.RIGHT) {
				appendEndNode = true;
			    } else if (endPoint.getPosition() == Point.STRING_INDEX) {
				// FIXME: get text node fragment
				appendEndNode = true;
			    } else {
				// FIXME
			    }
			} else {
			    // we will be iterating until the last node of the end pointer
			    endNode = Utils.getLastNode(endPointer);
			    appendEndNode = true;
			}

			// process the intersection
			boolean endReached = false;

			// case start node equals end node: no axis
			// iteration needed, end node already reached.
			boolean startEndSame = false;
			if (startNode != null) {
			    if (startNode.equals(endNode)) {
				LOG.info("start node and end node are the same node");
				endReached = true;
				startEndSame = true;
			    }
			}

			// iterate the axis to the end node
			int n = 0;
			while (followingIterator.hasNext() && !endReached) {
			    XdmItem item = followingIterator.next();
			    if (item instanceof XdmNode) {
				XdmNode node = (XdmNode) item;
				if (node.equals(endNode)) {
				    LOG.debug("end node reached at axis step {}", n);
				    endReached = true;
				} else {
				    range = range.append(node);
				    n++;
				    LOG.debug("appending node {} to intersection", n);
				}
			    }
			}

			// append the end node if required
			if (appendEndNode && !startEndSame) {
			    range = range.append(endNode);
			}

			LOG.debug("range with {} nodes", n);

			// append range to ranges
			if (startEndSame && !appendEndNode) {
			    // do not append range
			} else if (endReached) {
			    ranges = ranges.append(range);
			}

		    }
		} catch (SaxonApiException e) {
		    errorSeen = true;
		    errorStack.add(e);
		}
		// store the generated selection of nodes to the stack
		selectedNodesStack.clear();
		selectedNodesStack.add(ranges);
	    }
	}
    }

    /**
     * Internal.
     */
    @Override
    public void enterStringRangePointer(TEIXPointerParser.StringRangePointerContext ctx) {
	bindDefaultNamespaceTEI();
    }

    /**
     * Internal.
     */
    @Override
    public void exitStringRangePointer(TEIXPointerParser.StringRangePointerContext ctx) {
	if (!errorSeen) {
	    // we get the XPath from the xpath state variable on the
	    // exit event
	    int pairsCount = ctx.stringRangePointerPair().size();
	    LOG.debug("found string-right() pointer with {} pairs, reference point evaluating: {}", pairsCount, xpath);

	    // start with ranges empty
	    XdmValue ranges = XdmEmptySequence.getInstance();

	    try {
		// get the frist node referenced by the first argument
		XdmValue referenceNodes = evaluateXPath(xpath, "string-range()");

		/* for preformance reasons we could do: */
		// Iterator<XdmItem> iter = referenceNodes.documentOrder().iterator();
		// while (iter.hasNext()) {
		//     XdmItem item = iter.next();
		//     if (item instanceof XdmNode) {
		// 	referenceNodes = (XdmNode) item;
		// 	break;
		//     }
		// }

		// iterate over pairs
		for (int i = 0; i < pairsCount; i++) {

		    // we start with an empty range
		    XdmValue range = XdmEmptySequence.getInstance();

		    // get the offset and length from the parser context and cast to integer
		    int offset = Integer.parseInt(ctx.stringRangePointerPair(i).offset().IntegerLiteral().getText());
		    int length = Integer.parseInt(ctx.stringRangePointerPair(i).length().IntegerLiteral().getText());

		    // the start point is like in the string-index() scheme
		    Point startPoint = Point.makeStringIndexPoint(referenceNodes, offset);

		    // step to the next pair if we have no string index point
		    if (startPoint == null) {
			LOG.error("no string index found for the offset-length pair {} of the string-range() pointer. Skipping", i);
			continue;
		    }

		    // get the starting text node and offset in it from the Point object
		    XdmNode startNode = startPoint.getNode();
		    int startTextOffset = startPoint.getOffset();
		    LOG.debug("found start point at offset {} in text node '{}'", startTextOffset, startNode.toString());

		    // iterate until the length is reached
		    boolean lengthReached = false;
		    int currentLength = 0;
		    StringValue textValue;
		    // use method to make the range
		    // ranges = ranges.append(makeStringRange(startNode, offset, length, currentOffset));
		    if (startNode != null) {
			LOG.debug("searching for the end text node");
			String startText = startNode.toString();
			int startTextLength = startText.length();

			if (startTextLength >= startTextOffset + length) {
			    // the whole string range is contained in the start text node
			    LOG.debug("the whole text range is contained in one text node");

			    // add a xs:string to range
			    textValue =	StringValue.makeStringValue(startText.substring(startTextOffset, startTextOffset + length));
			    range = range.append(XdmValue.makeValue(textValue));
			} else {
			    // we have to get following nodes to get a string of the required length
			    LOG.debug("the text range exceeds one text node");

			    // add part of starting text node as xs:string to range
			    textValue =	StringValue.makeStringValue(startText.substring(startTextOffset));
			    range = range.append(XdmValue.makeValue(textValue));
			    currentLength = currentLength + startTextLength - startTextOffset;

			    // walk the following axis
			    Iterator<XdmNode> followingIterator = startNode.axisIterator(Axis.FOLLOWING);
			    while (followingIterator.hasNext() && !lengthReached) {
				XdmNode followingNode = followingIterator.next();
				LOG.debug("evaluating node of type {}", followingNode.getNodeKind());
				if (followingNode.getNodeKind() == XdmNodeKind.TEXT) {
				    String text = followingNode.toString();
				    int textLength = text.length();
				    if (length < currentLength + textLength) {
					// add part of the text node as xs:string
					LOG.debug("adding part of text node '{}'", text);
					textValue = StringValue.makeStringValue(text.substring(0, length - currentLength));
					range = range.append(XdmValue.makeValue(textValue));
					lengthReached = true;
					currentLength = length;
				    } else {
					// append the whole text node
					LOG.debug("adding full text node '{}'", text.toString());
					if (length == currentLength + textLength) {
					    lengthReached = true;
					}
					range = range.append(followingNode);
					currentLength = currentLength + textLength;
				    }
				} else if (followingNode.getNodeKind() == XdmNodeKind.ELEMENT) {
				    // add element node if its text nodes do not exceed the remaining length

				    // spec: As with range(), the
				    // addressed sequence may contain
				    // text nodes and/or
				    // elements. [...] Because
				    // string-range() addresses points
				    // in the text stream, tags are
				    // invisible to it. For example,
				    // if an empty tag like lb is
				    // encountered while processing a
				    // string-range(), it will be
				    // included in the resulting
				    // sequence, but the LENGTH count
				    // will not increment when it is
				    // captured."
				    String text = Utils.descendantTextNodesAsString(followingNode);
				    if (length >= currentLength + text.length()) {
					LOG.debug("adding element node '{}'", followingNode.getNodeName());
					range = range.append(followingNode);
				    }
				} else {
				    // add all other kind of nodes, since they do not contribute to the length.
				    range = range.append(followingNode);
				}
			    }
			}
		    }

		    // append the range of this offset-length pair
		    ranges = ranges.append(range);

		}
		// reset the xpath state variable
		xpath = null;
	    } catch (NullPointerException e) {
		errorSeen = true;
		errorStack.add(e);
	    } catch (SaxonApiException e) {
		errorSeen = true;
		errorStack.add(e);
	    } catch (StringIndexOutOfBoundsException e) {
		errorSeen = true;
		errorStack.add(e);
	    }
	    // store the generated selection of nodes to the stack
	    selectedNodesStack.clear();
	    selectedNodesStack.add(ranges);
	}
    }
}
