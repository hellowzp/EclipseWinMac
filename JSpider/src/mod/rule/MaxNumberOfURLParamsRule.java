package mod.rule;

import core.rule.impl.BaseRuleImpl;
import core.util.config.PropertySet;
import core.logging.LogFactory;
import core.logging.Log;
import core.SpiderContext;
import core.model.DecisionInternal;
import api.model.Decision;
import api.model.Site;

import java.net.URL;

/**
 * $Id: MaxNumberOfURLParamsRule.java,v 1.1 2003/04/07 15:51:00 vanrogu Exp $
 */
public class MaxNumberOfURLParamsRule extends BaseRuleImpl {

    public static final String MAX = "max";

    protected int max;

    public MaxNumberOfURLParamsRule ( PropertySet config ) {
        Log log = LogFactory.getLog(MaxNumberOfURLParamsRule.class);
        max = config.getInteger(MaxNumberOfURLParamsRule.MAX, 0);
        log.info("max set to " + max);
    }

    public Decision apply(SpiderContext context, Site currentSite, URL url) {
        Decision decision = null;

        String query = url.getQuery();
        int params;

        if ( query == null || query.length() < 2  ) {
            params = 0;
        } else {
            int amps = 0;
            int pos = query.indexOf('&');
            while ( pos != -1 ) {
                amps++;
                pos = query.indexOf('&', pos + 1);
            }
            params = amps + 1;
        }


        if ( params > max ) {
            decision = new DecisionInternal(Decision.RULE_IGNORE, "params = " + params + ", max = " + max + ", url ingored");
        } else {
            decision = new DecisionInternal(Decision.RULE_ACCEPT, "params = " + params + ", max = " + max + ", url accepted");
        }

        return decision;
    }

}
