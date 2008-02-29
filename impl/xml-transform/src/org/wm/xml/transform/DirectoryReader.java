package org.wm.xml.transform;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.io.File;
import java.io.IOException;

// Error code prefix "XTF0001"

/**
 * Outputs directory contents as a stream of SAX events.
 *
 * @author Aleksei Valikov
 * @version 1.1
 * @see AbstractXMLReader
 */
public class DirectoryReader extends AbstractXMLReader {
  /**
   * Reads contents of directory specified by given <code>inputSource</code>.
   * Contents are listed in the following manner:
   * <pre>
   * &lt;directory name="WINNT" absolutePath="C:\WINNT">
   *   &lt;directory name="system32" absolutePath="C:\WINNT\system32"/>
   *   &lt;file="win.ini" absolutePath="C:\WINNT\win.ini"/>
   *   &lt;file="regedit.exe" absolutePath="C:\WINNT\regedit.exe"/>
   *   &lt;directory name="system" absolutePath="C:\WINNT\system"/>
   *    &lt;!-- ... -->
   * &lt;/directory>
   * </pre>
   * Note that this format may be extended with more attributes.
   *
   * @param inputSource input source that specifies a directory to read. This
   *                    method will use
   *                    <code>File directory = new File(inputSource.getSystemId());</code>
   *                    to get a directory handle.
   * @throws SAXException Any SAX exception, possibly wrapping another
   *                      exception.
   * @throws IOException  An IO exception from the parser, possibly from a byte
   *                      stream or character stream supplied by the application. Also thrown, if
   *                      directory is invalid.
   * @see AbstractXMLReader#parse(InputSource)
   */
  public void parse(final InputSource inputSource) throws IOException, SAXException {
    // Create the directory file
    final File directory = new File(inputSource.getSystemId());
    // Check directory's existence
    if (!directory.exists())
      throw new IOException("[XTF00011] Directory [" + directory.getAbsolutePath() + "] does not exist.");

    // Start document
    contentHandler.startDocument();
    // Create directory's attributes
    final AttributesImpl directoryAttributes = new AttributesImpl();
    directoryAttributes.addAttribute("", "name", "name", "CDATA", directory.getName());
    directoryAttributes.addAttribute("", "absolutePath", "absolutePath", "CDATA", directory.getAbsolutePath());
    // Start element
    contentHandler.startElement("", "directory", "directory", directoryAttributes);

    // Get files in the directory
    final File[] files = directory.listFiles();

    // Go throught the files in directory
    for (int i = 0; i < files.length; i++) {
      // Get current file
      final File file = files[i];

      // Create file's attributes
      final AttributesImpl fileAttributes = new AttributesImpl();
      fileAttributes.addAttribute("", "name", "name", "CDATA", file.getName());
      fileAttributes.addAttribute("", "absolutePath", "absolutePath", "CDATA", file.getAbsolutePath());

      // Deside if it is a file or a directory
      final String elementName = file.isDirectory() ? "directory" : "file";
      //
      contentHandler.startElement("", elementName, elementName, fileAttributes);
      contentHandler.endElement("", elementName, elementName);
    }
    // End root element
    contentHandler.endElement("", "directory", "directory");
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