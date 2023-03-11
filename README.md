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

### Running XSLT and XQuery

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

### Inspecting the parse tree

If you want to investigate, how a TEI XPointer is parsed, you can use
the script `grammar/target/bin/antlr-grun` which is present after
running `mvn package`. You may need to run `chmod +x` on it.

For example, to inspect the parse tree of the pointer in
`testsuite/systematic/rn09.txt`, you can run the following command:

```{shell}
grammar/target/bin/antlr-grun org.teic.teixptr.grammar.TEIXPointer fragm -gui < testsamples/systematic/rn09.txt
```

Here, `org.teic.teixptr.grammar.TEIXPointer` is the name of the
grammar and `fragm` is the name of the grammar's start rule. Try the
same command with the `-tokens` or `-tree` option, instead.


# Further reading

- [Getting started with
  ANTLR](https://github.com/antlr/antlr4/blob/master/doc/getting-started.md)
