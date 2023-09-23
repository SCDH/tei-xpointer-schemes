package org.teic.teixptr.implementation;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;


import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmValue;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmItem;


public class TEIXPointerTest extends TestSetup {

    private Processor proc = new Processor(false);

    @Test
    void test_satsexp01() throws Exception {
	TEIXPointer pointer = TEIXPointer.parseTEIXPointer(satsxp01, satsXml, proc);
	assertEquals("xpath", pointer.getPointerType());
	assertEquals("<reg xmlns=\"http://www.tei-c.org/ns/1.0\">habui</reg>", pointer.getRelatedNodes().toString());
    }

    @Test
    void test_satsr01() throws Exception {
	TEIXPointer pointer = TEIXPointer.parseTEIXPointer(satsr01, satsXml, proc);
	assertEquals("right", pointer.getPointerType());
	XdmValue selection = pointer.getRelatedNodes();
	assertEquals(1, selection.size());
	Point point = Point.getPoint(selection);
	assertNotNull(point);
	assertEquals("<lb xmlns=\"http://www.tei-c.org/ns/1.0\" n=\"3\"/>", point.getNode().toString());
	assertEquals(Point.RIGHT, point.getPosition());
	assertEquals("right", point.getPointerType());
	assertEquals(0, point.getOffset());
    }

    @Test
    void test_satsl01() throws Exception {
	TEIXPointer pointer = TEIXPointer.parseTEIXPointer(satsl01, satsXml, proc);
	assertEquals("left", pointer.getPointerType());
	XdmValue selection = pointer.getRelatedNodes();
	assertEquals(1, selection.size());
	Point point = Point.getPoint(selection);
	assertNotNull(point);
	assertEquals("<supplied xmlns=\"http://www.tei-c.org/ns/1.0\" reason=\"lost\">si</supplied>", point.getNode().toString());
	assertEquals(Point.LEFT, point.getPosition());
	assertEquals("left", point.getPointerType());
	assertEquals(Point.LEFT_OFFSET, point.getOffset());
    }

    @Test
    void test_satssi01() throws Exception {
	TEIXPointer pointer = TEIXPointer.parseTEIXPointer(satssi01, satsXml, proc);
	assertEquals("string-index", pointer.getPointerType());
	XdmValue selection = pointer.getRelatedNodes();
	assertEquals(1, selection.size());
	assertTrue(Point.isPoint(selection));
	Point point = Point.getPoint(selection);
	assertNotNull(point);
	assertEquals("si", point.getNode().toString());
	assertEquals("s", point.getNode().toString().substring(0, point.getOffset()));
	assertEquals("i", point.getNode().toString().substring(point.getOffset()));
	assertEquals(Point.STRING_INDEX, point.getPosition());
	assertEquals("string-index", point.getPointerType());
	assertEquals(1, point.getOffset());
	// the point is not returned when getting nodes
	XdmValue nodes = pointer.getNodes();
	assertEquals(0, nodes.size());
    }

    @Test
    void test_satsrn01() throws Exception {
	TEIXPointer pointer = TEIXPointer.parseTEIXPointer(satsrn01, satsXml, proc);
	assertEquals("range", pointer.getPointerType());
	XdmValue selection = pointer.getRelatedNodes();
	assertEquals(19, selection.size());
	// assert right first and last node
	XdmNode node = Utils.getFirstNode(selection);
	assertEquals("<lb xmlns=\"http://www.tei-c.org/ns/1.0\" n=\"3\"/>", node.toString());
	node = Utils.getLastNode(selection);
	assertEquals("supra res", node.toString().strip());
    }

    @Test
    void test_satssr01() throws Exception {
	TEIXPointer pointer = TEIXPointer.parseTEIXPointer(satssr01, satsXml, proc);
	assertEquals("string-range", pointer.getPointerType());
	XdmValue selection = pointer.getRelatedNodes();
	assertEquals(4, selection.size());
	assertEquals("auge et opto u", TestUtils.getFirstItem(selection).toString());
	assertEquals(" bene valeas", TestUtils.getLastItem(selection).toString());
    }

    @Test
    void test_satssr02() throws Exception {
	TEIXPointer pointer = TEIXPointer.parseTEIXPointer(satssr02, satsXml, proc);
	assertEquals("string-range", pointer.getPointerType());
	XdmValue selection = pointer.getRelatedNodes();
	assertEquals(3, selection.size());
	assertEquals("in ", TestUtils.getFirstItem(selection).toString());
	assertEquals("mente", TestUtils.getLastItem(selection).toString());
    }

    @Test
    void test_satssr03() throws Exception {
	TEIXPointer pointer = TEIXPointer.parseTEIXPointer(satssr03, satsXml, proc);
	assertEquals("string-range", pointer.getPointerType());
	XdmValue selection = pointer.getRelatedNodes();
	assertEquals(2, selection.size());
	assertEquals("in ", TestUtils.getFirstItem(selection).toString());
	assertEquals("mentem", TestUtils.getLastItem(selection).toString());
    }


    @Test
    void test_sysidref01() throws Exception {
	TEIXPointer pointer = TEIXPointer.parseTEIXPointer(sysidref01, satsXml, proc);
	assertEquals("IDREF", pointer.getPointerType());
	XdmValue selection = pointer.getRelatedNodes();
	assertEquals(1, selection.size());
	// assert right first and last node
    	XdmNode node = Utils.getFirstNode(selection);
	assertEquals("<lb xmlns=\"http://www.tei-c.org/ns/1.0\" n=\"1\" xml:id=\"line1\"/>", node.toString());
    }

