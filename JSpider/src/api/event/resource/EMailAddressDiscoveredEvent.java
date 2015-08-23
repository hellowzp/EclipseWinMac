package api.event.resource;

import api.model.FetchedResource;
import api.event.EventVisitor;

/**
 * $Id: EMailAddressDiscoveredEvent.java,v 1.1 2003/04/08 15:50:25 vanrogu Exp $
 */
public class EMailAddressDiscoveredEvent extends ResourceRelatedEvent {

    protected String email;

    public EMailAddressDiscoveredEvent ( FetchedResource resource, String email ) {
        super ( resource );
        this.email = email;
    }

    public FetchedResource getResource ( ) {
        return (FetchedResource)resource;
    }

    public String getEmailAddress ( ) {
        return email;
    }

    public String getComment() {
        return "e-mail address '" + email + "' discovered";
    }

    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }

    public String toString ( ) {
        return getComment();
    }

}
