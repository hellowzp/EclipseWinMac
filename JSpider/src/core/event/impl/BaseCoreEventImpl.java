package core.event.impl;


import core.SpiderContext;
import core.event.CoreEvent;
import core.event.CoreEventVisitor;

import java.net.URL;


/**
 *
 * $Id: BaseCoreEventImpl.java,v 1.3 2003/02/28 17:39:03 vanrogu Exp $
 *
 * @author Günther Van Roey
 */
public abstract class BaseCoreEventImpl implements CoreEvent {

    protected SpiderContext context;

    public BaseCoreEventImpl(SpiderContext context) {
        this.context = context;
    }

    public String getName() {
        return getClass().getName();
    }

    public void accept(URL url, CoreEventVisitor visitor) {
        visitor.visit(url, this);
    }

    public SpiderContext getContext() {
        return context;
    }
}
