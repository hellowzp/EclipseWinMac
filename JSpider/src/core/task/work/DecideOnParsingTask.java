package core.task.work;

import api.event.resource.ResourceIgnoredForParsingEvent;
import api.model.Decision;
import core.SpiderContext;
import core.util.URLUtil;
import core.storage.Storage;
import core.task.WorkerTask;

import java.net.URL;

public class DecideOnParsingTask extends BaseWorkerTaskImpl {

    protected URL url;

    public DecideOnParsingTask(SpiderContext context, URL url ) {
        super(context, WorkerTask.WORKERTASK_THINKERTASK);
        this.url = url;
    }

    public void prepare() {
    }

    public void execute() {
        Storage storage = context.getStorage();

        Decision parseDecision = context.getSiteParserRules(storage.getSiteDAO().find(URLUtil.getSiteURL(url))).applyRules(context, null, url);
        storage.getDecisionDAO().saveParseDecision(storage.getResourceDAO().getResource(url), parseDecision);

        if (parseDecision.getDecision() == Decision.RULE_ACCEPT || parseDecision.getDecision() == Decision.RULE_DONTCARE) {
            context.getAgent().scheduleForParsing(url);
        } else {
            storage.getResourceDAO().setIgnoredForParsing(url);
            context.getEventDispatcher().dispatch(new ResourceIgnoredForParsingEvent(storage.getResourceDAO().getResource(url)));
        }
    }

}
