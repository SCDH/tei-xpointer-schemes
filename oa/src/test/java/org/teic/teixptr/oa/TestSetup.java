package org.teic.teixptr.oa;

import java.net.URI;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;

/**
 * Setup common things needed for unit testing.
 */
public class TestSetup {

    public static URI satsSamples = Paths.get("..", "testsamples", "sats").toUri();

    public static URI systematicSamples = Paths.get("..", "testsamples", "systematic").toUri();

    public static URI realworldSamples = Paths.get("..", "testsamples", "realworld").toUri();

    public static String satsXml = Paths.get(satsSamples.resolve("sats.xml")).toFile().toString();

    public static String mtXml = Paths.get(realworldSamples.resolve("MT.tei.xml")).toFile().toString();

    public static String satsxp01, satsr01, satsl01, satssi01, satsrn01, satssr01, satssr02, satssr03;

    public static String sysidref01, sysidref02, sysxp02, sysxp03, sysxp04, sysrn04, sysrn05, sysrn06, sysrn07, sysrn08, sysrn09, syssi02;

    public static String ijobidref01, ijobrn01;

    public static String readSatsSample(String sample) throws IOException {
	Path path = Paths.get(satsSamples.resolve(sample));
	return Files.readString(path);
    }

    public static String readSystematicSample(String sample) throws IOException {
	Path path = Paths.get(systematicSamples.resolve(sample));
	return Files.readString(path);
    }

    public static String readRealWorldSample(String sample) throws IOException {
	Path path = Paths.get(realworldSamples.resolve(sample));
	return Files.readString(path);
    }

    @BeforeAll
    public static void setupExamples() throws IOException {
	satsxp01 = readSatsSample("satsxp01.txt");
	satsr01 = readSatsSample("satsr01.txt");
	satsl01 = readSatsSample("satsl01.txt");
	satssi01 = readSatsSample("satssi01.txt");
	satsrn01 = readSatsSample("satsrn01.txt");
	satssr01 = readSatsSample("satssr01.txt");
	satssr02 = readSatsSample("satssr02.txt");
	satssr03 = readSatsSample("satssr03.txt");


	sysidref01 = readSystematicSample("idref01.txt");
	sysidref02 = readSystematicSample("idref02.txt");
	sysrn04 = readSystematicSample("rn04.txt");
	sysrn05 = readSystematicSample("rn05.txt");
	sysrn06 = readSystematicSample("rn06.txt");
	sysrn07 = readSystematicSample("rn07.txt");
	sysrn08 = readSystematicSample("rn08.txt");
	sysrn09 = readSystematicSample("rn09.txt");
	sysxp02 = readSystematicSample("xp02.txt");
	sysxp03 = readSystematicSample("xp03.txt");
	sysxp04 = readSystematicSample("xp04.txt");
	syssi02 = readSystematicSample("si02.txt");

	ijobidref01 = readRealWorldSample("ijobidref01.txt");
	ijobrn01 = readRealWorldSample("ijobrn01.txt");
    }

}
