package com.puresoltechnologies.ductiledb.xo.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.spi.datastore.DatastoreRelationManager;
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.type.RelationTypeMetadata;
import com.puresoltechnologies.ductiledb.api.Direction;
import com.puresoltechnologies.ductiledb.api.Edge;
import com.puresoltechnologies.ductiledb.api.Graph;
import com.puresoltechnologies.ductiledb.api.Vertex;
import com.puresoltechnologies.ductiledb.xo.impl.metadata.DuctileDBEdgeMetadata;
import com.puresoltechnologies.ductiledb.xo.impl.metadata.DuctileDBPropertyMetadata;

/**
 * This class implements the XO DatastorePropertyManager for Titan database.
 * 
 * @author Rick-Rainer Ludwig
 */
public class DucileDBStoreEdgeManager implements
	DatastoreRelationManager<Vertex, Long, Edge, DuctileDBEdgeMetadata, String, DuctileDBPropertyMetadata> {

    private final Graph graph;

    DucileDBStoreEdgeManager(Graph graph) {
	this.graph = graph;
    }

    @Override
    public boolean hasSingleRelation(Vertex source, RelationTypeMetadata<DuctileDBEdgeMetadata> metadata,
	    RelationTypeMetadata.Direction direction) {
	String label = metadata.getDatastoreMetadata().getDiscriminator();
	long count;
	switch (direction) {
	case FROM:
	    count = source.query().direction(Direction.OUT).labels(label).count();
	    break;
	case TO:
	    count = source.query().direction(Direction.IN).labels(label).count();
	    break;
	default:
	    throw new XOException("Unkown direction '" + direction.name() + "'.");
	}
	if (count > 1) {
	    throw new XOException("Multiple results are available.");
	}
	return count == 1;
    }

    @Override
    public Edge getSingleRelation(Vertex source, RelationTypeMetadata<EdgeMetadata> metadata,
	    RelationTypeMetadata.Direction direction) {
	String descriminator = metadata.getDatastoreMetadata().getDiscriminator();
	Iterable<Edge> edges;
	switch (direction) {
	case FROM:
	    edges = source.getEdges(Direction.OUT, descriminator);
	    break;
	case TO:
	    edges = source.getEdges(Direction.IN, descriminator);
	    break;
	default:
	    throw new XOException("Unkown direction '" + direction.name() + "'.");
	}
	Iterator<Edge> iterator = edges.iterator();
	if (!iterator.hasNext()) {
	    throw new XOException("No result is available.");
	}
	Edge result = iterator.next();
	if (iterator.hasNext()) {
	    throw new XOException("Multiple results are available.");
	}
	return result;
    }

    @Override
    public Iterable<Edge> getRelations(Vertex source, RelationTypeMetadata<EdgeMetadata> metadata,
	    RelationTypeMetadata.Direction direction) {
	String discriminator = metadata.getDatastoreMetadata().getDiscriminator();
	Iterable<Edge> edges = null;
	switch (direction) {
	case TO:
	    edges = source.getEdges(Direction.IN, discriminator);
	    break;
	case FROM:
	    edges = source.getEdges(Direction.OUT, discriminator);
	    break;
	default:
	    throw new XOException("Unknown direction '" + direction.name() + "'.");
	}
	List<Edge> result = new ArrayList<>();
	for (Edge edge : edges) {
	    result.add(edge);
	}
	return new Iterable<Edge>() {
	    @Override
	    public Iterator<Edge> iterator() {
		return result.iterator();
	    }
	};
    }

    @Override
    public Edge createRelation(Vertex source, RelationTypeMetadata<EdgeMetadata> metadata,
	    RelationTypeMetadata.Direction direction, Vertex target,
	    Map<PrimitivePropertyMethodMetadata<DuctileDBPropertyMetadata>, Object> exampleEntity) {
	String name = metadata.getDatastoreMetadata().getDiscriminator();
	switch (direction) {
	case FROM:
	    return source.addEdge(name, target);
	case TO:
	    return target.addEdge(name, source);
	default:
	    throw new XOException("Unknown direction '" + direction.name() + "'.");
	}
    }

    @Override
    public void deleteRelation(Edge edge) {
	edge.remove();
    }

    @Override
    public Vertex getTo(Edge edge) {
	return edge.getVertex(Direction.IN);
    }

    @Override
    public Vertex getFrom(Edge edge) {
	return edge.getVertex(Direction.OUT);
    }

    @Override
    public void setProperty(Edge edge, PrimitivePropertyMethodMetadata<DuctileDBPropertyMetadata> metadata,
	    Object value) {
	edge.setProperty(metadata.getDatastoreMetadata().getName(), value);
    }

    @Override
    public boolean hasProperty(Edge edge, PrimitivePropertyMethodMetadata<DuctileDBPropertyMetadata> metadata) {
	return edge.getProperty(metadata.getDatastoreMetadata().getName()) != null;
    }

    @Override
    public void removeProperty(Edge edge, PrimitivePropertyMethodMetadata<DuctileDBPropertyMetadata> metadata) {
	edge.removeProperty(metadata.getDatastoreMetadata().getName());
    }

    @Override
    public Object getProperty(Edge edge, PrimitivePropertyMethodMetadata<DuctileDBPropertyMetadata> metadata) {
	return edge.getProperty(metadata.getDatastoreMetadata().getName());
    }

    @Override
    public boolean isRelation(Object o) {
	return Edge.class.isAssignableFrom(o.getClass());
    }

    @Override
    public String getRelationDiscriminator(Edge edge) {
	return edge.getLabel();
    }

    @Override
    public Long getRelationId(Edge edge) {
	return edge.getId();
    }

    @Override
    public void flushRelation(Edge edge) {
	// intentionally left empty
    }

    @Override
    public Edge findRelationById(RelationTypeMetadata<DuctileDBEdgeMetadata> metadata, Long id) {
	return graph.getEdge(id);
    }

}
