package org.wm.xml.transform;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

// Error code prefix "XTF0007"

/**
 * XML reader that returns single <code>&lt;void/&gt;</code> element.
 *
 * @author Aleksei Valikov
 * @version 1.0
 * @see AbstractXMLReader
 */
public class VoidXMLReader extends AbstractXMLReader {
  /**
   * This method simply orders <code>VoidReader</code> to return four SAX
   * events via its <code>ContentHandler</code>. This implementation
   * ignores <code>inputSource</code>.
   *
   * @param input ignored;
   * @throws SAXException Any SAX exception, possibly wrapping another
   *                      exception.
   */
  public void parse(final InputSource input) throws SAXException {
    // Start document
    contentHandler.startDocument();
    // Output <void/> element
    contentHandler.startElement("", "void", "void", noAttributes);
    contentHandler.endElement("", "void", "void");
    // End document.
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