package flex.messaging.services.remoting.adapters;

import flex.management.runtime.messaging.services.ServiceAdapterControl;
import flex.management.runtime.messaging.services.remoting.adapters.ActionAdapterControl;
import flex.messaging.*;
import flex.messaging.config.ConfigMap;
import flex.messaging.config.ConfigurationConstants;
import flex.messaging.config.ConfigurationException;
import flex.messaging.config.SecurityConstraint;
import flex.messaging.log.Log;
import flex.messaging.log.LogCategories;
import flex.messaging.messages.Message;
import flex.messaging.messages.RemotingMessage;
import flex.messaging.services.ServiceAdapter;
import flex.messaging.services.remoting.RemotingDestination;
import flex.messaging.util.ExceptionUtil;
import flex.messaging.util.MethodMatcher;
import flex.messaging.util.StringUtils;
import org.webactions.Sentence;
import org.webactions.SentenceComparator;
import org.webactions.AbstractActionController;
import org.webactions.ServletJavaActionController;
import org.actions.ActionEvaluator;

import javax.script.ScriptContext;
import javax.script.SimpleScriptContext;
import java.security.AccessController;
import java.util.*;
import java.io.File;
import java.io.IOException;

/**
 * WebActions Adapter.
 * Allows to call an action direct from a FLEX app.
 *
 * @author Ivan Latysh
 * @since 9-Oct-2008 6:59:44 PM
 */
public class ActionAdapter extends ServiceAdapter {

  static final String LOG_CATEGORY = LogCategories.MESSAGE_REMOTING;

  // classes in these packages are accessible from flash clients only if permission is granted
  // in the server's policy file
  public static final String[] PROTECTED_PACKAGES = new String[]{
          "jrun", "jrunx", "macromedia", "flex", "flex2", "coldfusion",
          "allaire", "com.allaire", "com.macromedia"};

  // Error codes.
  public static final int REMOTING_METHOD_NULL_NAME_ERRMSG = 10658;
  public static final int REMOTING_METHOD_REFS_UNDEFINED_CONSTRAINT_ERRMSG = 10659;
  public static final int REMOTING_METHOD_NOT_DEFINED_ERRMSG = 10660;

  // ConfigMap property names for initialize(String, ConfigMap).
  public static final String PROPERTY_INCLUDE_METHODS = "include-methods";
  public static final String PROPERTY_EXCLUDE_METHODS = "exclude-methods";
  public static final String METHOD_ELEMENT = "method";
  public static final String NAME_ELEMENT = "name";

  /** The MBean control for this adapter. */
  protected ServiceAdapterControl controller;
  /** Map of exluded methods */
  protected Map<String, RemotingMethod> excludeMethods;
  /** Map of include methods */
  protected Map<String, RemotingMethod> includedMethods;
  /** Action controller */
  protected final ServletJavaActionController actionController = new ServletJavaActionController();
  /** Applicatio home dir */
  protected File appHome;

  /**
   * Constructs an unmanaged <code>EJBAdapter</code> instance.
   */
  public ActionAdapter() {
    this(false);
  }

  /**
   * Constructs a <code>EJBAdapter</code> instance.
   *
   * @param enableManagement <code>true</code> if the <code>EJBAdapter</code> has a
   *                         corresponding MBean control for management; otherwise <code>false</code>.
   */
  public ActionAdapter(boolean enableManagement) {
    super(enableManagement);
  }

  //----------------------------------
  //  destination
  //----------------------------------

  /**
   * Casts the <code>Destination</code> into <code>RemotingDestination</code>
   * and calls super.setDestination
   *
   * @param destination
   */
  public void setDestination(Destination destination) {
    Destination dest = (RemotingDestination) destination;
    super.setDestination(dest);
  }

  /**
   * Returns an <tt>Iterator</tt> over the currently registered exclude methods.
   */
  public Iterator getExcludeMethodIterator() {
    if (excludeMethods == null)
      return Collections.EMPTY_LIST.iterator();
    else
      return excludeMethods.values().iterator();
  }

  /**
   * Adds a method to the list of excluded methods for the adapter.
   * Invocations of excluded methods are blocked.
   */
  public void addExcludeMethod(RemotingMethod value) {
    String name = value.getName();
    if (name == null) {
      ConfigurationException ce = new ConfigurationException();
      ce.setMessage(REMOTING_METHOD_NULL_NAME_ERRMSG, new Object[]{getDestination().getId()});
      throw ce;
    }

    if (excludeMethods == null) {
      excludeMethods = new HashMap();
      excludeMethods.put(name, value);
    } else if (!excludeMethods.containsKey(name))
      excludeMethods.put(name, value);
  }

