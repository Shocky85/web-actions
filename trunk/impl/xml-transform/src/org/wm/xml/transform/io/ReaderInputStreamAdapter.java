package org.wm.xml.transform.io;

import sun.io.CharToByteConverter;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

/**
 * Presents <code>Reader</code> as <code>InputStream</code>. This adapter is
 * single-threaded.
 *
 * @author Aleksei Valikov
 */
public class ReaderInputStreamAdapter extends InputStream {
  /**
   * Encoding.
   */
  protected String encoding;

  /**
   * Character to byte converter.
   */
  protected CharToByteConverter charToByteConverter;

  /**
   * Reader to read from.
   */
  protected Reader reader;

  /**
   * Conversion buffer.
   */
  protected byte[] buffer = null;

  /**
   * Position in conversion buffer.
   */
  protected int position = -1;

  /**
   * Constructs a new reader to input stream adapter.
   *
   * @param reader   reader to adapt
   * @param encoding reader encoding.
   * @throws UnsupportedEncodingException In case encoding is not supported.
   */
  public ReaderInputStreamAdapter(final Reader reader, final String encoding)
    throws UnsupportedEncodingException {
    this.reader = reader;
    charToByteConverter = CharToByteConverter.getConverter(encoding);
  }

  public int read() throws IOException {
    if (buffer == null || position < 0 || position >= buffer.length) {
      final int next = reader.read();
      if (next == -1)
	return -1;
      final char ch = (char) next;
      buffer = charToByteConverter.convertAll(new char[]{ch});
      position = 0;
    }
    return (int) buffer[position++];
  }

  public void close() throws IOException {
    reader.close();
  }
}