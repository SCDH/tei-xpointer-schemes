<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xptr="http://www.tei-c.org/ns/xptr"
    xmlns="http://www.tei-c.org/ns/1.0" xpath-default-namespace="http://www.tei-c.org/ns/1.0"
    exclude-result-prefixes="#all" version="3.0">

    <xsl:param name="doc" as="xs:string" select="resolve-uri('sats.xml', static-base-uri())"/>

    <xsl:template name="get-nodes3">
        <xsl:param name="pointer-file" as="xs:string"/>
        <xsl:param name="wrap-points" as="xs:boolean" select="false()" required="false"/>
        <xsl:variable name="pointer" as="xs:string" select="unparsed-text($pointer-file)"/>
        <xsl:message>
            <xsl:text>evaluating pointer: </xsl:text>
            <xsl:value-of select="$pointer"/>
        </xsl:message>
        <div>
            <xsl:sequence select="xptr:get-nodes($doc, $pointer, $wrap-points)"/>
        </div>
    </xsl:template>

    <xsl:template name="get-nodes3-forrest">
        <xsl:param name="pointer-file" as="xs:string"/>
        <xsl:param name="wrap-points" as="xs:boolean" select="false()" required="false"/>
        <xsl:variable name="pointer" as="xs:string" select="unparsed-text($pointer-file)"/>
        <xsl:message>
            <xsl:text>evaluating pointer: </xsl:text>
            <xsl:value-of select="$pointer"/>
        </xsl:message>
        <div>
            <xsl:sequence select="xptr:get-nodes($doc, $pointer, $wrap-points) => xptr:forrest()"/>
        </div>
    </xsl:template>

</xsl:stylesheet>
