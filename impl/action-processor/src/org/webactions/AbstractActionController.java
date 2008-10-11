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
  /* Action config modification time */
  protected long actionConfigModificationTime;
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
   * Return action config last modified time
   *
   * @return last modified time
   */
  public long getActionConfigModificationTime() {
    return actionConfigModificationTime;
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
    this.actionConfigModificationTime = actionConfig.lastModified();
    configure();
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
    // reload configuration if needed
    if (actionConfig.lastModified() > actionConfigModificationTime) {
      try {
        logger.info("Reloading modified configuration file {"+actionConfig.getAbsolutePath()+"}.");
        reConfigure();
      } catch (Exception e) {
        throw new ProcessingException("Unable to re-configure action evaluator.", e);
      }
    }
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
  public List<File> constructClasspath(File[] classesDir, File[] libDir) {
    List<File> classpath = new ArrayList<File>();

    for (File f : classesDir) {
      if (f.isDirectory()) {
        classpath.add(f);
      } else {
        logger.warn("Webapp classes directory {"+f+"} does not exist.");
      }
    }

    for (File f : libDir) {
      logger.debug("Trying to load libraries from {"+ f.getAbsolutePath()+"}.");
      if (f.isDirectory()) {
        File[] libs = f.listFiles(new FilenameFilter(){
          public boolean accept(File dir, String name) {
            return null!=name && name.endsWith(".jar");
          }
        });
        classpath.addAll(Arrays.asList(libs));
      }
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

  /**
   * Configure action evaluator from {@link #actionConfig}
   *
   * @throws java.io.IOException when unable to load configuration
   * @throws org.actions.ConfigurationException when configuration failed
   */
  public void configure() throws IOException, ConfigurationException {
    evaluator.configure(actionConfig);
  }

  /**
   * Reload configuration from {@link #actionConfig}
   *
   * @throws java.io.IOException when unable to load configuration
   * @throws org.actions.ConfigurationException when configuration failed
   */
  public void reConfigure() throws IOException, ConfigurationException {
    configure();
  }

}
