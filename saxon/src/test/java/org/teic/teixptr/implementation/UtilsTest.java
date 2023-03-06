package org.teic.teixptr.implementation;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;


import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmValue;
import net.sf.saxon.s9api.XdmNode;


public class UtilsTest extends TestSetup {

    private Processor proc = new Processor(false);

    @Test
    void test_getFirstNode() throws Exception {
	TEIXPointer pointer = TEIXPointer.parseTEIXPointer(satsrn01, satsXml, proc);
	XdmValue selection = pointer.getSelectedNodes();
	assertEquals(19, selection.size());
	XdmNode node = Utils.getFirstNode(selection);
	assertEquals("<lb n=\"3\"/>", node.toString());
    }

    @Test
    void test_getLastNode() throws Exception {
	TEIXPointer pointer = TEIXPointer.parseTEIXPointer(satsrn01, satsXml, proc);
	XdmValue selection = pointer.getSelectedNodes();
	assertEquals(19, selection.size());
	XdmNode node = Utils.getLastNode(selection);
	assertEquals("supra res", node.toString().strip());
    }

    @Test
    void test_forrest() throws Exception {
	TEIXPointer pointer = TEIXPointer.parseTEIXPointer(satsrn01, satsXml, proc);
	XdmValue selection = pointer.getSelectedNodes();
	assertEquals(19, selection.size());
	XdmValue forrest = Utils.forrest(selection);
	assertEquals(9, forrest.size());
	//assertEquals("...", forrest.toString());
    }

}
