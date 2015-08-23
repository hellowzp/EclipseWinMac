package core.task.dispatch;


import core.SpiderContext;
import core.task.DispatcherTask;


/**
 *
 * $Id: BaseDispatchTaskImpl.java,v 1.6 2003/02/14 21:19:04 vanrogu Exp $
 *
 * @author G�nther Van Roey
 */
public abstract class BaseDispatchTaskImpl implements DispatcherTask {

    protected SpiderContext context;
    protected boolean running;


    public BaseDispatchTaskImpl(SpiderContext context) {
        this.context = context;
        running = true;
    }

}
