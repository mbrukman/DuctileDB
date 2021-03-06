package com.puresoltechnologies.ductiledb.stores.os;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.puresoltechnologies.ductiledb.storage.spi.Storage;
import com.puresoltechnologies.ductiledb.storage.spi.StorageConfiguration;
import com.puresoltechnologies.ductiledb.storage.spi.StorageFactoryService;

/**
 * This method is used to initialize the storage.
 * 
 * @author Rick-Rainer Ludwig
 */
public class OSStorageFactoryService implements StorageFactoryService {

    private static final Logger logger = LoggerFactory.getLogger(OSStorageFactoryService.class);

    @Override
    public Storage create(StorageConfiguration configuration) {
	logger.info("Creating OSStorage for configuration '" + configuration + "'...");
	return new OSStorage(configuration);
    }

}
