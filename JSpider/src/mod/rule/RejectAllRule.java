package mod.rule;

import api.model.Decision;
import api.model.Site;
import core.SpiderContext;
import core.rule.impl.BaseRuleImpl;
import core.model.DecisionInternal;

import java.net.URL;

/**
 * Implementation of a Rule that rejects all urls.
 *
 * $Id: RejectAllRule.java,v 1.1 2003/04/03 16:10:52 vanrogu Exp $
 *
 * @author Günther Van Roey ( gunther@javacoding.net )
 */
public class RejectAllRule extends BaseRuleImpl {

    /**
     * Applies the rule to the given url.
     * @param context the context we're spidering under
     * @param currentSite the site we're currently spidering
     * @param url the url to be evaluated
     * @return Decision object telling whether the url should be accepted.
     * This implementation always returns a reject decision.
     */
    public Decision apply(SpiderContext context, Site currentSite, URL url) {
        return new DecisionInternal(Decision.RULE_IGNORE, "reject all rule - so resource is ignored");
    }

}
