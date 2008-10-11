package org.webactions;

import org.apache.log4j.Logger;

import javax.script.ScriptContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;

/**
 * Action controller to be used in Servlets<br/>
 *
 * @author Ivan Latysh <ivan@yourmail.com>
 * @version 0.2
 * @since 25-May-2007 12:53:44 PM
 */
public class ServletJavaActionController extends JavaActionController {

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
   * Set application home.
   * This path is used to recover library and classes paths. 
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
  public ServletJavaActionController() {
    super();
  }

  /**
   * Construct a new ScriptContext with basic environmental parameters
   *
   * @param request request
   * @param response response
   * @return ScriptContext
   */
  public ScriptContext getScriptContext(HttpServletRequest request, HttpServletResponse response) {
    // create a new script context
    ScriptContext context = geScriptContext();

    // set compile classpath
    List<File> compileClasspath = constructClasspath(
        new File[] {actionClassesDir, new File(applicationHome, "/WEB-INF/classes")},
        new File[] {actionLibsDir, new File(applicationHome, "/WEB-INF/lib")});
    // check if compile classpath has been already defined
    if (null!=context.getAttribute(JFSE_COMPILE_CLASSPATH)) {
      List<File> addClasspath = (List<File>) context.getAttribute(JFSE_COMPILE_CLASSPATH);
      compileClasspath.addAll(addClasspath);
    }
    // set classpath
    context.setAttribute(JFSE_COMPILE_CLASSPATH, compileClasspath, ScriptContext.ENGINE_SCOPE);

    // set class loader
//    context.setAttribute(JFSE_CLASS_LOADER, getScriptClassLoaderForContext(context), ScriptContext.ENGINE_SCOPE);

    // add some environmental parameters
    context.setAttribute("request", request, ScriptContext.ENGINE_SCOPE);
    context.setAttribute("response", response, ScriptContext.ENGINE_SCOPE);    
    // return it
    return context;
  }

}