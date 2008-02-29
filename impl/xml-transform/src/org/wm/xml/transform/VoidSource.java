package org.wm.xml.transform;

import org.xml.sax.InputSource;

import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;

// Error code prefix "XTF0006"

/**
 * <code>SAXSource</code> compatible source of single <code>&lt;void/&gt;</code>
 * document element.
 *
 * @author Aleksei Valikov
 * @version 1.0
 * @see AbstractXMLReader
 */
public class VoidSource extends SAXSource {
  /**
   * A pre-initialized void source instance.
   */
  public static VoidSource voidSource = new VoidSource();


  /**
   * Constructs a void source as <code>SAXSource</code> with
   * <code>VoidXMLReader</code> and an empty <code>InputSource</code>.
   */
  public VoidSource() {
    // Call the inherited constructor
    super(new VoidXMLReader(), new InputSource());
  }

  /**
   * Returns a pre-initialized void source.
   *
   * @return Void source.
   */
  public static Source getVoidSource() {
    return voidSource;
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