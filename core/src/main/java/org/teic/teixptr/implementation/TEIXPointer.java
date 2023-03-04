package org.teic.teixptr.implementation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.teic.teixptr.parser.*;

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
    public static TEIXPointer parseTEIXPointer(String pointer, File file, Processor processor)
	throws SaxonApiException {
	ParseTree tree = parse(pointer);
	ParseTreeWalker walker = new ParseTreeWalker();
	TEIXPointer listener = new TEIXPointer(file, processor);
	walker.walk(listener, tree);
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
	} else if (ctx.getStart().getType() == TEIXPointerLexer.XPATH) {
	    selectedNodes = selectedNodesStack.get(0);
	} else {
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
	    selectedNodesStack.add(evaluateXPath(xpath, "xpath()"));
	    // reset the xpath state variable
	    xpath = null;
	}
    }

}
