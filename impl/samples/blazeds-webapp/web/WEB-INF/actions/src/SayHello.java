import flex.messaging.messages.RemotingMessage;

import javax.script.ScriptContext;

/**
 * Hello action
 *
 * @author Ivan Latysh
 * @version 0.1
 * @since 9-Oct-2008 8:04:36 PM
 */
public class SayHello {

  public static Object eval(ScriptContext context) {
    RemotingMessage message = (RemotingMessage) context.getAttribute("message", ScriptContext.ENGINE_SCOPE);
    String name = (message.getParameters().size()>0 ?String.valueOf(message.getParameters().get(0)) :null);
    return "Hello "+(null==name ?" anybody." :name);
  }

}
