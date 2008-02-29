package org.actions;

import javax.script.ScriptEngineManager;
import javax.script.ScriptEngineFactory;
import java.util.List;

/**
 * @author Ivan
 * @version 0.1
 * @since 27-Apr-2007 : 4:57:04 PM
 */
public class Main {

  public static void main(String[] args) {
    ScriptEngineManager engineManager = new ScriptEngineManager();
    List<ScriptEngineFactory> factories = engineManager.getEngineFactories();
    for (ScriptEngineFactory factory: factories) {
      System.out.println("Factory="+factory.getEngineName());
    }
  }

}
