package org.webactions;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

import javax.script.ScriptContext;
import javax.script.SimpleScriptContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.actions.ConfigurationException;
import org.actions.ProcessingException;
import org.actions.ActionEvaluator;
import org.apache.log4j.Logger;

/**
 * General purpose action controller.<br/>
 *
 * @author Ivan Latysh <ivan@yourmail.com>
 * @version 0.2
 * @since 25-May-2007 12:53:44 PM
 */
public class JavaActionController extends AbstractActionController {

  public static final String JFSE_COMPILE_CLASSPATH = "JFSE_COMPILE_CLASSPATH";
  public static final String JFSE_CLASS_LOADER = "JFSE_CLASS_LOADER";
  public static final String JFSE_CLASSES_DIR = "JFSE_CLASSES_DIR";
  public static final String JFSE_SOURCE_DIR = "JFSE_SOURCE_DIR";

  /** logger */
  protected Logger logger = Logger.getLogger(this.getClass().getName());

  /** Action libs dir */
  protected File actionLibsDir;
  /** Action classes dir */
  protected File actionClassesDir;
  /** Action src dir */
  protected File actionSourceDir;
  /** compile classpath */
  protected ArrayList<File> compileClasspath = new ArrayList<File>();
  /** Action class loader */
  protected ClassLoader classLoader;

  /**
   * Return actions libraries directory
   *
   * @return actions libraries directory
   */
  public File getActionLibsDir() {
    return actionLibsDir;
  }

  /**
   * Set actions libraries directory
   *
   * @param actionLibsDir action libraries directory
   * @throws Exception when given directory is not a directory or read permission has been denied
   */
  public void setActionLibsDir(File actionLibsDir) throws Exception {
    // sanity check
    if (actionLibsDir.exists()) {
      // check permissions
      if (null!=System.getSecurityManager()) {
        try {
          System.getSecurityManager().checkRead(actionLibsDir.getAbsolutePath());
        } catch (Exception e) {
          logger.error("Unable to read from {"+actionLibsDir.getAbsolutePath()+"} please check SecuritySettings.", e);
          throw e;
        }
      }
      if (!actionLibsDir.isDirectory()) {
        throw new Exception("Directory {"+actionLibsDir.getAbsolutePath()+"} is not a directory.");
      } else {
        actionLibsDir.mkdirs();
      }
    }
    this.actionLibsDir = actionLibsDir;
  }

  /**
   * Return action classes dir
   *
   * @return action classes dir
   */
  public File getActionClassesDir() {
    return actionClassesDir;
  }

  /**
   * Set action classes dir
   *
   * @param actionClassesDir classes dir
   * @throws Exception when URL could not be created from given classes dir file
   */
  public void setActionClassesDir(File actionClassesDir) throws Exception {
    // sanity check
    if (actionClassesDir.exists()) {
      // check permissions
      if (null!=System.getSecurityManager()) {
        try {
          System.getSecurityManager().checkWrite(actionClassesDir.getAbsolutePath());
        } catch (Exception e) {
          logger.error("Unable to write to {"+actionClassesDir.getAbsolutePath()+"} please check SecuritySettings.", e);
          throw e;
        }
      }
      if (!actionClassesDir.isDirectory()) {
        throw new Exception("Directory {"+actionClassesDir.getAbsolutePath()+"} is not a directory.");
      } else {
        actionClassesDir.mkdirs();
      }
    }

    this.actionClassesDir = actionClassesDir;

    // create a class loader
    try {
      classLoader = new URLClassLoader(new URL[]{actionClassesDir.toURI().toURL()}, this.getClass().getClassLoader());
    } catch (MalformedURLException e) {
      throw new Exception("Unable to create URL from {" + actionClassesDir.getAbsolutePath() + "}");
    }
    
  }

  /**
   * Return action source dir
   *
   * @return action source dir
   */
  public File getActionSourceDir() {
    return actionSourceDir;
  }

  /**
   * Set action source dir
   *
   * @param actionSourceDir action source dir
   * @throws Exception when given directory is not a directory or permission to write has been denied
   */
  public void setActionSourceDir(File actionSourceDir) throws Exception {
    // sanity check
    if (actionSourceDir.exists()) {
      // check permissions
      if (null!=System.getSecurityManager()) {
        try {
          System.getSecurityManager().checkWrite(actionSourceDir.getAbsolutePath());
        } catch (Exception e) {
          logger.error("Unable to write to {"+actionSourceDir.getAbsolutePath()+"} please check SecuritySettings.", e);
          throw e;
        }
      }
      if (!actionSourceDir.isDirectory()) {
        throw new Exception("Directory {"+actionSourceDir.getAbsolutePath()+"} is not a directory.");
      } else {
        actionSourceDir.mkdirs();
      }
    }
    this.actionSourceDir = actionSourceDir;
  }

  /**
   * Construct a new action processor with a default action evaluator
   */
  public JavaActionController() {
    evaluator = new ActionEvaluator();
  }

  /**
   * Process given action using default ScriptContext
   *
   * @param action action to process
   * @return action result
   * @throws org.actions.ProcessingException when exception occured during action processing
   */
  public Object process(String action) throws ProcessingException {
    return super.process(action, geScriptContext());
  }


