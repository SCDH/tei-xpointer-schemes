package org.teic.teixptr.implementation;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Iterator;
import java.net.URI;
import javax.xml.transform.stream.StreamSource;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import org.apache.jena.rdf.model.Model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.teic.teixptr.api.*;
import org.teic.teixptr.grammar.*;

public class ToOASelectors extends TEIXPointerBaseListener {

    private static final Logger LOG = LoggerFactory.getLogger(ToOASelectors.class);

    protected final String systemId;

    // state variables
    private boolean errorSeen = false;
    private List<Exception> errorStack = new ArrayList<Exception>();

    /**
     * Create a {@link ToOASelectors}. It does not read the referenced
     * XML document at all.
     * @throws {@link XPointerProcessorException}
     */
    public ToOASelectors(String systemId) throws XPointerProcessorException {
	this.systemId = systemId;
    }


    /**
     * A static method that parses a pointer to a {@link ToOASelectors}
     * object.
     *
     */
    public static ToOASelectors parseToOASelectors(String pointer, String systemId)
	throws XPointerProcessorException, Exception {
	ParseTree tree = parse(pointer);
	ParseTreeWalker walker = new ParseTreeWalker();
	ToOASelectors listener = new ToOASelectors(systemId);
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



}
