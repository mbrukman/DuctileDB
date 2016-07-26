package com.puresoltechnologies.ductiledb.storage.engine.io.commitlog;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.puresoltechnologies.ductiledb.storage.engine.io.Bytes;
import com.puresoltechnologies.ductiledb.storage.engine.io.DuctileDBInputStream;
import com.puresoltechnologies.ductiledb.storage.engine.io.InputStreamIterable;

public class CommitLogIterable extends InputStreamIterable<CommitLogEntry> {

    private static final Logger logger = LoggerFactory.getLogger(CommitLogIterable.class);

    public CommitLogIterable(DuctileDBInputStream inputStream) {
	super(inputStream);
    }

    @Override
    protected CommitLogEntry readEntry() {
	try {
	    DuctileDBInputStream inputStream = getInputStream();
	    byte[] bytes = new byte[4];
	    int len = inputStream.read(bytes, 0, 4);
	    if (len == -1) {
		return null;
	    } else if (len < 4) {
		throw new IOException("Could not read the needed amount of bytes.");
	    }
	    int length = Bytes.toInt(bytes);
	    byte[] rowKey = new byte[length];
	    len = inputStream.read(rowKey, 0, length);
	    if (len < length) {
		throw new IOException("Could not read the needed amount of bytes.");
	    }
	    len = inputStream.read(bytes, 0, 4);
	    if (len < 4) {
		throw new IOException("Could not read the needed amount of bytes.");
	    }
	    length = Bytes.toInt(bytes);
	    byte[] key = new byte[length];
	    len = inputStream.read(key, 0, length);
	    if (len < length) {
		throw new IOException("Could not read the needed amount of bytes.");
	    }
	    len = inputStream.read(bytes, 0, 4);
	    if (len < 4) {
		throw new IOException("Could not read the needed amount of bytes.");
	    }
	    length = Bytes.toInt(bytes);
	    byte[] value = new byte[length];
	    len = inputStream.read(value, 0, length);
	    if (len < length) {
		throw new IOException("Could not read the needed amount of bytes.");
	    }
	    return new CommitLogEntry(rowKey, key, value);
	} catch (IOException e) {
	    logger.error("Error reading index file.", e);
	    return null;
	}
    }

}