  /**
   * Process action with given script context
   *
   * @param action action to process
   * @param scriptContext context
   * @return processing result
   * @throws ProcessingException when exception occured during action processing
   */
  public Object process(String action, ScriptContext scriptContext) throws ProcessingException {
    // inject ClassLoader
    scriptContext.setAttribute(JFSE_CLASS_LOADER, classLoader, ScriptContext.ENGINE_SCOPE);
    // process actions
    Object reslt = super.process(action, scriptContext);    
    // don't forget to retain class loader, it will be the new one of action has been modified
    classLoader = (ClassLoader) scriptContext.getAttribute(JFSE_CLASS_LOADER, ScriptContext.ENGINE_SCOPE);
    // return action result
    return reslt;
  }


  /**
   * Process action in the request if any
   *
   * @param sentences parsed sentences
   * @param context context
   * @return action result
   */
  public Object process(TreeMap<Sentence, Sentence> sentences, ScriptContext context) {
    // make sure that action evaluator is set
    if (null==evaluator) throw new IllegalStateException("ActionEvaluator is not set.");
    Object result = null;
    // Process sentence map
    // While there are sentences to process
    while (null!=sentences && sentences.size()>0) {
      // action scope context
      SimpleScriptContext _context = new SimpleScriptContext();
      // copy original context parameters
      _context.setBindings(context.getBindings(ScriptContext.GLOBAL_SCOPE), ScriptContext.GLOBAL_SCOPE);
      _context.setBindings(context.getBindings(ScriptContext.ENGINE_SCOPE), ScriptContext.ENGINE_SCOPE);
      // Iterate over sentences
      final Iterator sentencesMapIterator = sentences.values().iterator();
      // Get first sentence
      final Sentence firstSentence = (Sentence) sentencesMapIterator.next();
      // Get expression of the first sentence
      final String expression = firstSentence.getExpression();
      // Remove first sentence from the map
      sentencesMapIterator.remove();
      // If suffix is null, use "values" as values parameter name
      final String paramName = firstSentence.getSuffix() == null ? "values" : firstSentence.getSuffix();
      // Put sentence values into the evaluation context
      _context.setAttribute(paramName, firstSentence.getValue(), ScriptContext.ENGINE_SCOPE);
//      localContext.put(paramName, firstSentence.getValues());
      // Search for other sentences of the same expression
      while (sentencesMapIterator.hasNext()) {
        final Sentence followingSentence = (Sentence) sentencesMapIterator.next();
        if (expression.equals(followingSentence.getExpression())) {
          _context.setAttribute(followingSentence.getSuffix(), followingSentence.getValue(), ScriptContext.ENGINE_SCOPE);
          // Remove sentence from the map
          sentencesMapIterator.remove();
        }
      }
      // Evaluate expression
      try {
        logger.debug("Processing expression {" + expression + "} in context {" + _context.toString() + "}.");
        // Perform the evaluation
        result = evaluator.process(expression, _context);
        // ClassLoade could be chaged, so retain it
        classLoader = (ClassLoader)_context.getAttribute(JFSE_CLASS_LOADER, ScriptContext.ENGINE_SCOPE);
        logger.debug("Expression result is {" + result + "}.");
      } catch (Exception ex) {
        logger.error("[WAC00002] Error processing expression {" + expression + "} in context {" + _context.toString() + "}.", ex);
      }
    }

    // Return evaluation result
    return result;

  }

  /**
   * Create and return the new script context.<br/>
   * Method will pre-set java-file script engine config parameters
   *
   * @return script context
   */
  public ScriptContext geScriptContext() {
    // get base script context
    SimpleScriptContext context = geBaseScriptContext();
    // inject java-file script engine parameters
    context.setAttribute(JFSE_CLASSES_DIR, actionClassesDir, ScriptContext.ENGINE_SCOPE);
    context.setAttribute(JFSE_SOURCE_DIR, actionSourceDir, ScriptContext.ENGINE_SCOPE);
    context.setAttribute(JFSE_COMPILE_CLASSPATH, compileClasspath, ScriptContext.ENGINE_SCOPE);
    // return context
    return context;
  }

  /**
   * Return a class loader
   *
   * @param ctx context
   * @return script class loader
   */
  protected ClassLoader getScriptClassLoaderForContext(ScriptContext ctx) {
    if (classLoader==null) {
      if (null!=ctx.getAttribute(JFSE_CLASSES_DIR, ScriptContext.ENGINE_SCOPE)) {
        // prepare a classloader
        try {
          URL url = ((File)ctx.getAttribute(JFSE_CLASSES_DIR, ScriptContext.ENGINE_SCOPE)).toURI().toURL();
          classLoader = new URLClassLoader(new URL[]{url}, getClass().getClassLoader());
        } catch (MalformedURLException e) {
          logger.error("Unable to construct an URL from {"+ctx.getAttribute(JFSE_CLASSES_DIR, ScriptContext.ENGINE_SCOPE)+"}", e);
        }
      } else {
        // use this class class loader
        classLoader = getClass().getClassLoader();
      }
    }
    return classLoader;
  }

}
