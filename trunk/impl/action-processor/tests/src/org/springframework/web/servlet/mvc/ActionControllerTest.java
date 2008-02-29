package org.springframework.web.servlet.mvc;

import junit.framework.TestCase;
import org.actions.ActionEvaluator;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.webactions.Sentence;

import javax.script.ScriptContext;
import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * @author Ivan Latysh
 * @version 0.1
 * @since 11-May-2007 : 2:02:40 PM
 */
public class ActionControllerTest extends TestCase {

  /** Test action config file */
  File testActions;
  /** Controller that we are testing */
  ActionController controller;
  /** Action evaluator */
  ActionEvaluator evaluator;

  /** Parameters map */
  Map<String, String[]> parameters = new HashMap<String, String[]>();
  // Request Mock
  MockHttpServletRequest request = new MockHttpServletRequest(){
    public Map getParameterMap() {
      return parameters;
    }
  };  
  // Response mock
  MockHttpServletResponse reponse = new MockHttpServletResponse();

  // Response mock
  MockHttpServletResponse response = new MockHttpServletResponse();

  protected void setUp() throws Exception {
    // test actions file
    testActions = new File(System.getProperty("user.dir"), "/tests/data/actions.xml");
    // check test action config
    assertNotNull(testActions);
    assertTrue(testActions.isFile());
    assertTrue(testActions.length()>0);
    // create and configure evaluator
    evaluator = new ActionEvaluator();
    assertNotNull(evaluator);
    evaluator.configure(new FileInputStream(testActions));
    // create and service controller
    controller = new ActionController();
    assertNotNull(controller);
    controller.setEvaluator(evaluator);
    // add parameters to the map
    parameters.put("hello-world~.text", new String[]{"Hello"});

    super.setUp();
  }

  /**
   * Test parse sentences
   */
  public void testParseSentences() {
    TreeMap<Sentence, Sentence> sentences = controller.parseSentences(request);
    assertNotNull(sentences);
    assertEquals(1, sentences.size());
    assertEquals("hello-world", sentences.firstKey().getExpression());
    assertEquals("Hello", sentences.firstKey().getValue());
  }

  /**
   * Test new script context  
   */
  public void testGetScriptContext() {
    ScriptContext context = controller.getScriptContext(request, response);
    assertNotNull(context);
    assertEquals(request, context.getAttribute("request", ScriptContext.ENGINE_SCOPE));
    assertEquals(response, context.getAttribute("response", ScriptContext.ENGINE_SCOPE));
  }

  /**
   * Test process action method 
   */
  public void testProcessActions() {
    ScriptContext context = controller.getScriptContext(request, response);
    assertNotNull(context);
    TreeMap<Sentence, Sentence> sentences = controller.parseSentences(request);
    assertNotNull(sentences);
    Object result = controller.processActions(sentences, context);
    assertNull(result);
  }

  /**
   * Test entry methos
   */
  public void testHandleRequestInternal() {
    try {
      Object result = controller.handleRequestInternal(request, response);
      assertNull(result);
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

}
