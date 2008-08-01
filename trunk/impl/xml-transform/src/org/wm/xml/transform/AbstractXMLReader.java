package org.wm.xml.transform;

import org.xml.sax.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Default template for an XML reader. To write a new <code>XMLReader</code>,
 * simply inherit from this class and implement <code>parse</code> method.
 *
 * @author Aleksei Valikov
 * @author Ivan Latysh
 * @see org.xml.sax.XMLReader
 */
public abstract class AbstractXMLReader implements XMLReader {
// Error code prefix "XTF0000"

  /** Namespaces feature URI. */
  public static final String NAMESPACES_FEATURE = "http://xml.org/sax/features/namespaces";
  /** Namespace-prefixes feature URI. */
  public static final String NAMESPACE_PREFIXES_FEATURE = "http://xml.org/sax/features/namespace-prefixes";

  /**
   * Empty attributes list.
   */
  public static final Attributes noAttributes = new NoAttributes();

  /**
   * Content handler field.
   */
  protected ContentHandler contentHandler;

  /**
   * DTD handler field.
   */
  protected DTDHandler dtdHandler;

  /**
   * Entity resolver field.
   */
  protected EntityResolver entityResolver;

  /**
   * Error handler field.
   */
  protected ErrorHandler errorHandler;

  /**
   * Namespaces feature flag. By default true.
   */
  protected boolean fNamespaces = true;

  /**
   * Namespace prefixes feature flag. By default false.
   */
  protected boolean fNamespacePrefixes = false;

  /** Properties map */
  protected Map<String, Object> properties = new HashMap<String, Object>();

  /**
   * Sets the content handler.
   *
   * @param contentHandler content handler to use.
   */
  public void setContentHandler(final ContentHandler contentHandler) {
    this.contentHandler = contentHandler;
  }

  /**
   * Returns current content handler.
   *
   * @return Current content handler.
   */
  public ContentHandler getContentHandler() {
    return contentHandler;
  }

  /**
   * Sets DTD hanlder.
   *
   * @param dtdHandler DTD handler to use.
   */
  public void setDTDHandler(final DTDHandler dtdHandler) {
    this.dtdHandler = dtdHandler;
  }

  /**
   * Returns current DTD handler.
   *
   * @return Current DTD handler.
   */
  public DTDHandler getDTDHandler() {
    return dtdHandler;
  }

  /**
   * Sets the entity resolver.
   *
   * @param entityResolver Entity resolver to use.
   */
  public void setEntityResolver(final EntityResolver entityResolver) {
    this.entityResolver = entityResolver;
  }

  /**
   * Returns current entity resolver.
   *
   * @return Current entity resolver.
   */
  public EntityResolver getEntityResolver() {
    return entityResolver;
  }

  /**
   * Sets the error handler.
   *
   * @param errorHandler Error handler to use.
   */
  public void setErrorHandler(final ErrorHandler errorHandler) {
    this.errorHandler = errorHandler;
  }

  /**
   * Returns current error handler.
   *
   * @return Current error handler.
   */
  public ErrorHandler getErrorHandler() {
    return errorHandler;
  }

  /**
   * Sets value of the property with given name. By default, no properties are
   * recognized.
   *
   * @param name  property name.
   * @param value property value.
   * @throws SAXNotRecognizedException When the XML reader does not recognize
   *                                   the property name. Thrown by default.
   * @throws SAXNotSupportedException  When the XMLReader recognizes the
   *                                   property name but cannot set the requested value.
   */
  public void setProperty(final String name, final Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
    // add property to the map
    properties.put(name, value);
  }

