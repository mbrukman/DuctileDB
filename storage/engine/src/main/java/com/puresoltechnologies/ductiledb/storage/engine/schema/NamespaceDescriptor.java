package com.puresoltechnologies.ductiledb.storage.engine.schema;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.puresoltechnologies.ductiledb.storage.spi.Storage;

public class NamespaceDescriptor {

    private final Map<String, TableDescriptor> tables = new HashMap<>();
    private final String name;
    private final Storage storage;
    private final File directory;

    public NamespaceDescriptor(String name, Storage storage, File directory) {
	this.name = name;
	this.storage = storage;
	this.directory = directory;
    }

    public final String getName() {
	return name;
    }

    public final File getDirectory() {
	return directory;
    }

    @Override
    public String toString() {
	return "namespace:" + name;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((directory == null) ? 0 : directory.hashCode());
	result = prime * result + ((name == null) ? 0 : name.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	NamespaceDescriptor other = (NamespaceDescriptor) obj;
	if (directory == null) {
	    if (other.directory != null)
		return false;
	} else if (!directory.equals(other.directory))
	    return false;
	if (name == null) {
	    if (other.name != null)
		return false;
	} else if (!name.equals(other.name))
	    return false;
	return true;
    }

    public void addTable(TableDescriptor tableDescriptor) {
	tables.put(tableDescriptor.getName(), tableDescriptor);
    }

    public void removeTable(TableDescriptor tableDescriptor) {
	tables.remove(tableDescriptor.getName());
    }

    public Iterator<TableDescriptor> getTables() {
	return tables.values().iterator();
    }

    public TableDescriptor getTable(String tableName) {
	return tables.get(tableName);
    }

}