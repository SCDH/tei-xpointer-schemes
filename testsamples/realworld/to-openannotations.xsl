<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xptr="http://www.tei-c.org/ns/xptr"
    xmlns:scdh="http://scdh.wwu.de/xslt#" exclude-result-prefixes="#all"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:oa="http://www.w3.org/ns/oa#"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xpath-default-namespace="http://www.tei-c.org/ns/1.0" version="2.0">

    <xsl:output method="xml" indent="yes"/>

    <xsl:mode on-no-match="shallow-skip"/>

    <xsl:include href="libref.xsl"/>

    <xsl:template match="/">
        <rdf:RDF>
            <xsl:apply-templates/>
        </rdf:RDF>
    </xsl:template>

    <xsl:template match="annotation[not(@xml:id)]">
      <xsl:message>
	<xsl:text>WARNING: annotation without @xml:id. Position </xsl:text>
	<xsl:value-of select="position()"/>
      </xsl:message>
    </xsl:template>

    <xsl:template match="annotation[@xml:id]">
        <xsl:variable name="context" select="."/>
        <rdf:Description rdf:about="{concat(base-uri(), '#', @xml:id)}">
            <oa:hasTarget>
                <xsl:for-each select="tokenize(@target)">
                    <xsl:variable name="reference" select="scdh:process-reference(., $context)"/>
                    <xsl:call-template name="target">
                        <xsl:with-param name="context" select="$context"/>
                        <xsl:with-param name="reference" select="$reference"/>
                    </xsl:call-template>
                </xsl:for-each>
            </oa:hasTarget>
        </rdf:Description>
    </xsl:template>

    <xsl:template name="target">
        <xsl:param name="reference" as="xs:string"/>
        <xsl:param name="context" as="node()"/>
        <xsl:variable name="tokens" as="xs:string*" select="tokenize($reference, '#')"/>
        <xsl:variable name="uri" as="xs:string?" select="$tokens[1]"/>
        <xsl:variable name="fragment" as="xs:string?"
            select="string-join($tokens[position() gt 1], '')"/>
        <xsl:message use-when="system-property('debug') eq 'true'">
            <xsl:text>dereferencing fragment &quot;</xsl:text>
            <xsl:value-of select="$fragment"/>
            <xsl:text>&quot; of </xsl:text>
            <xsl:value-of select="$uri"/>
        </xsl:message>
        <xsl:sequence select="xptr:to-oa-selector($uri, $fragment)"/>
    </xsl:template>

</xsl:stylesheet>
