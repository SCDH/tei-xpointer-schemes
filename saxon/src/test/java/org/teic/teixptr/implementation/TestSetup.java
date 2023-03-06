package org.teic.teixptr.implementation;

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

    public static String satsxp01, satsr01, satsl01, satssi01, satsrn01;

    public static String casern04;

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
    public static void setupSATSExamples() throws IOException {
	satsxp01 = readSatsSample("satsxp01.txt");
	satsr01 = readSatsSample("satsr01.txt");
	satsl01 = readSatsSample("satsl01.txt");
	satssi01 = readSatsSample("satssi01.txt");
	satsrn01 = readSatsSample("satsrn01.txt");

	casern04 = readSystematicSample("casern04.txt");

	ijobidref01 = readRealWorldSample("ijobidref01.txt");
	ijobrn01 = readRealWorldSample("ijobrn01.txt");
    }

}
