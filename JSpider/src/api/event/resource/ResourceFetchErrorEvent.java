package api.event.resource;


import api.event.EventVisitor;
import api.model.FetchErrorResource;
import api.model.Resource;


/**
 *
 * $Id: ResourceFetchErrorEvent.java,v 1.5 2003/05/01 08:19:16 vanrogu Exp $
 *
 * @author Günther Van Roey
 */
public class ResourceFetchErrorEvent extends ResourceRelatedEvent {

    protected int httpStatus;

    public ResourceFetchErrorEvent(Resource resource, int httpStatus) {
        super(resource);
        this.httpStatus = httpStatus;
    }

    public FetchErrorResource getResource() {
        return (FetchErrorResource) resource;
    }

    public int getHttpStatus ( ) {
        return httpStatus;
    }

    public String getComment() {
        return "resource " + resource.getURL() + " couldn't be fetched [" + httpStatus + "]";
    }

    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }

    public boolean isError() {
        return true;
    }
}