  /**
   * Removes a method from the list of excluded methods for the adapter.
   */
  public void removeExcludeMethod(RemotingMethod value) {
    excludeMethods.remove(value.getName());
  }

  /**
   * Returns an <tt>Iterator</tt> over the currently registered include methods.
   */
  public Iterator getIncludeMethodIterator() {
    if (includedMethods == null)
      return Collections.EMPTY_LIST.iterator();
    else
      return includedMethods.values().iterator();
  }

  /**
   * Adds a method to the list of included methods for the adapter.
   * Invocations of included methods are allowed, and invocations of any non-included methods will be blocked.
   */
  public void addIncludeMethod(RemotingMethod value) {
    String name = value.getName();
    if (name == null) {
      ConfigurationException ce = new ConfigurationException();
      ce.setMessage(REMOTING_METHOD_NULL_NAME_ERRMSG, new Object[]{getDestination().getId()});
      throw ce;
    }

    if (includedMethods == null) {
      includedMethods = new HashMap<String, RemotingMethod>();
      includedMethods.put(name, value);
    } else if (!includedMethods.containsKey(name))
      includedMethods.put(name, value);
  }

  /**
   * Removes a method from the list of included methods for the adapter.
   */
  public void removeIncludeMethod(RemotingMethod value) {
    includedMethods.remove(value.getName());
  }

  //--------------------------------------------------------------------------
  //
  // Initialize, validate, start, and stop methods.
  //
  //--------------------------------------------------------------------------
  public void initialize(String id, ConfigMap properties) {

    appHome = new File(FlexContext.getServletContext().getRealPath("/"));

    ConfigMap methodsToInclude = properties.getPropertyAsMap(PROPERTY_INCLUDE_METHODS, null);
    if (methodsToInclude != null) {
      List methods = methodsToInclude.getPropertyAsList(METHOD_ELEMENT, null);
      if ((methods != null) && !methods.isEmpty()) {
        int n = methods.size();
        for (int i = 0; i < n; i++) {
          ConfigMap methodSettings = (ConfigMap) methods.get(i);
          String name = methodSettings.getPropertyAsString(NAME_ELEMENT, null);
          RemotingMethod method = new RemotingMethod();
          method.setName(name);
          // Check for security constraint.
          String constraintRef = methodSettings.getPropertyAsString(ConfigurationConstants.SECURITY_CONSTRAINT_ELEMENT, null);
          if (constraintRef != null) {
            try {
              method.setSecurityConstraint(getDestination().getService().getMessageBroker().getSecurityConstraint(constraintRef));
            }
            catch (flex.messaging.security.SecurityException se) {
              // Rethrow with a more descriptive message.
              ConfigurationException ce = new ConfigurationException();
              ce.setMessage(REMOTING_METHOD_REFS_UNDEFINED_CONSTRAINT_ERRMSG, new Object[]{name, getDestination().getId(), constraintRef});
              throw ce;
            }
          }
          addIncludeMethod(method);
        }
      }
    }

    ConfigMap methodsToExclude = properties.getPropertyAsMap(PROPERTY_EXCLUDE_METHODS, null);
    if (methodsToExclude != null) {
      // Warn that <exclude-properties> will be ignored.
      if (includedMethods != null) {
        RemotingDestination dest = (RemotingDestination) getDestination();
        if (Log.isWarn())
          Log.getLogger(LogCategories.CONFIGURATION).warn("The remoting destination '" + dest.getId() + "' contains both <include-methods/> and <exclude-methods/> configuration. The <exclude-methods/> block will be ignored.");
      }
      // Excludes must be processed regardless of whether we add them or not to avoid 'Unused tags in <properties>' exceptions.
      List methods = methodsToExclude.getPropertyAsList(METHOD_ELEMENT, null);
      if ((methods != null) && !methods.isEmpty()) {
        int n = methods.size();
        for (int i = 0; i < n; i++) {
          ConfigMap methodSettings = (ConfigMap) methods.get(i);
          String name = methodSettings.getPropertyAsString(NAME_ELEMENT, null);
          RemotingMethod method = new RemotingMethod();
          method.setName(name);
          // Check for security constraint.
          String constraintRef = methodSettings.getPropertyAsString(ConfigurationConstants.SECURITY_CONSTRAINT_ELEMENT, null);
          // Conditionally add, only if include methods are not defined.
          if (includedMethods == null) {
            if (constraintRef != null) {
              RemotingDestination dest = (RemotingDestination) getDestination();
              if (Log.isWarn())
                Log.getLogger(LogCategories.CONFIGURATION).warn("The method '" + name + "' for remoting destination '" + dest.getId() + "' is configured to use a security constraint, but security constraints are not applicable for excluded methods.");
            }
            addExcludeMethod(method);
          }
        }
      }
    }

    ///////////////////////////////////
    // Configure ActionController
    ///////////////////////////////////

    // get actions-config
    String _actionsConfig = properties.getPropertyAsString("actions-config", null);
    if (null!=_actionsConfig) {
      File actionConfig = null;
        // configure Action Controller
        try {
          // get actions-source-dir
          actionController.setActionSourceDir(new File(appHome, properties.getPropertyAsString("actions-source-dir", "/WEB-INF/actions/src")));
          // get actions-classes-dir
          actionController.setActionClassesDir(new File(appHome, properties.getPropertyAsString("actions-classes-dir", "WEB-INF/actions/classes")));
          // get actions-compile-lib-dir
          actionController.setActionLibsDir(new File(appHome, properties.getPropertyAsString("actions-compile-lib-dir", "WEB-INF/actions/lib")));
          // set configuration file
          actionConfig = new File(appHome, _actionsConfig);
          actionController.setActionConfig(actionConfig);
        } catch (Exception e) {
          Log.getLogger(LogCategories.CONFIGURATION).fatal("Unable to configure action controller from file {"+(null!=actionConfig ?actionConfig.getAbsolutePath() :null)+"}.", e);
          throw new RuntimeException("Error configuring action controller from file {"+(null!=actionConfig ?actionConfig.getAbsolutePath() :null)+"}.", e);
        }
    } else {
      Log.getLogger(LogCategories.CONFIGURATION).error("Parameter 'actions-config' is not defined.");
    }
  }

