package net.sf.saxon.functions;

import junit.framework.TestCase;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.Source;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Test XSLT transformation with edditional functions
 *
 * @author Ivan
 * @version 0.1
 * @since 21-Apr-2006 : 8:11:54 PM
 */
public class FunctionsXSLTest extends TestCase {

  /** Transfromer factory */
  TransformerFactory factory = TransformerFactory.newInstance();

  protected void setUp() throws Exception {
//    assertEquals("org.wm.xml.transform.CachingTransformerFactory", factory.getClass().getName());
    super.setUp();
  }

  public void testTransformation() {
    try {
      FileInputStream xslIn = new FileInputStream(new File(System.getProperty("user.dir"),"/test_data/functions.xsl"));
      FileInputStream xmlIn = new FileInputStream(new File(System.getProperty("user.dir"),"/test_data/functions-data.xml"));
      FileOutputStream resOut = new FileOutputStream(new File(System.getProperty("user.dir"),"/test_data/functions-result.xml"));
      Result reslt = new StreamResult(resOut);
      Source src = new StreamSource(xslIn);
      Transformer transformer = factory.newTransformer(src);
      transformer.transform(new StreamSource(xmlIn), reslt);
    } catch (Exception e) {
      e.printStackTrace();
      assertTrue(false);
    }
  }

}
