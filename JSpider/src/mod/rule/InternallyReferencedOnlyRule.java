package mod.rule;


import api.model.Decision;
import api.model.Site;
import core.SpiderContext;
import core.rule.impl.BaseRuleImpl;
import core.model.DecisionInternal;

import java.net.URL;


/**
 * Rule implementation that only accepts a resource URL if it is internal to
 * the site currently being spidered.
 *
 * $Id: InternallyReferencedOnlyRule.java,v 1.2 2003/04/25 21:29:06 vanrogu Exp $
 *
 * @author Günther Van Roey
 */
public class InternallyReferencedOnlyRule extends BaseRuleImpl {

    public Decision apply(SpiderContext context, Site currentSite, URL url) {
        if (currentSite == null) {
            return new DecisionInternal(Decision.RULE_DONTCARE);
        } else {
            if (currentSite.getHost().equalsIgnoreCase(url.getHost()) && (currentSite.getPort() == url.getPort())) {
                return new DecisionInternal(Decision.RULE_ACCEPT, "url is within same site - accepted");
            } else {
                return new DecisionInternal(Decision.RULE_IGNORE, "url ignored because it points to another site");
            }
        }
    }


}
