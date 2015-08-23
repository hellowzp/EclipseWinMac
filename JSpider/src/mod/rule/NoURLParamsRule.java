package mod.rule;

import core.rule.impl.BaseRuleImpl;
import core.SpiderContext;
import core.model.DecisionInternal;
import api.model.Decision;
import api.model.Site;

import java.net.URL;

/**
 * $Id: NoURLParamsRule.java,v 1.1 2003/04/07 15:51:01 vanrogu Exp $
 */
public class NoURLParamsRule extends BaseRuleImpl {

    public Decision apply(SpiderContext context, Site currentSite, URL url) {
        Decision decision = null;

        String query = url.getQuery();
        if ( query != null && query.trim().length() > 0 ) {
            decision = new DecisionInternal(Decision.RULE_IGNORE, "url contains parameters, not accepted" );
        } else {
            decision = new DecisionInternal(Decision.RULE_ACCEPT, "url contains no parameters, accepted" );
        }
        return decision;
    }

}
