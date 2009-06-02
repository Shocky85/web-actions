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

  public static Object eval(ScriptContext ctx) {
    // get request
    HttpServletRequest request = (HttpServletRequest) ctx.getAttribute("request", ScriptContext.ENGINE_SCOPE);    
    // create a new result map
    Map<String, Object> reslts = new HashMap<String, Object>(1);
    // add from parameter
    reslts.put("from", "Java");
    // add name parameter if given
    if (null!=ctx.getAttribute("name")) {
      reslts.put("name", ctx.getAttribute("name"));
    }
    // return new model and view
    return new ModelAndView(request.getRequestURI(), reslts);
  }

}
