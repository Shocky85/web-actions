# Introduction #

This sample demonstrate how to configure and use the [`ActionAdapter`](http://code.google.com/p/web-actions/source/browse/trunk/impl/action-processor/src/flex/messaging/services/remoting/adapters/ActionAdapter.java) with BlazeDS.

_We are not covering web application setup or FLEX programming, but only topic of integrating `ActionAdapter` into BlazeDS._

# Summary #

As you may already know, Web Actions allow to use any JSR-223 complaint scripting engine for server-side scripting. And in this example we will use bundled Java engine to proxy FLEX requests to embedded [eXist](http://exist-db.org) XML database.

You can download the complete webapp here http://web-actions.googlecode.com/files/samples-blazeds.war. Just drop it into your servlet container and it will deploy to /blazeds-webapp url.

### BlazeDS configuration ###
Let's configure a default remoting service with id `web-actions` by specifying service class `flex.messaging.services.remoting.adapters.ActionAdapter`.
`ActionAdapter` will read it's configuration from a file specified in `actions-config`.
And since we are going to use Java engine we need to specify source, classes and lib directories for a Java compiler. Complete configuration will be:
```
  <services>
    <service id="remoting-service" class="flex.messaging.services.RemotingService">
      <adapters>
        <adapter-definition id="web-actions" class="flex.messaging.services.remoting.adapters.ActionAdapter" default="true">
          <properties>
            <actions-config>WEB-INF/actions.xml</actions-config>
            <actions-source-dir>WEB-INF/actions/src</actions-source-dir>
            <actions-classes-dir>WEB-INF/actions/classes</actions-classes-dir>
            <actions-compile-lib-dir>WEB-INF/actions/lib</actions-compile-lib-dir>
          </properties>
        </adapter-definition>
      </adapters>
      <!-- WebActions destination -->
      <destination id="web-actions">
      </destination>
      <default-channels>
        <channel ref="amf"/>
      </default-channels>
    </service>
  </services>
```

Next step is to configure an actions that will be called from FLEX application. Let's  add a basic CRUD actions to `actions.xml`.
```
  <!--
    Save xml document
  -->
  <action name="save" script-engine-name="java-file">
    <script>EXistAdapter.java</script>
  </action>

  <!--
    Execute XQuery
  -->
  <action name="query" script-engine-name="java-file">
    <script>EXistAdapter.java</script>
  </action>

  <!--
    Execute XUpdate
  -->
  <action name="update" script-engine-name="java-file">
    <script>EXistAdapter.java</script>
  </action>
```

Now configured action can be called directly from from FLEX application by it's name. First you need to define a Remote Object:
```
  <mx:RemoteObject id="actionsRO" endpoint="http://localhost:8080/amf" destination="web-actions"
    fault="actionFaultHandler(event)" showBusyCursor="true">    
    <mx:method name="query" result="queryResultHandler(event)" />    
    <mx:method name="save"/>
    <mx:method name="update"/>    
  </mx:RemoteObject>
```
Than you can call an action:
```
actionsRO.query("<contacts>{collection('contacts')}</contacts>")
```


---