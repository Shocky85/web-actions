<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output method="html" encoding="ISO-8859-1" indent="yes"/>

  <!-- name parameters -->
  <xsl:param name="name"/>
  <!-- from parameters -->
  <xsl:param name="from"/>

  <!--
    Root template
  -->
  <xsl:template match="/">
    <html>
      <head/>
      <body>
        
        <div style="background: yellow; margin:10px; padding-left: 15px; border: solid black;">
        <h2> Hello <xsl:value-of select="if ($name) then $name else 'world'"/> from <xsl:value-of select="if ($from) then $from else 'web-actions framework'"/> !</h2>
        </div>

        <p>
          <h3>Java Action</h3>

          <form method="post">
            Name: <input type="text" name="sayJavaHello~.name"/> &#xA0; <input type="submit" value="Say Hello"/>
          </form>
        </p>

        <p>
          <h3>JavaScript Action</h3>
          <form method="post">
            Name: <input type="text" name="sayJavaScriptHello~.name"/> &#xA0; <input type="submit" value="Say Hello"/>
          </form>
        </p>

        <!-- uncomment to see request XML
        <br/>
        Request XML: <br/>
        <textarea style="width: 100%" rows="10">
          <xsl:copy-of select="."/>
        </textarea><br/>
        -->

      </body>
    </html>

  </xsl:template>

</xsl:stylesheet>