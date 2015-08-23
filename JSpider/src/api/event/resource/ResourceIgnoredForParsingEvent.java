package api.event.resource;


import api.event.EventVisitor;
import api.model.ParseIgnoredResource;
import api.model.Resource;


/**
 *
 * $Id: ResourceIgnoredForParsingEvent.java,v 1.1.1.1 2002/11/20 17:02:32 vanrogu Exp $
 *
 * @author Günther Van Roey
 */
public class ResourceIgnoredForParsingEvent extends ResourceRelatedEvent {

    public ResourceIgnoredForParsingEvent(Resource resource) {
        super(resource);
    }

    public ParseIgnoredResource getResource() {
        return (ParseIgnoredResource) resource;
    }

    public String getComment() {
        return resource.getURL() + " ignored for parsing";
    }

    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }

}
