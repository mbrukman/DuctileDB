package com.puresoltechnologies.ductiledb.storage.engine;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.puresoltechnologies.commons.misc.StopWatch;
import com.puresoltechnologies.ductiledb.storage.engine.schema.NamespaceDescriptor;
import com.puresoltechnologies.ductiledb.storage.engine.schema.TableDescriptor;
import com.puresoltechnologies.ductiledb.storage.spi.Storage;

public class NamespaceEngineImpl implements NamespaceEngine {

    private static final Logger logger = LoggerFactory.getLogger(NamespaceEngine.class);

    private final Storage storage;
    private final NamespaceDescriptor namespaceDescriptor;
    private final DatabaseEngineConfiguration configuration;
    private final Map<String, TableEngineImpl> tableEngines = new HashMap<>();

    public NamespaceEngineImpl(Storage storage, NamespaceDescriptor namespaceDescriptor,
	    DatabaseEngineConfiguration configuration) {
	this.storage = storage;
	this.namespaceDescriptor = namespaceDescriptor;
	this.configuration = configuration;
	logger.info("Starting namespace engine '" + namespaceDescriptor.getName() + "'...");
	StopWatch stopWatch = new StopWatch();
	stopWatch.start();
	initializeTableEngines();
	stopWatch.stop();
	logger.info(
		"Namespace engine '" + namespaceDescriptor.getName() + "' started in " + stopWatch.getMillis() + "ms.");
    }

    private void initializeTableEngines() {
	for (TableDescriptor tableDescriptor : namespaceDescriptor.getTables()) {
	    addTable(tableDescriptor);
	}
    }

    public void setRunCompactions(boolean runCompaction) {
	tableEngines.values().forEach(engine -> engine.setRunCompactions(runCompaction));
    }

    public void addTable(TableDescriptor tableDescriptor) {
	tableEngines.put(tableDescriptor.getName(), new TableEngineImpl(storage, tableDescriptor, configuration));
    }

    public void dropTable(TableDescriptor tableDescriptor) {
	TableEngineImpl tableEngine = tableEngines.get(tableDescriptor.getName());
	if (tableEngine != null) {
	    tableEngines.remove(tableDescriptor.getName());
	    tableEngine.close();
	}
    }

    public void runCompaction() {
	tableEngines.values().forEach(engine -> engine.runCompaction());
    }

    @Override
    public void close() {
	logger.info("Closing namespace engine '" + namespaceDescriptor.getName() + "'...");
	StopWatch stopWatch = new StopWatch();
	stopWatch.start();
	for (TableEngineImpl tableEngine : tableEngines.values()) {
	    tableEngine.close();
	}
	stopWatch.stop();
	logger.info(
		"Namespace engine '" + namespaceDescriptor.getName() + "' closed in " + stopWatch.getMillis() + "ms.");
    }

    public TableEngineImpl getTableEngine(String tableName) {
	return tableEngines.get(tableName);
    }
}
