# Changes

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
