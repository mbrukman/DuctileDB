package com.puresoltechnologies.ductiledb.storage.engine.cf;

import com.puresoltechnologies.ductiledb.storage.api.StorageException;

public class ColumnFamily {

    private final ColumnFamilyEngine columnFamilyEngine;

    public ColumnFamily(ColumnFamilyEngine columnFamilyEngine) {
	super();
	this.columnFamilyEngine = columnFamilyEngine;
    }

    public byte[] getName() {
	return columnFamilyEngine.getName();
    }

    public ColumnFamilyEngine getEngine() {
	return columnFamilyEngine;
    }

    public ColumnFamilyScanner getScanner(byte[] startRowKey, byte[] endRowKey) throws StorageException {
	return columnFamilyEngine.getScanner(startRowKey, endRowKey);
    }
}