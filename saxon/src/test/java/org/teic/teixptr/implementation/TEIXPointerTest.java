package org.teic.teixptr.implementation;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;


import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmValue;
import net.sf.saxon.s9api.XdmNode;


public class TEIXPointerTest extends TestSetup {

    private Processor proc = new Processor(false);

    @Test
    void test_satsexp01() throws Exception {
	TEIXPointer pointer = TEIXPointer.parseTEIXPointer(satsxp01, satsXml, proc);
	assertEquals("xpath", pointer.getPointerType());
	assertEquals("<reg>habui</reg>", pointer.getSelectedNodes().toString());
    }

    @Test
    void test_satsr01() throws Exception {
	TEIXPointer pointer = TEIXPointer.parseTEIXPointer(satsr01, satsXml, proc);
	assertEquals("right", pointer.getPointerType());
	XdmValue selection = pointer.getSelectedNodes();
	assertEquals(1, selection.size());
	Point point = Point.getPoint(selection);
	assertNotNull(point);
	assertEquals("<lb n=\"3\"/>", point.getNode().toString());
	assertEquals(Point.RIGHT, point.getPosition());
	assertEquals("right", point.getPointerType());
	assertEquals(0, point.getOffset());
    }

    @Test
    void test_satsl01() throws Exception {
	TEIXPointer pointer = TEIXPointer.parseTEIXPointer(satsl01, satsXml, proc);
	assertEquals("left", pointer.getPointerType());
	XdmValue selection = pointer.getSelectedNodes();
	assertEquals(1, selection.size());
	Point point = Point.getPoint(selection);
	assertNotNull(point);
	assertEquals("<supplied reason=\"lost\">si</supplied>", point.getNode().toString());
	assertEquals(Point.LEFT, point.getPosition());
	assertEquals("left", point.getPointerType());
	assertEquals(Point.LEFT_OFFSET, point.getOffset());
    }

    @Test
    void test_satssi01() throws Exception {
	TEIXPointer pointer = TEIXPointer.parseTEIXPointer(satssi01, satsXml, proc);
	assertEquals("string-index", pointer.getPointerType());
	XdmValue selection = pointer.getSelectedNodes();
	assertEquals(1, selection.size());
	Point point = Point.getPoint(selection);
	assertNotNull(point);
	assertEquals("<lb n=\"2\"/>", point.getNode().toString());
	assertEquals(Point.STRING_INDEX, point.getPosition());
	assertEquals("string-index", point.getPointerType());
	assertEquals(1, point.getOffset());
    }

    @Test
    void test_satsrn01() throws Exception {
	TEIXPointer pointer = TEIXPointer.parseTEIXPointer(satsrn01, satsXml, proc);
	assertEquals("range", pointer.getPointerType());
	XdmValue selection = pointer.getSelectedNodes();
	assertEquals(19, selection.size());
	// assert right first and last node
	XdmNode node = Utils.getFirstNode(selection);
	assertEquals("<lb n=\"3\"/>", node.toString());
	node = Utils.getLastNode(selection);
	assertEquals("supra res", node.toString().strip());
    }

    @Test
    void test_sysrn04() throws Exception {
	TEIXPointer pointer = TEIXPointer.parseTEIXPointer(sysrn04, satsXml, proc);
	assertEquals("range", pointer.getPointerType());
	XdmValue selection = pointer.getSelectedNodes();
	assertEquals(19, selection.size());
	// assert right first and last node
    	XdmNode node = Utils.getFirstNode(selection);
	assertEquals("<unclear>s</unclear>", node.toString());
	node = Utils.getLastNode(selection);
	assertEquals("<lb n=\"4\"/>", node.toString().strip());
    }

    @Test
    void test_sysrn05() throws Exception {
	TEIXPointer pointer = TEIXPointer.parseTEIXPointer(sysrn05, satsXml, proc);
	assertEquals("range", pointer.getPointerType());
	XdmValue selection = pointer.getSelectedNodes();
	assertEquals(0, selection.size());
    }

    @Test
    void test_sysrn06() throws Exception {
	TEIXPointer pointer = TEIXPointer.parseTEIXPointer(sysrn06, satsXml, proc);
	assertEquals("range", pointer.getPointerType());
	XdmValue selection = pointer.getSelectedNodes();
	assertEquals(0, selection.size());
    }

    @Test
    void test_sysrn07() throws Exception {
	TEIXPointer pointer = TEIXPointer.parseTEIXPointer(sysrn07, satsXml, proc);
	assertEquals("range", pointer.getPointerType());
	XdmValue selection = pointer.getSelectedNodes();
	assertEquals(1, selection.size());
	// assert right first and last node
	assertEquals("<lb n=\"3\"/>", selection.toString());
    }

    @Test
    void test_sysrn08() throws Exception {
	TEIXPointer pointer = TEIXPointer.parseTEIXPointer(sysrn08, satsXml, proc);
	assertEquals("range", pointer.getPointerType());
	XdmValue selection = pointer.getSelectedNodes();
	assertEquals(0, selection.size());
    }

    @Disabled
    @Test
    void test_sysrn09() throws Exception {
	TEIXPointer pointer = TEIXPointer.parseTEIXPointer(sysrn09, satsXml, proc);
	assertEquals("range", pointer.getPointerType());
	XdmValue selection = pointer.getSelectedNodes();
	assertEquals(1, selection.size());
	// assert right first and last node
	assertEquals("<lb n=\"3\"/>", selection.toString());
    }


    @Test
    void test_ijobidref01() throws Exception {
	TEIXPointer pointer = TEIXPointer.parseTEIXPointer(ijobidref01, mtXml, proc);
	assertEquals("IDREF", pointer.getPointerType());
	XdmValue selection = pointer.getSelectedNodes();
	assertNotNull(selection);
	assertEquals(1, selection.size());
    }

    @Test
    void test_ijobrn04() throws Exception {
	TEIXPointer pointer = TEIXPointer.parseTEIXPointer(ijobrn01, mtXml, proc);
	assertEquals("range", pointer.getPointerType());
	XdmValue selection = pointer.getSelectedNodes();
	assertEquals(2, selection.size());
    }

}
