package com.puresoltechnologies.ductiledb.engine.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CountingOutputStream extends FilterOutputStream {

    private long count = 0;

    public CountingOutputStream(OutputStream out) {
	super(out);
    }

    public CountingOutputStream(OutputStream out, long startCount) {
	super(out);
	this.count = startCount;
    }

    @Override
    public void close() throws IOException {
    	super.close();
    }
    
    public long getCount() {
	return count;
    }

    @Override
    public void write(int b) throws IOException {
	super.write(b);
	count++;
    }

}