package org.wm.xml.transform.i18n;

import java.util.ResourceBundle;

/**
 * Class for convenient usage of resource bundles in transformations.
 */
public class Messenger {
  /**
   * Resource bundle to use by default.
   */
  protected static ResourceBundle resourceBundle;

  /**
   * Hidden constructor.
   */
  private Messenger() {
  }

  /**
   * Sets default resource bundler.
   *
   * @param resourceBundle new default resource bundle.
   */
  public static void setResourceBundle(final ResourceBundle resourceBundle) {
    Messenger.resourceBundle = resourceBundle;
  }

  /**
   * Returns default resource bundle.
   *
   * @return Default resource bundle.
   */
  public static ResourceBundle getResourceBundle() {
    return resourceBundle;
  }

  /**
   * Returns message from default resource bundle by its key or
   * <code>???...???</code> if a problem occurs.
   *
   * @param key message key.
   * @return Message from the default resource bundle for the given key.
   */
  public static String message(final String key) {
    return message(getResourceBundle(), key);
  }

  /**
   * Returns message from the specified resource bundle.
   *
   * @param resourceBundle resource bundle to search for message in.
   * @param key            message key.
   * @return Message for given key and resource bundle, <code>??????</code>, if
   *         key or bundle is <code>null</code>, <code>???<em>key</em>???</code>, if
   *         message for the key is not found or other error happened.
   */
  public static String message(
    final ResourceBundle resourceBundle,
    final String key) {
    // Check that key and resource bunde are not null
    if (key == null || resourceBundle == null)
      return "??????";
    try {
      // Look for key value in the resouce bundle
      final String value = resourceBundle.getString(key);
      // If value is null, throw an exception
      if (value == null)
	throw new NullPointerException();
      // Convert value to UTF-8
      return value;
    }
    catch (Exception e) {
      // If value is ot found output it with question marks
      return "???" + key + "???";
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