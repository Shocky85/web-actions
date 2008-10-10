package flex.management.runtime.messaging.services.remoting.adapters;

import flex.management.BaseControl;
import flex.management.runtime.messaging.services.ServiceAdapterControl;
import flex.messaging.services.remoting.adapters.JavaAdapter;
import flex.messaging.services.ServiceAdapter;

/**
 * The <code>WAAdapterControl<code> class is the MBean implemenation
 * for monitoring and managing Java service adapters at runtime.
 *
 * @author Ivan Latysh
 */
public class ActionAdapterControl extends ServiceAdapterControl implements JavaAdapterControlMBean {
    private static final String TYPE = "WebActionsAdapter";
    public ActionAdapterControl(ServiceAdapter serviceAdapter, BaseControl parent) {
      super(serviceAdapter, parent);
    }
    public String getType() {
      return TYPE;
    }
}