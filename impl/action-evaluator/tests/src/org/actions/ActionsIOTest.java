package org.actions;

import junit.framework.TestCase;

import java.io.File;

/**
 * Actions IO test
 *
 * @author Ivan Latysh
 * @version 0.1
 * @since 28-Apr-2007 : 6:29:31 PM
 */
public class ActionsIOTest extends TestCase {

  File output;

  protected void setUp() throws Exception {
    // prepare output file
    output = new File(System.getProperty("user.dir"), "/tests/output/actions.xml");
    assertNotNull(output);
    if (!output.getParentFile().exists()) output.getParentFile().mkdirs();
    // make sure that file does not exist
    if (output.exists()) output.delete();
    assertFalse(output.exists());

    super.setUp();
  }

  /**
   * Test write actions
   */
  public void testWriteActions() {
    try {
      ActionMap actions = new ActionMap();
      // construct dummy action
      Action action = new Action();
      action.setName("testAction");
      action.setScriptEngineName("java-file");
      action.setScriptEngineExtention("java");
      action.setScriptEngineMimeType("application");
      action.setAction("Some text \n some more\n once again.");
      Action action2 = new Action();
      action2.setName("hello-world");
      action2.setScriptEngineName("java-file");
      action2.setScriptEngineExtention("java");
      action2.setScriptEngineMimeType("application");
      action2.setAction("System.out.println(\"Hello World\");");
      // add it to the map
      actions.addAction(action);
      actions.addAction(action2);
      // write it
      ActionIO.writeActions(actions, output);
      assertNotNull(output);
      assertTrue(output.exists());
      assertTrue(output.length()>0);
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  /**
   * Test read actions
   */
  public void testReadActions() {
    try {
      ActionMap actions = new ActionMap();
      // construct dummy action
      Action action = new Action();
      action.setName("testAction");
      action.setScriptEngineName("java-file");
      action.setScriptEngineExtention("java");
      action.setScriptEngineMimeType("application");
      action.setAction("Some text \n some more\n once again.");
      Action action2 = new Action();
      action2.setName("hello-world");
      action2.setScriptEngineName("java-file");
      action2.setScriptEngineExtention("java");
      action2.setScriptEngineMimeType("application");
      action2.setAction("System.out.println(\"Hello World\");");
      // add it to the map
      actions.addAction(action);
      actions.addAction(action2);
      // write it
      ActionIO.writeActions(actions, output);
      assertNotNull(output);
      assertTrue(output.exists());
      assertTrue(output.length()>0);
      // read action
      ActionMap _actions = ActionIO.readActions(output);
      assertNotNull(_actions);
      assertEquals(2, _actions.getActions().size());
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }
  
}