    @Test
    void test_sysidref02() throws Exception {
	TEIXPointer pointer = TEIXPointer.parseTEIXPointer(sysidref02, satsXml, proc);
	assertEquals("IDREF", pointer.getPointerType());
	XdmValue selection = pointer.getRelatedNodes();
	assertEquals(0, selection.size());
    }

    @Test
    void test_sysxp02() throws Exception {
	TEIXPointer pointer = TEIXPointer.parseTEIXPointer(sysxp02, satsXml, proc);
	assertEquals("xpath", pointer.getPointerType());
	//XdmValue selection = pointer.getRelatedNodes();
	//assertEquals(0, selection.size());
    }

    @Test
    void test_sysxp03() throws Exception {
	TEIXPointer pointer = TEIXPointer.parseTEIXPointer(sysxp03, satsXml, proc);
	assertEquals("xpath", pointer.getPointerType());
	XdmValue selection = pointer.getRelatedNodes();
	assertEquals(1, selection.size());
    	XdmNode node = Utils.getFirstNode(selection);
	assertEquals("<lb xmlns=\"http://www.tei-c.org/ns/1.0\" n=\"1\" xml:id=\"line1\"/>", node.toString());
    }

    @Test
    void test_sysxp04() throws Exception {
	TEIXPointer pointer = TEIXPointer.parseTEIXPointer(sysxp04, satsXml, proc);
	assertEquals("xpath", pointer.getPointerType());
	XdmValue selection = pointer.getRelatedNodes();
	assertEquals(0, selection.size());
    }

    @Test
    void test_sysrn04() throws Exception {
	TEIXPointer pointer = TEIXPointer.parseTEIXPointer(sysrn04, satsXml, proc);
	assertEquals("range", pointer.getPointerType());
	XdmValue selection = pointer.getRelatedNodes();
	assertEquals(19, selection.size());
	// assert right first and last node
    	XdmNode node = Utils.getFirstNode(selection);
	assertEquals("<unclear xmlns=\"http://www.tei-c.org/ns/1.0\">s</unclear>", node.toString());
	node = Utils.getLastNode(selection);
	assertEquals("<lb xmlns=\"http://www.tei-c.org/ns/1.0\" n=\"4\"/>", node.toString().strip());
    }

    @Test
    void test_sysrn05() throws Exception {
	TEIXPointer pointer = TEIXPointer.parseTEIXPointer(sysrn05, satsXml, proc);
	assertEquals("range", pointer.getPointerType());
	XdmValue selection = pointer.getRelatedNodes();
	assertEquals(0, selection.size());
    }

    @Test
    void test_sysrn06() throws Exception {
	TEIXPointer pointer = TEIXPointer.parseTEIXPointer(sysrn06, satsXml, proc);
	assertEquals("range", pointer.getPointerType());
	XdmValue selection = pointer.getRelatedNodes();
	assertEquals(0, selection.size());
    }

    @Test
    void test_sysrn07() throws Exception {
	TEIXPointer pointer = TEIXPointer.parseTEIXPointer(sysrn07, satsXml, proc);
	assertEquals("range", pointer.getPointerType());
	XdmValue selection = pointer.getRelatedNodes();
	assertEquals(1, selection.size());
	// assert right first and last node
	assertEquals("<lb xmlns=\"http://www.tei-c.org/ns/1.0\" n=\"3\"/>", selection.toString());
    }

    @Test
    void test_sysrn08() throws Exception {
	TEIXPointer pointer = TEIXPointer.parseTEIXPointer(sysrn08, satsXml, proc);
	assertEquals("range", pointer.getPointerType());
	XdmValue selection = pointer.getRelatedNodes();
	assertEquals(0, selection.size());
    }

    @Test
    void test_sysrn09() throws Exception {
	TEIXPointer pointer = TEIXPointer.parseTEIXPointer(sysrn09, satsXml, proc);
	assertEquals("range", pointer.getPointerType());
	XdmValue selection = pointer.getRelatedNodes();
	assertEquals(1, selection.size());
	// assert right first and last node
	assertEquals("<lb xmlns=\"http://www.tei-c.org/ns/1.0\" n=\"3\"/>", selection.toString());
    }

    @Test
    void test_sysssi02() throws Exception {
	TEIXPointer pointer = TEIXPointer.parseTEIXPointer(syssi02, satsXml, proc);
	assertEquals("string-index", pointer.getPointerType());
	XdmValue selection = pointer.getRelatedNodes();
	assertEquals(1, selection.size());
	Point point = Point.getPoint(selection);
	assertNotNull(point);
	assertEquals("b", point.getNode().toString());
	assertEquals("", point.getNode().toString().substring(0, point.getOffset()));
	assertEquals("b", point.getNode().toString().substring(point.getOffset()));
	assertEquals(Point.STRING_INDEX, point.getPosition());
	assertEquals("string-index", point.getPointerType());
	assertEquals(0, point.getOffset());
    }

    @Test
    void test_ijobidref01() throws Exception {
	TEIXPointer pointer = TEIXPointer.parseTEIXPointer(ijobidref01, mtXml, proc);
	assertEquals("IDREF", pointer.getPointerType());
	XdmValue selection = pointer.getRelatedNodes();
	assertNotNull(selection);
	assertEquals(1, selection.size());
    }

    @Test
    void test_ijobrn01() throws Exception {
	TEIXPointer pointer = TEIXPointer.parseTEIXPointer(ijobrn01, mtXml, proc);
	assertEquals("range", pointer.getPointerType());
	XdmValue selection = pointer.getRelatedNodes();
	XdmValue forrest = Utils.forrest(selection);
	assertEquals(3, forrest.size());
    }

}
