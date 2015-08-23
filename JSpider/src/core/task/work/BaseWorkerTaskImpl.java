package core.task.work;


import core.SpiderContext;
import core.event.CoreEvent;
import core.logging.LogFactory;
import core.logging.Log;
import core.task.WorkerTask;

import java.net.URL;


/**
 *
 * $Id: BaseWorkerTaskImpl.java,v 1.4 2003/04/10 16:19:08 vanrogu Exp $
 *
 * @author Günther Van Roey
 */
public abstract class BaseWorkerTaskImpl implements WorkerTask {

    protected Log log;
    protected int type;
    protected SpiderContext context;

    public BaseWorkerTaskImpl(SpiderContext context, int type) {
        this.log = LogFactory.getLog(this.getClass());
        this.type = type;
        this.context = context;
    }

    public void tearDown() {
        context.getAgent().flagDone(this);
    }

    public int getType() {
        return type;
    }

    protected void notifyEvent(URL url, CoreEvent event) {
        if ( event == null ) {
            log.error("PANIC! event is null!");
            log.error("URL: " + url);
        } else {
          context.getAgent().registerEvent(url, event);
        }
    }

}
