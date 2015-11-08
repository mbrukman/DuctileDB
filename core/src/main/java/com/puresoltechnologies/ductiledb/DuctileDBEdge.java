package com.puresoltechnologies.ductiledb;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;

public interface DuctileDBEdge extends Edge {

    public DuctileDBVertex getStartVertex();

    public DuctileDBVertex getTargetVertex();

    @Override
    public Long getId();

    @Override
    public DuctileDBVertex getVertex(Direction direction) throws IllegalArgumentException;
}
