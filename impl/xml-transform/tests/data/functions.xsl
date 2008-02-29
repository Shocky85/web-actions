<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:geo="net.sf.saxon.functions.Geo"
  xmlns:resource="net.sf.saxon.functions.ResourceBundleProxy"
  exclude-result-prefixes="geo">

  <xsl:output method="xml" indent="yes"/>
  
  <!-- 
    Root template
  -->
  <xsl:template match="/">
    <functions>
      <!-- apply templates -->
      <xsl:apply-templates/>
    </functions>        
  </xsl:template>

  <!--
    Execute deg to rad method
  -->
  <xsl:template match="deg2rad">
    <rad><xsl:value-of select="geo:deg2rad(deg)"/></rad>
  </xsl:template>
  
  <!--
    Execute rad to deg method
  -->
  <xsl:template match="rad2deg">
    <deg><xsl:value-of select="geo:rad2deg(rad)"/></deg>
  </xsl:template>

  <!--
    Call resource bundle and get resurce from it
  -->
  <xsl:template match="resource">
    <resource><xsl:value-of select="resource:getResourceString(bundle, locale, key)"/></resource>
  </xsl:template>

  <!--
    Call resource bundle and get resurce from it
  -->
  <xsl:template match="resource-with-param">
    <resource><xsl:value-of select="resource:getResourceString(bundle, locale, key, param1, param2, '3', '4', '5', '6', '7', '8', '9', '10')"/></resource>
  </xsl:template>

</xsl:stylesheet>
