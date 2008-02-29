import org.springframework.web.servlet.ModelAndView;
import org.apache.log4j.Logger;

import javax.script.ScriptContext;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.HashMap;

/**
 * Say Hello action implementation
 *
 * @author Ivan Latysh
 * @version 0.1
 * @since 18-May-2007 : 5:12:32 PM
 */
public class SayHello {

  /** Component logger */
  protected static Logger logger = Logger.getLogger(SayHello.class.getName());

  public static Object eval(ScriptContext ctx) {
    // get request
    HttpServletRequest request = (HttpServletRequest) ctx.getAttribute("request", ScriptContext.ENGINE_SCOPE);
    System.out.println("RemoteAddress="+request.getRemoteAddr());

    Map<String, Object> reslts = new HashMap<String, Object>(1);
    // get name to say hello to
    if (null!=ctx.getAttribute("name")) {
      reslts.put("name", ctx.getAttribute("name"));
    }
    System.out.println("request> Name="+reslts.get("name"));

    return new ModelAndView("hello.html", reslts);
  }

}
