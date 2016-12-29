package com.puresoltechnologies.ductiledb.core.tables.dml;

import java.util.Map;

import com.puresoltechnologies.ductiledb.core.tables.TableStore;
import com.puresoltechnologies.ductiledb.core.tables.ddl.TableDefinition;

public class PreparedTruncateImpl extends AbstractPreparedDMLStatement implements PreparedTruncate {

    public PreparedTruncateImpl(TableDefinition tableDefinition) {
	super(tableDefinition);
    }

    @Override
    public TableRowIterable execute(TableStore tableStore, Map<Integer, Comparable<?>> placeholderValue) {
	// TODO Auto-generated method stub
	return null;
    }

}