package org.teic.teixptr.oa;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import org.apache.jena.vocabulary.OA;
import org.apache.jena.vocabulary.RDF;


public class ToOASelectorTest extends TestSetup {

    @Test
    void test_satsexp01() throws Exception {
	ToOASelector selector = ToOASelector.parseTEIXPointer(satsxp01, satsXml);
	assertEquals("xpath", selector.getPointerType());
	assertTrue(selector.getSelector().hasProperty(OA.hasSource));
	assertTrue(selector.getSelector().hasProperty(OA.hasSelector));
	assertTrue(selector.getSelector().getProperty(OA.hasSelector).getResource().hasProperty(RDF.value));
	assertTrue(selector.getSelector().getProperty(OA.hasSelector).getResource().getProperty(RDF.value).getObject().isLiteral());
	assertEquals("//lb[@n='1']/following-sibling::choice[1]/reg", selector.getSelector().getProperty(OA.hasSelector).getResource().getProperty(RDF.value).getObject().toString());
	assertTrue(selector.getSelector().getProperty(OA.hasSelector).getResource().hasProperty(RDF.type));
	assertTrue(selector.getSelector().getProperty(OA.hasSelector).getResource().getProperty(RDF.type).getObject().isResource());
	assertEquals(OA.XPathSelector, selector.getSelector().getProperty(OA.hasSelector).getResource().getProperty(RDF.type).getObject());
    }

}
