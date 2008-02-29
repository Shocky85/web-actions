package org.wm.xml.transform;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.Iterator;

// Error code prefix "XTF0008"

/**
 * Reads iterator into a stream of SAX events. This reader may be configured to
 * use custom <code>items</code> (root) and <code>item</code> (per iterator's
 * item) element names.
 *
 * @author Aleksei Valikov
 * @version 1.0.
 */
public class IteratorReader extends AbstractXMLReader {
  /**
   * Iterator to read.
   */
  protected Iterator iterator;

  /**
   * Items element name.
   */
  protected String itemsElementName = "items";

  /**
   * Items element qualified name.
   */
  protected String itemsElementQName = "items";

  /**
   * Items element namespace URI.
   */
  protected String itemsElementNamespaceURI = "";

  /**
   * Item element name.
   */
  protected String itemElementName = "item";

  /**
   * Item element qualified name.
   */
  protected String itemElementQName = "item";

  /**
   * Item element namespace URI.
   */
  protected String itemElementNamespaceURI = "";

  /**
   * Sets iterator for reading. Iterator must be set before <code>parser</code>
   * method is invoked.
   *
   * @param iterator the iterator to read.
   */
  public void setIterator(final Iterator iterator) {
    this.iterator = iterator;
  }

  /**
   * Sets items element name.
   *
   * @param itemsElementName name of the items element.
   */
  public void setItemsElementName(final String itemsElementName) {
    this.itemsElementName = itemsElementName;
  }

  /**
   * Sets items element qualified name.
   *
   * @param itemsElementQName qualified name of the items element.
   */
  public void setItemsElementQName(final String itemsElementQName) {
    this.itemsElementQName = itemsElementQName;
  }

  /**
   * Sets items element namespace URI.
   *
   * @param itemsElementNamespaceURI namespace URI of the items element.
   */
  public void setItemsElementNamespaceURI(final String itemsElementNamespaceURI) {
    this.itemsElementNamespaceURI = itemsElementNamespaceURI;
  }

  /**
   * Sets item element name.
   *
   * @param itemElementName name of the item element.
   */
  public void setItemElementName(final String itemElementName) {
    this.itemElementName = itemElementName;
  }

  /**
   * Sets item element qualified name.
   *
   * @param itemElementQName qualified name of the item element.
   */
  public void setItemElementQName(final String itemElementQName) {
    this.itemElementQName = itemElementQName;
  }

  /**
   * Sets item element namespace URI.
   *
   * @param itemElementNamespaceURI namespace URI of the item element.
   */
  public void setItemElementNamespaceURI(final String itemElementNamespaceURI) {
    this.itemElementNamespaceURI = itemElementNamespaceURI;
  }

  /**
   * Reads an iterator. Iterator contents are read and output in the following
   * format:
   * <pre>
   * &lt;<em>items</em>>
   *   &lt;<em>item</em>><em>item1 value</em>&lt;/<em>item</em>>
   *   &lt;<em>item</em>><em>item2 value</em>&lt;/<em>item</em>>
   *   &lt;<em>item</em>><em>item3 value</em>&lt;/<em>item</em>>
   *   &lt;<em>item</em>><em>item4 value</em>&lt;/<em>item</em>>
   * &lt;/<em>items</em>>
   * </pre>
   * Items are read as objects and converted to strings using
   * <code>toString()</code>. <code>null</code> values are not output (an empty
   * element is output).
   *
   * @param input ignored.
   * @throws IOException  in case of IO problems on parsing.
   * @throws SAXException in case of SAX problems on parsing.
   */
  public void parse(final InputSource input) throws IOException, SAXException {
    // Check that map is valid
    if (iterator == null)
      throw new SAXException("[XTF00080] Iterator is not set.");
    // Start document
    contentHandler.startDocument();
    // Start items element
    contentHandler.startElement(
      itemsElementNamespaceURI,
      itemsElementName,
      itemsElementQName,
      noAttributes);
    // While there are more items
    while (iterator.hasNext()) {
      final Object value = iterator.next();
      // Start item element
      contentHandler.startElement(
	itemElementNamespaceURI,
        itemElementName,
        itemElementQName,
        noAttributes);
      // If value is not null, add value text to element
      if (value != null) {
	final String stringValue = value.toString();
	contentHandler.characters(stringValue.toCharArray(), 0, stringValue.length());
      }
      // End item element
      contentHandler.endElement(itemElementNamespaceURI, itemElementName, itemElementQName);
    }
    // End items element
    contentHandler.endElement(itemElementNamespaceURI, itemsElementName, itemsElementQName);
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