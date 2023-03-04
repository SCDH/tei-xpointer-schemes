package org.teic.teixptr.implementation;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;


import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;


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
	assertEquals("<lb n=\"3\"/>", pointer.getSelectedNodes().toString());
	assertEquals(1, pointer.getSelectedNodes().size());
    }

    @Disabled
    @Test
    void test_satsr01Point() throws Exception {
	TEIXPointer pointer = TEIXPointer.parseTEIXPointer(satsr01, satsXml, proc);
	assertNotNull(TestUtils.getFirstNode(pointer.getSelectedNodes()));
	assertNotNull(TestUtils.getPoint(pointer.getSelectedNodes()));
	assertTrue(TestUtils.getPoint(pointer.getSelectedNodes()) instanceof Point);
    }

    @Test
    void test_satsl01() throws Exception {
	TEIXPointer pointer = TEIXPointer.parseTEIXPointer(satsl01, satsXml, proc);
	assertEquals("left", pointer.getPointerType());
	assertEquals("<supplied reason=\"lost\">si</supplied>", pointer.getSelectedNodes().toString());
	assertEquals(1, pointer.getSelectedNodes().size());
    }

    @Disabled
    @Test
    void test_satsl01Point() throws Exception {
	TEIXPointer pointer = TEIXPointer.parseTEIXPointer(satsl01, satsXml, proc);
	assertNotNull(TestUtils.getFirstNode(pointer.getSelectedNodes()));
	assertNotNull(TestUtils.getPoint(pointer.getSelectedNodes()));
	assertTrue(TestUtils.getPoint(pointer.getSelectedNodes()) instanceof Point);
    }

}
