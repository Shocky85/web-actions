package org.wm.xml.transform;

import javax.xml.transform.Source;

/**
 * This interface indicates, that object can present itself as an XML source.
 *
 * @author Aleksei Valikov
 * @see Source
 */
public interface Sourceable {
  /**
   * Returns object representation as an XML Source (DOM, SAX or simple stream
   * source). Should always return a valid (but, possibly, void) source.
   *
   * @return Returns source representation of the object.
   */
  public Source getSource();
}
