package com.sun.script.javafile;

import junit.framework.TestCase;

import java.io.File;
import java.io.PrintWriter;

/**
 * @author Ivan Latysh
 * @version 0.1
 * @since 27-Apr-2007 : 11:47:02 PM
 */
public class ToolsTest extends TestCase {

  public void testGetFullyQualifiedClassName() {
    String name = Tools.getFullyQualifiedClassName(new File("c:/"), new File("c:/com/sun/script/Test.java"));
    assertEquals("com.sun.script.Test", name);
  }

  public void testCompile() {
    try {
      // error writer
      PrintWriter err = new PrintWriter(System.err);
      // source dir root
      File sourceRoot = new File(System.getProperty("user.dir"),"/tests/data/scripts/src");
      // classes dir root
      File classesRoot = new File(System.getProperty("user.dir"),"/tests/data/scripts/classes");
      // script file to compile
      File script = new File(System.getProperty("user.dir"),"/tests/data/scripts/src/HelloWorld.java");
      // compile it
      Tools.compile(new File[]{script}, sourceRoot, classesRoot, err, null);

    } catch (Exception ex) {
      ex.printStackTrace();
      fail(ex.getMessage());
    }
  }

}
