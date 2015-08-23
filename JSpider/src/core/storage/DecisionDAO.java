package core.storage;

import api.model.Decision;
import api.model.Resource;

import java.net.URL;

/**
 * $Id: DecisionDAO.java,v 1.1 2003/04/04 20:06:10 vanrogu Exp $
 */
public interface DecisionDAO {

    public void saveSpiderDecision ( Resource resource, Decision decision );

    public void saveParseDecision ( Resource resource, Decision decision );

    public Decision findSpiderDecision ( Resource resource );

    public Decision findParseDecision ( Resource resource );
}
