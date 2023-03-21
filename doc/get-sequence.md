# `xptr:get-sequence`

This gets a node sequence if the pointer type is sequnce.

```
xptr:get-sequence(uri as xs:string, pointer as xs:string) as node()*
```

Evaluates the TEI XPointer on a given URI and returns a sequence of
nodes referenced by the pointer.

Arguments:

- `uri` is the URL of the target document. It will be fetched and
  parsed.

- `pointer` is the pointer as a string.

Implementing class:

[`org.teic.teixptr.implementation.extensions.GetSequence`](../saxon/src/main/java/org/teic/teixptr/implementation/extensions/GetSequence.java)