  /**
   * Returns value of the property with given name. By default, no properties
   * are recognized.
   *
   * @param name property name.
   * @return Value of the property (by default not returned).
   * @throws SAXNotRecognizedException When the XMLReader does not recognize
   *                                   the property name. Thrown by default.
   * @throws SAXNotSupportedException  When the XMLReader recognizes the
   *                                   property name but cannot determine its value at this time.
   */
  public Object getProperty(final String name) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (properties.containsKey(name)) return properties.get(name);
    // Throw the exception
    throw new SAXNotRecognizedException("[XTF00001] Property " + name + " is not recognized.");
  }

  /**
   * Sets feature by its name. By default, supports standard namespaces and
   * namespace prefixes features.
   *
   * @param name  feature name.
   * @param value feature value.
   * @throws SAXNotRecognizedException When the XMLReader does not recognize
   *                                   the feature name.
   * @throws SAXNotSupportedException  When the XMLReader recognizes the feature
   *                                   name but cannot set the requested value.
   */
  public void setFeature(final String name, final boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
    // Check, if feature is namespace feature
    if (name.equals(NAMESPACES_FEATURE)) {
      fNamespaces = value;
      return;
    }

    // Check, if feature is namespace prefixes feature
    if (name.equals(NAMESPACE_PREFIXES_FEATURE)) {
      fNamespacePrefixes = value;
      return;
    }

    // Otherwise feature is not recognized
    throw new SAXNotRecognizedException("[XTF00002] Feature " + name + " is not recognized.");
  }

  /**
   * Returns feature value by its name. By default, support standard namespaces
   * and namespace prefixes feature.
   *
   * @param name feature name.
   * @return Value of the feature (by default always <code>false</code>).
   * @throws SAXNotRecognizedException When the XMLReader does not recognize
   *                                   the feature name.
   * @throws SAXNotSupportedException  When the XMLReader recognizes the feature
   *                                   name but cannot determine its value at this time.
   */
  public boolean getFeature(final String name)
          throws SAXNotRecognizedException, SAXNotSupportedException {
    // Check, if feature is namespace feature
    if (name.equals(NAMESPACES_FEATURE))
      return fNamespaces;

    // Check, if feature is namespace prefixes feature
    if (name.equals(NAMESPACE_PREFIXES_FEATURE))
      return fNamespacePrefixes;

    // Otherwise, feature is not recognized
    throw new SAXNotRecognizedException(
            "[XTF00003] Feature " + name + " is not recognized.");
  }

  /**
   * Parses a document from a system identifier. By default, parses a document
   * from the input source constructed from <code>systemId</code>.
   *
   * @param systemId system identifier of the document.
   * @throws SAXException Any SAX exception, possibly wrapping another
   *                      exception.
   * @throws IOException  An IO exception from the parser, possibly from a byte
   *                      stream or character stream supplied by the application.
   */
  public void parse(final String systemId) throws IOException, SAXException {
    // Parse an input source constructed from systemId
    parse(new InputSource(systemId));
  }

  /**
   * Parses a document from the input source. Override this abstract method to
   * obtain a functioning SAX-driver.
   *
   * @param input input source to parse document from.
   * @throws SAXException Any SAX exception, possibly wrapping another
   *                      exception.
   * @throws IOException  An IO exception from the parser, possibly from a byte
   *                      stream or character stream supplied by the application.
   */
  public abstract void parse(InputSource input)
          throws IOException, SAXException;

  /**
   * Implementation of an empty attributes list.
   */
  public static final class NoAttributes implements Attributes

  {
    /**
     * Always returns <code>-1</code>.
     *
     * @param qName ignored.
     * @return <code>-1</code>.
     */
    public int getIndex(final String qName) {
      return -1;
    }

    /**
     * Always returns <code>-1</code>.
     *
     * @param uri       ignored.
     * @param localPart ignored.
     * @return <code>-1</code>.
     */
    public int getIndex(final String uri, final String localPart) {
      return -1;
    }

    /**
     * Always returns <code>0</code>.
     *
     * @return <code>0</code>
     */
    public int getLength() {
      return 0;
    }

    /**
     * Always returns <code>null</code>.
     *
     * @param index ignored.
     * @return <code>null</code>
     */
    public String getLocalName(final int index) {
      return null;
    }

    /**
     * Always returns <code>null</code>.
     *
     * @param index ignored.
     * @return <code>null</code>
     */
    public String getQName(final int index) {
      return null;
    }

    /**
     * Always returns <code>null</code>.
     *
     * @param index ignored.
     * @return <code>null</code>
     */
    public String getType(final int index) {
      return null;
    }

    /**
     * Always returns <code>null</code>.
     *
     * @param uri       ignored.
     * @param localName ignored.
     * @return <code>null</code>
     */
    public String getType(final String uri, final String localName) {
      return null;
    }

    /**
     * Always returns <code>null</code>.
     *
     * @param qName ignored.
     * @return <code>null</code>
     */
    public String getType(final String qName) {
      return null;
    }

    /**
     * Always returns <code>null</code>.
     *
     * @param index ignored.
     * @return <code>null</code>
     */
    public String getURI(final int index) {
      return null;
    }

    /**
     * Always returns <code>null</code>.
     *
     * @param index ignored.
     * @return <code>null</code>
     */
    public String getValue(final int index) {
      return null;
    }

    /**
     * Always returns <code>null</code>.
     *
     * @param uri       ignored.
     * @param localName ignored.
     * @return <code>null</code>
     */
    public String getValue(final String uri, final String localName) {
      return null;
    }

    /**
     * Always returns <code>null</code>.
     *
     * @param qName ignored.
     * @return <code>null</code>
     */
    public String getValue(final String qName) {
      return null;
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