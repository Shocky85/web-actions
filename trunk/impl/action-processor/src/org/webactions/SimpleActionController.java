package org.webactions;

import org.actions.ActionEvaluator;
import org.apache.log4j.Logger;

import javax.script.ScriptContext;
import javax.script.SimpleScriptContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * Simple action controller, provide basic action processing functionality.
 *
 * @author Ivan Latysh
 * @version 0.1
 * @since 11-May-2007 : 12:57:38 PM
 */
public class SimpleActionController {
  // Error prefix AAC00001

  /** Component logger */
  protected Logger logger = Logger.getLogger(this.getClass().getName());
  /** Script context parameters */
  protected Map<String, Object> scriptContextParameters;
  /** Action evaluator */
  protected ActionEvaluator evaluator;
  /** Action Class Loader */
  protected ClassLoader cl;
  /** Application libraries */
  protected URL libs;

  /**
   * Return action evaluator
   *
   * @return action evaluator
   */
  public ActionEvaluator getEvaluator() {
    return evaluator;
  }

  /**
   * Set action evluator
   *
   * @param evaluator action evaluator
   */
  public void setEvaluator(ActionEvaluator evaluator) {
    this.evaluator = evaluator;
  }

  /**
   * Return script context parameters
   *
   * @return script context parameters
   */
  public Map<String, Object> getScriptContextParameters() {
    return scriptContextParameters;
  }

  /**
   * Set script context parameters
   *
   * @param scriptContextParameters parameters
   */
  public void setScriptContextParameters(Map<String, Object> scriptContextParameters) {
    this.scriptContextParameters = scriptContextParameters;
  }

  /**
   * Construct a new ScriptContext with basic environmental parameters
   *
   * @param request request
   * @param response response
   * @return ScriptContext
   */
  public ScriptContext getScriptContext(HttpServletRequest request, HttpServletResponse response) {
    // create a new script context
    ScriptContext context = new SimpleScriptContext();
    // add pre-configured parameters
    if (null!=scriptContextParameters) {
      for (Map.Entry<String, Object> entry: scriptContextParameters.entrySet()) {
        context.setAttribute(entry.getKey(), entry.getValue(), ScriptContext.ENGINE_SCOPE);
      }
    }

    // set compile classpath
    List<File> compileClasspath = getAppClasspath(request);
    // check if compile classpath has been already defined
    if (null!=context.getAttribute("JFSE_COMPILE_CLASSPATH")) {
      List<File> addClasspath = (List<File>) context.getAttribute("JFSE_COMPILE_CLASSPATH");
      compileClasspath.addAll(addClasspath);
    }
    // set classpath
    context.setAttribute("JFSE_COMPILE_CLASSPATH", compileClasspath, ScriptContext.ENGINE_SCOPE);

    // set class loader
    context.setAttribute("JFSE_CLASS_LOADER", getScriptClassLoaderForContext(context), ScriptContext.ENGINE_SCOPE);

    // add some environmental parameters
    context.setAttribute("request", request, ScriptContext.ENGINE_SCOPE);
    context.setAttribute("response", response, ScriptContext.ENGINE_SCOPE);
    // return it
    return context;
  }

  /**
   * Construct a new ScriptContext with basic environmental parameters
   *
   * @param request request
   * @param response response
   * @return ScriptContext
   */
  public ScriptContext getScriptContext(ActionRequest request, ActionResponse response) {
    // create a new script context
    ScriptContext context = new SimpleScriptContext();
    // add pre-configured parameters
    if (null!=scriptContextParameters) {
      for (Map.Entry<String, Object> entry: scriptContextParameters.entrySet()) {
        context.setAttribute(entry.getKey(), entry.getValue(), ScriptContext.ENGINE_SCOPE);
      }
    }

    // set compile classpath
    List<File> compileClasspath = getAppClasspath(request);
    // check if compile classpath has been already defined
    if (null!=context.getAttribute("JFSE_COMPILE_CLASSPATH")) {
      List<File> addClasspath = (List<File>) context.getAttribute("JFSE_COMPILE_CLASSPATH");
      compileClasspath.addAll(addClasspath);
    }
    // set classpath
    context.setAttribute("JFSE_COMPILE_CLASSPATH", compileClasspath, ScriptContext.ENGINE_SCOPE);

    // set class loader
    context.setAttribute("JFSE_CLASS_LOADER", getScriptClassLoaderForContext(context), ScriptContext.ENGINE_SCOPE);

    // add some environmental parameters
    context.setAttribute("request", request, ScriptContext.ENGINE_SCOPE);
    context.setAttribute("response", response, ScriptContext.ENGINE_SCOPE);
    // return it
    return context;
  }

