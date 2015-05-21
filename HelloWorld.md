# Hello World #

> It is a good practice to start from something simple and progress to more complex things. We also will follow this approach in our 'Hello World' application.

> Before we begin, if you wish to have the full application in front of you, please download it [here](http://web-actions.googlecode.com/files/helloworld.war).

> So let's begin and set up a simple Spring web application. Your WEB-INF/web.xml will look:

```
<?xml version="1.0" encoding="ISO-8859-1"?>
<web-app version="2.4" xmlns="http://java.sun.com/xml/ns/j2ee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd">

  <context-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>/WEB-INF/applicationContext.xml</param-value>
  </context-param>
  
  <listener>
    <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
  </listener>

  <servlet>
    <servlet-name>dispatcher</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
    <load-on-startup>1</load-on-startup>
  </servlet>

  <servlet-mapping>
    <servlet-name>dispatcher</servlet-name>
    <url-pattern>*.xhtml</url-pattern>
  </servlet-mapping>

  <welcome-file-list>
    <welcome-file>index.xhtml</welcome-file>    
  </welcome-file-list>

</web-app>
```

Also we need a blank WEB-INF/applicationContext.xml:
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xmlns:context="http://www.springframework.org/schema/context"
		xmlns:tx="http://www.springframework.org/schema/tx"
		xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
				http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-2.5.xsd
				http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-2.5.xsd">

</beans>
```

and WEB-INF/dispatcher-servlet.xml
```
<?xml version="1.0" encoding="UTF-8"?>

<beans xmlns="http://www.springframework.org/schema/beans"
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:context="http://www.springframework.org/schema/context"
		xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
				http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-2.5.xsd">

  <description>Hello World WebApp</description>
</beans>
```

So we have got a simple Spring web application that does nothing at all. Now comes an interesting part, let's define the beans.
First comes Action Controller, this controller parse incoming actions and delegate execution to an action evaluator:
```
  <bean id="actionController" class="org.springframework.web.servlet.mvc.ActionController">
    <!-- Action evaluator -->
    <property name="evaluator" ref="actionEvaluator"/>
...
</bean>
```

Now we need to configure an Action Evaluator:
```
  <bean id="actionEvaluator" class="org.actions.ActionEvaluator">
    <constructor-arg type="java.io.File" value="WEB-INF/actions.xml"/>
  </bean>
```

Action Evaluator need a configuration file to be passed to it's constructor.
A simple action configuration file:
```
<?xml version="1.0" ?>
<actions>
</actions>
```

Note that Action Evaluator works with a standard ScriptContext that can be pre-configured in Action Controller. For out application we will use 2 script engines, Java and JavaScript. For Java engine we need to have some additional configuration. So fully configured Action Controller will look like:
```
  <bean id="actionController" class="org.springframework.web.servlet.mvc.ActionController">
    <!-- Action evaluator -->
    <property name="evaluator" ref="actionEvaluator"/>
    <!-- Set default view -->
    <property name="defaultView" value="index.xhtml"/>
    <!-- ScriptContext parameters, will be added to each ScriptContext -->
    <property name="scriptContextParameters">
      <map>
        <!-- Action src directory -->
        <entry key="JFSE_SOURCE_DIR">
          <value type="java.io.File">WEB-INF/jactions/src</value>
        </entry>
        <!-- Action classes directory -->
        <entry key="JFSE_CLASSES_DIR">
          <value type="java.io.File">WEB-INF/jactions/classes</value>
        </entry>
        <!-- Additional compile classpath -->
        <entry key="JFSE_COMPILE_CLASSPATH">
          <list>
            <value type="java.io.File">WEB-INF/jactions/lib/servlet-api.jar</value>
          </list>
        </entry>
      </map>
    </property>
  </bean>
```

We have configured Action Controller and Action Evaluator now we need to map them so they can do some work for us. We will use a standard URL handler:
```
  <bean id="urlMapping" class="org.springframework.web.servlet.handler.SimpleUrlHandlerMapping">
    <property name="mappings">
      <props>
        <prop key="**/*.xhtml">actionController</prop>        
        <prop key="/">actionController</prop>
      </props>
    </property>
  </bean>
```
Note that action controller mapped twice to handle urls pointed to directory such as `http://www.host.com/personal`. Also Action controller has a parameter `defaultView` that takes care of this situation. When URI does not have a resource name, Action Controller will use pre-configured default view.

To complete our configuration let's configure view resolver, we are going to use a standard internal resource view with improved Xslt View:
```
  <bean id="viewResolver" class="org.springframework.web.servlet.view.InternalResourceViewResolver">
    <property name="viewClass" value="org.springframework.web.servlet.view.XsltView"/>
    <property name="prefix" value="/"/>
    <property name="suffix" value=".xslt"/>
  </bean>
```

Now let's create a XSLT page with a simple form:
```
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output method="html" encoding="ISO-8859-1" indent="yes"/>

  <!-- name parameters -->
  <xsl:param name="name"/>
  <!-- from parameters -->
  <xsl:param name="from"/>

  <xsl:template match="/">
    <html>
      <head/>
      <body>
        
        <div style="background: yellow; margin:10px; padding-left: 15px; border: solid black;">
        <h2> Hello <xsl:value-of select="if ($name) then $name else 'world'"/> from <xsl:value-of select="if ($from) then $from else 'web-actions framework'"/> !</h2>
        </div>

        <p>
          <h3>JavaScript Action</h3>
          <form method="post">
            Name: <input type="text" name="sayJavaScriptHello~.name"/> &#xA0; <input type="submit" value="Say Hello"/>
          </form>
        </p>

      </body>
    </html>

  </xsl:template>

</xsl:stylesheet>
```

Note: as you know XSLT should be executed against an XML document, and you have seen none so far. The reason for it is that improved XSLT view, that we are using, execute XSL template against an XML representation of the request.

Now about our form, you see that it has no target it will be posted to itself, one more advantage of an Action Controller is that you can post your form to any xhtml page. Let's pay an extra attention to text box name it defines an action name and a parameter name.

And the last step is to actually a write an action. As you remember actions configured in `actions.xml` file, so let's add our action to it:
```
<?xml version="1.0" ?>
  <actions>
  
    <!-- JavaScript action -->
    <action name="sayJavaScriptHello" script-engine-extention="js">
      <script><![CDATA[                
        // create a new result map
        var result = new java.util.HashMap();
        // add name
        result.put("name", context.getAttribute("name"));
        // add from parameter
        result.put("from", "JavaScript");
        // return result map
        result
      ]]>
      </script>
    </action>

  </actions>
```
This simple action collect submitted parameter and return the data map that will be used to construct Model And View.

We have completed a simple Hello World application.