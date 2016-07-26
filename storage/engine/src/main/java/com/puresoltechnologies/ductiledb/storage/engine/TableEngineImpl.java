package com.puresoltechnologies.ductiledb.storage.engine;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.puresoltechnologies.commons.misc.StopWatch;
import com.puresoltechnologies.ductiledb.storage.api.StorageException;
import com.puresoltechnologies.ductiledb.storage.engine.schema.ColumnFamilyDescriptor;
import com.puresoltechnologies.ductiledb.storage.engine.schema.TableDescriptor;
import com.puresoltechnologies.ductiledb.storage.spi.Storage;

/**
 * This class is the central engine class for table storage. It is using the
 * {@link ColumnFamilyEngine} to store the separated column families.
 * 
 * @author Rick-Rainer Ludwig
 */
public class TableEngineImpl implements TableEngine {

    private static final Logger logger = LoggerFactory.getLogger(TableEngine.class);

    private final Storage storage;
    private final TableDescriptor tableDescriptor;
    private final DatabaseEngineConfiguration configuration;
    private final Map<String, ColumnFamilyEngineImpl> columnFamilyEngines = new HashMap<>();

    public TableEngineImpl(Storage storage, TableDescriptor tableDescriptor, DatabaseEngineConfiguration configuration)
	    throws StorageException {
	super();
	this.storage = storage;
	this.tableDescriptor = tableDescriptor;
	this.configuration = configuration;
	logger.info("Starting table engine '" + tableDescriptor.getName() + "'...");
	StopWatch stopWatch = new StopWatch();
	stopWatch.start();
	initializeColumnFamilyEngines();
	stopWatch.stop();
	logger.info("Table engine '" + tableDescriptor.getName() + "' started in " + stopWatch.getMillis() + "ms.");
    }

    private void initializeColumnFamilyEngines() throws StorageException {
	for (ColumnFamilyDescriptor columnFamilyDescriptor : tableDescriptor.getColumnFamilies()) {
	    addColumnFamily(columnFamilyDescriptor);
	}
    }

    public void addColumnFamily(ColumnFamilyDescriptor columnFamilyDescriptor) throws StorageException {
	columnFamilyEngines.put(columnFamilyDescriptor.getName(),
		new ColumnFamilyEngineImpl(storage, columnFamilyDescriptor, configuration));
    }

    public void dropColumnFamily(ColumnFamilyDescriptor columnFamilyDescriptor) {
	ColumnFamilyEngineImpl columnFamilyEngine = columnFamilyEngines.get(tableDescriptor.getName());
	if (columnFamilyEngine != null) {
	    columnFamilyEngines.remove(columnFamilyDescriptor.getName());
	    columnFamilyEngine.close();
	}
    }

    @Override
    public void close() {
	logger.info("Closing table engine '" + tableDescriptor.getName() + "'...");
	StopWatch stopWatch = new StopWatch();
	stopWatch.start();
	for (ColumnFamilyEngineImpl columnFamilyEngine : columnFamilyEngines.values()) {
	    columnFamilyEngine.close();
	}
	stopWatch.stop();
	logger.info("Table engine '" + tableDescriptor.getName() + "' closed in " + stopWatch.getMillis() + "ms.");
    }

    public ColumnFamilyEngineImpl getColumnFamilyEngine(ColumnFamilyDescriptor columnFamilyDescriptor) {
	return columnFamilyEngines.get(columnFamilyDescriptor.getName());
    }
}