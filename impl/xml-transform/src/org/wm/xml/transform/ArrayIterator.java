package org.wm.xml.transform;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterates over an array of items. This utility class implements standard
 * iterator interface.
 *
 * @author Aleksei Valikov
 */
public class ArrayIterator implements Iterator {
  /**
   * Array to iterate over.
   */
  protected Object[] array;

  /**
   * Index of the current element.
   */
  private int index = 0;

  /**
   * Sets array to iterate over.
   *
   * @param array array to iterate over.
   */
  public void setArray(final Object[] array) {
    this.array = array;
    index = 0;
  }


  /**
   * Returns <code>true</code>, if current array is not null and has more
   * elements, <code>false</code otherwise.
   *
   * @return <code>true</code>, if current array is not null and has more
   *         elements, <code>false</code otherwise.
   */
  public boolean hasNext() {
    return array != null && index < array.length;
  }

  /**
   * Returns next element in the array and shifts current position.
   *
   * @return Next element in the array.
   * @throws NoSuchElementException if array is null, or iterator has already
   *                                reached the end of the array.
   */
  public Object next() {
    if (array == null || index < 0 || index >= array.length)
      throw new NoSuchElementException();
    return array[index++];
  }

  /**
   * Remove opertaion is not supported. This method always throws an exception.
   *
   * @throws UnsupportedOperationException thrown in any case, as remove
   *                                       operation is not supported.
   */
  public void remove() {
    throw new UnsupportedOperationException();
  }
}
/*
 * The contents of this file are subject to the Mozilla Public License Version 1.0 (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * See the License for the specific language governing rights and limitations under the License.
 *
 * The Original Code is: all this file.
 *
 * The Initial Developer of the Original Code is
 * Aleksei Valikov of Forschungszentrum Informatik (valikov@fzi.de).
 *
 * Portions created by (your name) are Copyright (C) (your legal entity). All Rights Reserved.
 *
 * Contributor(s): none.
 */