  public void start() {
    if (isStarted()) {
      return;
    }
    super.start();
    validateInstanceSettings();

    RemotingDestination remotingDestination = (RemotingDestination) getDestination();
    if (FlexFactory.SCOPE_APPLICATION.equals(remotingDestination.getScope())) {
      FactoryInstance factoryInstance = remotingDestination.getFactoryInstance();
      createInstance(factoryInstance.getInstanceClass());
    }
  }

  /**
   * Invoke the method.
   *
   * @param message received message
   * @return invocation result
   */
  public Object invoke(Message message) {
    RemotingDestination remotingDestination = (RemotingDestination) getDestination();
    RemotingMessage remotingMessage = (RemotingMessage) message;

    /** operation to call */
    String methodName = remotingMessage.getOperation();

    // some debug info
    if (Log.isDebug()) {
      Log.getLogger(LOG_CATEGORY).debug("Processing the call {"+methodName+"} with parameters {"+Arrays.toString(remotingMessage.getParameters().toArray())+"}");
    }

    /** Result */
    Object result = null;

    try {
      // Test that the target method may be invoked based upon include/exclude method settings.
      if (includedMethods != null && includedMethods.containsKey(methodName)) {
        RemotingMethod method = includedMethods.get(methodName);
        if (method == null) MethodMatcher.methodNotFound(methodName, null, new MethodMatcher.Match(null));

        // Check method-level security constraint, if defined.
        SecurityConstraint constraint = method.getSecurityConstraint();
        if (constraint != null)
          getDestination().getService().getMessageBroker().getLoginManager().checkConstraint(constraint);
      } else if ((excludeMethods != null) && excludeMethods.containsKey(methodName)) {
        MethodMatcher.methodNotFound(methodName, null, new MethodMatcher.Match(null));
      }

      // get start time
      long start = -1;
      if (Log.isDebug()) {
        start = System.currentTimeMillis();
      }

      //////////////////////////
      // process action
      //////////////////////////

/*
      // Parse sentences
      TreeMap<Sentence, Sentence> sentences = new TreeMap<Sentence, Sentence>(new SentenceComparator());
      int i = 0;
      for (Object o : remotingMessage.getParameters()) {
        Sentence newSentence = new Sentence(methodName+"~.param-"+i, new Object[]{o});
        sentences.put(newSentence, newSentence);
        i++;
      }
*/

      // Get script context
      ScriptContext context = actionController.getScriptContext(FlexContext.getHttpRequest(), FlexContext.getHttpResponse());
      // add message
      context.setAttribute("message", remotingMessage, ScriptContext.ENGINE_SCOPE);

      // invoke action
      result = actionController.process(methodName, context);

      // show result if debug enabled
      if (Log.isDebug()) {
        Log.getLogger(LOG_CATEGORY).debug("Action {"+methodName+"} returned result {"+result+"}");
      }

    } catch (Exception ex) {
      // log the error
      Log.getLogger(LOG_CATEGORY).warn("Method Invocation Exception.", ex);
      /*
      * If the invocation exception wraps a message exception, unwrap it and
      * rethrow the nested message exception. Otherwise, build and throw a new
      * message exception.
      */
      Throwable cause = ex.getCause();
      if ((cause != null) && (cause instanceof MessageException)) {
        throw (MessageException) cause;
      } else if (cause != null) {
        // Log a warning for this client's selector and continue
        if (Log.isError()) {
          Log.getLogger(LOG_CATEGORY).error("Error processing remote invocation: " +
                  cause.toString() + StringUtils.NEWLINE +
                  "  incomingMessage: " + message + StringUtils.NEWLINE +
                  ExceptionUtil.toString(cause));
        }
        MessageException me = new MessageException(cause.getClass().getName() + " : " + cause.getMessage());
        me.setCode("Server.Processing");
        me.setRootCause(cause);
        throw me;
      } else {
        MessageException me = new MessageException(ex.getMessage());
        me.setCode("Server.Processing");
        throw me;
      }
    }

    return result;
  }

