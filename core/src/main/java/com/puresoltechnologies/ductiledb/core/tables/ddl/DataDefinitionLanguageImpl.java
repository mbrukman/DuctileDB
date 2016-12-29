package com.puresoltechnologies.ductiledb.core.tables.ddl;

import java.io.File;

import com.puresoltechnologies.ductiledb.core.tables.TableStoreImpl;
import com.puresoltechnologies.ductiledb.core.tables.schema.TableStoreSchema;

public class DataDefinitionLanguageImpl implements DataDefinitionLanguage {

    private final TableStoreImpl tableStore;
    private final File directory;

    public DataDefinitionLanguageImpl(TableStoreImpl tableStore, File directory) {
	this.tableStore = tableStore;
	this.directory = directory;
    }

    @Override
    public CreateNamespace createCreateNamespace(String namespace) {
	return new CreateNamespaceImpl(tableStore, namespace);
    }

    @Override
    public DropNamespace createDropNamespace(String namespace) {
	return new DropNamespaceImpl(tableStore, namespace);
    }

    @Override
    public NamespaceDefinition getNamespace(String namespace) {
	TableStoreSchema schema = tableStore.getSchema();
	return schema.getNamespaceDefinition(namespace);
    }

    @Override
    public Iterable<NamespaceDefinition> getNamespaces() {
	TableStoreSchema schema = tableStore.getSchema();
	return schema.getNamespaceDefinitions();
    }

    @Override
    public CreateTable createCreateTable(String namespace, String table, String remark) {
	return new CreateTableImpl(tableStore, namespace, table, remark);
    }

    @Override
    public DropTable createDropTable(String namespace, String table) {
	return new DropTableImpl(tableStore, namespace, table);
    }

    @Override
    public Iterable<TableDefinition> getTables(String namespace) {
	TableStoreSchema schema = tableStore.getSchema();
	return schema.getTableDefinitions(namespace);
    }

    @Override
    public TableDefinition getTable(String namespace, String table) {
	TableStoreSchema schema = tableStore.getSchema();
	return schema.getTableDefinition(namespace, table);
    }

    @Override
    public CreateIndex createCreateIndex(String namespace, String table, String columnFamily, String index) {
	return new CreateIndexImpl(tableStore, namespace, table, columnFamily, index);
    }

    @Override
    public DropIndex createDropIndex(String namespace, String table, String index) {
	return new DropIndexImpl(tableStore, namespace, table, index);
    }
}