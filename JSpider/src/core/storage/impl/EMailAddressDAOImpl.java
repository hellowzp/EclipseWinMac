package core.storage.impl;

import core.storage.EMailAddressDAO;
import core.storage.Storage;
import core.storage.spi.EMailAddressDAOSPI;
import core.storage.spi.StorageSPI;
import core.logging.Log;
import core.model.ResourceInternal;
import core.model.EMailAddressInternal;
import api.model.*;

/**
 * $Id: EMailAddressDAOImpl.java,v 1.1 2003/04/11 16:37:05 vanrogu Exp $
 */
class EMailAddressDAOImpl implements EMailAddressDAO {

    protected Log log;
    protected StorageSPI storage;
    protected EMailAddressDAOSPI spi;

    public EMailAddressDAOImpl ( Log log, StorageSPI storage, EMailAddressDAOSPI spi ) {
        this.log = log;
        this.storage = storage;
        this.spi = spi;
    }

    public EMailAddress find(String address) {
        return spi.find(address);
    }

    public void register(Resource resource, EMailAddress address) {
        ResourceInternal ri = TypeTranslator.translate(resource);
        EMailAddressInternal ai = TypeTranslator.translate(address);
        spi.register(ri, ai);
    }

    public EMailAddress[] findByResource(Resource resource) {
        ResourceInternal ri = TypeTranslator.translate(resource);
        return spi.findByResource(ri);
    }

    public EMailAddressReference[] findReferencesByResource(Resource resource) {
        ResourceInternal ri = TypeTranslator.translate(resource);
        return spi.findReferencesByResource(ri);
    }

}
