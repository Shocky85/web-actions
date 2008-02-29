package org.wm.xml.transform.taglib;

import javax.servlet.jsp.JspException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.File;

/**
 * Extension of standard <code>import</code> tag that is capable of saving
 * imported resource as source. Use <code>varSource</code> tag attribute
 * to specify name of the attribute, which will hold source object of
 * imported resource.
 *
 * @author Aleksei Valikov
 */
public class ImportTag
  extends org.apache.taglibs.standard.tag.el.core.ImportTag {
  /**
   * Name of the variable.
   */
  private String varSource;

  /**
   * Source object of the resource.
   */
  private Source source;

  /**
   * Tag constructor. Calls parent constructor and initializes self.
   */
  public ImportTag() {
    super();
    init();
  }

  /**
   * Initialization procedure.
   */
  private void init() {
// Reset field values
    varSource = null;
    source = null;
  }

  /**
   * Releases the tag.
   */
  public void release() {
    super.release();
    init();
  }

  /**
   * Sets name of source variable.
   *
   * @param varSource Name of the source variable.
   */
  public void setVarSource(final String varSource) {
    this.varSource = varSource;
  }

  /**
   * Performs start tag processing. Acquires external resource and saves it in
   * page context variable.
   *
   * @return Start tag processing result.
   * @throws JspException if there is a problem acquiring an external resource.
   */
  public int doStartTag() throws JspException {
    final int superResult = super.doStartTag();
    if (varSource != null) {
      source = acquireSource();
      pageContext.setAttribute(varSource, source);
    }
    return superResult;
  }

  /**
   * Performs end tag processing.
   *
   * @return End tag processing result.
   * @throws JspException if there was a problem with tag processing.
   */
  public int doEndTag() throws JspException {
    if (varSource == null)
      return super.doEndTag();

    return EVAL_PAGE;
  }

  /**
   * Acquires an external resource. Returned source object is a stream source;
   * in case of relative local URL it is resolved against location of the
   * application and source in constructed from the resolved file.
   *
   * @return Source object of the external resource.
   */

  private Source acquireSource() {
    if (isAbsoluteUrl(url)) {
      return new StreamSource(url);
    } else {
      final File file =
	new File(pageContext.getServletContext().getRealPath(url));
      return new StreamSource(file.toURI().toString());
    }
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