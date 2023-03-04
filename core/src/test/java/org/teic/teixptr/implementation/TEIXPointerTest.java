package org.teic.teixptr.implementation;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;


import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmValue;


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
	assertEquals("<lb n=\"3\"/>", point.toString());
	assertEquals(Point.RIGHT, point.getPosition());
    }

    @Test
    void test_satsl01() throws Exception {
	TEIXPointer pointer = TEIXPointer.parseTEIXPointer(satsl01, satsXml, proc);
	assertEquals("left", pointer.getPointerType());
	XdmValue selection = pointer.getSelectedNodes();
	assertEquals(1, selection.size());
	Point point = Point.getPoint(selection);
	assertNotNull(point);
	assertEquals("<supplied reason=\"lost\">si</supplied>", point.toString());
	assertEquals(Point.LEFT, point.getPosition());
    }
}
