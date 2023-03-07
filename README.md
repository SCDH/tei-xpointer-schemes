# Implementation of the TEI XPointer Scheme

This project is an implementation of the TEI XPointer scheme as
outlined in the [SATS
section](https://www.tei-c.org/release/doc/tei-p5-doc/de/html/SA.html#SATS)
in the TEI guidelines.

### State

development

## Modules

- `grammar`: an ANTLR4-based grammar of the TEI XPointer scheme
- `xpath-extension`: definitions of XPath functions for processing XPointers
- `saxon`: an implementation of processing TEI XPointers based on
  Saxon's s9api

## Getting started

Build, test and packetize:

```{shell}
mvn clean package verify
```

Java API docs are present in `target/site/apidocs/index.html` after
running the following command:

```{shell}
mvn javadoc:aggregate
```

After running `mvn package` there are scripts in `saxon/target/bin`
for running Saxon from commandline. These scripts set the classpath
correctly, so that the TEI XPointer processing is available.

Note that you might have to run `chmod +x` on these scripts.

Here is an example invokation of an XSLT stylesheet from the
testsuite:

```{shell}
saxon/target/bin/xslt.sh -config:saxon/saxon-config.xml -xi:on -s:testsamples/realworld/Annotationen.xml -xsl:testsamples/realworld/extract-annotated-spans.xsl
```

Note the `-config:saxon/saxon-config.xml` commandline parameter: It
passes the Saxon configuration in
[`saxon/saxon-config.xml`](saxon/saxon-config.xml) to Saxon, so that
the XPath functions defined in this project are available to the XSLT
processor.
