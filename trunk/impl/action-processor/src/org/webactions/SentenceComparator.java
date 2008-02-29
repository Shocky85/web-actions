package org.webactions;

import java.util.Comparator;

/**
 * This class is used to compare sentences so that they could be processed in
 * a defined order.
 *
 * @author Aleksei Valikov
 * @author Ivan Latysh <IvanLatysh@yahoo.ca>
 * @version 1.1
 */
public class SentenceComparator implements Comparator {
  /**
   * Compares two objects as sentences. Rules are as follows:
   * <ul>
   * <li>two non-sentence objects are equal;</li>
   * <li>non-sentence is less than sentence;</li>
   * <li>null is less than sentence;</li>
   * <li>sentence A is less than sentence B if suffix of A is less than
   * suffix of B in lexicographical order;</li>
   * <li>sentence A is less than sentence B if suffix of A equals
   * suffix of B and expression of A is less than expression of B in
   * lexicographical order;</li>
   * <li>sentence A equals sentence B, if their expressions and suffixes are
   * pairwise equals in lexicographical order.</li>
   * </ul>
   *
   * @param arg0 the first argument.
   * @param arg1 the second argument.
   * @return a negative integer, zero, or a positive integer as the first
   *         argument is less than, equal to, or greater than the second.
   * @see Comparator#compare(Object,Object)
   */
  public int compare(final Object arg0, final Object arg1) {
    // If both arguments are not sentences, they are equal
    if (!(arg0 instanceof Sentence) && !(arg1 instanceof Sentence)) {
      return 0;
    }
    // If arg0 is not sentence and arg1 is, arg1 is greater
    else if (!(arg0 instanceof Sentence) && arg1 instanceof Sentence) {
      return -1;
    }
    // If arg1 is not sentence and arg0 is, arg0 is greater
    else if (!(arg1 instanceof Sentence)) {
      return 1;
    }
    // If both arguments are sentences
    else {
      final Sentence sentence0 = (Sentence) arg0;
      final Sentence sentence1 = (Sentence) arg1;

      // If suffixes are equal, compare expression
      if (sentence0.getSuffix() == null && sentence1.getSuffix() != null)
        return -1;
      else if (sentence0.getSuffix() != null && sentence1.getSuffix() == null)
        return 1;
      else if (sentence0.getSuffix() == null && sentence1.getSuffix() == null)
        return sentence0.getExpression().compareTo(sentence1.getExpression());
      else if (sentence0.getSuffix().equals(sentence1.getSuffix()))
        return sentence0.getExpression().compareTo(sentence1.getExpression());
      else
        // Otherwise, compare suffixes
        return sentence0.getSuffix().compareTo(sentence1.getSuffix());
    }
  }
}