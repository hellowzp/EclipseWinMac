package mod.eventfilter;

import api.event.JSpiderEvent;
import spi.EventFilter;

/**
 *
 * $Id: AllowAllEventFilter.java,v 1.2 2003/04/03 16:25:03 vanrogu Exp $
 *
 * @author Günther Van Roey
 */
public class AllowAllEventFilter implements EventFilter {

    public boolean filterEvent(JSpiderEvent event) {
        return true;
    }
}
