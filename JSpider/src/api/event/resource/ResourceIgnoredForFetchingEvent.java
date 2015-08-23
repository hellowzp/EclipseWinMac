package api.event.resource;


import api.event.EventVisitor;
import api.model.FetchIgnoredResource;
import api.model.Resource;


/**
 *
 * $Id: ResourceIgnoredForFetchingEvent.java,v 1.1.1.1 2002/11/20 17:02:31 vanrogu Exp $
 *
 * @author Günther Van Roey
 */
public class ResourceIgnoredForFetchingEvent extends ResourceRelatedEvent {

    public ResourceIgnoredForFetchingEvent(Resource resource) {
        super(resource);
    }

    public FetchIgnoredResource getResource() {
        return (FetchIgnoredResource) resource;
    }

    public String getComment() {
        return resource.getURL() + " ignored for fetching";
    }

    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }

}
