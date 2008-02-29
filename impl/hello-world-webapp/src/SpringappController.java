import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * @author Ivan Latysh
 * @version 0.1
 * @since 5-May-2007 : 4:17:33 PM
 */
public class SpringappController implements Controller {
  /** Component logger */
  protected Logger logger = Logger.getLogger(this.getClass().getName());

  public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception, IOException {
    logger.info("SpringappController - returning hello view");
    return new ModelAndView("hello", "now", new Date().toString());

  }

}
