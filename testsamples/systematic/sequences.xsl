<?xml version="1.0" encoding="UTF-8"?>
<!--

Demo XSL script for processing TEI XPointers.

USAGE EXAMPLE:

saxon/target/bin/xslt.sh -config:saxon/saxon-config.xml -xsl:testsamples/systematic/sequences.xsl -it pointer="rn04.txt"
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xptr="http://www.tei-c.org/ns/xptr"
    xmlns:scdh="http://scdh.wwu.de/xslt#" exclude-result-prefixes="#all"
    xpath-default-namespace="http://www.tei-c.org/ns/1.0" version="2.0">

   <xsl:output omit-xml-declaration="yes"/>

   <xsl:param name="pointer" as="xs:string" required="yes"/>

   <xsl:param
       name="xml-resource" as="xs:string"
       select="resolve-uri('../sats/sats.xml', static-base-uri())"/>

   <xsl:template name="xsl:initial-template">
      <xsl:variable name="ptr" select="unparsed-text($pointer)"/>
      <xsl:message use-when="system-property('debug') eq 'true'">
	 <xsl:text>processing XPointer </xsl:text>
	 <xsl:value-of select="$ptr"/>
	 <xsl:text> on </xsl:text>
	 <xsl:value-of select="$xml-resource"/>
      </xsl:message>
      <!--xsl:sequence select="document($xml-resource)"/-->
      <xsl:sequence select="xptr:get-sequence($xml-resource, $ptr)"/>
      <xsl:text>&#xa;</xsl:text>
   </xsl:template>

</xsl:stylesheet>
