package org.springframework.web.servlet.view;

import org.xml.sax.*;
import org.wm.xml.transform.AbstractXMLReader;
import org.springframework.web.servlet.WebactionsUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.sax.SAXSource;
import java.util.Map;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.lang.reflect.Method;

/**
 * XSLT based view
 *
 * @author Ivan Latysh <ivan@yourmail.com>
 * @version 0.1
 * @since 30-Jan-2008 2:50:58 PM
 */
public class XsltView extends InternalResourceView {

  /** Transformert factory */
  protected static final TransformerFactory factory = TransformerFactory.newInstance();

  public XsltView() {
    logger.info("Using {"+factory.getClass().getName()+"} transformer factory.");
  }

  public XsltView(String url) {
    super(url);
    logger.info("Using {"+factory.getClass().getName()+"} transformer factory.");
  }

  public XsltView(String url, boolean alwaysInclude) {
    super(url, alwaysInclude);
    logger.info("Using {"+factory.getClass().getName()+"} transformer factory.");
  }

  /**
   * Render the view
   *
   * @param model model map
   * @param request request
   * @param response response
   * @throws Exception when error occured
   */
  @Override
  protected void renderMergedOutputModel(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {
    InputStream templateIn = null;

    try {
      // get template file
      final String fileName = request.getSession().getServletContext().getRealPath(getRequestedResourcePath(request));
      File templateFile = new File(fileName);
      // sanity checks
      if (!templateFile.exists()) throw new IOException("File {"+fileName+"} does not exist.");
      if (!templateFile.isFile()) throw new IOException("File {"+fileName+"} is not a file");
      // Construct Template Source
      templateIn = new FileInputStream(templateFile);
      Source templateSource = new StreamSource(templateIn);

      // get the new transformer
      Transformer transformer = null;
      synchronized (factory) {
        transformer = factory.newTransformer(templateSource);
      }

      // prepare resuilt
      Result result = new StreamResult(response.getOutputStream());

      // prepare transformation parameters
      for (Object entryObj: model.entrySet()) {
        if (entryObj instanceof Map.Entry) {
          Map.Entry entry = (Map.Entry) entryObj;
          String entryKey = String.valueOf(entry.getKey());
          Object entryValue = entry.getValue();
          if (entryValue instanceof Source) {
            // set parameter
            transformer.setParameter(entryKey, entryValue);
          } else {
            // construct SAX Source from the object
            transformer.setParameter(entryKey, getObjectSource(entryKey, entryValue));
          }
        }
      }

      // perform transformation
      transformer.transform(getRequestSource(request), result);
      // flush the buffer
      response.flushBuffer();

    } catch (ServletException e) {
      e.printStackTrace();
    } catch (TransformerConfigurationException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      // close streams
      if (null!=templateIn) templateIn.close();
    }


  }

  /**
   * Return requested resource relative path
   *
   * @param request HttpServletRequest
   * @return requested resource relative path
   * @throws ServletException when error occured
   */
  protected String getRequestedResourcePath(HttpServletRequest request) throws ServletException {
    String path = getUrl();
    String uri = request.getRequestURI();
    if (path.startsWith("/") ? uri.equals(path) : uri.equals(StringUtils.applyRelativePath(uri, path))) {
      throw new ServletException("Invalid view path [" + path + "]: would dispatch back to the current handler path [" + uri + "] again. Check your ViewResolver setup!");
    }
    return path;
  }

  /**
   * Prepare HttpServletRequest SAX Source
   *
   * @param request HttpServletRequest
   * @return SAX Source
   */
  protected Source getRequestSource(final HttpServletRequest request) {
    return new SAXSource(new AbstractXMLReader() {
      public void parse(InputSource input) throws IOException, SAXException {
        final Attributes na = AbstractXMLReader.noAttributes;
        contentHandler.startDocument();
        // contruct Http Servlet Request source
        WebactionsUtils.toHttpRequestSAX(contentHandler, request);
        contentHandler.endDocument();
      }
    }, new InputSource());
  }

  /**
   * Return Object XML source
   *
   * @param name object name
   * @param object Object to contruct SAX from
   * @return Source Object SAX source
   */
  public Source getObjectSource(final String name, final Object object){
    return new SAXSource(new AbstractXMLReader() {
      public void parse(InputSource input) throws IOException, SAXException {
        contentHandler.startDocument();
        toObjectSAX(contentHandler, object, name);
        contentHandler.endDocument();
      }
    }, new InputSource());
  }

  /**
   * Construct Object SAX event queue
   *
   * @param contentHandler ContextHandler object
   * @param object object to construct SAX from
   * @param name object name
   * @throws org.xml.sax.SAXException when unable to construct SAX Source
   */
  public void toObjectSAX(ContentHandler contentHandler, Object object, String name) throws SAXException {
    final Attributes na = AbstractXMLReader.noAttributes;
    // see if we can get SAX Source from the object
    Method getSourceMethod = null;
    String methodName = "getSource";
    try {
      getSourceMethod = object.getClass().getMethod(methodName, ContentHandler.class);
    } catch (Exception ex) {
      logger.debug("No `"+methodName+"` method.");
    }
    if (null!=getSourceMethod) {
      // invoke getSource method
      try {
        getSourceMethod.invoke(object, contentHandler);
      } catch (Exception e) {
        logger.error("Unable to invoke "+methodName+" method.", e);
      }
    } else  {
      String objString = String.valueOf(object);
      contentHandler.startElement("", name, name, na);
      if (null != objString) contentHandler.characters(objString.toCharArray(), 0,objString.length());
      contentHandler.endElement("", name, name);
    }
  }

}