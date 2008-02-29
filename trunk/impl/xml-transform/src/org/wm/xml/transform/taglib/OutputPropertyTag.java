package org.wm.xml.transform.taglib;

import org.apache.taglibs.standard.tag.common.core.NullAttributeException;
import org.apache.taglibs.standard.tag.el.core.ExpressionUtil;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

/**
 * <code>param</code> tag that adds named parameter to parent action
 * processing context.
 */
public class OutputPropertyTag extends BodyTagSupport {
  /**
   * Name object.
   */
  protected String name;

  /**
   * Name object expression.
   */
//  protected String nameExpression;

  /**
   * Value object.
   */
  protected Object value;

  /**
   * Value object expression.
   */
  protected String valueExpression;

  /**
   * OutputPropertyTag constructor. Calls parent constructor and then performs
   * initialization.
   */
  public OutputPropertyTag() {
    super();
    init();
  }

  /**
   * Initialization. Sets inner fields to null.
   */
  private void init() {
    name = null;
    value = null;
//    nameExpression = null;
    valueExpression = null;
  }

  /**
   * Releases tag resources.
   */
  public void release() {
    name = null;
    value = null;
//    nameExpression = null;
    valueExpression = null;
  }

  /**
   * Processes opening tag. Evaluates attribute expressions and calls
   * inherited method.
   *
   * @return Result of inherited method invocation.
   * @throws javax.servlet.jsp.JspException if there is a problem evaluating expression
   *                                        or invoking parent method.
   */
  public int doStartTag() throws JspException {
    // Evaluate expression given in attributes
    evaluateExpressions();
    // Call inherited method
    return super.doStartTag();
  }

  /**
   * Processes closing tag. Sends name and value of the parameter to the
   * parent tag.
   *
   * @return Result of end tag processing.
   * @throws javax.servlet.jsp.JspException if problem with this tag occurs (ex.
   *                                        <code>param<code> tag is placed outside parent <code>process</code> tag).
   */
  public int doEndTag() throws JspException {
    // Find parent Tag
    final Tag tag = findAncestorWithClass(this, ReadTag.class);
    // Check that parent tag exists
    if (tag == null) throw new JspTagException("Param tag is outside " + ReadTag.class.getName() + " tag.");
    final ReadTag processTag = (ReadTag) tag;
    // Calculate the value
    Object value = this.value;
    if (value == null) {
      if (bodyContent == null || bodyContent.getString() == null)
        value = "";
      else
        value = bodyContent.getString().trim();
    }
    // Pass the value to parent tag
    processTag.addParameter(name, value);
    return EVAL_PAGE;
  }

  /**
   * Sets name object expression.
   *
   * @param name name object expression.
   */
  public void setName(final String name) {
    this.name = name;
  }

  /**
   * Sets value object expression.
   *
   * @param value value object expression.
   */
  public void setValue(final Object value) {
    if (value instanceof String) {
      valueExpression = (String) value;
      this.value = null;
    } else {
      this.value = value;
      valueExpression = null;
    }

  }

  /**
   * Evaluates attribute expressions.
   *
   * @throws javax.servlet.jsp.JspException if an expression evaluation error occurs.
   */
  private void evaluateExpressions() throws JspException {
    // Evaluate name attribute
//    name = (String) ExpressionUtil.evalNotNull("param", "name", nameExpression, String.class, this, pageContext);
    // Evaluate value attribute
    if (null != valueExpression) {
      value = ExpressionUtil.evalNotNull("param", "value", valueExpression, Object.class, this, pageContext);
    } else if (null == value) {
      throw new NullAttributeException("param", "value");
    }
  }
}
