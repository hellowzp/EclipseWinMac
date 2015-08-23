package api.event.folder;


import api.event.EventVisitor;
import api.event.JSpiderEvent;
import api.model.Folder;


/**
 *
 * $Id: FolderRelatedEvent.java,v 1.2 2003/04/07 15:50:46 vanrogu Exp $
 *
 * @author Günther Van Roey
 */
public abstract class FolderRelatedEvent extends JSpiderEvent {

    protected Folder folder;

    public FolderRelatedEvent(Folder folder) {
        this.folder = folder;
    }

    public Folder getFolder() {
        return folder;
    }

    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }

    public String toString ( ) {
        return super.toString() + " " + getComment();
    }
}