  //--------------------------------------------------------------------------
  //
  // Protected/private APIs
  //
  //--------------------------------------------------------------------------

  /**
   * This method returns the instance of the given class.  You can override this in
   * your subclass to control how the instance is constructed.  Note that you can
   * can more general control how components are created by implementing the
   * flex.messaging.FlexFactory interface.
   *
   * @see flex.messaging.FlexFactory
   */
  protected Object createInstance(Class cl) {
    RemotingDestination remotingDestination = (RemotingDestination) getDestination();
    // Note: this breaks the admin console right now as we use this to call
    // mbean methods.  Might have performance impact as well?
    //assertAccess(cl.getName());
    FactoryInstance factoryInstance = remotingDestination.getFactoryInstance();
    Object instance = factoryInstance.lookup();
    if (isStarted() && instance instanceof FlexComponent
            && !((FlexComponent) instance).isStarted()) {
      ((FlexComponent) instance).start();
    }
    return instance;
  }

  /**
   * This method is called by the adapter after the remote method has been invoked.
   * For session scoped components, by default FlexFactory provides an
   * operationComplete method to implement this operation.  For the JavaFactory,
   * this sets the attribute in the FlexSession to trigger sesison replication
   * for this attribute.
   */
  protected void saveInstance(Object instance) {
    RemotingDestination remotingDestination = (RemotingDestination) getDestination();
    FactoryInstance factoryInstance = remotingDestination.getFactoryInstance();
    factoryInstance.operationComplete(instance);
  }

  protected void assertAccess(String serviceClass) {
    SecurityManager sm = System.getSecurityManager();
    if (sm != null) {
      // if there is a SecurityManager, check for specific access privileges on this class
      if (serviceClass.indexOf(".") != -1) {
        StringBuffer permissionData = new StringBuffer("accessClassInPackage.");
        permissionData.append(serviceClass.substring(0, serviceClass.lastIndexOf(".")));
        RuntimePermission perm = new RuntimePermission(permissionData.toString());
        AccessController.checkPermission(perm);
      }
    } else {
      // even without a SecurityManager, protect server packages
      for (int i = 0; i < PROTECTED_PACKAGES.length; i++) {
        if (serviceClass.startsWith(PROTECTED_PACKAGES[i])) {
          StringBuffer permissionData = new StringBuffer("accessClassInPackage.");
          permissionData.append(PROTECTED_PACKAGES[i].substring(0, PROTECTED_PACKAGES[i].length()));
          RuntimePermission perm = new RuntimePermission(permissionData.toString());
          AccessController.checkPermission(perm);
        }
      }
    }
  }

  protected void validateInstanceSettings() {
    RemotingDestination remotingDestination = (RemotingDestination) getDestination();
    // This will validate that we have a valid factory instance and accesses
    // any constructor properties needed for our factory so they do not give
    // startup warnings.
    remotingDestination.getFactoryInstance();
  }

  /**
   * Invoked automatically to allow the <code>EJBAdapter</code> to setup its corresponding
   * MBean control.
   *
   * @param destination The <code>Destination</code> that manages this <code>EJBAdapter</code>.
   */
  protected void setupAdapterControl(Destination destination) {
    controller = new ActionAdapterControl(this, destination.getControl());
    controller.register();
    setControl(controller);
  }

}