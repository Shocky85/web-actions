package org.actions;

/**
 * Configuration exception
 *
 * @author Ivan Latysh
 * @version 0.1
 * @since 27-Apr-2007 : 9:07:08 PM
 */
public class ConfigurationException extends Exception{

  public ConfigurationException() {
  }

  public ConfigurationException(String message) {
    super(message);
  }

  public ConfigurationException(String message, Throwable cause) {
    super(message, cause);
  }

  public ConfigurationException(Throwable cause) {
    super(cause);
  }
  
}
