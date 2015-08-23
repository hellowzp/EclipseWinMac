package tool.impl;

import api.event.JSpiderEvent;
import api.event.resource.*;
import api.model.*;
import tool.Parameters;

/**
 * $Id: HeadersTool.java,v 1.3 2003/04/01 19:44:42 vanrogu Exp $
 */
public class HeadersTool extends BaseToolImpl {

    protected boolean done;

    public HeadersTool() {
        done = false;
    }

    public String getName() {
        return "headers";
    }

    public boolean validateParams(Parameters parameters) {
        return parameters.getValues().length == 0;
    }

    public void notify(JSpiderEvent event) {
        if (!done) {
            FetchTriedResource resource = null;
            if (event instanceof ResourceFetchedEvent) {
                ResourceFetchedEvent rfe = (ResourceFetchedEvent) event;
                resource = rfe.getResource();
                done = true;
            }
            if (event instanceof ResourceFetchErrorEvent) {
                ResourceFetchErrorEvent rfe = (ResourceFetchErrorEvent) event;
                resource = rfe.getResource();
                done = true;
            }
            if (resource != null) {
                HTTPHeader[] headers = resource.getHeaders();
                for (int i = 0; i < headers.length; i++) {
                    HTTPHeader header = headers[i];
                    System.out.println(header.getName() + ":" + header.getValue());
                }
            }
        }
    }
}