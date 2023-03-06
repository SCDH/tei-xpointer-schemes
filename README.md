# Implementation of the TEI XPointer Scheme

This project is an implementation of the TEI XPointer scheme as
outlined in the [SATS
section](https://www.tei-c.org/release/doc/tei-p5-doc/de/html/SA.html#SATS)
in the TEI guidelines.

### State

development

## Modules

- `grammar`: an ANTLR4-based grammar of the TEI XPointer scheme
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
