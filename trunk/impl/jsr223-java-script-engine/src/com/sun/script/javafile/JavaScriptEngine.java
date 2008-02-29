package com.sun.script.javafile;

import javax.script.*;
import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.net.URLClassLoader;
import java.net.URL;

/**
 * This is script engine for Java programming language.
 */
public class JavaScriptEngine extends AbstractScriptEngine {

  /** Classes folder, where compiled script classes will be saved */
  protected static final String CLASSES_DIR = "JFSE_CLASSES_DIR";
  /** Scripts source folder, where to look for a script .java file */
  protected static final String SOURCE_DIR = "JFSE_SOURCE_DIR";
  /** Compile class path */
  protected static final String COMPILE_CLASSPATH = "JFSE_COMPILE_CLASSPATH";
  /** Class loader */
  protected static final String CLASS_LOADER = "JFSE_CLASS_LOADER";

  /** Error output */
  protected final PrintWriter err = new PrintWriter(System.err);

  // my factory, may be null
  private ScriptEngineFactory factory;

  /**
   * Read script string, and invoke {@link #eval(String, javax.script.ScriptContext)}
   *
   * @param reader reader
   * @param ctx context
   * @return evaluation result
   * @throws ScriptException when error occur in script
   */
  public Object eval(Reader reader, ScriptContext ctx) throws ScriptException {
    return eval(Tools.readFully(reader), ctx);
  }

  /**
   * Evaluate script denoted by the filename given in the str parameter.
   *
   * @param str script filename to evaluate
   * @param ctx evaluation context
   * @return evaluation result
   * @throws ScriptException when error occur in script
   */
  public Object eval(String str, ScriptContext ctx) throws ScriptException {
    Class clazz;
    try {
      // get class name
      String scriptClass = (str.endsWith(".java") ?str.substring(0, str.indexOf(".java")) :str);
      // replace all '.' with '/'
      scriptClass = scriptClass.replaceAll("\\.", "/");
      // script source directory
      File scriptsDir = getScriptsSourceDir(ctx);
      // locate classes dir
      File classesDir = getScriptsClassesDir(ctx);
      // locate script file
      File scriptFile = locateScriptFile(scriptClass+".java", scriptsDir);
      // get Class Loader
      ClassLoader cl;
      if (null!=ctx.getAttribute(CLASS_LOADER, ScriptContext.ENGINE_SCOPE)) {
        // retrieve the class loader from the context
        cl = (ClassLoader) ctx.getAttribute(CLASS_LOADER, ScriptContext.ENGINE_SCOPE);
      } else {
        // if no class loader given use the current
        cl = getClass().getClassLoader();        
      }


      // get script class file
      String scriptClassFileName = scriptClass+".class";
      String relativePath = scriptsDir.toURI().relativize(scriptFile.getParentFile().toURI()).toString();
      File scriptClassFile = new File(classesDir, (!relativePath.endsWith("/") ?relativePath+"/" :relativePath) +scriptClassFileName);
      // check if script has been modified
      if (!scriptClassFile.exists() || scriptClassFile.exists() && scriptClassFile.lastModified() < scriptFile.lastModified()) {
        if (scriptClassFile.exists()) {
          // recycle classloader if needed
          ClassLoader parent = cl.getParent();
          ctx.removeAttribute(CLASS_LOADER, ScriptContext.ENGINE_SCOPE);
          cl = new URLClassLoader(new URL[]{classesDir.toURI().toURL()}, parent);
          ctx.setAttribute(CLASS_LOADER, cl, ScriptContext.ENGINE_SCOPE);
        }
        // compile script
        compile(scriptFile, scriptsDir, classesDir, err, (List<File>) ctx.getAttribute(COMPILE_CLASSPATH, ScriptContext.ENGINE_SCOPE));
      }
      try {
        // load compiled script class
        clazz = loadScriptClass(scriptFile, scriptsDir, cl);
      } catch (ClassNotFoundException ex) {
        // looks like script is not compiled yet
        compile(scriptFile, scriptsDir, classesDir, err, (List<File>) ctx.getAttribute(COMPILE_CLASSPATH, ScriptContext.ENGINE_SCOPE));
        // try to load again, and if it fails, throw an exception
        clazz = loadScriptClass(scriptFile, scriptsDir, cl);
      }

    } catch (Exception e) {
      throw new ScriptException(e);
    } finally {
      err.flush();
    }

    // evaluate and return result
    return evalClass(clazz, ctx);
  }

  public ScriptEngineFactory getFactory() {
    if (factory == null) {
      synchronized (this) {
        factory = new com.sun.script.javafile.JavaScriptEngineFactory();
      }
    }
    return factory;
  }

  public Bindings createBindings() {
    return new SimpleBindings();
  }

  void setFactory(ScriptEngineFactory factory) {
    synchronized(this) {
      this.factory = factory;
    }
  }

  // Internals only below this point

  /**
   * Locate script file
   *
   * @param scriptFileName script file name
   * @param scriptsDir script source root directory
   * @return script file
   * @throws IllegalStateException when script root directory is not defined
   * @throws FileNotFoundException when script file could not be found
   */
  protected static File locateScriptFile(String scriptFileName, File scriptsDir) throws IllegalStateException, FileNotFoundException{
    // construct file
    File scriptFile = new File(scriptsDir, scriptFileName);
    if (!scriptFile.exists() || !scriptFile.isFile()) throw new FileNotFoundException("Script {"+scriptFileName+"} resolved to {"+scriptFile.getAbsolutePath()+"} could not be found.");
    // return script file
    return scriptFile;
  }

