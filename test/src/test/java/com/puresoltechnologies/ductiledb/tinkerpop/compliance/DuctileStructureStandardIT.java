package com.puresoltechnologies.ductiledb.tinkerpop.compliance;

import java.io.IOException;

import org.apache.tinkerpop.gremlin.GraphProviderClass;
import org.apache.tinkerpop.gremlin.structure.StructureStandardSuite;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.runner.RunWith;

import com.puresoltechnologies.ductiledb.core.AbstractDuctileDBTest;
import com.puresoltechnologies.ductiledb.core.DuctileDBConfiguration;
import com.puresoltechnologies.ductiledb.storage.api.StorageFactory;
import com.puresoltechnologies.ductiledb.storage.api.StorageFactoryServiceException;
import com.puresoltechnologies.ductiledb.storage.spi.Storage;
import com.puresoltechnologies.ductiledb.tinkerpop.DuctileGraph;
import com.puresoltechnologies.ductiledb.tinkerpop.DuctileGraphProvider;

@Ignore("Not fully implemented, yet.")
@RunWith(StructureStandardSuite.class)
@GraphProviderClass(provider = DuctileGraphProvider.class, graph = DuctileGraph.class)
public class DuctileStructureStandardIT {

    @BeforeClass
    public static void clear() throws IOException, StorageFactoryServiceException {
	AbstractDuctileDBTest.readTestConfigration();
	DuctileDBConfiguration configuration = AbstractDuctileDBTest.readTestConfigration();
	try (Storage storage = StorageFactory.getStorageInstance(configuration.getBigTableEngine().getStorage())) {
	    AbstractDuctileDBTest.cleanTestStorageDirectory(storage);
	}
    }

}
