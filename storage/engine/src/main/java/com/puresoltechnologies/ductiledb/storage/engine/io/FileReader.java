package com.puresoltechnologies.ductiledb.storage.engine.io;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.puresoltechnologies.ductiledb.storage.spi.Storage;

public abstract class FileReader<Stream extends DuctileDBInputStream> implements Closeable {

    private final Storage storage;
    private final File file;
    private Stream stream = null;

    public FileReader(Storage storage, File file) throws FileNotFoundException {
	super();
	this.storage = storage;
	this.file = file;
	stream = createStream(storage.open(file));
    }

    protected File getFile() {
	return file;
    }

    protected abstract Stream createStream(BufferedInputStream bufferedInputStream);

    protected Stream getStream() {
	return stream;
    }

    @Override
    public void close() throws IOException {
	stream.close();
    }

    public long getOffset() {
	return stream.getOffset();
    }

    public long skip(long n) throws IOException {
	return stream.skip(n);
    }

    public long goToOffset(long offset) throws IOException {
	if (stream.getOffset() < offset) {
	    stream.goToOffset(offset);
	} else {
	    stream.close();
	    stream = createStream(storage.open(file));
	    stream.skip(offset);
	}
	return stream.goToOffset(offset);
    }

    public int read(byte[] buffer, int off, int len) throws IOException {
	return stream.read(buffer, off, len);
    }

    public int read(byte[] buffer) throws IOException {
	return stream.read(buffer);
    }

}