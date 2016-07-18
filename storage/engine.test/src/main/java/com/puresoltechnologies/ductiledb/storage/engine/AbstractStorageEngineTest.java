package com.puresoltechnologies.ductiledb.storage.engine;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;

import com.puresoltechnologies.ductiledb.storage.api.StorageException;
import com.puresoltechnologies.ductiledb.storage.api.StorageFactory;
import com.puresoltechnologies.ductiledb.storage.spi.Storage;
import com.puresoltechnologies.ductiledb.stores.os.OSStorage;

public abstract class AbstractStorageEngineTest {

    private static final File baseDirectory = new File("/tmp/storage");
    private final File storageDirectory = new File(baseDirectory, this.getClass().getSimpleName());
    private StorageEngine storageEngine;

    @Before
    public void initializeStorageEngine() throws StorageException {
	Map<String, String> configuration = new HashMap<>();
	configuration.put(OSStorage.DIRECTORY_PROPERTY, storageDirectory.getPath());
	storageEngine = new StorageEngine(StorageFactory.getStorageInstance(configuration), "test");
    }

    @After
    public void cleanupStorageEngine() throws IOException {
	Storage storage = storageEngine.getStorage();
	storage.removeDirectory(new File("test"), true);
	storageDirectory.delete();
	baseDirectory.delete();
    }

    protected StorageEngine getEngine() {
	return storageEngine;
    }

}
