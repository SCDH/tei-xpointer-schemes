package org.teic.teixptr.implementation;

import java.io.File;

import org.xml.sax.InputSource;
import javax.xml.transform.Source;

import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmNode;

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

    private String pointerType;

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
     * Returns the basic pointer type.
     */
    public String getPointerType() {
	return pointerType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterPointer(TEIXPointerParser.PointerContext ctx) {
	pointerType = ctx.getStart().getText();
	LOG.debug("found base pointer: {}", pointerType);
    }


}
