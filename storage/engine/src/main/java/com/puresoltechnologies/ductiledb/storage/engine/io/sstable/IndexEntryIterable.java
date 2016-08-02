package com.puresoltechnologies.ductiledb.storage.engine.io.sstable;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.puresoltechnologies.ductiledb.storage.engine.index.IndexEntry;
import com.puresoltechnologies.ductiledb.storage.engine.index.RowKey;
import com.puresoltechnologies.ductiledb.storage.engine.io.Bytes;
import com.puresoltechnologies.ductiledb.storage.engine.io.DuctileDBInputStream;
import com.puresoltechnologies.ductiledb.storage.engine.io.InputStreamIterable;

public class IndexEntryIterable extends InputStreamIterable<IndexEntry> {

    private static final Logger logger = LoggerFactory.getLogger(IndexEntryIterable.class);

    private final File indexFile;

    public IndexEntryIterable(File indexFile, IndexInputStream inputStream) {
	super(inputStream);
	this.indexFile = indexFile;
    }

    @Override
    protected IndexEntry readEntry() {
	DuctileDBInputStream inputStream = getInputStream();
	try {
	    byte[] buffer = new byte[8];
	    int len = inputStream.read(buffer, 0, 4);
	    if (len == -1) {
		return null;
	    } else if (len < 4) {
		logger.warn("Could not read full number of bytes needed. It is maybe a broken index file.");
		return null;
	    }
	    int keyLength = Bytes.toInt(buffer);
	    byte[] rowKey = new byte[keyLength];
	    len = inputStream.read(rowKey);
	    if (len < keyLength) {
		logger.warn("Could not read full number of bytes needed. It is maybe a broken index file.");
		return null;
	    }
	    len = inputStream.read(buffer, 0, 8);
	    if (len < 8) {
		logger.warn("Could not read full number of bytes needed. It is maybe a broken index file.");
		return null;
	    }
	    long offset = Bytes.toLong(buffer);
	    return new IndexEntry(new RowKey(rowKey), indexFile, offset);
	} catch (IOException e) {
	    logger.error("Error reading index file.", e);
	    return null;
	}
    }

}