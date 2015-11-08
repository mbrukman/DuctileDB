package com.puresoltechnologies.ductiledb;

import java.util.Iterator;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;

import com.puresoltechnologies.ductiledb.utils.IdEncoder;
import com.tinkerpop.blueprints.Edge;

public class EdgeIterable implements Iterable<Edge> {

    private final DuctileDBGraphImpl graph;
    private final Iterator<Result> resultIterator;

    public EdgeIterable(DuctileDBGraphImpl graph, ResultScanner resultScanner) {
	super();
	this.graph = graph;
	resultIterator = resultScanner.iterator();
    }

    @Override
    public Iterator<Edge> iterator() {
	return new Iterator<Edge>() {

	    @Override
	    public boolean hasNext() {
		return resultIterator.hasNext();
	    }

	    @Override
	    public Edge next() {
		Result result = resultIterator.next();
		return ResultDecoder.toEdge(graph, IdEncoder.decodeRowId(result.getRow()), result);
	    }
	};
    }

}
