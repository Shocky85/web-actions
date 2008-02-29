package org.webactions;

import org.apache.log4j.Logger;

import java.util.Map;
import java.util.TreeMap;
import java.util.Arrays;

/**
 * Sentence parser
 *
 * @author Ivan Latysh <IvanLatysh@yahoo.ca>
 * @author Aleksei Valikov
 * @version 1.2
 */
public abstract class SentenceParser {

  /**
   * Sentence delimiter
   */
  public static final String SENTENCE_DELIMITER = ";";

  /**
   * logger
   */
  protected static Logger logger = Logger.getLogger(SentenceParser.class.getName());

  /**
   * No instances, please
   */
  private SentenceParser() {
  }

  /**
   * Parse sentences from given parameters map
   *
   * @param parameters parameters to parse sentences from
   * @return sentence map
   */
  public static TreeMap<Sentence, Sentence> parseSentence(Map<String, String[]> parameters) {
    TreeMap<Sentence, Sentence> sentencesMap = new TreeMap<Sentence, Sentence>(new SentenceComparator());
    // Re-arrange parameter map: separate parameters with delimiters
    // and unite separated parameters with equal names
    // Iterate over request parameters map entries
    for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
      // Get name of current entry (and trim it)
      final String parameterName = decode(null != entry.getKey() ? entry.getKey().trim() : null);
      // Get values of current entry
      final Object[] parameterValues = entry.getValue();
      // Iterate over delimited sentences in parameter name
      int offset = 0;
      final int length = parameterName.length();
      while (offset < length) {
        // Find the delimiter
        final int newOffset = parameterName.indexOf(SENTENCE_DELIMITER, offset);
        // Get string of current sentence
        final String currentSentenceString;
        // If delimiter is not found
        if (newOffset == -1) {
          // Current sentence is the whole remainder
          currentSentenceString = parameterName.substring(offset).trim();
          offset = length;
        } else {
          // Current sentence is name paret before the delimiter
          currentSentenceString = parameterName.substring(offset, newOffset).trim();
          offset = newOffset + 1;
        }
        // Create new sentence instance
        final Sentence newSentence = new Sentence(currentSentenceString, parameterValues);
        // Check that sentence has non-null suffix
        if (newSentence.getSuffix() != null) {
          // Check if equal sentence is already in the sentence map
          final Sentence sentence = sentencesMap.get(newSentence);
          // If equal sentence is there
          if (sentence != null) {
            // Unite values of these two sentences
            sentence.addValues(parameterValues);
          }
          // Otherwise
          else {
            // Add new sentence
            sentencesMap.put(newSentence, newSentence);
          }
        } else {
          logger.debug("Sentence [" + Arrays.toString(newSentence.getValues()) + "] in parameter [" + parameterName + "] has no suffix and will be ignored.");
        }
      }
    }

    // Return sentence map
    return sentencesMap;
  }

  /**
   * Decodes a string: replaces all <code>%XY</code> subsequences with
   * characters with <code>XY</code> hexadecimal code.
   *
   * @param str string to decode.
   * @return New string with replaced coded subsequences.
   */
  public static String decode(final String str) {
    if (str == null) return (null);

    final int len = str.length();
    final char[] chars = new char[len];
    str.getChars(0, len, chars, 0);
    return decode(chars);
  }

  /**
   * Decodes a character array: replaces all <code>%XY</code> subsequences
   * with characters with <code>XY</code> hexadecimal code.
   *
   * @param chars character array to decode.
   * @return New string with replaced coded subsequences.
   */
  public static String decode(final char[] chars) {
    if (chars == null) return (null);

    final int len = chars.length;
    int ix = 0;
    int ox = 0;
    while (ix < len) {
      char c = chars[ix++]; // Get byte to test
      if (c == '+') {
        c = ' ';
      } else if (c == '%') {
        c = (char) ((convertHexDigit(chars[ix++]) << 4) + convertHexDigit(chars[ix++]));
      }
      chars[ox++] = c;
    }
    return new String(chars, 0, ox);

  }

  /**
   * Converts hexadecimal character (0..F) to integer.
   *
   * @param b character to convert.
   * @return Integer corresponding to hexadecimal character ('0' = 0, ... ,
   *         'A' = 10, 'a' = 10 , ... , 'f' = 15, anything else = 0).
   */
  private static int convertHexDigit(final char b) {
    if ((b >= '0') && (b <= '9'))
      return (b - '0');
    if ((b >= 'a') && (b <= 'f'))
      return (b - 'a' + 10);
    if ((b >= 'A') && (b <= 'F'))
      return (b - 'A' + 10);
    return 0;
  }

}
