# Changes

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
