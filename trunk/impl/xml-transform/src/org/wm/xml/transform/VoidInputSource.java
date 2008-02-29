package org.wm.xml.transform;

import org.xml.sax.InputSource;

/**
 * Minimal primitive input source.
 *
 * @author Alekse Valikov
 */
public class VoidInputSource extends InputSource {
  /**
   * Static instance of a minimal primitive input source.
   */
  public static VoidInputSource voidInputSource = new VoidInputSource();

  /**
   * Returns minimal primitive input source.
   *
   * @return Minimal primitive input source.
   */
  public static VoidInputSource getVoidInputSource() {
    return voidInputSource;
  }
}
