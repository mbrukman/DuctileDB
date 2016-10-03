package com.puresoltechnologies.ductiledb.core.rdbms;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.puresoltechnologies.ductiledb.api.rdbms.RelationalDuctileDB;
import com.puresoltechnologies.ductiledb.api.rdbms.dcl.DataControlLanguage;
import com.puresoltechnologies.ductiledb.api.rdbms.ddl.DataDefinitionLanguage;
import com.puresoltechnologies.ductiledb.api.rdbms.dml.DataManipulationLanguage;
import com.puresoltechnologies.ductiledb.core.rdbms.dcl.DataControlLanguageImpl;
import com.puresoltechnologies.ductiledb.core.rdbms.ddl.DataDefinitionLanguageImpl;
import com.puresoltechnologies.ductiledb.core.rdbms.dml.DataManipulationLanguageImpl;
import com.puresoltechnologies.ductiledb.core.rdbms.schema.RdbmsSchema;
import com.puresoltechnologies.ductiledb.storage.api.StorageException;
import com.puresoltechnologies.ductiledb.storage.engine.DatabaseEngineImpl;
import com.puresoltechnologies.ductiledb.storage.engine.schema.SchemaException;

/**
 * This is the central class for the RDBMS functionality of DuctileDB.
 * 
 * @author Rick-Rainer Ludwig
 */
public class RelationalDuctileDBImpl implements RelationalDuctileDB {

    private static Logger logger = LoggerFactory.getLogger(RelationalDuctileDBImpl.class);

    private final DuctileDBRdbmsConfiguration configuration;
    private final DatabaseEngineImpl storageEngine;
    private final boolean autoCloseConnection;
    private final RdbmsSchema rdbmsSchema;
    private final DataDefinitionLanguage dataDefinitionLanguage;
    private final DataManipulationLanguage dataManipulationLanguage;
    private final DataControlLanguage dataControlLanguage;

    public RelationalDuctileDBImpl(DuctileDBRdbmsConfiguration configuration, DatabaseEngineImpl storageEngine,
	    boolean autoCloseConnection) throws StorageException, SchemaException {
	this.configuration = configuration;
	this.storageEngine = storageEngine;
	this.autoCloseConnection = autoCloseConnection;
	// Schema...
	this.rdbmsSchema = new RdbmsSchema(storageEngine, configuration);
	rdbmsSchema.checkAndCreateEnvironment();
	// Languages...
	this.dataDefinitionLanguage = new DataDefinitionLanguageImpl(storageEngine);
	this.dataManipulationLanguage = new DataManipulationLanguageImpl(storageEngine);
	this.dataControlLanguage = new DataControlLanguageImpl(storageEngine);
    }

    @Override
    public void close() throws IOException {
	if (autoCloseConnection) {
	    if (!storageEngine.isClosed()) {
		logger.info("Closes connection '" + storageEngine.toString() + "'...");
		storageEngine.close();
		logger.info("Connection '" + storageEngine.toString() + "' closed.");
	    }
	}
    }

    public DataDefinitionLanguage getDataDefinitionLanguage() {
	return dataDefinitionLanguage;
    }

    public DataManipulationLanguage getDataManipulationLanguage() {
	return dataManipulationLanguage;
    }

    public DataControlLanguage getDataControlLanguage() {
	return dataControlLanguage;
    }

    public void runCompaction() {
	storageEngine.runCompaction();
    }

}