  /**
   * Process action in the request if any
   *
   * @param sentences parsed sentences
   * @param context context
   * @return action result
   */
  public Object processActions(TreeMap<Sentence, Sentence> sentences, ScriptContext context) {
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
        cl = (ClassLoader)_context.getAttribute("JFSE_CLASS_LOADER", ScriptContext.ENGINE_SCOPE);
        logger.debug("Expression result is {" + result + "}.");
      } catch (Exception ex) {
        logger.error("[WAC00002] Error processing expression {" + expression + "} in context {" + _context.toString() + "}.", ex);
      }
    }

    // Return evaluation result
    return result;

  }

  /**
   * Return a class loader
   *
   * @param ctx context
   * @return script class loader
   */
  protected ClassLoader getScriptClassLoaderForContext(ScriptContext ctx) {
    if (cl==null) {
      if (null!=ctx.getAttribute("JFSE_CLASSES_DIR", ScriptContext.ENGINE_SCOPE)) {
        // prepare a classloader
        try {
          URL url = ((File)ctx.getAttribute("JFSE_CLASSES_DIR", ScriptContext.ENGINE_SCOPE)).toURI().toURL();
          cl = new URLClassLoader(new URL[]{url}, getClass().getClassLoader());
        } catch (MalformedURLException e) {
          logger.error("Unable to construct an URL from {"+ctx.getAttribute("JFSE_CLASSES_DIR", ScriptContext.ENGINE_SCOPE)+"}", e);
        }
      } else {
        // use this class class loader
        cl = getClass().getClassLoader();
      }
    }
    return cl;
  }

  /**
   * Construct and return an array of files with compile classpath
   *
   * @param request HttpServletRequest
   * @return file array
   */
  protected List<File> getAppClasspath(HttpServletRequest request) {
    // get app home
    File appHome = new File(request.getSession().getServletContext().getRealPath("/"));
    // webapp classes should be in WEB-INF/classes
    File appClasses = new File(appHome, "WEB-INF/classes");
    // webapp libs should be in WEB-INF/lib
    File appLib = new File(appHome, "WEB-INF/lib");

    // return classpath array
    return getAppClasspath(appClasses, appLib);
  }

  /**
   * Construct and return an array of files with compile classpath
   *
   * @param request HttpServletRequest
   * @return file array
   */
  protected List<File> getAppClasspath(ActionRequest request) {
    // get app home
    File appHome = new File(request.getPortletSession().getPortletContext().getRealPath("/"));
    // webapp classes should be in WEB-INF/classes
    File appClasses = new File(appHome, "WEB-INF/classes");
    // webapp libs should be in WEB-INF/lib
    File appLib = new File(appHome, "WEB-INF/lib");
    
    // return classpath array
    return getAppClasspath(appClasses, appLib);
  }

  /**
   * Construct and return an array of files with compile classpath
   *
   * @param classesDir classes dir
   * @param libDir lib dir
   * @return file array
   */
  protected List<File> getAppClasspath(File classesDir, File libDir) {
    List<File> classpath = new ArrayList<File>();

    // webapp classes should be in WEB-INF/classes
    if (classesDir.isDirectory()) {
      classpath.add(classesDir);
    } else {
      logger.warn("Webapp classes directory {"+classesDir+"} does not exist.");
    }

    // webapp libs should be in WEB-INF/lib
    logger.debug("Trying to load libraries from {"+ libDir.getAbsolutePath()+"}.");
    if (libDir.isDirectory()) {
      File[] libs = libDir.listFiles(new FilenameFilter(){
        public boolean accept(File dir, String name) {
          return null!=name && name.endsWith(".jar");
        }
      });
      classpath.addAll(Arrays.asList(libs));
    }

    // return classpath array
    return classpath;
  }

}
