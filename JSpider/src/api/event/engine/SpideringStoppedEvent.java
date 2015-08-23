package api.event.engine;


import api.event.EventVisitor;
import api.model.Resource;
import api.model.Site;
import core.storage.Storage;

/**
 *
 * $Id: SpideringStoppedEvent.java,v 1.3 2003/03/28 17:26:25 vanrogu Exp $
 *
 * @author Günther Van Roey
 */
public class SpideringStoppedEvent extends EngineRelatedEvent {

    protected Storage storage;

    public SpideringStoppedEvent(Storage storage) {
        this.storage = storage;
    }

    public boolean isFilterable() {
        return false;
    }

    public String getComment() {
        return "Spidering stopped";
    }

    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }

    public Resource[] getResources ( ) {
        return storage.getResourceDAO().getAllResources ( );
    }

    public Site[] getSites ( ) {
        return storage.getSiteDAO().findAll ( );
    }

}
