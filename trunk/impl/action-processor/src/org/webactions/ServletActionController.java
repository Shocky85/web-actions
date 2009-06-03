package org.webactions;

import org.actions.ActionEvaluator;
import org.actions.ProcessingException;
import org.apache.log4j.Logger;

import javax.script.ScriptContext;
import javax.script.SimpleScriptContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Action controller to be used in Servlets
 *
 * @author Ivan Latysh <ivan@yourmail.com>
 * @version 0.2
 * @since 25-May-2007 12:53:44 PM
 */
public class ServletActionController {

  /** logger */
  protected Logger logger = Logger.getLogger(this.getClass().getName());

  /** Application home */
  protected File applicationHome;

  /** Script context parameters */
  protected Map<String, Object> scriptContextParameters;

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
   * Return actions home
   *
   * @return actions home
   */
  public File getApplicationHome() {
    return applicationHome;
  }

  /**
   * Set application home.
   * This path is used to recover library and classes paths. 
   *
   * @param applicationHome actions home
   * @throws Exception when unable to read libs dir
   */
  public void setApplicationHome(File applicationHome) throws Exception {
    this.applicationHome = applicationHome;
  }

  /**
   * Construct a new action processor
   */
  public ServletActionController() {
    super();
  }

  /**
   * Construct a new ScriptContext with basic environmental parameters
   *
   * @param request request
   * @param response response
   * @return ScriptContext
   */
  public ScriptContext getScriptContext(HttpServletRequest request, HttpServletResponse response) {
    // create the new context
    SimpleScriptContext context = new SimpleScriptContext();
    // append script context parameters
    if (null!=scriptContextParameters) {
      for (Map.Entry<String, Object> entry : scriptContextParameters.entrySet()) {
        context.setAttribute(entry.getKey(), entry.getValue(), ScriptContext.ENGINE_SCOPE);
      }
    }
    // add some environmental parameters
    context.setAttribute("request", request, ScriptContext.ENGINE_SCOPE);
    context.setAttribute("response", response, ScriptContext.ENGINE_SCOPE);    
    // return it
    return context;
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
    // process actions
    Object reslt = evaluator.process(action, scriptContext);
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
        logger.debug("Expression result is {" + result + "}.");
      } catch (Exception ex) {
        logger.error("[WAC00002] Error processing expression {" + expression + "} in context {" + _context.toString() + "}.", ex);
      }
    }
    // Return evaluation result
    return result;
  }

}