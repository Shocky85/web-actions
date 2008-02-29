package org.springframework.web.servlet.mvc;

import org.actions.ActionEvaluator;
import org.springframework.web.servlet.ModelAndView;
import org.webactions.Sentence;
import org.webactions.SentenceParser;
import org.webactions.SimpleActionController;

import javax.script.ScriptContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.TreeMap;

/**
 * Controller parse request parameters and invoke ActionEvaluator 
 *
 * @author Ivan Latysh
 * @version 0.1
 * @since 11-May-2007 : 12:57:38 PM
 */
public class ActionController extends org.springframework.web.servlet.mvc.AbstractController {

  /** Action controller */
  private final SimpleActionController actionController = new SimpleActionController();

  /** Default view name */
  protected String defaultView = "index.html";

  /**
   * Return default view name
   * @return default view name
   */
  public String getDefaultView() {
    return defaultView;
  }

  /**
   * Set default view name
   * @param defaultView default view name to set
   */
  public void setDefaultView(String defaultView) {
    this.defaultView = defaultView;
  }

  /**
   * Method that perform an actual work
   *
   * @param httpServletRequest request
   * @param httpServletResponse response
   * @return model and view
   * @throws Exception when unable to handle request
   */
  public ModelAndView handleRequestInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
    // parse sentences
    TreeMap<Sentence, Sentence> sentences = parseSentences(httpServletRequest);
    // get ScriptContext
    ScriptContext context = actionController.getScriptContext(httpServletRequest, httpServletResponse);
    // process actions
    Object result = actionController.processActions(sentences, context);
    // check the result
    if (result instanceof ModelAndView) {
      return (ModelAndView)result;
    }
    // @todo handle ModelAndView return if action didn't return ModelAndView

    // return requested view 
    return getRequestedView(httpServletRequest);
  }

  /**
   * Return ModelAndView from requested resource.
   *
   * @param httpServletRequest request
   * @return ModelAndView from requested resource
   */
  protected ModelAndView getRequestedView(HttpServletRequest httpServletRequest) {
    // get requester URI
    String requestURI = httpServletRequest.getRequestURI();
    // remove leading slash
    if (requestURI.startsWith("/")) requestURI = requestURI.replaceFirst("/+", "");
    // if requested URI is blank or and with / append default view name
    if (requestURI.trim().length()==0 || requestURI.endsWith("/")) requestURI += defaultView;
    // return model and view
    return new ModelAndView(requestURI);
  }

  /**
   * Parse sentences from incoming request
   *
   * @param httpServletRequest request
   * @return parsed sentences
   */
  public TreeMap<Sentence, Sentence> parseSentences(HttpServletRequest httpServletRequest) {
    // @todo fix this dangerous casting
    return SentenceParser.parseSentence((Map<String, String[]>)httpServletRequest.getParameterMap());
  }


  /**
   * Return action evaluator
   *
   * @return action evaluator
   */
  public ActionEvaluator getEvaluator() {
    return actionController.getEvaluator();
  }

  /**
   * Set action evluator
   *
   * @param evaluator action evaluator
   */
  public void setEvaluator(ActionEvaluator evaluator) {
    actionController.setEvaluator(evaluator);
  }

  /**
   * Return script context parameters
   *
   * @return script context parameters
   */
  public Map<String, Object> getScriptContextParameters() {
    return actionController.getScriptContextParameters();
  }

  /**
   * Set script context parameters
   *
   * @param scriptContextParameters parameters
   */
  public void setScriptContextParameters(Map<String, Object> scriptContextParameters) {
    actionController.setScriptContextParameters(scriptContextParameters);
  }

  /**
   * Return script context parameters
   *
   * @return script context parameters
   */
  public ScriptContext getScriptContext(HttpServletRequest request, HttpServletResponse response) {
    return actionController.getScriptContext(request, response);
  }

  public Object processActions(TreeMap<Sentence, Sentence> sentences, ScriptContext context) {
    return actionController.processActions(sentences, context);
  }
}
