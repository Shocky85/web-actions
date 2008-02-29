package org.actions;

import org.xml.sax.SAXException;

import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;
import javax.script.ScriptContext;
import javax.script.SimpleScriptContext;
import java.io.*;
import java.beans.IntrospectionException;

/**
 * Action processor.<br/>
 * <i><strong>Note :</strong> Implementation is thread safe.</i> 
 *
 * @author Ivan Latysh
 * @version 0.1
 * @since 27-Apr-2007 : 5:07:52 PM
 */
public class ActionEvaluator {

  /** Script engine manager, used to access script engine factories */
  protected final ScriptEngineManager engineManager = new ScriptEngineManager();

  /** Action map */
  protected final ActionMap actions = new ActionMap();

  /** Empty constructor */
  public ActionEvaluator() {
  }

  /**
   * Construct a new action evaluator and configure it from the given file
   *
   * @param configuration action configuration file
   * @throws ConfigurationException when unable to configure Action Processor
   * @throws java.io.IOException when unable to read action configuration from given file
   */
  public ActionEvaluator(File configuration) throws ConfigurationException, IOException {
    configure(configuration);
  }


  /**
   * Configure action processor
   *
   * @param configuration configuration
   * @throws ConfigurationException when unable to configure Action Processor
   * @throws java.io.IOException when unable to read action configuration from given file
   */
  public void configure(File configuration) throws ConfigurationException, IOException {
    FileInputStream in = null;
    if (!configuration.isFile()) throw new IllegalArgumentException("Configuration file {"+configuration.getAbsolutePath()+"} does not exist or not a file.");
    try {
      in = new FileInputStream(configuration);
      configure(in);
    } catch (Exception ex) {
      throw new ConfigurationException("Unable to configure from {"+configuration.getAbsolutePath()+"}", ex);
    } finally {
      if (null!=in) in.close();
    }
  }

  /**
   * Configure action processor
   *
   * @param configuration configuration
   * @throws ConfigurationException when unable to configure Action Processor
   */
  public void configure(InputStream configuration) throws ConfigurationException {
    // load action from given configuration
    try {
      loadActions(configuration, true);
    } catch (Exception e) {
      throw new ConfigurationException("Unable to load actions configuration.", e);
    }
  }

  /**
   * Process given action in default context
   *
   * @param action action name
   * @return evaluation result
   * @throws ProcessingException when action could not be processed
   */
  public Object process(String action) throws ProcessingException {
    return process(action, new SimpleScriptContext());
  }

  /**
   * Process given action in given context
   *
   * @param action action name
   * @param scriptContext evaluation context
   * @return evaluation result
   * @throws ProcessingException when action could not be processed
   */
  public Object process(String action, ScriptContext scriptContext) throws ProcessingException {
    Action _action = null;
    synchronized(actions) {
      _action = actions.get(action);
    }
    if (null==_action) throw new ProcessingException("Action {"+action+"} not found.");
    return processAction(_action, scriptContext);
  }

  /**
   * Process given action
   *
   * @param action action to process
   * @param scriptContext Context
   * @return action result
   * @throws ProcessingException when error occured  
   */
  protected Object processAction(Action action, ScriptContext scriptContext) throws ProcessingException {
    try {
      // get the scripting engine for an action
      ScriptEngine sEngine = getScriptEngineForAction(action);
      // Evaluate action in given context
      return sEngine.eval(action.getAction(), scriptContext);
    } catch (Exception ex) {
      throw new ProcessingException(ex);
    }    
  }

  /**
   * Return ScriptEngine for given action
   *
   * @param action action
   * @return ScriptEngine
   * @throws IllegalArgumentException when unable to obtain a script engine
   */
  protected ScriptEngine getScriptEngineForAction(Action action) throws IllegalArgumentException {
    if (null!=action.getScriptEngineName()) {
      return engineManager.getEngineByName(action.getScriptEngineName());
    } else if (null!=action.getScriptEngineExtention()) {
      return engineManager.getEngineByExtension(action.getScriptEngineExtention());
    } else if (null!=action.getScriptEngineMimeType()) {
      return engineManager.getEngineByMimeType(action.getScriptEngineMimeType());
    }
    throw new IllegalArgumentException("Unable to obtain ScriptEngine for action {"+action+"}");
  }

  /**
   * Load/Reload actions
   *
   * @param configuration actions XML
   * @param cleanFirst if existent actions should be cleared
   * @throws java.io.IOException when IOException occured
   * @throws org.xml.sax.SAXException when SAXException occured
   * @throws java.beans.IntrospectionException  when IntrospectionException occured
   */
  protected void loadActions(InputStream configuration, boolean cleanFirst) throws IOException, SAXException, IntrospectionException {
    synchronized(actions) {
      if (cleanFirst) actions.getActions().clear();
      // load actions from given config
      ActionMap _actions = ActionIO.readActions(configuration);
      if (null==_actions) throw new IOException("Error while reading actions.");
      // add all read actions
      for (Action action : _actions.getActions()) {
        actions.addAction(action);
      }
    }
  }

}
