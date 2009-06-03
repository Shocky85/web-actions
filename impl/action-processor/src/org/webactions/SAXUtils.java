package org.webactions;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;
import org.wm.xml.transform.AbstractXMLReader;

import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.io.IOException;

/**
 * Webactions Servlet utilities.
 *
 * @author Ivan Latysh <ivan@yourmail.com>
 * @version 0.1
 * @since 30-Jan-2008 3:11:10 PM
 */
public class SAXUtils {

  /**
   * Return HttpRequest XML source
   *
   * @param request HttpServlet request
   * @return Source HttpRequest SAX source
   */
  public static Source getHttpRequestSource(final HttpServletRequest request){
    return new SAXSource(new AbstractXMLReader() {
      public void parse(InputSource input) throws IOException, SAXException {
        contentHandler.startDocument();
        toHttpRequestSAX(contentHandler, request);
        contentHandler.endDocument();
      }
    }, new InputSource());
  }

  /**
   * Construct HttpRequest SAX Source
   *
   * @param contentHandler ContextHandler object
   * @param request HttpServlet request to construct SAX source of
   * @throws org.xml.sax.SAXException when unable to construct SAX Source
   */
  public static void toHttpRequestSAX(ContentHandler contentHandler, HttpServletRequest request) throws SAXException {
    final Attributes na = AbstractXMLReader.noAttributes;
    contentHandler.startElement("", "http-request", "http-request", na);
    if (null!=request) {
      // include header values
      contentHandler.startElement("", "header", "header", na);
      Enumeration _header = request.getHeaderNames();
      while (_header.hasMoreElements()) {
        contentHandler.startElement("", "parameter", "parameter", na);
        String hName = String.valueOf(_header.nextElement());
        Enumeration hValue = request.getHeaders(hName);
        contentHandler.startElement("", "name", "name", na);
        if (null != hName) contentHandler.characters(hName.toLowerCase().toCharArray(), 0, hName.length());
        contentHandler.endElement("", "name", "name");
        while(hValue.hasMoreElements()) {
          String _v = String.valueOf(hValue.nextElement());
          contentHandler.startElement("", "value", "value", na);
          if (null != _v) contentHandler.characters(_v.toCharArray(), 0, _v.length());
          contentHandler.endElement("", "value", "value");
        }
        contentHandler.endElement("", "parameter", "parameter");
      }
      contentHandler.endElement("", "header", "header");
      contentHandler.startElement("", "parameters", "parameters", na);
      Enumeration _params = request.getParameterNames();
      while (_params.hasMoreElements()) {
        contentHandler.startElement("", "parameter", "parameter", na);
        String pName = String.valueOf(_params.nextElement());
        String[] pValues = request.getParameterValues(pName);
        contentHandler.startElement("", "name", "name", na);
        if (null != pName) contentHandler.characters(pName.toLowerCase().toCharArray(), 0, pName.length());
        contentHandler.endElement("", "name", "name");
        for (String _v : pValues) {
          contentHandler.startElement("", "value", "value", na);
          if (null != _v) contentHandler.characters(_v.toCharArray(), 0, _v.length());
          contentHandler.endElement("", "value", "value");
        }
        contentHandler.endElement("", "parameter", "parameter");
      }
      contentHandler.endElement("", "parameters", "parameters");
      contentHandler.startElement("", "url", "url", na);
      contentHandler.startElement("", "full", "full", na);
      if (null != request.getRequestURL().toString()) contentHandler.characters(request.getRequestURL().toString().toCharArray(), 0, request.getRequestURL().toString().length());
      contentHandler.endElement("", "full", "full");
      contentHandler.endElement("", "url", "url");
    }
    contentHandler.endElement("", "http-request", "http-request");
  }

}