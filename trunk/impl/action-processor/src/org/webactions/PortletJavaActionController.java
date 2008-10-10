package org.webactions;

import org.apache.log4j.Logger;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.script.ScriptContext;
import javax.script.SimpleScriptContext;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Action processor to be used with portlets.<br/>
 *
 * @author Ivan Latysh <ivan@yourmail.com>
 * @version 0.2
 * @since 25-May-2007 12:53:44 PM
 */
public class PortletJavaActionController extends JavaActionController {

  /** logger */
  protected Logger logger = Logger.getLogger(this.getClass().getName());

  /** Application home */
  protected File applicationHome;

  /**
   * Return actions home
   *
   * @return actions home
   */
  public File getApplicationHome() {
    return applicationHome;
  }

  /**
   * Set application home
   *
   * @param applicationHome actions home
   * @throws Exception when unable to read libs dir
   */
  public void setApplicationHome(File applicationHome) throws Exception {
    this.applicationHome = applicationHome;
    File libs = new File(applicationHome, "WEB-INF/lib");

    // create a compile classpath
    // check read permissions
    if (null != System.getSecurityManager()) {
      try {
        System.getSecurityManager().checkRead(libs.getAbsolutePath());
      } catch (SecurityException e) {
        logger.error("Unable to read {" + libs.getAbsolutePath() + "}. Access denied.");
        throw e;
      }
    }
    // add classes dir
    compileClasspath.add(new File(applicationHome, "WEB-INF/classes"));
    // list jars
    File[] jars = libs.listFiles(new FilenameFilter() {
      public boolean accept(File dir, String name) {
        return null != name && name.endsWith(".jar");
      }
    });
    // add jars
    if (null != jars) compileClasspath.addAll(Arrays.asList(jars));
  }

  /**
   * Construct a new action processor
   */
  public PortletJavaActionController() {
    super();
  }

  /**
   * Create and return the new script context.<br/>
   * Method will pre-set java-file script engine config parameters
   *
   * @return script context
   */
  public ScriptContext geScriptContext() {
    // get base script context
    SimpleScriptContext context = geBaseScriptContext();
    // inject java-file script engine parameters
    context.setAttribute(JFSE_CLASSES_DIR, actionClassesDir, ScriptContext.ENGINE_SCOPE);
    context.setAttribute(JFSE_SOURCE_DIR, actionSourceDir, ScriptContext.ENGINE_SCOPE);
    context.setAttribute(JFSE_COMPILE_CLASSPATH, compileClasspath, ScriptContext.ENGINE_SCOPE);
    // return context
    return context;
  }

  /**
   * Construct a new ScriptContext with basic environmental parameters
   *
   * @param request request
   * @param response response
   * @return ScriptContext
   */
  public ScriptContext getScriptContext(ActionRequest request, ActionResponse response) {
    // create a new script context
    ScriptContext context = new SimpleScriptContext();
    // add pre-configured parameters
    if (null!=scriptContextParameters) {
      for (Map.Entry<String, Object> entry: scriptContextParameters.entrySet()) {
        context.setAttribute(entry.getKey(), entry.getValue(), ScriptContext.ENGINE_SCOPE);
      }
    }

    // set compile classpath
    List<File> compileClasspath = getAppClasspath(request);
    // check if compile classpath has been already defined
    if (null!=context.getAttribute(JFSE_COMPILE_CLASSPATH)) {
      List<File> addClasspath = (List<File>) context.getAttribute(JFSE_COMPILE_CLASSPATH);
      compileClasspath.addAll(addClasspath);
    }
    // set classpath
    context.setAttribute(JFSE_COMPILE_CLASSPATH, compileClasspath, ScriptContext.ENGINE_SCOPE);

    // set class loader
    context.setAttribute(JFSE_CLASS_LOADER, getScriptClassLoaderForContext(context), ScriptContext.ENGINE_SCOPE);

    // add some environmental parameters
    context.setAttribute("request", request, ScriptContext.ENGINE_SCOPE);
    context.setAttribute("response", response, ScriptContext.ENGINE_SCOPE);
    // return it
    return context;
  }

  /**
   * Construct and return an array of files with compile classpath
   *
   * @param request HttpServletRequest
   * @return file array
   */
  protected List<File> getAppClasspath(ActionRequest request) {
    // get app home
    File appHome = new File(request.getPortletSession().getPortletContext().getRealPath("/"));
    // webapp classes should be in WEB-INF/classes
    File appClasses = new File(appHome, "WEB-INF/classes");
    // webapp libs should be in WEB-INF/lib
    File appLib = new File(appHome, "WEB-INF/lib");

    // return classpath array
    return constructClasspath(appClasses, appLib);
  }

}