package org.springframework.web.servlet.mvc;

import org.actions.ActionEvaluator;
import org.springframework.web.servlet.ModelAndView;
import org.webactions.Sentence;
import org.webactions.SentenceParser;

import javax.script.ScriptContext;
import javax.script.SimpleScriptContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Controller parse request parameters and delegate action processing to underlaying servlet action controller 
 *
 * @author Ivan Latysh
 * @version 0.1
 * @since 11-May-2007 : 12:57:38 PM
 */
public class ActionController extends org.springframework.web.servlet.mvc.AbstractController {

  /** Same view name */
  public static final String SAME_VIEW = ":this";

  /** Action evaluator */
  protected ActionEvaluator evaluator;

  /** Default view name */
  protected String defaultView = "index.xhtml";

  /** Script context parameters */
  protected Map<String, Object> scriptContextParameters;

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
    ScriptContext context = getScriptContext(httpServletRequest, httpServletResponse);
    // process actions
    Object result = process(sentences, context);

    // log some debug info
    if (logger.isDebugEnabled()){
      logger.debug("Actions processed with result {"+result+"}");
    }

    // result model and view
    ModelAndView modelAndView = null;

    // return requested view when result is null
    if (null==result) {
      return new ModelAndView(getRequestedViewName(httpServletRequest));
    }

    // check the result
    if (result instanceof ModelAndView) {
      // cast it
      modelAndView = (ModelAndView) result;
      // if view is not set or set to `:this` use the current view
      if (!modelAndView.hasView() || SAME_VIEW.equals(modelAndView.getViewName())) {
        modelAndView = new ModelAndView(httpServletRequest.getRequestURI() ,modelAndView.getModel());
      }
    } else if (result instanceof Map) {
      // user returned map to create a new Model and View
      modelAndView = new ModelAndView(getRequestedViewName(httpServletRequest), (Map)result);
    } else {
      // create a new Model and View from returned object
      Map<String, Object> model = new HashMap<String, Object>(1);
      model.put("result", result);
      modelAndView = new ModelAndView(httpServletRequest.getRequestURI(), model);
    }

    // return Model and View
    return modelAndView;
  }

  /**
   * Return requested view name
   *
   * @param httpServletRequest request
   * @return view name
   */
  protected String getRequestedViewName(HttpServletRequest httpServletRequest) {
    // get requester URI
    String requestURI = httpServletRequest.getRequestURI();
    // remove leading slash
    if (requestURI.startsWith("/")) requestURI = requestURI.replaceFirst("/+", "");
    // if requested URI is blank or and with / append default view name
    if (requestURI.trim().length()==0 || requestURI.endsWith("/")) {
      if (logger.isDebugEnabled()) logger.debug("Mapping request {"+requestURI+"} to {"+(requestURI + defaultView)+"}");
      requestURI += defaultView;
    }
    // return view name
    return requestURI;
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

  /**
   * Return script context parameters
   *
   * @return script context parameters
   */
  public final Map<String, Object> getScriptContextParameters() {
    return scriptContextParameters;
  }

  /**
   * Set script context parameters
   *
   * @param scriptContextParameters parameters
   */
  public void setScriptContextParameters(Map<String, Object> scriptContextParameters) {
    // copy all given parameters
    scriptContextParameters.clear();
    scriptContextParameters.putAll(scriptContextParameters);
  }

  /**
   * Return script context
   *
   * @param request Http Servlet Request
   * @param response Http Servlet Response
   * @return script context parameters
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

    // return context
    return context;
  }

}
