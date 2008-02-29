package org.wm.xml.transform;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.io.IOException;
import java.util.Map;

/**
 * Serializes entry iterator (like one returned by
 * <code>Map.entrySet().iterator()</code>) into a stream of SAX events.
 *
 * @author Aleksei Valikov
 */
public class EntryIteratorReader extends IteratorReader {
  /**
   * Default key attribute name.
   */
  protected String keyAttributeName = "name";

  /**
   * Sets key attribute name.
   *
   * @param keyAttributeName name of the key attribute.
   */
  public void setKeyAttributeName(final String keyAttributeName) {
    this.keyAttributeName = keyAttributeName;
  }

  /**
   * Reads an entry iterator. Iterator contents are read and output in the
   * followingformat:
   * <pre>
   * &lt;<em>items</em>>
   *   &lt;<em>item key=&quot;item1 key&quot;</em>><em>item1 value</em>&lt;/<em>item</em>>
   *   &lt;<em>item key=&quot;item2 key&quot;</em>><em>item2 value</em>&lt;/<em>item</em>>
   *   &lt;<em>item key=&quot;item3 key&quot;</em>><em>item3 value</em>&lt;/<em>item</em>>
   *   &lt;<em>item key=&quot;item4 key&quot;</em>><em>item4 value</em>&lt;/<em>item</em>>
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
    contentHandler.startElement("", itemsElementName, itemsElementName, noAttributes);
    // While there are more items
    while (iterator.hasNext()) {
      Object next = null;
      final Map.Entry entry;
      final String key;
      final String value;
      try {
	next = iterator.next();
	entry = (Map.Entry) next;
	key = (String) entry.getKey();
	value = (String) entry.getValue();
      }
      catch (Exception e) {
	throw new SAXException("[XTF00081] Iterator returned invalid entry [" + next + "].", e);
      }
      if (key == null)
	throw new SAXException("[XTF00082] Iterator returned entry [" + next + "] with empty key.");

      final AttributesImpl attributes = new AttributesImpl();
      attributes.addAttribute("", keyAttributeName, keyAttributeName, "CDATA", key);

      // Start item element
      contentHandler.startElement("", itemElementName, itemElementName, attributes);
      // If value is not null, add value text to element
      if (value != null) {
	final String stringValue = value.toString();
	contentHandler.characters(stringValue.toCharArray(), 0, stringValue.length());
      }
      // End item element
      contentHandler.endElement("", itemElementName, itemElementName);
    }
    // End items element
    contentHandler.endElement("", itemsElementName, itemsElementName);
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