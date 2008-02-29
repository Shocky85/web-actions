package com.sun.script.javafile;

import junit.framework.TestCase;

import javax.script.SimpleScriptContext;
import javax.script.ScriptContext;
import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.net.URL;

/**
 * @author Ivan Latysh
 * @version 0.1
 * @since 27-Apr-2007 : 11:56:41 PM
 */
public class JavaScriptEngineTest extends TestCase {

  JavaScriptEngine engine;
  File sourceRoot;
  File classesRoot;
  SimpleScriptContext ctx;
  PrintWriter err;
  File helloWorldScript;

  protected void setUp() throws Exception {
    engine = new JavaScriptEngine();
    // create source dir
    sourceRoot = new File(System.getProperty("user.dir"),"/tests/data/scripts/src");
    if (!sourceRoot.exists()) sourceRoot.mkdirs();
    assertNotNull(sourceRoot);
    assertTrue(sourceRoot.exists());
    // create classes dir
    classesRoot = new File(System.getProperty("user.dir"),"/tests/data/scripts/classes");
    assertNotNull(classesRoot);
    // delete classes dir if exist
    if (classesRoot.exists()) TestHelper.deleteDir(classesRoot);
    assertFalse(classesRoot.exists());
    // create classes dir
    classesRoot.mkdirs();
    assertTrue(classesRoot.exists());
    // create context
    ctx = new SimpleScriptContext();
    assertNotNull(ctx);
    // set source path
    ctx.setAttribute(JavaScriptEngine.SOURCE_DIR, sourceRoot, ScriptContext.ENGINE_SCOPE);
    assertNotNull(ctx.getAttribute(JavaScriptEngine.SOURCE_DIR));
    assertEquals(sourceRoot, ctx.getAttribute(JavaScriptEngine.SOURCE_DIR));
    // set class path
    ctx.setAttribute(JavaScriptEngine.CLASSES_DIR, classesRoot, ScriptContext.ENGINE_SCOPE);
    assertNotNull(ctx.getAttribute(JavaScriptEngine.CLASSES_DIR));
    assertEquals(classesRoot, ctx.getAttribute(JavaScriptEngine.CLASSES_DIR));
    // error writer
    err = new PrintWriter(System.err);
    assertNotNull(err);
    // Hello world script
    helloWorldScript = new File(System.getProperty("user.dir"),"/tests/data/scripts/src/HelloWorld.java");
    assertNotNull(helloWorldScript);
    assertTrue(helloWorldScript.exists());
    assertTrue(helloWorldScript.isFile());

    super.setUp();
  }

  /**
   * Test locate script file
   */
  public void testLocateScriptFile() {
    try {
      // source dir root
      File scriptFile = JavaScriptEngine.locateScriptFile("HelloWorld.java", sourceRoot);
      assertNotNull(scriptFile);
      assertTrue(scriptFile.exists());
      assertTrue(scriptFile.isFile());
      assertEquals("HelloWorld.java", scriptFile.getName());
    } catch (Exception ex) {
      ex.printStackTrace();
      fail(ex.getMessage());
    }
  }

  /**
   * Test get script dir from ScriptContext
   */
  public void testGetScriptsDir() {
    try {
      // try to get source path
      File scriptsDir = JavaScriptEngine.getScriptsSourceDir(ctx);
      assertNotNull(scriptsDir);
      assertEquals(sourceRoot, scriptsDir);
    } catch (Exception ex) {
      ex.printStackTrace();
      fail(ex.getMessage());
    }
  }

  /**
   * Test get script classes dir from ScriptContext
   */
  public void testGetClassesDir() {
    try {
      // try to get class path
      File classesDir = JavaScriptEngine.getScriptsClassesDir(ctx);
      assertNotNull(classesDir);
      assertEquals(classesRoot, classesDir);
    } catch (Exception ex) {
      ex.printStackTrace();
      fail(ex.getMessage());
    }
  }

