package org.wm.xml.transform.io;

import sun.io.ByteToCharConverter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

/**
 * Presents <code>Writer</code> as <code>OutputStream</code>. This adapter is
 * single-threaded.
 *
 * @author Aleksei Valikov
 */
public class WriterOutputStreamAdapter extends OutputStream {
  /**
   * Writer to stream to.
   */
  protected Writer writer;

  /**
   * Encoding-dependent byte-char converter.
   */
  protected ByteToCharConverter byteToCharConverter;

  /**
   * Single-byte conversion buffer.
   */
  private byte[] singleByteBuffer = new byte[1];

  /**
   * Constructs adapter for given writer with specified encoding. Use
   * 8-bit encodings only.
   *
   * @param writer   writer to construct adapter for.
   * @param encoding writer encoding - should be 8-bit encoding.
   * @throws UnsupportedEncodingException Thrown, if encoding is not known.
   */
  public WriterOutputStreamAdapter(final Writer writer, final String encoding)
    throws UnsupportedEncodingException {
    // Assign writer
    this.writer = writer;
    // Instantiate converter
    byteToCharConverter = ByteToCharConverter.getConverter(encoding);
  }

  /**
   * Converts byte into a character and writes it into the writer.
   *
   * @param b byte to write into the writer.
   * @throws IOException Thrown in case of conversion or writing problems.
   */
  public void write(final int b) throws IOException {
    // Put byte into the buffer
    singleByteBuffer[0] = (byte) b;
    // Write converted buffer into the writer
    writer.write(byteToCharConverter.convertAll(singleByteBuffer));
  }

  /**
   * Converts byte array into character array and writes in into the writer.
   *
   * @param b byte array.
   * @throws IOException Thrown in case of conversion or writing problems.
   */
  public void write(final byte[] b) throws IOException {
    writer.write(byteToCharConverter.convertAll(b));
  }

  /**
   * Flushes the adapted writer.
   *
   * @throws IOException Thrown in case of problems flushing the adapted writer.
   */
  public void flush() throws IOException {
    writer.flush();
  }

  /**
   * Closes the adapted writer.
   *
   * @throws IOException Thrown in case of problems closing the adapted writer.
   */
  public void close() throws IOException {
    writer.close();
  }
}