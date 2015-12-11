package com.puresoltechnologies.ductiledb.tinkerpop.test;

import org.apache.tinkerpop.gremlin.GraphProviderClass;
import org.apache.tinkerpop.gremlin.process.GroovyProcessComputerSuite;
import org.junit.runner.RunWith;

import com.puresoltechnologies.ductiledb.tinkerpop.DuctileGraph;
import com.puresoltechnologies.ductiledb.tinkerpop.DuctileGraphProvider;

@RunWith(GroovyProcessComputerSuite.class)
@GraphProviderClass(provider = DuctileGraphProvider.class, graph = DuctileGraph.class)
public class DuctileGroovyProcessComputerIT {
}