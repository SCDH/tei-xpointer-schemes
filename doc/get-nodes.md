# `xptr:get-nodes`

This gets a sequence of nodes.

```
xptr:get-nodes(href as xs:string, pointer as xs:string, wrap-points as xs:boolean) as node()*
```

Evaluates the TEI XPointer on a given URI and returns a sequence of
nodes referenced by the pointer.

Arguments:

- `uri` is the URL of the target document. It will be fetched and
  parsed.

- `pointer` is the pointer as a string.

- `wrap-points` is boolean switch: If `true()` then fragments of text
  nodes referrenced by string-range or string-index pointers will be
  included in the output. They are wrapped virtual copies of the
  original nodes. These serialize to a fragment of the original text
  node. They are *not identical* to the original nodes, i.e. calling
  `generate-id()` on it, will return an other ID than for the original
  node. They also do not have a parent. If `false()` these fragments
  will not be included in the output.


Implementing class:

[`org.teic.teixptr.implementation.extensions.GetNodes3`](../saxon/src/main/java/org/teic/teixptr/implementation/extensions/GetNodes3.java)
