<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output method="html" encoding="ISO-8859-1" indent="yes"/>

  <!-- name parameter -->
  <xsl:param name="name"/>

  <!--
    Root template
  -->
  <xsl:template match="/">
    <html>
      <head>

      </head>
      <body>

        <h3>Index XSLT Page</h3>

        <a href="hello.html">Hello Page</a>
        <br/>
        Request:<br/>
        <textarea cols="35" rows="5">
          <xsl:copy-of select="."/>
        </textarea>

      </body>
    </html>

  </xsl:template>

</xsl:stylesheet>