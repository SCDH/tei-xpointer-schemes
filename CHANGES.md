# Changes

## 0.3.4 (dev)

- introduce `xptr:get-nodes(href as xs:string, pointer as xs:string,
  wrap-points as xs:boolean) as node()*` XPath function which returns
  homogenous sequence of nodes
  - fragments of text nodes as referred to by the `string-range`
    scheme are wrapped in virtual text nodes, i.e. nodes without
    parent and different ID than the original text node.
  - this greatly simplifies the usage of the XPath API
- change in `xptr:get-sequence/2`:
  - this will not return points any more but only complete nodes. Use
    the new function for getting points wrapped in virtual text nodes
    instead.
- change in `xptr:forrest(nodes as node()*) as node()*`
  - This function will first call `documentOrder()`, only if all nodes
    have a tree info. This was required for dealing with virtual nodes
    produced by the new function for getting nodes.
- new utils for running XSpec tests
- XSpec tests for new `xptr:get-nodes/3` function

## 0.3.3

- fix github release action

## 0.3.2

- use github actions for tests, making releases and publishing

## 0.3.1

- fix builds in CI/CD pipeline

## 0.3.0

- fix dependencies for Oxygen 24.0
- implement `string-range()` scheme
- improve `string-index()` scheme

## 0.2.2

- fix pipeline

## 0.2.1

- fix pipeline

## 0.2.0

- grammar based on XPath 3.1 grammar from the ANTLR4 grammars project,
  written by Ken Domino:
  [https://github.com/antlr/grammars-v4/blob/master/xpath/xpath31/XPath31.g4](https://github.com/antlr/grammars-v4/blob/master/xpath/xpath31/XPath31.g4)
- allow `COMMA` in path expressions
- added more test cases
- delivery as oxygen plugin

## 0.1.0

- implementation of the following pointers:
  - `xpath()`
  - `left()`
  - `right()`
  - `string-index()` in parts
  - `range()`
  - `IDREF` (simple fragment identifier without TEI XPointer)
- parse pointers with an ANTLR4 grammar
  - not allowing `COMMA` in path expressions due to its syntactical
    ambiguity
- XPath function extension
  - `xptr:get-sequence(pointer as xs:string) as node()*`
  - `xptr:forrest(nodes as node()*) as node()*`
