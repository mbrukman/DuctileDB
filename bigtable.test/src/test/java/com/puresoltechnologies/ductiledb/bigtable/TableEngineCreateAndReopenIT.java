package com.puresoltechnologies.ductiledb.bigtable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.puresoltechnologies.ductiledb.columnfamily.ColumnValue;
import com.puresoltechnologies.ductiledb.logstore.Key;
import com.puresoltechnologies.ductiledb.logstore.LogStructuredStoreTestUtils;
import com.puresoltechnologies.ductiledb.logstore.utils.Bytes;
import com.puresoltechnologies.ductiledb.storage.api.StorageFactory;
import com.puresoltechnologies.ductiledb.storage.spi.Storage;
import com.puresoltechnologies.ductiledb.storage.spi.StorageConfiguration;

public class TableEngineCreateAndReopenIT {

    @Test
    public void testCreationAndReopen() throws IOException {
	StorageConfiguration configuration = LogStructuredStoreTestUtils.createStorageConfiguration();
	Storage storage = StorageFactory.getStorageInstance(configuration);

	File directory = new File("/" + TableEngineCreateAndReopenIT.class.getSimpleName() + ".testCreationAndReopen");
	if (storage.exists(directory)) {
	    storage.removeDirectory(directory, true);
	}
	TableDescriptor tableDescriptor = new TableDescriptor("table", "description", directory);
	try (TableEngine store = TableEngine.create(storage, tableDescriptor, new BigTableEngineConfiguration())) {
	    store.addColumnFamily(Key.of("ColumnFamily"));
	    Put put = new Put(Key.of("Row"));
	    put.addColumn(Key.of("ColumnFamily"), Key.of("Column"), ColumnValue.of("Value"));
	    store.put(put);

	}
	try (TableEngine store = TableEngine.reopen(storage, directory)) {
	    Get get = new Get(Key.of("Row"));
	    get.addColumn(Key.of("ColumnFamily"), Key.of("Column"));
	    Result result = store.get(get);
	    assertEquals("Value",
		    Bytes.toString(result.getFamilyMap(Key.of("ColumnFamily")).get(Key.of("Column")).getBytes()));
	}
    }

    @Test(expected = IOException.class)
    public void testDoubleCreationNotAllowed() throws IOException {
	StorageConfiguration configuration = LogStructuredStoreTestUtils.createStorageConfiguration();
	Storage storage = StorageFactory.getStorageInstance(configuration);

	File directory = new File(
		"/" + TableEngineCreateAndReopenIT.class.getSimpleName() + ".testDoubleCreationNotAllowed");
	if (storage.exists(directory)) {
	    storage.removeDirectory(directory, true);
	}
	TableDescriptor tableDescriptor = new TableDescriptor("table", "description", directory);
	try (TableEngine store = TableEngine.create(storage, tableDescriptor, new BigTableEngineConfiguration())) {
	    store.addColumnFamily(Key.of("ColumnFamily"));
	    Put put = new Put(Key.of("Row"));
	    put.addColumn(Key.of("ColumnFamily"), Key.of("Column"), ColumnValue.of("Value"));
	    store.put(put);
	}
	try (TableEngine store = TableEngine.create(storage, tableDescriptor, new BigTableEngineConfiguration())) {
	    fail("Should not work.");
	}
    }

}
