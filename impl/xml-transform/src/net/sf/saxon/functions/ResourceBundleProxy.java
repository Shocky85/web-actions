package net.sf.saxon.functions;

import java.util.ResourceBundle;
import java.util.Locale;
import java.text.MessageFormat;

/**
 * Provide
 *
 * @author Ivan
 * @version 0.1
 * @since 21-Apr-2006 : 8:45:10 PM
 */
public class ResourceBundleProxy {

  /**
   * Return string with given key from current resource bundle
   *
   * @param key resource key
   * @return resource string
   */
  public static String getResourceString(String bundleName, String locale, String key) {
    return ResourceBundle.getBundle(bundleName, new Locale(locale)).getString(key);
  }

  /**
   * Return formatted resource string
   *
   * @param key resource key
   * @param arguments format arguments
   * @return formatted resource string
   */
  public static String getResourceString(String bundleName, String locale, String key, Object ... arguments) {
    return MessageFormat.format(getResourceString(bundleName, locale, key), arguments);
  }

  /**
   * Return formatted resource string
   *
   * @param key resource key
   * @param arg1 arg1
   * @return formatted resource string
   */
  public static String getResourceString(String bundleName, String locale, String key, String arg1) {
    return MessageFormat.format(getResourceString(bundleName, locale, key), arg1);
  }

  /**
   * Return formatted resource string
   *
   * @param key resource key
   * @param arg1 arg1
   * @return formatted resource string
   */
  public static String getResourceString(String bundleName, String locale, String key, String arg1, String arg2) {
    return MessageFormat.format(getResourceString(bundleName, locale, key), arg1, arg2);
  }

  /**
   * Return formatted resource string
   *
   * @param key resource key
   * @param arg1 arg1
   * @return formatted resource string
   */
  public static String getResourceString(String bundleName, String locale, String key, String arg1, String arg2, String arg3) {
    return MessageFormat.format(getResourceString(bundleName, locale, key), arg1, arg2, arg3);
  }

  /**
   * Return formatted resource string
   *
   * @param key resource key
   * @param arg1 arg1
   * @return formatted resource string
   */
  public static String getResourceString(String bundleName, String locale, String key, String arg1, String arg2, String arg3, String arg4) {
    return MessageFormat.format(getResourceString(bundleName, locale, key), arg1, arg2, arg3, arg4);
  }

  /**
   * Return formatted resource string
   *
   * @param key resource key
   * @param arg1 arg1
   * @return formatted resource string
   */
  public static String getResourceString(String bundleName, String locale, String key, String arg1, String arg2, String arg3, String arg4, String arg5) {
    return MessageFormat.format(getResourceString(bundleName, locale, key), arg1, arg2, arg3, arg4, arg5);
  }

  /**
   * Return formatted resource string
   *
   * @param key resource key
   * @param arg1 arg1
   * @return formatted resource string
   */
  public static String getResourceString(String bundleName, String locale, String key, String arg1, String arg2, String arg3, String arg4, String arg5, String arg6) {
    return MessageFormat.format(getResourceString(bundleName, locale, key), arg1, arg2, arg3, arg4, arg5, arg6);
  }

  /**
   * Return formatted resource string
   *
   * @param key resource key
   * @param arg1 arg1
   * @return formatted resource string
   */
  public static String getResourceString(String bundleName, String locale, String key, String arg1, String arg2, String arg3, String arg4, String arg5, String arg6, String arg7) {
    return MessageFormat.format(getResourceString(bundleName, locale, key), arg1, arg2, arg3, arg4, arg5, arg6, arg7);
  }

  /**
   * Return formatted resource string
   *
   * @param key resource key
   * @param arg1 arg1
   * @return formatted resource string
   */
  public static String getResourceString(String bundleName, String locale, String key, String arg1, String arg2, String arg3, String arg4, String arg5, String arg6, String arg7, String arg8) {
    return MessageFormat.format(getResourceString(bundleName, locale, key), arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
  }

  /**
   * Return formatted resource string
   *
   * @param key resource key
   * @param arg1 arg1
   * @return formatted resource string
   */
  public static String getResourceString(String bundleName, String locale, String key, String arg1, String arg2, String arg3, String arg4, String arg5, String arg6, String arg7, String arg8, String arg9) {
    return MessageFormat.format(getResourceString(bundleName, locale, key), arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);
  }

  /**
   * Return formatted resource string
   *
   * @param key resource key
   * @param arg1 arg1
   * @return formatted resource string
   */
  public static String getResourceString(String bundleName, String locale, String key, String arg1, String arg2, String arg3, String arg4, String arg5, String arg6, String arg7, String arg8, String arg9, String arg10) {
    return MessageFormat.format(getResourceString(bundleName, locale, key), arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10);
  }

}
