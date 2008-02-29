package org.wm.xml.transform;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Presents directory index in XML form.
 *
 * @author Aleksei Valikov
 * @see DirectoryReader
 */
public class URIDirectoryReader extends DirectoryReader {
  /**
   * Returns XML representation of directory identified by the URI given in
   * system ID of the input source.
   *
   * @param inputSource Identifies the directory.
   * @throws IOException  Thrown in case of I/O problems.
   * @throws SAXException Thrown in case of SAX problem or if the system ID
   *                      has invalid syntax.
   */
  public void parse(final InputSource inputSource) throws IOException, SAXException {
    try {
      final URI uri = new URI(inputSource.getSystemId());
      final File file = new File(uri);
      super.parse(new InputSource(file.getAbsolutePath()));
    }
    catch (URISyntaxException usex) {
      throw new SAXException("Directory URI is incorrect.", usex);
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