package mod.rule;

import api.model.Decision;
import api.model.Site;
import core.SpiderContext;
import core.rule.impl.BaseRuleImpl;
import core.model.DecisionInternal;

import java.net.URL;

/**
 * Rule implementation that only decides to accept a URL in case the resource
 * to which this url points lies in the site of the original starting point
 * of spidering.
 *
 * $Id: BaseSiteOnlyRule.java,v 1.1 2003/04/03 16:10:48 vanrogu Exp $
 *
 * @author Günther Van Roey.
 */
public class BaseSiteOnlyRule extends BaseRuleImpl {

    public Decision apply(SpiderContext context, Site currentSite, URL url) {
        if (context.getBaseURL().getHost().equalsIgnoreCase(url.getHost())) {
            return new DecisionInternal(Decision.RULE_ACCEPT, "url accepted");
        } else {
            return new DecisionInternal(Decision.RULE_IGNORE, "url ignored because it points to an external site");
        }
    }

}

