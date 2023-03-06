package org.teic.teixptr.implementation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.net.URI;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.InputSource;
import javax.xml.transform.Source;

import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XPathExecutable;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.Axis;

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
public class TEIXPointer extends TEIXPointerParserBaseListener {

    private static final Logger LOG = LoggerFactory.getLogger(TEIXPointer.class);

    protected final XdmNode docNode;

    protected final Processor processor;

    protected final XPathCompiler xpathCompiler;


    private String pointerType;

    private XdmValue selectedNodes = null;

    protected boolean assertNonEmpty = true;

    // state variables
    private boolean errorSeen = false;
    private List<Exception> errorStack = new ArrayList<Exception>();
    private List<XdmValue> selectedNodesStack = new ArrayList<XdmValue>();
    private String xpath = "";

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
	xpathCompiler = processor.newXPathCompiler();
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
	xpathCompiler = processor.newXPathCompiler();
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
	xpathCompiler = processor.newXPathCompiler();
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
    }

    @Override
    public void exitPointer(TEIXPointerParser.PointerContext ctx) {
	if (errorSeen) {
	    LOG.error("errors occurred while processing the pointer");
	}
	// set selectedNodes according to the base pointer type
	else if (ctx.getStart().getType() == TEIXPointerLexer.XPATH) {
	    selectedNodes = selectedNodesStack.get(0);
	} else if (ctx.getStart().getType() == TEIXPointerLexer.LEFT) {
	    selectedNodes = selectedNodesStack.get(0);
	} else if (ctx.getStart().getType() == TEIXPointerLexer.RIGHT) {
	    selectedNodes = selectedNodesStack.get(0);
	} else if (ctx.getStart().getType() == TEIXPointerLexer.RANGE) {
	    selectedNodes = selectedNodesStack.get(0);
	} else {
	    // TODO
	    // it's an IDREF
	    selectedNodes = selectedNodesStack.get(0);
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
	xpath = "id(" + ctx.getText() + ")";
    }

    /**
     * Internal.
     */
    @Override
    public void enterXpath(TEIXPointerParser.XpathContext ctx) {
	// we save the XPATH to the state and let the pointer evaluate
	// it on the exit<POINTER> event
	LOG.debug("found XPATH {}", ctx.getText());
	xpath = ctx.getText();
    }

    /**
     * Internal.
     */
    @Override
    public void exitXpath_pointer(TEIXPointerParser.Xpath_pointerContext ctx) {
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
    public void exitLeft_pointer(TEIXPointerParser.Left_pointerContext ctx) {
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
    public void exitRight_pointer(TEIXPointerParser.Right_pointerContext ctx) {
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
    public void exitString_index_pointer(TEIXPointerParser.String_index_pointerContext ctx) {
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
    public void exitRange_pointer(TEIXPointerParser.Range_pointerContext ctx) {
	if (!errorSeen) {
	    // we get the XPath from the xpath state variable on the
	    // exit event
	    int pairsCount = ctx.range_pointer_pair().size();
	    LOG.debug("found range() pointer with {} pair(s)", pairsCount);
	    if (pairsCount*2 != selectedNodesStack.size()) {
		LOG.error("error processing range pointers: {} range pairs mismatch {} processed pointers",
			  pairsCount, selectedNodesStack.size());
		errorSeen = true;
		errorStack.add(new Exception("error processing range pointer: number of range pairs and processed pointers differ"));
	    } else {
		// start with an empty sequence
		XdmValue seqs = new XdmValue(new ArrayList<XdmNode>());
		try {
		    for (int i = 0; i < pairsCount; i++) {

			// collect nodes following the start node
			XdmValue startPointer = selectedNodesStack.get(2*i);
			XdmValue followingStartNodes = new XdmValue(new ArrayList<XdmNode>());
			// initialize axis iterator with an empty iterator
			Iterator<XdmNode> followingIterator = new ArrayList<XdmNode>().iterator();
			// handle pointer types
			if (Point.isPoint(startPointer)) {
			    // Point pointer
			    Point startPoint = Point.getPoint(startPointer);
			    if (startPoint.getPosition() == Point.LEFT) {
				followingStartNodes = followingStartNodes.append(startPoint.getNode());
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
			    followingIterator = Utils.getFirstNode(startPointer).axisIterator(Axis.FOLLOWING);
			}
    			// add nodes on the following axis
			while (followingIterator.hasNext()) {
			    XdmNode node = followingIterator.next();
			    followingStartNodes = followingStartNodes.append(node);
			    LOG.debug("adding {} node following start: {}", node.getNodeKind(), node.toString());
			}
			LOG.debug("nodes in following start node: {}", followingStartNodes.size());


			// collect nodes preceding the end node
			XdmValue endPointer = selectedNodesStack.get(2*i+1);
			XdmValue precedingEndNodes = new XdmValue(new ArrayList<XdmNode>());
			// initialize axis iterator with an empty iterator
			Iterator<XdmNode> precedingIterator = new ArrayList<XdmNode>().iterator();
			// handle pointer types
			if (Point.isPoint(endPointer)) {
			    // Point pointer
			    Point endPoint = Point.getPoint(endPointer);
			    if (endPoint.getPosition() == Point.LEFT) {
				precedingIterator = endPoint.getNode().axisIterator(Axis.PRECEDING);
			    } else if (endPoint.getPosition() == Point.RIGHT) {
				precedingEndNodes = precedingEndNodes.append(endPoint.getNode());
				precedingIterator = endPoint.getNode().axisIterator(Axis.PRECEDING);
			    } else if (endPoint.getPosition() == Point.STRING_INDEX) {
				// FIXME: get text node fragment
				precedingEndNodes = precedingEndNodes.append(endPoint.getNode());
				precedingIterator = endPoint.getNode().axisIterator(Axis.PRECEDING);
			    } else {
				// FIXME
			    }
			} else {
			    // Sequence pointer
			    precedingIterator = Utils.getFirstNode(endPointer).axisIterator(Axis.PRECEDING);
			}
    			// add nodes on the preceding axis
			while (precedingIterator.hasNext()) {
			    XdmNode node = precedingIterator.next();
			    precedingEndNodes = precedingEndNodes.append(node);
			    LOG.debug("adding {} node preceding end: {}", node.getNodeKind(), node.toString());
			}
			LOG.debug("nodes in preceding end node: {}", precedingEndNodes.size());

			// intersection
			// TODO: is there a faster solution?
			Iterator<XdmItem> startIterator = followingStartNodes.iterator();
			int n = 0;
			while (startIterator.hasNext()) {
			    XdmItem startItem = startIterator.next();
			    if (startItem instanceof XdmNode) {
				XdmNode startNode = (XdmNode) startItem;
				Iterator<XdmItem> endIterator = precedingEndNodes.iterator();
				while (endIterator.hasNext()) {
				    XdmItem endItem = endIterator.next();
				    if (endItem instanceof XdmNode) {
					XdmNode endNode = (XdmNode) endItem;
					if (startNode.equals(endNode)) {
					    seqs = seqs.append(startNode);
					    n++;
					    break;
					}
				    }
				}
			    }
			}
			LOG.debug("range pair with {} nodes", n);
		    }
		} catch (SaxonApiException e) {
		    errorSeen = true;
		    errorStack.add(e);
		}
		// store the generated selection of nodes to the stack
		selectedNodesStack.clear();
		selectedNodesStack.add(seqs);
	    }
	}
    }

}
