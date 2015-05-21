# Introduction #

If you are reading this, you probably had enough of Adobe mess and just want things to work!
Unfortunately Adobe won't do a thing, except saying that they will not fix the issues.
Remember "Ignorance is a bliss" !

But if you run Tomcat (standalone or embedded) on the server and you have control over the web application, you may want to try Ivan's fix.

# Details #

The idea of a fix is to pass basic authentication tokens to the server as a parameter and turn them into proper headers on the server.

Step 1 - Download Tomcat valve and put it into Tomcat libs folder.

> http://web-actions.googlecode.com/files/flex-basic-authenticator-valve.jar


Step 2 - Configure webapp context (META-INF/context.xml):
```
<?xml version="1.0" encoding="UTF-8"?>
<Context>
  <Valve className="org.apache.catalina.valves.FlexBasicAuthenticatorValve" />
  <!-- Uncomment the following line if you are using standard Tomcat BasicAuthenticator -->
  <!-- If you are using Spring, leave it as is -->
  <!--<Valve className="org.apache.catalina.authenticator.BasicAuthenticator" />-->
</Context>
```

Step 3 - Pass basic authentication parameters like so
```
// a request
var httpService:HTTPService = new HTTPService();

...

// some credentials
var creds:String="admin:password";
                   
// encode credentials                           
var encoder:Base64Encoder = new Base64Encoder();
encoder.insertNewLines = false;
encoder.encode(creds);

// create parameters that will be passed with a request
var parameters:Object=new Object();
// set authentication parameter `_basic_auth` value
parameters._basic_auth=encoder.toString()

// call server
httpService.send(parameters);
```

Configured valve will create the authentication header from the passed token.

Also valve solve the issue with inability to intercept bad logins. Browser will not prompt user for login and password on failed login attempt, and you will be able to handle it in your app.

Everyone is welcome to the [user forum](http://groups.google.com/group/web-actions-user) to discuss this.