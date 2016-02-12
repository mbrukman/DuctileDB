package com.puresoltechnologies.ductiledb.tinkerpop;

import java.io.File;
import java.io.IOException;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.Connection;

import com.google.protobuf.ServiceException;
import com.puresoltechnologies.ductiledb.api.DuctileDBGraph;
import com.puresoltechnologies.ductiledb.core.DuctileDBGraphFactory;

/**
 * This class is used to create a new {@link DuctileGraph} object.
 * 
 * @author Rick-Rainer Ludwig
 *
 */
public class DuctileGraphFactory {

    /**
     * This method creates a new {@link DuctileGraph} based on an existing HBase
     * {@link Connection}.
     * 
     * @param connection
     * @param configuration
     * @return
     * @throws IOException
     */
    public static DuctileGraph createGraph(Connection connection, BaseConfiguration configuration) throws IOException {
	DuctileDBGraph ductileDBGraph = DuctileDBGraphFactory.createGraph(connection);
	return new DuctileGraph(ductileDBGraph, configuration);
    }

    /**
     * This method create a new {@link DuctileGraph} only based on its
     * configuration. A connection to HBase is opened during the process.
     * 
     * @param configuration
     * @return
     * @throws IOException
     * @throws ServiceException
     */
    public static DuctileGraph createGraph(File hbaseSiteFile, BaseConfiguration configuration)
	    throws IOException, ServiceException {
	DuctileDBGraph graph = DuctileDBGraphFactory.createGraph(hbaseSiteFile);
	return new DuctileGraph(graph, configuration);
    }

    public static DuctileGraph createGraph(String zookeeperHost, int zookeeperPort, String masterHost, int masterPort,
	    BaseConfiguration configuration)
		    throws MasterNotRunningException, ZooKeeperConnectionException, ServiceException, IOException {
	DuctileDBGraph graph = DuctileDBGraphFactory.createGraph(zookeeperHost, zookeeperPort, masterHost, masterPort);
	return new DuctileGraph(graph, configuration);
    }

}
