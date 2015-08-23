package core.storage;

import api.model.Site;
import api.model.Cookie;

/**
 * $Id: CookieDAO.java,v 1.1 2003/04/05 16:22:11 vanrogu Exp $
 */
public interface CookieDAO {

    public Cookie[] find ( Site site );

    public void save ( Site site, Cookie[] cookies );

}
