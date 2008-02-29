package org.actions;

import org.apache.commons.betwixt.io.BeanWriter;
import org.apache.commons.betwixt.io.BeanReader;
import org.apache.commons.betwixt.strategy.NameMapper;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;

import java.io.*;
import java.util.Map;
import java.beans.IntrospectionException;
import java.beans.XMLEncoder;
import java.beans.XMLDecoder;

/**
 * Class incapsulate actions - XML transformation methods.
 *
 * @author Ivan Latysh
 * @version 0.1
 * @since 28-Apr-2007 : 6:21:57 PM
 */
public class ActionIO {

  /**
   * Write actions into given out file
   *
   * @param actions action map to write
   * @param out file to write to
   * @throws java.io.IOException when unable to write to output
   * @throws org.xml.sax.SAXException when error occur while writing actions map
   * @throws java.beans.IntrospectionException when error occur while writing actions map
   */
  public static void writeActions(ActionMap actions, File out) throws IOException, SAXException, IntrospectionException {
    // prepare output writer
    FileWriter writer = new FileWriter(out);
    try {
      // write XML prolog
      writer.write("<?xml version='1.0' ?>\n");
      // create a bean writer
      BeanWriter beanWriter = new BeanWriter(writer);
      beanWriter.getBindingConfiguration().setMapIDs(false);
      beanWriter.getXMLIntrospector().getConfiguration().setWrapCollectionsInElement(false);
      beanWriter.enablePrettyPrint();
      // write map
      beanWriter.write(actions);
      // flush and close output
    } finally {
      writer.flush();
      writer.close();
    } 
  }

  /**
   * Read actions from given XML file
   *
   * @param actions actions XML file
   * @return Action Map
   * @throws java.io.IOException when unable to write to output
   * @throws org.xml.sax.SAXException when error occur while writing actions map
   * @throws java.beans.IntrospectionException when error occur while writing actions map
   */
  public static ActionMap readActions(File actions) throws IOException, SAXException, IntrospectionException {
    InputStream in = null;
    try {
      in = new FileInputStream(actions);
      return readActions(in);
    } finally {
      if (null!=in) in.close();
    }
  }

  /**
   * Read actions from given XML file
   *
   * @param in XML input stream
   * @return Action Map
   * @throws java.io.IOException when unable to write to output
   * @throws org.xml.sax.SAXException when error occur while writing actions map
   * @throws java.beans.IntrospectionException when error occur while writing actions map
   */
  public static ActionMap readActions(InputStream in) throws IOException, SAXException, IntrospectionException {
    // create a bean reader
    BeanReader beanReader = new BeanReader();
    beanReader.getXMLIntrospector().getConfiguration().setWrapCollectionsInElement(false);
    beanReader.registerBeanClass(ActionMap.class);
    beanReader.registerBeanClass(Action.class);
    // read map
    return (ActionMap) beanReader.parse(in);
  }

}
