package org.teic.teixptr.oa;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;


import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.OA;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.DCTerms;


public class ToOASelectorTest extends TestSetup {

    public static final Resource RFC5147 = ModelFactory.createDefaultModel().createResource(ToOASelector.RFC5147_URI);

    @Test
    void test_satsexp01() throws Exception {
	ToOASelector selector = ToOASelector.parseTEIXPointer(satsxp01, satsXml);
	assertEquals("xpath", selector.getPointerType());
	assertTrue(selector.getSelector().hasProperty(OA.hasSource));
	assertTrue(selector.getSelector().getProperty(OA.hasSource).getObject().isLiteral());
	assertEquals(satsXml, selector.getSelector().getProperty(OA.hasSource).getObject().toString());
	assertTrue(selector.getSelector().hasProperty(OA.hasSelector));
	assertTrue(selector.getSelector().getProperty(OA.hasSelector).getResource().hasProperty(RDF.value));
	assertTrue(selector.getSelector().getProperty(OA.hasSelector).getResource().getProperty(RDF.value).getObject().isLiteral());
	assertEquals("//lb[@n='1']/following-sibling::choice[1]/reg", selector.getSelector().getProperty(OA.hasSelector).getResource().getProperty(RDF.value).getObject().toString());
	assertTrue(selector.getSelector().getProperty(OA.hasSelector).getResource().hasProperty(RDF.type));
	assertTrue(selector.getSelector().getProperty(OA.hasSelector).getResource().getProperty(RDF.type).getObject().isResource());
	assertEquals(OA.XPathSelector, selector.getSelector().getProperty(OA.hasSelector).getResource().getProperty(RDF.type).getObject());
    }

    @Test
    void test_sysidref01() throws Exception {
	ToOASelector selector = ToOASelector.parseTEIXPointer(sysidref01, satsXml);
	assertEquals("IDREF", selector.getPointerType());
	assertTrue(selector.getSelector().hasProperty(OA.hasSource));
	assertTrue(selector.getSelector().hasProperty(OA.hasSelector));
	assertTrue(selector.getSelector().getProperty(OA.hasSelector).getResource().hasProperty(RDF.value));
	assertTrue(selector.getSelector().getProperty(OA.hasSelector).getResource().getProperty(RDF.value).getObject().isLiteral());
	assertEquals("id('line1')", selector.getSelector().getProperty(OA.hasSelector).getResource().getProperty(RDF.value).getObject().toString());
	assertTrue(selector.getSelector().getProperty(OA.hasSelector).getResource().hasProperty(RDF.type));
	assertTrue(selector.getSelector().getProperty(OA.hasSelector).getResource().getProperty(RDF.type).getObject().isResource());
	assertEquals(OA.XPathSelector, selector.getSelector().getProperty(OA.hasSelector).getResource().getProperty(RDF.type).getObject());
    }

    @Test
    void test_satssi01() throws Exception {
	ToOASelector selector = ToOASelector.parseTEIXPointer(satssi01, satsXml);
	assertEquals("string-index", selector.getPointerType());
	assertTrue(selector.getSelector().hasProperty(OA.hasSource));
	assertTrue(selector.getSelector().hasProperty(OA.hasSelector));
	assertTrue(selector.getSelector().getProperty(OA.hasSelector).getResource().hasProperty(RDF.value));
	assertTrue(selector.getSelector().getProperty(OA.hasSelector).getResource().getProperty(RDF.value).getObject().isLiteral());
	assertEquals("//lb[@n='2']", selector.getSelector().getProperty(OA.hasSelector).getResource().getProperty(RDF.value).getObject().toString());
	assertTrue(selector.getSelector().getProperty(OA.hasSelector).getResource().hasProperty(RDF.type));
	assertTrue(selector.getSelector().getProperty(OA.hasSelector).getResource().getProperty(RDF.type).getObject().isResource());
	assertEquals(OA.XPathSelector, selector.getSelector().getProperty(OA.hasSelector).getResource().getProperty(RDF.type).getObject());
	// refinement
	assertTrue(selector.getSelector().getProperty(OA.hasSelector).getResource().hasProperty(OA.refinedBy));
	assertTrue(selector.getSelector().getProperty(OA.hasSelector).getResource().getProperty(OA.refinedBy).getResource().hasProperty(RDF.type));
	assertTrue(selector.getSelector().getProperty(OA.hasSelector).getResource().getProperty(OA.refinedBy).getResource().getProperty(RDF.type).getObject().isResource());
	assertEquals(OA.FragmentSelector, selector.getSelector().getProperty(OA.hasSelector).getResource().getProperty(OA.refinedBy).getResource().getProperty(RDF.type).getObject());
	assertTrue(selector.getSelector().getProperty(OA.hasSelector).getResource().getProperty(OA.refinedBy).getResource().hasProperty(RDF.value));
	assertTrue(selector.getSelector().getProperty(OA.hasSelector).getResource().getProperty(OA.refinedBy).getResource().getProperty(RDF.value).getObject().isLiteral());
	assertEquals("char=1", selector.getSelector().getProperty(OA.hasSelector).getResource().getProperty(OA.refinedBy).getResource().getProperty(RDF.value).getObject().toString());
	assertTrue(selector.getSelector().getProperty(OA.hasSelector).getResource().getProperty(OA.refinedBy).getResource().hasProperty(DCTerms.conformsTo));
	assertTrue(selector.getSelector().getProperty(OA.hasSelector).getResource().getProperty(OA.refinedBy).getResource().getProperty(DCTerms.conformsTo).getObject().isResource());
	assertEquals(RFC5147, selector.getSelector().getProperty(OA.hasSelector).getResource().getProperty(OA.refinedBy).getResource().getProperty(DCTerms.conformsTo).getObject());
    }

    @Disabled("Wrong pointer type!")
    @Test
    void test_satsrn01() throws Exception {
	ToOASelector selector = ToOASelector.parseTEIXPointer(satsrn01, satsXml);
	assertEquals("range", selector.getPointerType());
	assertTrue(selector.getSelector().hasProperty(OA.hasSource));
	assertTrue(selector.getSelector().getProperty(OA.hasSource).getObject().isLiteral());
	assertEquals(satsXml, selector.getSelector().getProperty(OA.hasSource).getObject().toString());
	assertTrue(selector.getSelector().hasProperty(OA.hasSelector));
	assertEquals(OA.RangeSelector, selector.getSelector().getProperty(OA.hasSelector).getResource().getProperty(RDF.type).getObject());
    }

}