  /**
   * Return SCRIPTS global parameter
   *
   * @param ctx Script Context
   * @return script source root directory
   * @throws IllegalStateException when parameter is not defined
   */
  protected static File getScriptsSourceDir(ScriptContext ctx) throws IllegalStateException {
    File scriptsDir = null;
    // source dir
    Object srcDir = ctx.getAttribute(SOURCE_DIR, ScriptContext.ENGINE_SCOPE);
    if (null==srcDir) throw new IllegalStateException("Scripts source root directory parameter {"+ SOURCE_DIR +"} is not defined.");
    if (srcDir instanceof File) {
      // if given script source dir is a file use it
      scriptsDir = (File) srcDir;
    } else if (srcDir instanceof String) {
      // if given script source dir is a string, create a file
      scriptsDir = new File((String)srcDir);
    } else {
      throw new IllegalStateException("Unexpected type for scripts source root directory parameter {"+ SOURCE_DIR +"}. Expected "+File.class.getName()+" or "+String.class.getName());
    }
    return scriptsDir;
  }

  /**
   * Return CLASSES global parameter
   *
   * @param ctx Script Context
   * @return script classes directory
   * @throws IllegalStateException when parameter is not defined
   */
  protected static File getScriptsClassesDir(ScriptContext ctx) throws IllegalStateException {
    File classesDirFile = null;
    // classes dir
    Object classesDir = ctx.getAttribute(CLASSES_DIR, ScriptContext.ENGINE_SCOPE);
    if (null==classesDir) throw new IllegalStateException("Scripts classes root directory parameter {"+ CLASSES_DIR +"} is not defined.");
    if (classesDir instanceof File) {
      // if given script classes dir is a file use it
      classesDirFile = (File) classesDir;
    } else if (classesDir instanceof String) {
      // if given script classes dir is a string, create a file
      classesDirFile = new File((String)classesDir);
    } else {
      throw new IllegalStateException("Unexpected type for scripts classes root directory parameter {"+ CLASSES_DIR +"}. Expected "+File.class.getName()+" or "+String.class.getName());
    }
    // check if dir exist, and create it, if it is not
    if (!classesDirFile.isDirectory() && !classesDirFile.isFile()) {      
      classesDirFile.mkdirs();
    }
    return classesDirFile;
  }

  /**
   * Load script class for the script file specified.
   * If script file is not compiled yet it will be compiled and compiled class loaded.
   * When script file timestamp is alter than compiled class, script file will be re-compiled.
   *
   * @param scriptFile script file
   * @param scriptsDir script source directory
   * @param cl class loader
   * @return script Class
   * @throws IllegalStateException when missing required parameters
   * @throws java.io.FileNotFoundException when script source file is not found
   * @throws ClassNotFoundException when class could not be loaded
   */
  protected Class loadScriptClass(File scriptFile, File scriptsDir, ClassLoader cl) throws FileNotFoundException, IllegalStateException, ClassNotFoundException{
    // try to load class
    return cl.loadClass(Tools.getFullyQualifiedClassName(scriptsDir, scriptFile));
  }

  /**
   * Compile given script file
   *
   * @param scriptFile script file to compile
   * @param scriptsDir script root directory
   * @param classesDir classes root directory
   * @param err error output   
   * @param classpath compile classpath
   * @throws IllegalStateException when any of required global parameters is not set
   * @throws java.io.IOException when an IO error accured
   */
  protected void compile(File scriptFile, File scriptsDir, File classesDir, Writer err, Iterable<? extends File> classpath) throws IllegalStateException, IOException{
    // compile script
    Tools.compile(new File[]{scriptFile}, scriptsDir, classesDir, err, classpath);
  }

  /**
   * Find and return <tt>public static Object eval(ScriptContext context)</tt> method.
   *
   * @param clazz class
   * @return found method
   * @throws NoSuchMethodException when method is not found
   */
  protected static Method findEvalMethod(Class clazz) throws NoSuchMethodException{
    Method evalMethod = clazz.getMethod("eval", new Class[]{ScriptContext.class});
    int modifiers = evalMethod.getModifiers();
    if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Object.class==evalMethod.getReturnType()) {
      return evalMethod;
    }
    throw new NoSuchMethodException("Method public static Object eval(ScriptContext context); not found in class {"+clazz.getName()+"}");
  }

  /**
   * Evaluate script
   *
   * @param clazz script class
   * @param ctx evaluation context
   * @return evaluation result
   * @throws ScriptException when error occured during script evaluation
   */
  protected Object evalClass(Class clazz, ScriptContext ctx) throws ScriptException {
    Object result = null;
    // JSR-223 requirement
    ctx.setAttribute("context", ctx, ScriptContext.ENGINE_SCOPE);
    if (clazz == null) {
      return null;
    }
    try {
      if (Modifier.isPublic(clazz.getModifiers())) {
        // find entry point
        Method entry = findEvalMethod(clazz);
        // try to relax access
        entry.setAccessible(true);
        // invoke it
        result = entry.invoke(null, new Object[]{ctx});
      } else {
        throw new ScriptException("Class {"+clazz.getName()+"} must be public.");
      }
    } catch (ScriptException exp) {
      throw exp;
    } catch (Exception exp) {
      throw new ScriptException(exp);
    }
    // return result
    return result;
  }

}
