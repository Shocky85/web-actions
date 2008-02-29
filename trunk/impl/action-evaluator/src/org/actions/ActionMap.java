package org.actions;

import java.util.*;

/**
 * Action map
 *
 * @author Ivan Latysh
 * @version 0.1
 * @since 28-Apr-2007 : 6:41:40 PM
 */
public class ActionMap {

  protected Map<String, Action> values = new HashMap<String, Action>(); 

  public Collection<Action> getActions() {
    return values.values();
  }

  public void addAction(Action action) {
    values.put(action.getName(), action);
  }

  public Action get(Object key) {
    return values.get(key);
  }
}
