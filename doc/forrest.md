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


The function is equivalent to the following XSLT function:

```{xml}
    <!-- helper function:
        for given a sequence of nodes, filter away (drop) those nodes,
        that are descendants of other nodes in the sequence -->
    <xsl:function name="seed:drop-descendants" as="node()*" visibility="private">
        <xsl:param name="nodes" as="node()*"/>
        <xsl:variable name="ids" as="xs:string*" select="
                for $n in $nodes
                return
                    generate-id($n)"/>
        <xsl:message use-when="system-property('debug') eq 'true'">
            <xsl:text>Nodes in set: </xsl:text>
            <xsl:value-of select="$ids"/>
        </xsl:message>
        <!-- TODO: Might there be a case where ./parent::* is () ? -->
        <xsl:sequence select="
                $nodes[let $parent-id := generate-id(./parent::*)
                return
                    every $id in $ids
                        satisfies $id ne $parent-id]"/>
    </xsl:function>
```
