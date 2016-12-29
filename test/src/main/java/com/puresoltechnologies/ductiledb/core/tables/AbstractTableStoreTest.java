package com.puresoltechnologies.ductiledb.core.tables;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.puresoltechnologies.ductiledb.core.AbstractDuctileDBTest;
import com.puresoltechnologies.ductiledb.core.DuctileDB;
import com.puresoltechnologies.ductiledb.core.graph.DuctileDBTestHelper;
import com.puresoltechnologies.ductiledb.core.graph.schema.DuctileDBHealthCheck;
import com.puresoltechnologies.ductiledb.core.utils.BuildInformation;
import com.puresoltechnologies.ductiledb.engine.schema.SchemaException;
import com.puresoltechnologies.ductiledb.storage.api.StorageException;

public class AbstractTableStoreTest extends AbstractDuctileDBTest {

    private static DuctileDB ductileDB = null;
    private static TableStoreImpl rdbms = null;

    @BeforeClass
    public static void connect() throws IOException, SchemaException, ExecutionException {
	ductileDB = getDuctileDB();
	rdbms = (TableStoreImpl) ductileDB.getTableStore();
	// Normally meaningless, but we do nevertheless, if tests change...
	DuctileDBTestHelper.removeRDBMS(rdbms);
	DuctileDBHealthCheck.runCheckForEmpty(rdbms);

	String version = BuildInformation.getVersion();
	if (!version.startsWith("${")) {
	    // TODO!!!
	    // assertEquals("Schema version is wrong.", version,
	    // rdbms.getVersion().toString());
	}
    }

    @AfterClass
    public static void disconnect() throws IOException {
	if (rdbms != null) {
	    rdbms.close();
	    rdbms = null;
	}
    }

    @Before
    public final void cleanup() throws IOException, StorageException, ExecutionException {
	DuctileDBTestHelper.removeRDBMS(rdbms);
	rdbms.runCompaction();
	DuctileDBHealthCheck.runCheckForEmpty(rdbms);
    }

    protected static TableStoreImpl getTableStore() {
	return rdbms;
    }
}