package core.storage.impl;

import core.storage.SiteDAO;
import core.storage.spi.SiteDAOSPI;
import core.storage.spi.StorageSPI;
import core.logging.Log;
import core.model.SiteInternal;
import api.model.Site;

import java.net.URL;

/**
 * $Id: SiteDAOImpl.java,v 1.1 2003/04/11 16:37:05 vanrogu Exp $
 */
class SiteDAOImpl implements SiteDAO {

    protected Log log;
    protected StorageSPI storage;
    protected SiteDAOSPI spi;

    protected int counter;

    public SiteDAOImpl  ( Log log, StorageSPI storage, SiteDAOSPI spi ) {
        this.log = log;
        this.storage = storage;
        this.spi = spi;
        this.counter = 0;
    }

    public Site createSite(URL siteURL) {
        int id = ++counter;
        SiteInternal si = new SiteInternal (id, storage, siteURL);
        spi.create(id, si);
        return si;
    }

    public Site find(URL siteURL) {
        return spi.find(siteURL);
    }

    public void save(Site site) {
        SiteInternal si = TypeTranslator.translate(site);
        spi.save(si.getId(), si);
    }

    public Site[] findAll() {
        return spi.findAll();
    }
}
