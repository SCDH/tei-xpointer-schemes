# XPath function extension

The namespace name of the XPath extension functions is
`http://www.tei-c.org/ns/xptr`. In this documentation, we are
using the prefix `xptr` bound to this namespace:
`xmlns:xptr="http://www.tei-c.org/ns/xptr"`.


TEI XPointers are just pointing to something. But one can evaluate
them in several manners. Even the type of the pointers differ from
pointer type to pointer type. The functions' names reflect this.

# Querying the pointer type

TODO

# Type dependent functions

[`xptr:get-nodes(href as xs:string, pointer as xs:string,
  wrap-points as xs:boolean) as node()*`](get-nodes3.md)

[`xptr:get-sequence(uri as xs:string, pointer as xs:string) as node()*`](get-sequence.md)

TODO

# Converting to other referencing mechanisms

TODO

# Helper functions

[`xptr:forrest(nodes as node()*) as node()*`](forrest.md)
