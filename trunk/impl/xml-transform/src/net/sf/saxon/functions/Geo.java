package net.sf.saxon.functions;

/**
 * @author Ivan
 * @version 0.1
 * @since 5-Nov-2004 : 10:23:27 AM
 */
public class Geo {

  public static final int STATUTE_MILES = 0;
  public static final int KILOMETERS = 1;
  public static final int NAUTICAL_MILES = 2;

  /**
   * This routine calculates the distance between two points
   * (given the latitude/longitude of those points).
   * It is being used to calculate
   * <p/>
   * This routine calculates the distance between two points
   * (given the latitude/longitude of those points).
   * <p/>
   * South latitudes are negative, east longitudes are positive
   * <p/>
   *
   * @param lat1 Latitude of point 1 (in decimal degrees)
   * @param lon1 Longitude of point 1 (in decimal degrees)
   * @param lat2 Latitude of point 2 (in decimal degrees)
   * @param lon2 Longitude of point 2 (in decimal degrees)
   * @param unit 'M' is statute miles; 'K' is kilometers(default); 'N' is nautical miles
   * @return
   */
  public static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
    final int _unit = ("M".equalsIgnoreCase(unit) ?STATUTE_MILES :("N".equalsIgnoreCase(unit) ?NAUTICAL_MILES :KILOMETERS));
    return distance(lat1, lon1, lat2, lon2, _unit);
  }

  /**
   * This routine calculates the distance between two points
   * (given the latitude/longitude of those points).
   * It is being used to calculate
   * <p/>
   * This routine calculates the distance between two points
   * (given the latitude/longitude of those points).
   * <p/>
   * South latitudes are negative, east longitudes are positive
   * <p/>
   * @param lat1 Latitude of point 1 (in decimal degrees)
   * @param lon1 Longitude of point 1 (in decimal degrees)
   * @param lat2 Latitude of point 2 (in decimal degrees)
   * @param lon2 Longitude of point 2 (in decimal degrees)
   * @param unit desirable result units {@link #STATUTE_MILES}, {@link #KILOMETERS}, {@link #NAUTICAL_MILES}.
   */
  public static double distance(double lat1, double lon1, double lat2, double lon2, int unit) {    
    double theta = lon1 - lon2;
    double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
    dist = Math.acos(dist);
    dist = rad2deg(dist);
    dist = dist * 60 * 1.1515;
    if (unit == KILOMETERS) {
      dist = dist * 1.609344;
    } else if (unit == NAUTICAL_MILES) {
      dist = dist * 0.8684;
    }
    return (dist);
  }

  /**
   * This function converts decimal degrees to radians
   *
   * @param deg
   * @return
   */
  public static double deg2rad(double deg) {
    return (deg * Math.PI / 180.0);
  }

  /**
   * This function converts radians to decimal degrees
   *
   * @param rad
   * @return
   */
  public static double rad2deg(double rad) {
    return (rad * 180.0 / Math.PI);
  }

}
