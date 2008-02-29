package org.webactions;

/**
 * This class represents an action sentence: an expression plus a suffix mapped
 * onto an object array of values.
 *
 * @author Aleksei Valikov.
 * @author Ivan Latysh <IvanLatysh@yahoo.ca>
 * @version 1.1
 */
public class Sentence {
  /**
   * Suffix delimiter.
   */
  public static final String SUFFIX_DELIMITER = "~.";

  /**
   * Sentence expression.
   */
  protected String expression;

  /**
   * Sentence suffix.
   */
  protected String suffix;

  /**
   * Sentence values.
   */
  protected Object[] values;

  /**
   * Constructs the sentence. Searches provided sentence string for suffix
   * delimiter and extracts expression and (possibly null) suffix.
   *
   * @param sentenceString sentence string.
   * @param values         sentence values.
   */
  public Sentence(final String sentenceString, final Object[] values) {
    // Find suffix delimiter
    final int sdPosition = sentenceString.indexOf(SUFFIX_DELIMITER);
    // If suffix delimiter is not found
    if (sdPosition < 0) {
      // Use the whole sentence string as expression
      expression = sentenceString;
      // Set suffix to null
      suffix = null;
    } else {
      // Use part before delimiter as expression
      expression = sentenceString.substring(0, sdPosition);
      // Use part after delimiter as suffix
      suffix = sentenceString.substring(sdPosition + SUFFIX_DELIMITER.length());
    }
    this.values = values;
  }

  /**
   * Returns sentence expression.
   *
   * @return Sentence expression.
   */
  public String getExpression() {
    return expression;
  }

  /**
   * Returns sentence suffix.
   *
   * @return Sentence suffix.
   */
  public String getSuffix() {
    return suffix;
  }

  /**
   * Returns sentence values.
   *
   * @return Sentence values.
   */
  public Object[] getValues() {
    return values;
  }

  /**
   *
   * @return Sentence value
   */
  public Object getValue() {
    if (null==values) return null;
    return (values.length>1 ?values :values[0]);
  }

  /**
   * Appends current values array.
   *
   * @param values values to append.
   */
  public void addValues(final Object[] values) {
    // Create new values array
    final Object[] values_ = new Object[this.values.length + values.length];
    // Copy old values
    System.arraycopy(this.values, 0, values_, 0, this.values.length);
    // Copy new values
    System.arraycopy(values, 0, values_, values.length, values.length);
    // Replace old values array
    this.values = values_;
  }
}