package org.wm.xml.transform;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpRecoverableException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import java.io.IOException;
import java.io.InputStream;

/**
 * URI resolver for HTML documents. This JTidy-based implementation allows
 * transformers accessing HTML documents otherwise invalid in XML syntax.
 */
public class JTidyResolver implements URIResolver {
  /**
   * Class logger.
   */
  protected static final Log logger = LogFactory.getLog(JTidyResolver.class);

  /**
   * Scheme identifier.
   */
  public static final String SCHEME = "jtidy";

  /**
   * Resolver instance.
   */
  public static final JTidyResolver resolver = new JTidyResolver();

  /**
   * Returns resolver instance.
   *
   * @return Static resolver instance.
   */
  public static JTidyResolver getResolver() {
    return resolver;
  }

  /**
   * Resolves <code>href</code> relatively to <code>base</code> into a source.
   *
   * @param href target URI, must be absolute.
   * @param base base URI, ignored.
   * @return Source for the transformer, or <code>null</code> if scheme does
   *         not match, or processing errors occured.
   * @throws TransformerException if this source could not be resolved.
   */
  public Source resolve(final String href, final String base)
    throws TransformerException {
    // If URI does not match the SCHEME, return null
    if (!href.toLowerCase().startsWith(SCHEME))
      return null;

    // Construct an HTTP address
    final String address = "http" + href.substring(SCHEME.length());

    // Create a client
    final HttpClient client = new HttpClient();
    // Check proxy configuration
    try {
      if ("true".equals(System.getProperty("proxySet"))) {
	client.getHostConfiguration().setProxy(System.getProperty("proxyHost"),
	                                       Integer.parseInt(System.getProperty("proxyPort")));
      }
    }
    catch (Exception ex) {
      logger.error("Error checking proxy configuration.", ex);
    }

    // Create a method
    final GetMethod method = new GetMethod(address);

    int statusCode = -1;
    final int attempt = 0;
    // We will retry up to 3 times.
    while (statusCode == -1 && attempt < 3) {
      try {
	// Execute the method.
	statusCode = client.executeMethod(method);
      }
      catch (HttpRecoverableException hrex) {
	logger.warn("A recoverable exception occurred, retrying." +
	  hrex.getMessage());
      }
      catch (IOException ioex) {
	logger.error("Failed to retrieve content.", ioex);
	return VoidSource.voidSource;
      }
    }
    // Check that we didn't run out of retries.
    if (statusCode == -1) {
      logger.error("Failed to recover from exception.");
      return VoidSource.voidSource;
    }

    // Read the response body.
    final byte[] responseBody = method.getResponseBody();
    method.releaseConnection();

    // Release the connection.
    final String encoding = method.getResponseCharSet();
    String content;
    try {
      if (null != encoding)
	content = new String(responseBody, encoding);
      else
	content = new String(responseBody);
    }
    catch (Exception ex) {
      content = "";
    }

    // Create Tidy instance
    final Tidy tidy = new Tidy();
    try {
      // Parse HTML into DOM
      final InputStream is = new java.io.ByteArrayInputStream(content.getBytes("UTF-8"));
      tidy.setCharEncoding(org.w3c.tidy.Configuration.UTF8);
      final Document document = tidy.parseDOM(is, null);
      // Return a DOMSource based on the document.
      final DOMSource domSource = new DOMSource(document);
      domSource.setSystemId(address);
      return domSource;
    }
    catch (IOException e) {
      throw new TransformerException(
	"Error parsing HTML at [" + address + "].", e);
    }
  }
}

/*
 * The contents of this file are subject to the Mozilla Public License Version 1.0 (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * See the License for the specific language governing rights and limitations under the License.
 *
 * The Original Code is: all this file.
 *
 * The Initial Developer of the Original Code is
 * Aleksei Valikov of Forschungszentrum Informatik (valikov@fzi.de).
 *
 * Portions created by (your name) are Copyright (C) (your legal entity). All Rights Reserved.
 *
 * Contributor(s): none.
 */