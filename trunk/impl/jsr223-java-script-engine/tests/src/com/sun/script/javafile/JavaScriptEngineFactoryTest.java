package com.sun.script.javafile;

import junit.framework.TestCase;

import javax.script.ScriptEngine;

/**
 * Test case for Engine Factory
 *
 * @author Ivan Latysh
 * @version 0.1
 * @since 28-Apr-2007 : 5:16:44 PM
 */
public class JavaScriptEngineFactoryTest extends TestCase {

  JavaScriptEngineFactory factory;

  protected void setUp() throws Exception {
    factory = new JavaScriptEngineFactory();
    assertNotNull(factory);
    super.setUp();
  }

  /**
   * Test get engine name
   */
  public void testGetEngineName() {
    assertEquals("java-file", factory.getEngineName());
  }

  /**
   * Test get script engine
   *
   */
  public void testGetScriptEngine() {
    ScriptEngine eng1 = factory.getScriptEngine();
    ScriptEngine eng2 = factory.getScriptEngine();
    assertNotSame(eng1, eng2);
  }

}
