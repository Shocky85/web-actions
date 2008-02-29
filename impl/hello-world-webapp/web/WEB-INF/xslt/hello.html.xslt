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

        <h3>Hello World</h3>

        <form method="post">
          Name: <input type="text" name="sayHello~.name"/> <input type="submit" value="Say Hello"/>
        </form>

        <br/>
        Request:<br/>
        <textarea cols="35" rows="5">
          <xsl:copy-of select="."/>
        </textarea>

        Name:<br/>
        <textarea cols="35" rows="5">
          <xsl:copy-of select="$name"/>
        </textarea>

      </body>
    </html>

  </xsl:template>

</xsl:stylesheet>