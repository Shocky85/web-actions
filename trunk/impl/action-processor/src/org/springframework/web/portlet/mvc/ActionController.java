package org.springframework.web.portlet.mvc;

import org.webactions.SimpleActionController;
import org.webactions.Sentence;
import org.webactions.SentenceParser;
import org.springframework.web.portlet.ModelAndView;
import org.actions.ActionEvaluator;

import javax.portlet.*;
import javax.script.ScriptContext;
import java.util.*;

/**
 * Controller parse incoming parameters into sentences and invoke requested actions using ActionEvaluator
 *
 * @author Ivan Latysh
 * @version 0.1
 * @since 27-Jun-2007 : 18:57:38 PM
 */
public class ActionController extends org.springframework.web.portlet.mvc.AbstractController {
  // Error prefix ACP00001

  /** Action controller */
  private final SimpleActionController actionController = new SimpleActionController();

  /** Last or default view */
  protected String view = "index";

  /** Application objects */
  protected final Map<String, Object> appObjects = new HashMap<String, Object>();

  /**
   * Return last view
   *
   * @return view
   */
  public String getView() {
    if (null==view || view.trim().length()==0) view = "index";
    return view;
  }

  /**
   * Set view
   *
   * @param view view to set
   */
  public void setView(String view) {
    this.view = view;
  }

  /**
   * Return application object map
   *
   * @return objects map or null
   */
  public final Map<String, Object> getAppObjects() {
    return appObjects;
  }

  /**
   * Set application objects map
   *
   * @param appObjects object map
   */
  public void setAppObjects(Map<String, Object> appObjects) {
    this.appObjects.putAll(appObjects);
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
   * Render view.<br/>
   * Will render last set view or default view
   *
   * @param request request
   * @param response response
   * @return ModelAndView
   * @throws Exception never
   */
  protected ModelAndView handleRenderRequestInternal(RenderRequest request, RenderResponse response) throws Exception {
    // check if view has been given
    if (null!=request.getParameter("view")) view = request.getParameter("view");
    // return view
    return new ModelAndView(view, "application", appObjects);
  }

  /**
   * Handle action request
   *
   * @param request request
   * @param response response
   * @throws Exception when unable to process actions
   */
  protected void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
    try {
      // check if view has been given
      if (null!=request.getParameter("view")) view = request.getParameter("view");      
      // parse sentences
      TreeMap<Sentence, Sentence> sentences = SentenceParser.parseSentence((Map<String, String[]>)request.getParameterMap());
      // get ScriptContext
      ScriptContext context = actionController.getScriptContext(request, response);
      // add application objects
      context.setAttribute("application", appObjects, ScriptContext.ENGINE_SCOPE);
      // process actions
      Object result = null;
      synchronized(appObjects) {
        result = actionController.processActions(sentences, context);
      }
      // check the result
      if (result instanceof org.springframework.web.servlet.ModelAndView) {
        view = ((org.springframework.web.servlet.ModelAndView)result).getViewName();
      }
    } catch (Exception e) {
      logger.error("[ACP00001] Unable process actions.", e);
      throw new PortletException(e);
    }
  }
}
