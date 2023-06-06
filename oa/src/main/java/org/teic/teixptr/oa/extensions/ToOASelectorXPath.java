package org.teic.teixptr.oa.extensions;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.value.StringValue;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.XdmNode;

import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.Lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.teic.teixptr.oa.ToOASelector;
import org.teic.teixptr.extension.xpath.AbstractToOASelector;


/**
 * This class defines and implements the XPath function
 *
 * <code>get-sequence(uri as xs:string, pointer as xs:string, format as xs:string) as node()*</code>
 *
 */
public class ToOASelectorXPath extends AbstractToOASelector {

    private static final Logger LOG = LoggerFactory.getLogger(ToOASelectorXPath.class);

    /**
     * {@inheritDoc}
     */
    @Override
	public ExtensionFunctionCall makeCallExpression() {
	return new ExtensionFunctionCall() {
	    @Override
	    public Sequence call(XPathContext context, Sequence[] arguments)
		throws XPathException {

		LOG.debug("{} called", NAME);

		try {
		    // we can get the processor from context
		    Processor processor = new Processor(context.getConfiguration());

		    // get the arguments
		    String uriArg = XPathUtils.getStringArgument(arguments[0]);
		    String pointerArg = XPathUtils.getStringArgument(arguments[1]);

		    LOG.debug("pointer: {}", pointerArg);
		    LOG.debug("uri: {}", uriArg);

		    // process the pointer
		    ToOASelector pointer = ToOASelector.parseTEIXPointer(pointerArg, uriArg);

		    // serialize the model to RDF/XML format
		    StringWriter serialization = new StringWriter();
		    RDFDataMgr rdf = new RDFDataMgr();
		    rdf.write(serialization, pointer.getSelectorModel(), Lang.RDFXML);
		    LOG.debug("converted {} on {} to {}", pointerArg, uriArg, serialization.toString());

		    // read as XML using the Saxon processor and return it
		    StringReader xmlInput = new StringReader(serialization.toString());
		    StreamSource source = new StreamSource(xmlInput);
		    DocumentBuilder documentBuilder = processor.newDocumentBuilder();
		    XdmNode docNode = documentBuilder.build(source);
		    return docNode.getUnderlyingNode();
		} catch (SaxonApiException e) {
		    throw new XPathException(e);
		} catch (Exception e) {
		    throw new XPathException(e);
		}
	    }
	};
    }

}
