package mod.rule;

import api.model.Decision;
import api.model.Site;
import core.SpiderContext;
import core.rule.impl.BaseRuleImpl;
import core.model.DecisionInternal;
import core.util.URLUtil;

import java.net.URL;

/**
 * $Id: OnlyDeeperInSiteRule.java,v 1.1 2003/04/03 16:10:52 vanrogu Exp $
 */
public class OnlyDeeperInSiteRule extends BaseRuleImpl {

    public Decision apply(SpiderContext context, Site currentSite, URL url) {

        Decision decision = new DecisionInternal ( Decision.RULE_FORBIDDEN, "url not deeper in site that baseURL" );

        URL baseURL = context.getBaseURL();

        String baseUrlPath = URLUtil.stripResource ( context.getBaseURL().getPath() );
        String urlPath = URLUtil.stripResource ( url.getPath() );

        if ( url.getProtocol().equals(baseURL.getProtocol())) {
            if (url.getPort() == baseURL.getPort()) {
                if ( urlPath.startsWith(baseUrlPath )) {
                  decision = new DecisionInternal(Decision.RULE_ACCEPT);
                }
            }
        }


        context.getBaseURL();

        return decision;

    }
}
