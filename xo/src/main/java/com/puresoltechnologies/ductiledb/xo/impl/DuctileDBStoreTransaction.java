package com.puresoltechnologies.ductiledb.xo.impl;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.spi.datastore.DatastoreTransaction;
import com.puresoltechnologies.ductiledb.core.core.core.DuctileDBGraph;

/**
 * This class implements an XO DatastoreTransaction for Titan databases.
 * 
 * @author Rick-Rainer Ludwig
 */
public class DuctileDBStoreTransaction implements DatastoreTransaction {

    /**
     * This field stores whether the transaction is currently active or not.
     */
    private boolean active = false;

    /**
     * This method contains the Titan graph as {@link TitanGraph} to handle on
     * it its transactions.
     */
    private final DuctileDBGraph graph;

    /**
     * This is the initial value constructor.
     * 
     * @param graph
     *            is the Titan graph as TitanGraph object on which this
     *            transaction shall work on.
     */
    public DuctileDBStoreTransaction(DuctileDBGraph graph) {
	if (graph == null) {
	    throw new IllegalArgumentException("titanGraph must not be null");
	}
	this.graph = graph;
    }

    @Override
    public void begin() {
	if (active) {
	    throw new XOException("There is already an active transaction.");
	}
	active = true;
    }

    @Override
    public void commit() {
	if (!active) {
	    throw new XOException("There is no active transaction.");
	}
	active = false;
	graph.commit();
    }

    @Override
    public void rollback() {
	if (!active) {
	    throw new XOException("There is no active transaction.");
	}
	active = false;
	graph.rollback();
    }

    @Override
    public boolean isActive() {
	return active;
    }
}
