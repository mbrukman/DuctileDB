package com.puresoltechnologies.ductiledb.core.tables;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.puresoltechnologies.ductiledb.core.tables.dcl.DataControlLanguage;
import com.puresoltechnologies.ductiledb.core.tables.dcl.DataControlLanguageImpl;
import com.puresoltechnologies.ductiledb.core.tables.ddl.DataDefinitionLanguage;
import com.puresoltechnologies.ductiledb.core.tables.ddl.DataDefinitionLanguageImpl;
import com.puresoltechnologies.ductiledb.core.tables.ddl.TableDefinition;
import com.puresoltechnologies.ductiledb.core.tables.dml.DataManipulationLanguage;
import com.puresoltechnologies.ductiledb.core.tables.dml.DataManipulationLanguageImpl;
import com.puresoltechnologies.ductiledb.core.tables.ductileql.SQLParser;
import com.puresoltechnologies.ductiledb.core.tables.ductileql.StatementCreator;
import com.puresoltechnologies.ductiledb.core.tables.schema.TableStoreSchema;
import com.puresoltechnologies.ductiledb.engine.DatabaseEngineImpl;
import com.puresoltechnologies.ductiledb.engine.schema.SchemaException;
import com.puresoltechnologies.ductiledb.storage.api.StorageException;
import com.puresoltechnologies.parsers.parser.ParseTreeNode;

/**
 * This is the central class for the RDBMS functionality of DuctileDB.
 * 
 * @author Rick-Rainer Ludwig
 */
public class TableStoreImpl implements TableStore {

    private static Logger logger = LoggerFactory.getLogger(TableStoreImpl.class);

    public static String STORAGE_DIRECTORY = "tables";

    private final TableStoreConfiguration configuration;
    private final DatabaseEngineImpl storageEngine;
    private final boolean autoCloseConnection;
    private final TableStoreSchema schema;
    private final DataDefinitionLanguage dataDefinitionLanguage;
    private final DataManipulationLanguage dataManipulationLanguage;
    private final DataControlLanguage dataControlLanguage;

    public TableStoreImpl(TableStoreConfiguration configuration, DatabaseEngineImpl storageEngine,
	    boolean autoCloseConnection) throws StorageException, SchemaException {
	this.configuration = configuration;
	this.storageEngine = storageEngine;
	this.autoCloseConnection = autoCloseConnection;
	// Schema...
	this.schema = new TableStoreSchema(storageEngine, configuration);
	schema.checkAndCreateEnvironment();
	schema.readDefinitions();
	// Languages...
	this.dataDefinitionLanguage = new DataDefinitionLanguageImpl(this, new File(STORAGE_DIRECTORY));
	this.dataManipulationLanguage = new DataManipulationLanguageImpl(this);
	this.dataControlLanguage = new DataControlLanguageImpl(this);
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

    public DatabaseEngineImpl getStorageEngine() {
	return storageEngine;
    }

    public TableStoreSchema getSchema() {
	return schema;
    }

    @Override
    public DataDefinitionLanguage getDataDefinitionLanguage() {
	return dataDefinitionLanguage;
    }

    @Override
    public DataManipulationLanguage getDataManipulationLanguage() {
	return dataManipulationLanguage;
    }

    @Override
    public DataControlLanguage getDataControlLanguage() {
	return dataControlLanguage;
    }

    public void runCompaction() {
	storageEngine.runCompaction();
    }

    public TableDefinition getTableDefinition(String namespace, String table) {
	return schema.getTableDefinition(namespace, table);
    }

    @Override
    public PreparedStatement prepare(String query) throws ExecutionException {
	ParseTreeNode parsed = SQLParser.parse(query);
	return new StatementCreator(this).create(parsed);
    }
}