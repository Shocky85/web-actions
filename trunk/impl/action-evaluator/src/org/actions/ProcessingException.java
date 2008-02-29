package org.actions;

/**
 * Action Processing exception, thrown when action could not be processed
 *
 * @author Ivan Latysh
 * @version 0.1
 * @since 27-Apr-2007 : 9:08:53 PM
 */
public class ProcessingException extends Exception {

  public ProcessingException() {
  }

  public ProcessingException(String message) {
    super(message);
  }

  public ProcessingException(String message, Throwable cause) {
    super(message, cause);
  }

  public ProcessingException(Throwable cause) {
    super(cause);
  }
}
