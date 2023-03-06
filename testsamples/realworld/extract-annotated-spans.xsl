<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xptr="http://www.tei-c.org/ns/xptr"
    xmlns:scdh="http://scdh.wwu.de/xslt#" exclude-result-prefixes="#all"
    xpath-default-namespace="http://www.tei-c.org/ns/1.0" version="2.0">

    <xsl:output method="text"/>

    <xsl:mode on-no-match="shallow-skip"/>

    <xsl:include href="libref.xsl"/>

    <xsl:template match="annotation">
        <xsl:text>&#xa;</xsl:text>
        <xsl:text>Targets: </xsl:text>
        <xsl:variable name="context" select="."/>
        <xsl:for-each select="tokenize(@target)">
            <xsl:variable name="reference" select="scdh:process-reference(., $context)"/>
            <xsl:variable name="dereferenced" select="scdh:dereference($reference, $context)"/>
            <xsl:value-of select="count($dereferenced)"/>
        </xsl:for-each>
    </xsl:template>

</xsl:stylesheet>
