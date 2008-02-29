package org.wm.xml.transform;

import org.xml.sax.SAXException;

import java.util.HashMap;

/**
 * Namespace class.
 */
public final class Namespace {

  /**
   * Factory list of namespaces.
   * Keys are <i>prefix</i>&amp;<i>URI</i>.
   * Values are Namespace objects
   */
  private static HashMap namespaces;

  /**
   * Define a <code>Namespace</code> for when <i>not</i> in a namespace.
   */
  public static final Namespace NO_NAMESPACE = new Namespace("", "");

  /**
   * Define a <code>Namespace</code> for the XML namespace.
   */
  public static final Namespace XML_NAMESPACE =
    new Namespace("xml", "http://www.w3.org/XML/1998/namespace");

  /**
   * The prefix mapped to this namespace.
   */
  private String prefix;

  /**
   * The URI for this namespace.
   */
  private String uri;

  /**
   * <p>
   *  This static initializer acts as a factory contructor.
   *  It sets up storage and required initial values.
   * </p>
   */
  static {
    namespaces = new HashMap();

    // Add the "empty" namespace
    namespaces.put("&", NO_NAMESPACE);
    namespaces.put("xml&http://www.w3.org/XML/1998/namespace", XML_NAMESPACE);
  }

  /**
   * <p/>
   * This will retrieve (if in existence) or create (if not) a
   * <code>Namespace</code> for the supplied prefix and URI.
   * </p>
   *
   * @param prefix <code>String</code> prefix to map to
   *               <code>Namespace</code>.
   * @param uri    <code>String</code> URI of new <code>Namespace</code>.
   * @return <code>Namespace</code> - ready to use namespace.
   * @throws SAXException in case of namespace resolution problems.
   */
  public static Namespace getNamespace(String prefix, String uri) throws SAXException {
    // Sanity checking
    if ((prefix == null) || (prefix.trim().equals(""))) {
      prefix = "";
    }
    if ((uri == null) || (uri.trim().equals(""))) {
      uri = "";
    }

    // Handle XML namespace
    if (prefix.equals("xml")) {
      return XML_NAMESPACE;
    }

    // Return existing namespace if found
    final String lookup = new StringBuffer(128).append(prefix).append('&').append(uri).toString();
    final Namespace preexisting = (Namespace) namespaces.get(lookup);
    if (preexisting != null) {
      return preexisting;
    }

    // Unless the "empty" Namespace (no prefix and no URI), require a URI
    if ((!prefix.equals("")) && (uri.equals(""))) {
      throw new SAXException(
	"Namespace URIs must be non-null and non-empty Strings");
    }

    // Finally, store and return
    final Namespace ns = new Namespace(prefix, uri);
    namespaces.put(lookup, ns);
    return ns;
  }

  /**
   * <p/>
   * This will retrieve (if in existence) or create (if not) a
   * <code>Namespace</code> for the supplied URI, and make it usable
   * as a default namespace, as no prefix is supplied.
   * </p>
   *
   * @param uri <code>String</code> URI of new <code>Namespace</code>.
   * @return <code>Namespace</code> - ready to use namespace.
   * @throws SAXException in case of namespace resolution problems.
   */
  public static Namespace getNamespace(final String uri) throws SAXException {
    return getNamespace("", uri);
  }

  /**
   * <p/>
   * This constructor handles creation of a <code>Namespace</code> object
   * with a prefix and URI; it is intentionally left <code>private</code>
   * so that it cannot be invoked by external programs/code.
   * </p>
   *
   * @param prefix <code>String</code> prefix to map to this namespace.
   * @param uri    <code>String</code> URI for namespace.
   */
  private Namespace(final String prefix, final String uri) {
    this.prefix = prefix;
    this.uri = uri;
  }

  /**
   * <p/>
   * This returns the prefix mapped to this <code>Namespace</code>.
   * </p>
   *
   * @return <code>String</code> - prefix for this <code>Namespace</code>.
   */
  public String getPrefix() {
    return prefix;
  }

  /**
   * <p/>
   * This returns the namespace URI for this <code>Namespace</code>.
   * </p>
   *
   * @return <code>String</code> - URI for this <code>Namespace</code>.
   */
  public String getURI() {
    return uri;
  }

  /**
   * <p/>
   * This tests for equality - Two <code>Namespaces</code>
   * are equal if and only if their URIs are byte-for-byte equals.
   * </p>
   *
   * @param object <code>Object</code> to compare to this <code>Namespace</code>.
   * @return <code>boolean</code> - whether the supplied object is equal to
   *         this <code>Namespace</code>.
   */
  public boolean equals(final Object object) {
    // Check that object is Namespace instance
    if (object instanceof Namespace)
      // Check URI equality
      return uri.equals(((Namespace) object).uri);
    return false;
  }

  /**
   * <p/>
   * This returns a <code>String</code> representation of this
   * <code>Namespace</code>, suitable for use in debugging.
   * </p>
   *
   * @return <code>String</code> - information about this instance.
   */
  public String toString() {
    return "[xmlns:" + prefix + "=\"" + uri + "\"]";
  }

  /**
   * <p/>
   * This returns a probably unique hash code for the <code>Namespace</code>.
   * If two namespaces have the same URI, they are equal and have the same
   * hash code, even if they have different prefixes.
   * </p>
   *
   * @return <code>int</code> - hash code for this <code>Namespace</code>.
   */
  public int hashCode() {
    return uri.hashCode();
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