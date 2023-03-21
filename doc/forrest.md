# `xptr:forrest`

This gets a node sequence if the pointer type is sequnce.

```
xptr:forrest(nodes as node()*) as node()*
```

Given a sequence of nodes, all nodes that are descendants of other nodes
in the sequence a dropped. This is a very useful function if you want
to further process the node sequences from TEI XPointers. 

Arguments:

- `nodes`: a sequence of nodes

Implementing class:

[`org.teic.teixptr.implementation.extensions.Forrest`](../saxon/src/main/java/org/teic/teixptr/implementation/extensions/Forrest.java)