  /**
   * Test script class loading
   */
  public void testLoadScriptClass() {
    try {
      // compile script
      engine.compile(helloWorldScript, sourceRoot, classesRoot, err, null);
      // create a classLoader
      URLClassLoader scriptClassLoader = new URLClassLoader(new URL[]{classesRoot.toURI().toURL()}, getClass().getClassLoader());
      // load compiled class
      Class clazz = engine.loadScriptClass(helloWorldScript, sourceRoot, scriptClassLoader);
      assertNotNull(clazz);
      assertEquals("HelloWorld",clazz.getName());
    } catch (Exception ex) {
      ex.printStackTrace();
      fail(ex.getMessage());
    }
  }

  /**
   * Test script class loading
   */
  public void testFindEvalMethod() {
    try {
      // blank Hello World script
      File emptyScript = new File(System.getProperty("user.dir"),"/tests/data/scripts/src/HelloWorldEmpty.java");
      assertNotNull(emptyScript);
      assertTrue(emptyScript.exists());
      assertTrue(emptyScript.isFile());
      // compile scripts
      engine.compile(helloWorldScript, sourceRoot, classesRoot, err, null);
      engine.compile(emptyScript, sourceRoot, classesRoot, err, null);
      // create a classLoader
      URLClassLoader scriptClassLoader = new URLClassLoader(new URL[]{classesRoot.toURI().toURL()}, getClass().getClassLoader());
      // load compiled class
      Class helloWorldClazz = engine.loadScriptClass(helloWorldScript, sourceRoot, scriptClassLoader);
      assertNotNull(helloWorldClazz);
      Class emptyHelloWorldClazz = engine.loadScriptClass(emptyScript, sourceRoot, scriptClassLoader);
      assertNotNull(emptyHelloWorldClazz);

      // look for eval method
      Method method = JavaScriptEngine.findEvalMethod(helloWorldClazz);
      assertNotNull(method);
      assertEquals("eval", method.getName());
      assertTrue(Modifier.isPublic(method.getModifiers()));
      assertTrue(Modifier.isStatic(method.getModifiers()));
      assertEquals(Object.class, method.getReturnType());

      // when no eval method found, exception has to be thrown
      try {
        JavaScriptEngine.findEvalMethod(emptyHelloWorldClazz);
        fail("Must throw {NoSuchMethodException}");
      } catch (NoSuchMethodException ex) {
        assertTrue(true);
      }
    } catch (Exception ex) {
      ex.printStackTrace();
      fail(ex.getMessage());
    }
  }

  /**
   * Test script class evaluation
   */
  public void testEvalClass() {
    try {
      // compile scripts
      engine.compile(helloWorldScript, sourceRoot, classesRoot, err, null);
      // create a classLoader
      URLClassLoader scriptClassLoader = new URLClassLoader(new URL[]{classesRoot.toURI().toURL()}, getClass().getClassLoader());
      // load compiled class
      Class helloWorldClazz = engine.loadScriptClass(helloWorldScript, sourceRoot, scriptClassLoader);
      assertNotNull(helloWorldClazz);
      // evaluate script
      Object result = engine.evalClass(helloWorldClazz, ctx);
      assertNotNull(result);
      assertTrue(result instanceof String);
      assertEquals("Hello World!", result);
    } catch (Exception ex) {
      ex.printStackTrace();
      fail(ex.getMessage());
    }
  }

  /**
   * Test JSR 223 evaluation method
   */
  public void testEval() {
    try {
      // create a classLoader
      URLClassLoader scriptClassLoader = new URLClassLoader(new URL[]{classesRoot.toURI().toURL()}, getClass().getClassLoader());
      ctx.setAttribute(JavaScriptEngine.CLASS_LOADER, scriptClassLoader, ScriptContext.ENGINE_SCOPE);
      // evaluate script
      Object result = engine.eval("HelloWorld.java", ctx);
      assertNotNull(result);
      assertTrue(result instanceof String);
      assertEquals("Hello World!", result);
    } catch (Exception ex) {
      ex.printStackTrace();
      fail(ex.getMessage());
    }
  }

}
