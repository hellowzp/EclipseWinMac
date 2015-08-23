package tool.impl;

import api.event.JSpiderEvent;
import api.event.resource.EMailAddressDiscoveredEvent;

/**
 * $Id: EMailTool.java,v 1.1 2003/04/08 15:50:38 vanrogu Exp $
 */
public class EMailTool extends BaseToolImpl {

    public String getName() {
        return "email";
    }

    public void notify(JSpiderEvent event) {
        if ( event instanceof EMailAddressDiscoveredEvent ){
            EMailAddressDiscoveredEvent eade = (EMailAddressDiscoveredEvent) event;
            System.out.println(eade.getEmailAddress());
        }
    }

}
