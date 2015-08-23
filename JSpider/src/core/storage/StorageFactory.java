package core.storage;


import core.logging.LogFactory;
import core.logging.Log;
import core.storage.memory.InMemoryStorageProvider;
import core.storage.impl.StorageImpl;
import core.util.config.*;


/**
 *
 * $Id: StorageFactory.java,v 1.7 2003/04/11 16:37:05 vanrogu Exp $
 *
 * @author Günther Van Roey
 */
public class StorageFactory {

    public Storage createStorage() {


        PropertySet props = ConfigurationFactory.getConfiguration().getJSpiderConfiguration();

        Log log = LogFactory.getLog(StorageFactory.class);

        PropertySet storageProps = new MappedPropertySet ( ConfigConstants.CONFIG_STORAGE, props );
        Class providerClass = storageProps.getClass(ConfigConstants.CONFIG_STORAGE_PROVIDER, InMemoryStorageProvider.class);
        log.info("Storage provider class is '" + providerClass + "'");

        try {
            StorageProvider provider = (StorageProvider) providerClass.newInstance();
            PropertySet storageConfigProps = new MappedPropertySet(ConfigConstants.CONFIG_STORAGE_CONFIG, storageProps);
            return new StorageImpl( provider, storageConfigProps );
        } catch (InstantiationException e) {
            log.error("InstantiationException on Storage", e);
            return null;
        } catch (IllegalAccessException e) {
            log.error("IllegalAccessException on Storage", e);
            return null;
        }
    }
}
