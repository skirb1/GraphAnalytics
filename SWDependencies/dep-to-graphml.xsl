<?xml version="1.0"?>

<xsl:stylesheet version="2.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:functx="http://www.functx.com"
	xmlns="http://graphml.graphdrawing.org/xmlns"
	exclude-result-prefixes="xsl xs functx">
      
<xsl:output name="XML" method="xml" encoding="UTF-8" indent="yes"/>

<xsl:function name="functx:escape-for-regex" as="xs:string">
	<xsl:param name="arg" as="xs:string?"/>
	<xsl:sequence select=" replace($arg, '(\.|\[|\]|\\|\||\-|\^|\$|\?|\*|\+|\{|\}|\(|\))','\\$1') "/>
</xsl:function>

<xsl:function name="functx:substring-before-last" as="xs:string">
	<xsl:param name="arg" as="xs:string?"/>
	<xsl:param name="delim" as="xs:string"/>
	<xsl:sequence select=" if (matches($arg, functx:escape-for-regex($delim))) then replace($arg, concat('^(.*)', functx:escape-for-regex($delim),'.*'), '$1') else '' "/>
</xsl:function>

<xsl:function name="functx:substring-after-last" as="xs:string">
	<xsl:param name="arg" as="xs:string?"/>
	<xsl:param name="delim" as="xs:string"/>
	<xsl:sequence select=" replace ($arg,concat('^.*',functx:escape-for-regex($delim)),'') "/>
</xsl:function>

<xsl:template match="/">
<xsl:result-document format="XML" >
<graphml>
  <graph id="G" edgedefault="directed">
	<xsl:apply-templates select="//file"/>
      <xsl:apply-templates select="//file/dependency"/>
  </graph>
</graphml>
</xsl:result-document>
</xsl:template>

<xsl:template match="file[contains(@path, 'src/')]">
	<node id="{./@path}">
		<xsl:variable name="path" select="substring-after(./@path, 'src/')"/>
		<xsl:variable name="file" select="functx:substring-after-last($path, '/')"/>
		<xsl:variable name="package" select="replace(functx:substring-before-last($path, '/'), '/', '.')"/>
		<data key="package"><xsl:value-of select="$package"/></data>
		<data key="file"><xsl:value-of select="$file"/></data>
	</node>
</xsl:template>

<xsl:template match="file/dependency">
      <edge source="{../@path}" target="{./@path}"/>
</xsl:template>

</xsl:stylesheet>