package core.storage.impl;

import core.storage.CookieDAO;
import core.storage.spi.CookieDAOSPI;
import core.storage.spi.StorageSPI;
import core.logging.Log;
import core.model.SiteInternal;
import api.model.Cookie;
import api.model.Site;

/**
 * $Id: CookieDAOImpl.java,v 1.1 2003/04/11 16:37:05 vanrogu Exp $
 */
class CookieDAOImpl implements CookieDAO {

    protected Log log;
    protected StorageSPI storage;
    protected CookieDAOSPI spi;

    public CookieDAOImpl ( Log log, StorageSPI storage, CookieDAOSPI spi ) {
        this.log = log;
        this.storage = storage;
        this.spi = spi;
    }

    public Cookie[] find(Site site) {
        SiteInternal si = TypeTranslator.translate(site);
        return spi.find(si.getId());
    }

    public void save(Site site, Cookie[] cookies) {
        SiteInternal si = TypeTranslator.translate(site);
        spi.save(si.getId(),cookies);
    }

}
