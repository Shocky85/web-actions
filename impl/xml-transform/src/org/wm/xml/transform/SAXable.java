package org.wm.xml.transform;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * Marks object as "saxable", capable of representing its content as a stream
 * of SAX events.
 *
 * @author Aleksei Valikov.
 * @see #toSAX
 */
public interface SAXable {
  /**
   * Represents object as a stream of SAX events.
   *
   * @param ch content handler to stream events to
   * @throws SAXException Thrown in case of SAX streaming problems.
   */
  public void toSAX(ContentHandler ch) throws SAXException;
}
