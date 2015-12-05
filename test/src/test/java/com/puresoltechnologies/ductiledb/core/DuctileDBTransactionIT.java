package com.puresoltechnologies.ductiledb.core;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.junit.Test;

import com.puresoltechnologies.ductiledb.api.DuctileDBEdge;
import com.puresoltechnologies.ductiledb.api.DuctileDBVertex;

public class DuctileDBTransactionIT extends AbstractDuctileDBGraphTest {

    @Test
    public void testTransaction() throws IOException {
	DuctileDBVertex vertex1 = graph.addVertex();
	DuctileDBVertex vertex2 = graph.addVertex();
	DuctileDBEdge edge = graph.addEdge(vertex1, vertex2, "edge");

	assertNull(graph.getVertex(vertex1.getId()));
	assertNull(graph.getVertex(vertex2.getId()));
	assertNull(graph.getEdge(edge.getId()));

	graph.commit();

	assertNotNull(graph.getVertex(vertex1.getId()));
	assertNotNull(graph.getVertex(vertex2.getId()));
	assertNotNull(graph.getEdge(edge.getId()));

	edge.remove();

	assertNotNull(graph.getVertex(vertex1.getId()));
	assertNotNull(graph.getVertex(vertex2.getId()));
	assertNotNull(graph.getEdge(edge.getId()));

	graph.commit();

	assertNotNull(graph.getVertex(vertex1.getId()));
	assertNotNull(graph.getVertex(vertex2.getId()));
	assertNull(graph.getEdge(edge.getId()));

	vertex1.remove();
	vertex2.remove();

	assertNotNull(graph.getVertex(vertex1.getId()));
	assertNotNull(graph.getVertex(vertex2.getId()));
	assertNull(graph.getEdge(edge.getId()));

	graph.commit();

	assertNull(graph.getVertex(vertex1.getId()));
	assertNull(graph.getVertex(vertex2.getId()));
	assertNull(graph.getEdge(edge.getId()));
    }

    @Test
    public void testRollback() throws IOException {
	DuctileDBVertex vertex1 = graph.addVertex();
	DuctileDBVertex vertex2 = graph.addVertex();
	DuctileDBEdge edge = graph.addEdge(vertex1, vertex2, "edge");

	assertNull(graph.getVertex(vertex1.getId()));
	assertNull(graph.getVertex(vertex2.getId()));
	assertNull(graph.getEdge(edge.getId()));

	graph.rollback();

	assertNull(graph.getVertex(vertex1.getId()));
	assertNull(graph.getVertex(vertex2.getId()));
	assertNull(graph.getEdge(edge.getId()));

	graph.commit();

	assertNull(graph.getVertex(vertex1.getId()));
	assertNull(graph.getVertex(vertex2.getId()));
	assertNull(graph.getEdge(edge.getId()));
    }
}