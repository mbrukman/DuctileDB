package com.puresoltechnologies.ductiledb.core.tables.columns;

import com.puresoltechnologies.ductiledb.api.tables.columns.ColumnType;
import com.puresoltechnologies.ductiledb.storage.engine.io.Bytes;

public class ByteColumnType implements ColumnType<Byte> {

    @Override
    public String getName() {
	return "BYTE";
    }

    @Override
    public Class<Byte> getJavaClass() {
	return Byte.class;
    }

    @Override
    public byte[] toBytes(Byte value) {
	return Bytes.toBytes(value);
    }

    @Override
    public Byte fromBytes(byte[] value) {
	return Bytes.toByte(value);
    }

}
