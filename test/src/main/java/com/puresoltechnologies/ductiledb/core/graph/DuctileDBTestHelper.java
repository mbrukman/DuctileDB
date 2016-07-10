package com.puresoltechnologies.ductiledb.core.graph;

import static com.puresoltechnologies.ductiledb.core.graph.schema.HBaseSchema.DUCTILEDB_NAMESPACE;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ServiceException;
import com.puresoltechnologies.ductiledb.api.graph.DuctileDBEdge;
import com.puresoltechnologies.ductiledb.api.graph.DuctileDBGraph;
import com.puresoltechnologies.ductiledb.api.graph.DuctileDBVertex;
import com.puresoltechnologies.ductiledb.api.graph.ElementType;
import com.puresoltechnologies.ductiledb.api.graph.manager.DuctileDBGraphManager;
import com.puresoltechnologies.ductiledb.api.graph.schema.DuctileDBSchemaManager;
import com.puresoltechnologies.ductiledb.core.graph.DuctileDBGraphFactory;
import com.puresoltechnologies.ductiledb.core.graph.DuctileDBGraphImpl;
import com.puresoltechnologies.ductiledb.core.graph.schema.DuctileDBHealthCheck;

/**
 * A collection of simple methods to support testing.
 * 
 * @author Rick-Rainer Ludwig
 */
public class DuctileDBTestHelper {

    private static final Logger logger = LoggerFactory.getLogger(DuctileDBTestHelper.class);

    /**
     * Runs through an {@link Iterable} and counts the number of elements.
     * 
     * @param iterable
     *            is the {@link Iterable} to count the elements in.
     * @return A long value with the number of elements is returned.
     */
    public static long count(Iterable<?> iterable) {
	long[] count = { 0 };
	iterable.forEach(c -> count[0]++);
	return count[0];
    }

    /**
     * Runs through an {@link Iterator} and counts the number of elements.
     * 
     * @param iterator
     *            is the {@link Iterator} to count the elements in.
     * @return The current number of elements is returned.
     */
    public static long count(Iterator<?> iterator) {
	long[] count = { 0 };
	iterator.forEachRemaining(c -> count[0]++);
	return count[0];
    }

    public static void removeTables() throws IOException, ServiceException {
	try (Connection connection = DuctileDBGraphFactory.createConnection("localhost",
		DuctileDBGraphFactory.DEFAULT_ZOOKEEPER_PORT, "localhost", DuctileDBGraphFactory.DEFAULT_MASTER_PORT)) {
	    removeTables(connection);
	}
    }

    private static void removeTables(Connection connection) throws IOException {
	logger.info("Remove all DuctileDB tables...");
	Admin admin = connection.getAdmin();
	HTableDescriptor[] listTables = admin.listTables();
	for (HTableDescriptor tableDescriptor : listTables) {
	    TableName tableName = tableDescriptor.getTableName();
	    if (DUCTILEDB_NAMESPACE.equals(tableName.getNamespaceAsString())) {
		if (admin.isTableEnabled(tableName)) {
		    logger.info("Disable table '" + tableName + "'...");
		    admin.disableTableAsync(tableName);
		    logger.info("Table '" + tableName + "' disabled.");
		}
	    }
	}
	for (HTableDescriptor tableDescriptor : listTables) {
	    TableName tableName = tableDescriptor.getTableName();
	    if (DUCTILEDB_NAMESPACE.equals(tableName.getNamespaceAsString())) {
		logger.info("Delete table '" + tableName + "'...");
		admin.deleteTable(tableName);
		logger.info("Table '" + tableName + "' deleted.");
	    }
	}
	NamespaceDescriptor[] namespaceDescriptors = admin.listNamespaceDescriptors();
	for (NamespaceDescriptor namespaceDescriptor : namespaceDescriptors) {
	    if (DUCTILEDB_NAMESPACE.equals(namespaceDescriptor.getName())) {
		admin.deleteNamespace(DUCTILEDB_NAMESPACE);
	    }
	}
	logger.info("All DuctileDB tables removed.");
    }

    public static void removeGraph(DuctileDBGraph graph) throws IOException {
	logger.info("Delete ductile graph...");
	for (DuctileDBEdge edge : graph.getEdges()) {
	    edge.remove();
	}
	for (DuctileDBVertex vertex : graph.getVertices()) {
	    vertex.remove();
	}
	graph.commit();
	DuctileDBGraphManager graphManager = graph.createGraphManager();
	for (String variableName : graphManager.getVariableNames()) {
	    graphManager.removeVariable(variableName);
	}
	DuctileDBSchemaManager schemaManager = graph.createSchemaManager();
	for (String typeName : schemaManager.getDefinedTypes()) {
	    for (ElementType elementType : ElementType.values()) {
		schemaManager.removeTypeDefinition(elementType, typeName);
	    }
	}
	for (String propertyName : schemaManager.getDefinedProperties()) {
	    for (ElementType elementType : ElementType.values()) {
		schemaManager.removePropertyDefinition(elementType, propertyName);
	    }
	}
	assertEquals(DuctileDBGraphImpl.class, graph.getClass());
	new DuctileDBHealthCheck((DuctileDBGraphImpl) graph).runCheck();
	logger.info("Ductile graph deleted.");
    }
}