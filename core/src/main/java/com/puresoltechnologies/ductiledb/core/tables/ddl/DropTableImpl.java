package com.puresoltechnologies.ductiledb.core.tables.ddl;

import com.puresoltechnologies.ductiledb.api.tables.ddl.DropTable;
import com.puresoltechnologies.ductiledb.core.tables.TableStoreImpl;

public class DropTableImpl implements DropTable {

    private final TableStoreImpl tableStore;

    public DropTableImpl(TableStoreImpl tableStore, String namespace, String table) {
	super();
	this.tableStore = tableStore;
    }

    @Override
    public void execute() {
	// TODO Auto-generated method stub

    }

}
