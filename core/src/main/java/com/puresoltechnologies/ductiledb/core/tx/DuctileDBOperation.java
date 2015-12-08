package com.puresoltechnologies.ductiledb.core.tx;

import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Put;

public class DuctileDBOperation {

    private final OperationType operationType;
    private final Put put;
    private final Delete delete;

    public DuctileDBOperation(Put put) {
	super();
	this.operationType = OperationType.PUT;
	this.put = put;
	this.delete = null;
    }

    public DuctileDBOperation(Delete delete) {
	super();
	this.operationType = OperationType.DELETE;
	this.put = null;
	this.delete = delete;
    }

    public OperationType getOperationType() {
	return operationType;
    }

    public Put getPut() {
	return put;
    }

    public Delete getDelete() {
	return delete;
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	switch (operationType) {
	case PUT:
	    builder.append("put: ");
	    builder.append(put.toString());
	    break;
	case DELETE:
	    builder.append("delete: ");
	    builder.append(put.toString());
	    break;
	default:
	    builder.append(operationType.name());
	}
	return builder.toString();
    }
}
