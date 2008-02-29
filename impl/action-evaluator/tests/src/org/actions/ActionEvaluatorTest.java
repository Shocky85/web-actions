package org.actions;

import junit.framework.TestCase;

import javax.script.ScriptEngine;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Action evaluator Unit Tests
 *
 * @author Ivan Latysh
 * @version 0.1
 * @since 28-Apr-2007 : 5:50:28 PM
 */
public class ActionEvaluatorTest extends TestCase {

  ActionEvaluator evaluator;

  File testActions;

  protected void setUp() throws Exception {
    testActions = new File(System.getProperty("user.dir"), "/tests/data/actions.xml");
    // check test action config
    assertNotNull(testActions);
    assertTrue(testActions.exists());
    assertTrue(testActions.isFile());
    assertTrue(testActions.length()>0);

    evaluator = new ActionEvaluator();
    // check created processor
    assertNotNull(evaluator);
    assertEquals(0, evaluator.actions.getActions().size());
    super.setUp();
  }

  /**
   * Test scripting engine lookup
   */
  public void testGetScriptEngineForAction() {
    // construct dummy action
    Action action = new Action();
    action.setScriptEngineName("java-file");
    assertNotNull(action);
    assertEquals("java-file", action.getScriptEngineName());

    // lookup scripting engine by it's name
    ScriptEngine engine = evaluator.getScriptEngineForAction(action);
    assertNotNull(engine);
    assertEquals("java-file", engine.getFactory().getEngineName());

    // update action params
    action.setScriptEngineName(null);
    action.setScriptEngineExtention("java");
    // lookup by extention
    engine = evaluator.getScriptEngineForAction(action);
    assertNotNull(engine);
    assertEquals("java", engine.getFactory().getEngineName());

    // update action params
    action.setScriptEngineName(null);
    action.setScriptEngineExtention("js");
    // lookup by extention
    engine = evaluator.getScriptEngineForAction(action);
    assertNotNull(engine);
    assertEquals("Mozilla Rhino", engine.getFactory().getEngineName());
  }

  /**
   * Test load actions
   */
  public void testLoadActions() {
    InputStream in = null;
    try {
      in = new FileInputStream(testActions);
      // add a dummy action
      Action action = new Action();
      action.setScriptEngineName("java-file");
      assertNotNull(action);
      assertEquals("java-file", action.getScriptEngineName());
      evaluator.actions.addAction(action);
      // check that we have 1 action
      assertEquals(1, evaluator.actions.getActions().size());
      // load actions, do not clear
      evaluator.loadActions(in, false);
      // check how many has been loaded
      assertEquals(3, evaluator.actions.getActions().size());
      // load actions, and clear
      in = new FileInputStream(testActions);
      evaluator.loadActions(in, true);
      // check how many has been loaded
      assertEquals(2, evaluator.actions.getActions().size());
    } catch (Exception ex) {
      ex.printStackTrace();
      fail(ex.getMessage());
    } finally {
      if (null!=in) try {
        in.close();
      } catch (IOException e) {
        e.printStackTrace();
        fail(e.getMessage());
      }
    }
  }

}
