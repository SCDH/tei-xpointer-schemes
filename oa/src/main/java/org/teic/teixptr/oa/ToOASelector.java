package org.teic.teixptr.oa;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Iterator;
import java.net.URI;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.helpers.NamespaceSupport;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.OA;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.DCTerms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.teic.teixptr.api.*;
import org.teic.teixptr.grammar.*;

public class ToOASelector extends TEIXPointerBaseListener {

    private static final Logger LOG = LoggerFactory.getLogger(ToOASelector.class);

    public static final String TEINS = "http://www.tei-c.org/ns/1.0";

    public static final String RFC5147_URI = "http://tools.ietf.org/rfc/rfc5147";

    protected final String systemId;

    private String pointerType;

    private final Model model;

    private List<Resource> selector = null;


    // state variables
    private boolean errorSeen = false;
    private List<Exception> errorStack = new ArrayList<Exception>();
    private String xpath = null;
    private List<List<Resource>> selectorStack = new ArrayList<List<Resource>>();
    private NamespaceSupport namespaces = new NamespaceSupport();


    /**
     * Create a {@link ToOASelector}. It does not read the referenced
     * XML document at all.
     * @throws {@link XPointerProcessorException}
     */
    public ToOASelector(String systemId) throws XPointerProcessorException {
	this.systemId = systemId;
	this.model = ModelFactory.createDefaultModel();
    }


    /**
     * A static method that parses a pointer to a {@link ToOASelector}
     * object.
     *
     */
    public static ToOASelector parseTEIXPointer(String pointer, String systemId)
	throws XPointerProcessorException, Exception {
	ParseTree tree = parse(pointer);
	ParseTreeWalker walker = new ParseTreeWalker();
	ToOASelector listener = new ToOASelector(systemId);
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

    protected List<Resource> mkXPathSelector(String xpath, String context) {
	// we have no ID for an XPointer, so we create an anonymous resource
	Resource selector = model.createResource();
	selector.addProperty(RDF.type, OA.XPathSelector);
	selector.addProperty(RDF.value, xpath);
	return new ArrayList<Resource>(Collections.singletonList(selector));
    }


    protected Resource charScheme(int offset) {
	Resource charScheme = model.createResource();
	charScheme.addProperty(RDF.type, OA.FragmentSelector);
	charScheme.addProperty(DCTerms.conformsTo, model.createResource(RFC5147_URI));
	charScheme.addProperty(RDF.value, "char=" + Integer.toString(offset));
	return charScheme;
    }


    protected List<Resource> refineByCharScheme(List<Resource> selector, int offset) {
	List<Resource> newList = new ArrayList<Resource>();
	for (Resource sel : selector) {
	    sel.addProperty(OA.refinedBy, charScheme(offset));
	    newList.add(sel);
	}
	return newList;
    }

    protected List<Resource> refineByCharScheme(List<Resource> selector, int offset, int selectorIndex) {
	Resource sel = selector.get(selectorIndex);
	sel.addProperty(OA.refinedBy, charScheme(offset));
	selector.set(selectorIndex, sel);
	return selector;
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
     * Returns the basic pointer type.
     */
    public String getPointerType() {
	return pointerType;
    }


    public Resource getSelector() {
	// wrap selector in target with oa:hasSource
	Resource target = model.createResource();
	target.addProperty(OA.hasSource, this.systemId);
	for (Resource sel : this.selector) {
	    target.addProperty(OA.hasSelector, sel);
	}
	return target;
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
	// set selector according to the base pointer type
	else if (ctx.xpathPointer() != null) {
	    selector = selectorStack.get(0);
	} else if (ctx.leftPointer() != null) {
	    selector = selectorStack.get(0);
	} else if (ctx.rightPointer() != null) {
	    selector = selectorStack.get(0);
	} else if (ctx.rangePointer() != null) {
	    selector = selectorStack.get(0);
	} else if (ctx.stringIndexPointer() != null) {
	    selector = selectorStack.get(0);
	} else if (ctx.stringRangePointer() != null) {
	    selector = selectorStack.get(0);
	} else if (ctx.idref() != null) {
	    // handle IDREF
	    pointerType = "IDREF";
	    LOG.debug("found IDREF, evaluating: {}", xpath);
	    selector = mkXPathSelector(xpath, "IDREF");
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
	xpath = "id('" + ctx.getText() + "')";
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
	    List<Resource> selector = mkXPathSelector(xpath, "xpath()");
	    selectorStack.add(selector);
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
		List<Resource> selector = mkXPathSelector(xpath, "left()");
		selectorStack.add(selector);
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
	    // wrap node(s) into a point
	    try {
		List<Resource> selector = mkXPathSelector(xpath, "right()");
		selectorStack.add(selector);
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
		List<Resource> selector = mkXPathSelector(xpath, "string-index()");
		selector = refineByCharScheme(selector, offset);
		selectorStack.add(selector);
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
		    List<Resource> selector = mkXPathSelector(xpath, "IDREF");
		    selectorStack.add(selector);
		    // reset the xpath state variable
		    xpath = null;
		}
	    } else if (ctx.pathexpr() != null && xpath != null) {
		if (!ctx.pathexpr().getText().isEmpty()) {
		    LOG.debug("found XPATH argument to range(), evaluating: {}", xpath);
		    List<Resource> selector = mkXPathSelector(xpath, "XPATH");
		    selectorStack.add(selector);
		    // reset the xpath state variable
		    xpath = null;
		}
	    }
	}
    }



}
