package org.webactions;

import org.actions.ActionEvaluator;
import org.actions.ConfigurationException;
import org.actions.ProcessingException;
import org.apache.log4j.Logger;

import javax.script.ScriptContext;
import javax.script.SimpleScriptContext;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Abstract action controller
 *
 * @author Ivan Latysh
 * @version 0.1
 * @since 11-May-2007 : 12:57:38 PM
 */
abstract public class AbstractActionController {
  // Error prefix AAC00001

  /** Component logger */
  protected Logger logger = Logger.getLogger(this.getClass().getName());
  /** Script context parameters */
  protected Map<String, Object> scriptContextParameters;
  /** Action config */
  protected File actionConfig;
  /** Action evaluator */
  protected ActionEvaluator evaluator;

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
   * Get actions config
   *
   * @return actions config
   */
  public File getActionConfig() {
    return actionConfig;
  }

  /**
   * Set actions config
   *
   * @param actionConfig action config
   * @throws org.actions.ConfigurationException
   *                             when error occured while configuring action evaluator
   * @throws java.io.IOException when IO exception occured
   */
  public void setActionConfig(File actionConfig) throws ConfigurationException, IOException {
    this.actionConfig = actionConfig;
    evaluator.configure(actionConfig);
  }

  /**
   * Process given action using default ScriptContext
   *
   * @param action action to process
   * @return action result
   * @throws org.actions.ProcessingException when exception occured during action processing
   */
  public Object process(String action) throws ProcessingException {
    // process action
    return process(action, geBaseScriptContext());
  }

  /**
   * Process action with given script context
   *
   * @param action ation to process
   * @param scriptContext context
   * @return processing result
   * @throws ProcessingException when exception occured during action processing
   */
  public Object process(String action, ScriptContext scriptContext) throws ProcessingException {
    // process action
    return evaluator.process(action, scriptContext);
  }

  /**
   * Construct and return an array of files with compile classpath
   *
   * @param classesDir classes dir
   * @param libDir lib dir
   * @return file array
   */
  public List<File> constructClasspath(File classesDir, File libDir) {
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

  /**
   * Create and return the new script context with pre-defined {@link #scriptContextParameters}.
   *
   * @return script context
   */
  public SimpleScriptContext geBaseScriptContext() {
    // create the new context
    SimpleScriptContext context = new SimpleScriptContext();
    // append script context parameters
    if (null!=scriptContextParameters) {
      for (Map.Entry<String, Object> entry : scriptContextParameters.entrySet()) {
        context.setAttribute(entry.getKey(), entry.getValue(), ScriptContext.ENGINE_SCOPE);
      }
    }
    // return context
    return context;
  }

}
