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

    public static File satsXml = Paths.get(satsSamples.resolve("sats.xml")).toFile();

    public static String satsxp01, satsr01, satsl01, satssi01, satsrn01;

    public static String casern04;

    public static String readSample(String sample) throws IOException {
	Path path = Paths.get(satsSamples.resolve(sample));
	return Files.readString(path);
    }

    @BeforeAll
    public static void setupSATSExamples() throws IOException {
	satsxp01 = readSample("satsxp01.txt");
	satsr01 = readSample("satsr01.txt");
	satsl01 = readSample("satsl01.txt");
	satssi01 = readSample("satssi01.txt");
	satsrn01 = readSample("satsrn01.txt");

	casern04 = readSample("casern04.txt");
    }

}
