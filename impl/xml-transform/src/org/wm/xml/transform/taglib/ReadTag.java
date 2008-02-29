package org.wm.xml.transform.taglib;

import org.wm.xml.transform.Tools;

import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.JspException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import java.util.Properties;
import java.util.HashMap;

/**
 * Helper tag to read SAX Source and write it to JspWriter,
 *
 * @author Ivan Latysh <IvanLatysh@yahoo.ca>
 */
public class ReadTag extends BodyTagSupport {

  /** Source object of the resource. */
  private Source source;

  private HashMap<String, Object> parameters = new HashMap<String, Object>();

  /**
   * Adds evaluation context parameter.
   *
   * @param name  parameter name.
   * @param value parameter value.
   */
  public void addParameter(final String name, final Object value) {
    parameters.put(name, value);
  }

  public Source getSource() {
    return source;
  }

  public void setSource(Source source) {
    this.source = source;
  }

  public int doEndTag() throws JspException {
    Properties props = null;
    // ommit XML Declaration if needed
    if (parameters.size()>0) {
      props = new Properties();
      props.putAll(parameters);      
    }
    try {
      if (null!=source) Tools.serialize(source, pageContext.getOut(), props);
    } catch (TransformerException e) {
      throw new JspException("Unable to serialize SAX Source.", e);
    }
    return super.doEndTag();
  }

}
