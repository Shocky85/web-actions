package org.wm.xml.transform;

import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Properties;

// Error code prefix "XTF0005"

/**
 * A collection of transformation tools.
 */
public class Tools {
  /**
   * Transformer factory.
   */
  protected static TransformerFactory transformerFactory;

  static {
    transformerFactory = TransformerFactory.newInstance();
  }

  /**
   * Hidden constructor.
   */
  private Tools() {
  }

  /**
   * Serializes a source into a writer.
   *
   * @param source to serialize.
   * @param writer writer to serialize into.
   * @throws TransformerException If an unrecoverable error occurs during the
   *                              course of the serialization.
   */
  public static void serialize(final Source source, final Writer writer) throws TransformerException {
    serialize(source, writer, null);
  }

  /**
   * Serializes a source into a writer.
   *
   * @param source to serialize.
   * @param writer writer to serialize into.
   * @throws TransformerException If an unrecoverable error occurs during the
   *                              course of the serialization.
   */
  public static void serialize(final Source source, final Writer writer, final Properties outputProperties)
    throws TransformerException {
    final Transformer transformer;
    synchronized (transformerFactory) {
      transformer = transformerFactory.newTransformer();
    }
    if (null!=outputProperties) {
      transformer.setOutputProperties(outputProperties);
    }
    transformer.transform(source, new StreamResult(writer));
  }

  /**
   * Serializes DOM node into a writer.
   *
   * @param node   DOM node to serialize.
   * @param writer writer to serialize into.
   * @throws TransformerException If an unrecoverable error occurs during the
   *                              course of the serialization.
   */
  public static void serialize(final Node node, final Writer writer)
    throws TransformerException {
    serialize(new DOMSource(node), writer);
  }

  /**
   * Serializes a source into a string.
   *
   * @param source source to serialize.
   * @return Source serialized to a string or <code>null</code>, if
   *         serialization failed.
   */
  public static String serialize(final Source source) {
    try {
      // Create a new string writer
      final StringWriter writer = new StringWriter();
      // Serialize a source into it
      serialize(source, writer);
      // Return string value of the writer
      return writer.toString();
    }
    catch (TransformerException e) {
      return null;
    }
  }

  /**
   * Serializes a DOM node into a string.
   *
   * @param node DOM node to serialize.
   * @return Node serialized to string or <code>null</code>, if serialization
   *         failed.
   */
  public static String serialize(final Node node) {
    try {
      // Create a new string writer
      final StringWriter writer = new StringWriter();
      // Serialize DOM source of node into this writer
      serialize(node, writer);
      // Return string value of the writer
      return writer.toString();
    }
    catch (TransformerException e) {
      return null;
    }
  }

  /**
   * Serializes a SAXable into a writer.
   *
   * @param saxable saxable to serialize.
   * @param writer  writer to serialize into.
   * @throws TransformerException If an unrecoverable error occurs during the
   *                              course of the serialization.
   */
  public static void serialize(final SAXable saxable, final Writer writer)
    throws TransformerException {
    // Create a new reader for the specified saxable
    final XMLReader xmlReader = new AbstractXMLReader() {
      public void parse(final InputSource input)
	throws IOException, SAXException {
	contentHandler.startDocument();
	saxable.toSAX(contentHandler);
	contentHandler.endDocument();
      }
    };
    serialize(new SAXSource(xmlReader, VoidInputSource.voidInputSource),
              writer);
  }


  /**
   * Serializes a SAXable node into a string.
   *
   * @param saxable SAXable  to serialize.
   * @return SAXable serialized to string or <code>null</code>, if serialization
   *         failed.
   */
  public static String serialize(final SAXable saxable) {
    try {
      // Create a new string writer
      final StringWriter writer = new StringWriter();
      // Serialize saxable into this writer
      serialize(saxable, writer);
      // Return string value of the writer
      return writer.toString();
    }
    catch (TransformerException e) {
      return null;
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