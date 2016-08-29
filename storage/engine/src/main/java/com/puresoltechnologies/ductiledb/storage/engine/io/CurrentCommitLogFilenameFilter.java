package com.puresoltechnologies.ductiledb.storage.engine.io;

import java.io.File;
import java.io.FilenameFilter;

import com.puresoltechnologies.ductiledb.storage.engine.cf.ColumnFamilyEngine;
import com.puresoltechnologies.ductiledb.storage.engine.cf.ColumnFamilyEngineImpl;
import com.puresoltechnologies.ductiledb.storage.spi.Storage;

/**
 * This {@link FilenameFilter} is used to list commit logs.
 * 
 * @author Rick-Rainer Ludwig
 */
public class CurrentCommitLogFilenameFilter implements FilenameFilter {

    private final Storage storage;

    public CurrentCommitLogFilenameFilter(Storage storage) {
	this.storage = storage;
    }

    @Override
    public boolean accept(File dir, String name) {
	if (name.startsWith(ColumnFamilyEngine.COMMIT_LOG_PREFIX)
		&& name.endsWith(ColumnFamilyEngine.DATA_FILE_SUFFIX)) {
	    File dataFile = new File(dir, name);
	    File compactedName = ColumnFamilyEngineImpl.getCompactedName(dataFile);
	    if (storage.exists(compactedName)) {
		return false;
	    }
	    File indexFile = DataFileSet.getIndexName(dataFile);
	    return !storage.exists(indexFile);
	}
	return false;
    }

}
