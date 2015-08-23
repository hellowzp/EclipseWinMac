package core.event.impl;


import core.SpiderContext;
import core.event.CoreEventVisitor;
import api.model.Folder;

import java.net.URL;


/**
 *
 * $Id: URLFoundEvent.java,v 1.5 2003/04/09 17:08:04 vanrogu Exp $
 *
 * @author G�nther Van Roey
 */
public class URLFoundEvent extends BaseCoreEventImpl {

    protected URL url;
    protected URL foundURL;

    public URLFoundEvent(SpiderContext context, URL url, URL foundURL) {
        super(context);
        this.url = url;
        this.foundURL = foundURL;
    }

    public URL getURL() {
        return url;
    }

    public URL getFoundURL() {
        return foundURL;
    }

    public void accept(URL url, CoreEventVisitor visitor) {
        visitor.visit(url, this);
    }

}
