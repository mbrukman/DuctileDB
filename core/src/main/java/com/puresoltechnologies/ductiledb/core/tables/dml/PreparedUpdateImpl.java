package com.puresoltechnologies.ductiledb.core.tables.dml;

import java.util.Map;

import com.puresoltechnologies.ductiledb.core.tables.TableStore;
import com.puresoltechnologies.ductiledb.core.tables.ddl.TableDefinition;

public class PreparedUpdateImpl extends AbstractPreparedWhereSelectionStatement implements PreparedUpdate {

    public PreparedUpdateImpl(TableDefinition tableDefinition) {
	super(tableDefinition);
    }

    @Override
    public TableRowIterable execute(TableStore tableStore, Map<Integer, Object> placeholderValue) {
	// TODO Auto-generated method stub
	return null;
    }

}