package core.storage.jdbc;


import core.storage.StorageProvider;
import core.storage.spi.StorageSPI;
import core.util.config.PropertySet;


/**
 *
 * $Id: JdbcStorageProvider.java,v 1.2 2003/04/11 16:37:06 vanrogu Exp $
 *
 * @author Günther Van Roey
 */
public class JdbcStorageProvider implements StorageProvider {
    public StorageSPI createStorage(PropertySet props) {
        return new JdbcStorageImpl(props);
    }
}
