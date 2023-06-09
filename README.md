# Implementation of the TEI XPointer Schemes

This project is an implementation of the TEI XPointer scheme as
outlined in the [SATS
section](https://www.tei-c.org/release/doc/tei-p5-doc/de/html/SA.html#SATS)
in the TEI guidelines.

Using this implementation, TEI XPointers can be parsed and evaluated
either through a Java API or from XSLT and XQuery through [XPath
extension functions](doc/xpath-functions.md).

### State

development, testing

implemented XPointer schemes: `xpath()`, `left()`, `right()`,
`string-index()`, `range()`, `string-range()`

not (yet) implemented: `match()` TEI XPointer scheme, and the
`xmlns()` scheme for binding namespaces to prefixes; chaining pointers

## Modules

- `grammar`: an ANTLR4-based grammar of the TEI XPointer schemes
- `xpath-extension`: definitions of XPath functions for processing XPointers
- `saxon`: an implementation of a XPointer processor based on Saxon's
  s9api
- `oxygen`: the implementation of the TEI XPointer scheme packetized
  as a plugin for the Oxygen XML editor

## Getting started

### Oxygen plugin

Installation of the Oxygen plugin is done through the extension
manager (menu **Help** -> **Install new add-ons**) with the following
URL:

```
https://scdh.github.io/tei-xpointer-schemes/descriptor.xml
```

The plugin will enable you to evaluate TEI XPointers using the [XPath
extension functions](doc/xpath-functions.md).


### Evaluating TEI XPointers with XSLT and XQuery from the command line

For building, testing and packetizing locally run the following
command from the root of the cloned repository:

```{shell}
mvn clean package verify
```

After running `mvn package` there are scripts in `saxon/target/bin`
for running Saxon from commandline. These scripts set the classpath
correctly, so that the TEI XPointer processor is available.

Note that you might have to run `chmod +x` on these scripts.

Here are two example invokations of an XSLT stylesheet from the
testsuite:

```{shell}
saxon/target/bin/xslt.sh -config:saxon/saxon-config.xml -xsl:testsamples/systematic/sequences.xsl -it pointer="rn04.txt"
```

```{shell}
saxon/target/bin/xslt.sh -config:saxon/saxon-config.xml -xi:on -s:testsamples/realworld/Annotationen.xml -xsl:testsamples/realworld/extract-annotated-spans.xsl
```

Note the `-config:saxon/saxon-config.xml` commandline parameter: It
passes the Saxon configuration in
[`saxon/saxon-config.xml`](saxon/saxon-config.xml) to Saxon, so that
the XPath functions defined in this project are available to the XSLT
processor.

The XSLT in `testsamples/realworld/extract-annotated-spans.xsl` or the
imported `libref.xsl` evaluates TEI XPointers with the new
[`xptr:get-sequence()` XPath function](doc/xpath-functions.md). It
takes two `xs:string` arguments, one for the file URL and one for the
pointer, and returns a sequence of nodes.


### Java API

Java API docs are present in `target/site/apidocs/index.html` after
running the following command:

```{shell}
mvn javadoc:aggregate
```


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

## Security

Please read the [security instruction](security.md) before using.

# Further reading

- [Getting started with
  ANTLR](https://github.com/antlr/antlr4/blob/master/doc/getting-started.md)
