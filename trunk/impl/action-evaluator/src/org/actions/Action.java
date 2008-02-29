package org.actions;

/**
 * Action holder
 *
 * @author Ivan Latysh
 * @version 0.1
 * @since 27-Apr-2007 : 9:14:18 PM
 */
public class Action {

  protected String name;

  protected String scriptEngineName;

  protected String scriptEngineExtention;

  protected String scriptEngineMimeType;

  protected String action;


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getScriptEngineName() {
    return scriptEngineName;
  }

  public void setScriptEngineName(String scriptEngineName) {
    this.scriptEngineName = scriptEngineName;
  }

  public String getScriptEngineExtention() {
    return scriptEngineExtention;
  }

  public void setScriptEngineExtention(String scriptEngineExtention) {
    this.scriptEngineExtention = scriptEngineExtention;
  }

  public String getScriptEngineMimeType() {
    return scriptEngineMimeType;
  }

  public void setScriptEngineMimeType(String scriptEngineMimeType) {
    this.scriptEngineMimeType = scriptEngineMimeType;
  }


  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }
}
