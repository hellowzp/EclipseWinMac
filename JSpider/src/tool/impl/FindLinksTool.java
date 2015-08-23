package tool.impl;

import api.event.JSpiderEvent;
import api.event.resource.ResourceReferenceDiscoveredEvent;
import api.model.Resource;

/**
 * $Id: FindLinksTool.java,v 1.2 2003/04/01 19:44:42 vanrogu Exp $
 */
public class FindLinksTool extends BaseToolImpl {

    public String getName() {
        return "findlinks";
    }

    public void notify(JSpiderEvent event) {

        if ( event instanceof ResourceReferenceDiscoveredEvent ) {
            ResourceReferenceDiscoveredEvent rrde = (ResourceReferenceDiscoveredEvent) event;
            Resource resource = rrde.getReferencedResource();
            System.out.println(resource.getURL());
        }

    }
}
