/**
 * User: ivan
 * Date: 3-Sep-2003
 * Time: 1:05:33 PM
 */
package org.wm.xml.transform;

import javax.xml.transform.TransformerFactory;

public class TransformerFactorySingleton {

  /**
   * Transformer factory.<br/>
   * Must be used synchronized.
   */
  public final static TransformerFactory factory = TransformerFactory.newInstance();

}

