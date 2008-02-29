package org.wm.xml.transform;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.util.Iterator;
import java.util.Map;

// Error prefix code "XTF0003"

/**
 * Converts a map into a stream of SAX-events.
 *
 * @author Aleksei Valikov
 * @version 1.0
 * @see AbstractXMLReader
 */
public class MapReader extends AbstractXMLReader {
  /**
   * Default map element name.
   */
  private String mapElementName = "map";

  /**
   * Default map element qualified name.
   */
  private String mapElementQName = "map";

  /**
   * Default map element namespace URI.
   */
  private String mapElementNamespaceURI = "";

  /**
   * Default entry element name.
   */
  private String entryElementName = "entry";

  /**
   * Default entry element qualified name.
   */
  private String entryElementQName = "entry";

  /**
   * Default entry element namespaceURI.
   */
  private String entryElementNamespaceURI = "";

  /**
   * Default key attribute name.
   */
  private String keyAttributeName = "key";

  /**
   * Default key attribute qualified name.
   */
  private String keyAttributeQName = "key";

  /**
   * Default key attribute namespace URI.
   */
  private String keyAttributeNamespaceURI = "";

  /**
   * Map to read.
   */
  private Map map;

  /**
   * Sets map for reading.
   *
   * @param map the map to read.
   */
  public void setMap(final Map map) {
    this.map = map;
  }

  /**
   * Sets map element name.
   *
   * @param mapElementName name of the map element.
   */
  public void setMapElementName(final String mapElementName) {
    this.mapElementName = mapElementName;
  }

  /**
   * Sets map element qualified name.
   *
   * @param mapElementQName qualified name of the map element.
   */
  public void setMapElementQName(final String mapElementQName) {
    this.mapElementQName = mapElementQName;
  }

  /**
   * Sets map element namespace URI.
   *
   * @param mapElementNamespaceURI namespace URI of the map element.
   */
  public void setMapElementNamespaceURI(final String mapElementNamespaceURI) {
    this.mapElementNamespaceURI = mapElementNamespaceURI;
  }

  /**
   * Sets entry element name.
   *
   * @param entryElementName name of the entry element.
   */
  public void setEntryElementName(final String entryElementName) {
    this.entryElementName = entryElementName;
  }

  /**
   * Sets entry element qualified name.
   *
   * @param entryElementQName qualified name of the entry element.
   */
  public void setEntryElementQName(final String entryElementQName) {
    this.entryElementQName = entryElementQName;
  }

  /**
   * Sets entry element namespace URI.
   *
   * @param entryElementNamespaceURI namespace URI of the entry element.
   */
  public void setEntryElementNamespaceURI(final String entryElementNamespaceURI) {
    this.entryElementNamespaceURI = entryElementNamespaceURI;
  }

  /**
   * Sets key attribute name.
   *
   * @param keyAttributeName name of the key attribute.
   */
  public void setKeyAttributeName(final String keyAttributeName) {
    this.keyAttributeName = keyAttributeName;
  }

  /**
   * Sets key attribute qualified name.
   *
   * @param keyAttributeQName qualified name of the key attribute.
   */
  public void setKeyAttributeQName(final String keyAttributeQName) {
    this.keyAttributeQName = keyAttributeQName;
  }

  /**
   * Sets key attribute namespaceURI.
   *
   * @param keyAttributeNamespaceURI namespace URI of the key attribute.
   */
  public void setKeyAttributeNamespaceURI(final String keyAttributeNamespaceURI) {
    this.keyAttributeNamespaceURI = keyAttributeNamespaceURI;
  }

  /**
   * Performs the map output. Map is output in the following format:
   * <pre>
   * &lt;<em>mapElement</em>>
   *   &lt;<em>entryElement</em> <em>keyAttribute</em>="<em>key1</em>"><em>entry1 value</em>&lt;/<em>entryElement</em>>
   *   &lt;<em>entryElement</em> <em>keyAttribute</em>="<em>key2</em>"><em>entry2 value</em>&lt;/<em>entryElement</em>>
   *   &lt;<em>entryElement</em> <em>keyAttribute</em>="<em>key3</em>"><em>entry3 value</em>&lt;/<em>entryElement</em>>
   *   &lt;<em>entryElement</em> <em>keyAttribute</em>="<em>key4</em>"><em>entry4 value</em>&lt;/<em>entryElement</em>>
   * &lt;/<em>mapElement</em>>
   * </pre>
   *
   * @param inputSource ignored.
   * @throws SAXException Any SAX exception, possibly wrapping another
   *                      exception.
   * @see AbstractXMLReader#parse(InputSource)
   */
  public void parse(final InputSource inputSource) throws SAXException {
    // Check that map is valid
    if (map == null)
      throw new SAXException("[XTF00030] Map is not set.");
    // Start document
    contentHandler.startDocument();
    // Start map element
    contentHandler.startElement(mapElementNamespaceURI, mapElementName, mapElementQName, noAttributes);
    // Get iterator over map's entries
    final Iterator iterator = map.entrySet().iterator();
    // While there are more entries
    while (iterator.hasNext()) {
      // Get entry
      final Map.Entry entry = (Map.Entry) iterator.next();
      // Get key
      final Object key = entry.getKey();
      // If key is null, skip
      if (key == null)
	continue;
      // Get key
      final String keyString = key.toString();
      // Get value
      final Object value = entry.getValue();
      // Create attributes list
      final AttributesImpl attributes = new AttributesImpl();
      attributes.addAttribute(keyAttributeNamespaceURI, keyAttributeName, keyAttributeQName, "CDATA", keyString);
      // Start entry element
      contentHandler.startElement(entryElementNamespaceURI, entryElementName, entryElementQName, attributes);
      // If value is not null, add value text to element
      if (value != null) {
	final String stringValue = value.toString();
	contentHandler.characters(stringValue.toCharArray(), 0, stringValue.length());
      }
      // End entry element
      contentHandler.endElement(entryElementNamespaceURI, entryElementName, entryElementQName);
    }
    // End map element
    contentHandler.endElement(mapElementNamespaceURI, mapElementName, mapElementQName);
    // End document
    contentHandler.endDocument();
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