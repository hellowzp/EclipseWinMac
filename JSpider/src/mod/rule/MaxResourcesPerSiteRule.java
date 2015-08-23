package mod.rule;

import core.rule.impl.BaseRuleImpl;
import core.util.config.PropertySet;
import core.util.URLUtil;
import core.SpiderContext;
import core.model.DecisionInternal;
import core.logging.LogFactory;
import core.logging.Log;
import api.model.Decision;
import api.model.Site;

import java.net.URL;
import java.util.Map;
import java.util.HashMap;

/**
 * $Id: MaxResourcesPerSiteRule.java,v 1.1 2003/04/07 15:51:00 vanrogu Exp $
 */
public class MaxResourcesPerSiteRule extends BaseRuleImpl {

    public static final String MAX = "max";

    protected int max;
    protected Map<URL, Integer> counters;

    public MaxResourcesPerSiteRule ( PropertySet config ) {
        Log log = LogFactory.getLog(MaxResourcesPerSiteRule.class);
        max = config.getInteger(MaxResourcesPerSiteRule.MAX, 0);
        this.counters = new HashMap<URL, Integer> ( );
        log.info("maximum resources per site set to " + max );
    }

    public synchronized Decision apply(SpiderContext context, Site currentSite, URL url) {
        URL siteURL = URLUtil.getSiteURL(url);

        Integer counter = (Integer) counters.get(siteURL);
        if ( counter == null ) {
            counter = new Integer(0);
        }

        Decision decision = null;

        if ( counter.intValue() + 1 > max ) {
            decision = new DecisionInternal (Decision.RULE_IGNORE, "counter for site is " + counter.intValue() + ", max is " + max + ", so not allowed anymore!");
        } else {
            decision = new DecisionInternal (Decision.RULE_ACCEPT, "counter for site is " + counter.intValue() + ", max is " + max + ", so allowed!");
            counter = new Integer(counter.intValue() + 1);
            counters.put(siteURL, counter);
        }
        return decision;
    }

}
