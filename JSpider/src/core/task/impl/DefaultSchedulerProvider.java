package core.task.impl;

import core.task.Scheduler;
import core.task.SchedulerProvider;


/**
 * Default provider class implementation for task schedulers.
 *
 * $Id: DefaultSchedulerProvider.java,v 1.2 2002/11/27 22:07:37 vanrogu Exp $
 *
 * @author Günther Van Roey
 */
public class DefaultSchedulerProvider implements SchedulerProvider {

    /**
     * Creates a new default scheduler implementation.
     * @return Scheduler instance
     */
    public Scheduler createScheduler() {
        return new SchedulerImpl();
    }

}
