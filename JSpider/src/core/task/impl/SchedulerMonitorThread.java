package core.task.impl;

import api.event.monitor.MonitorEvent;
import api.event.monitor.SchedulerMonitorEvent;
import core.dispatch.EventDispatcher;
import core.task.Scheduler;
import core.threading.MonitorThread;

/**
 * $Id: SchedulerMonitorThread.java,v 1.7 2003/04/03 15:57:19 vanrogu Exp $
 */
public class SchedulerMonitorThread extends MonitorThread {

    protected Scheduler scheduler;

    public SchedulerMonitorThread ( Scheduler scheduler, EventDispatcher dispatcher, int interval ) {
        super ( dispatcher, interval, "Job Scheduler");
        this.scheduler = scheduler;
        start();
    }

    public MonitorEvent doMonitorTask() {
        //System.out.println(scheduler);
        return new SchedulerMonitorEvent ( scheduler.getJobCount(), scheduler.getSpiderJobCount(),  scheduler.getThinkerJobCount(), scheduler.getJobsDone(), scheduler.getSpiderJobsDone(),  scheduler.getThinkerJobsDone(), scheduler.getBlockedCount(), scheduler.getAssignedCount() );
    }

}
