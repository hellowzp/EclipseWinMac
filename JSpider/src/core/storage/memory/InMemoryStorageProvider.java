package core.storage.memory;


import core.storage.Storage;
import core.storage.StorageProvider;
import core.storage.spi.StorageSPI;
import core.util.config.PropertySet;


/**
 *
 * $Id: InMemoryStorageProvider.java,v 1.3 2003/04/11 16:37:07 vanrogu Exp $
 *
 * @author Günther Van Roey
 */
public class InMemoryStorageProvider implements StorageProvider {
    public StorageSPI createStorage(PropertySet props) {
        return new InMemoryStorageImpl();
    }
}
