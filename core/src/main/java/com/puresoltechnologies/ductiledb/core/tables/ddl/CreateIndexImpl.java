package com.puresoltechnologies.ductiledb.core.tables.ddl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.puresoltechnologies.ductiledb.core.tables.ExecutionException;
import com.puresoltechnologies.ductiledb.core.tables.TableStore;
import com.puresoltechnologies.ductiledb.core.tables.TableStoreImpl;
import com.puresoltechnologies.ductiledb.core.tables.dml.TableRowIterable;
import com.puresoltechnologies.ductiledb.engine.DatabaseEngineImpl;
import com.puresoltechnologies.ductiledb.engine.cf.index.secondary.IndexType;
import com.puresoltechnologies.ductiledb.engine.cf.index.secondary.SecondaryIndexDescriptor;
import com.puresoltechnologies.ductiledb.engine.schema.ColumnFamilyDescriptor;
import com.puresoltechnologies.ductiledb.engine.schema.NamespaceDescriptor;
import com.puresoltechnologies.ductiledb.engine.schema.SchemaException;
import com.puresoltechnologies.ductiledb.engine.schema.SchemaManager;
import com.puresoltechnologies.ductiledb.engine.schema.TableDescriptor;
import com.puresoltechnologies.ductiledb.logstore.Key;
import com.puresoltechnologies.ductiledb.storage.api.StorageException;

public class CreateIndexImpl extends AbstractDDLStatement implements CreateIndex {

    private final List<String> columns = new ArrayList<>();
    private final TableStoreImpl tableStore;
    private final String namespace;
    private final String table;
    private final String columnFamily;
    private final String name;

    public CreateIndexImpl(TableStoreImpl tableStore, String namespace, String table, String columnFamily,
	    String name) {
	super();
	this.tableStore = tableStore;
	this.namespace = namespace;
	this.table = table;
	this.columnFamily = columnFamily;
	this.name = name;
    }

    @Override
    public void addColumn(String column) {
	columns.add(column);
    }

    @Override
    public TableRowIterable execute(TableStore tableStore, Map<Integer, Comparable<?>> placeholderValue)
	    throws ExecutionException {
	if ("system".equals(namespace)) {
	    throw new ExecutionException("Creating tables in 'system' namespace is not allowed.");
	}
	try {
	    DatabaseEngineImpl storageEngine = ((TableStoreImpl) tableStore).getStorageEngine();
	    SchemaManager schemaManager = storageEngine.getSchemaManager();
	    NamespaceDescriptor namespaceDescriptor = schemaManager.getNamespace(namespace);
	    TableDescriptor tableDescriptor = schemaManager.getTable(namespaceDescriptor, table);
	    ColumnFamilyDescriptor columnFamilyDescriptor = schemaManager.getColumnFamily(tableDescriptor,
		    Key.of(columnFamily));

	    Set<Key> columns = new HashSet<>();
	    SecondaryIndexDescriptor indexDescriptor = new SecondaryIndexDescriptor(name, columnFamilyDescriptor,
		    columns, IndexType.HEAP);
	    schemaManager.createIndex(columnFamilyDescriptor, indexDescriptor);

	    ((TableStoreImpl) tableStore).getSchema().addIndexDefinition(namespace, indexDescriptor);
	    return null;
	} catch (StorageException | SchemaException e) {
	    throw new ExecutionException("Could not create index '" + name + " in namespace " + namespace + "'.");
	}
    }

}
