/**
 * User: ivan
 * Date: 5-Nov-2004
 * Time: 10:30:20 AM
 */

package net.sf.saxon.functions;

import junit.framework.TestCase;

public class GeoTest extends TestCase {

  public void testDistance() {
    assertEquals("Zero distance", 0.0, Geo.distance(43.72871, -79.48288, 43.72871, -79.48288, ""), 0.1);
  }

}
