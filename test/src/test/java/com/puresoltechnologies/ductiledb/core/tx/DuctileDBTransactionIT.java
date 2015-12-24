package com.puresoltechnologies.ductiledb.core.tx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.configuration.BaseConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.puresoltechnologies.ductiledb.api.DuctileDBEdge;
import com.puresoltechnologies.ductiledb.api.DuctileDBGraph;
import com.puresoltechnologies.ductiledb.api.DuctileDBVertex;
import com.puresoltechnologies.ductiledb.api.tx.DuctileDBTransaction;
import com.puresoltechnologies.ductiledb.core.AbstractDuctileDBGraphTest;
import com.puresoltechnologies.ductiledb.core.DuctileDBGraphImpl;
import com.puresoltechnologies.ductiledb.core.DuctileDBTestHelper;
import com.puresoltechnologies.ductiledb.core.GraphFactory;
import com.puresoltechnologies.ductiledb.core.schema.DuctileDBHealthCheck;

/**
 * This intergrationt tests check the correct behavior of transactions.
 * 
 * @author Rick-Rainer Ludwig
 */
public class DuctileDBTransactionIT extends AbstractDuctileDBGraphTest {

    private static DuctileDBGraphImpl graph;

    @BeforeClass
    public static void initializeGraph() {
	graph = getGraph();
    }

    @Before
    public void cleanupGraph() throws IOException {
	DuctileDBTestHelper.removeGraph(graph);
    }

    @After
    public void checkGraph() throws IOException {
	DuctileDBHealthCheck.runCheck(graph);
    }

    @Test
    public void testCommitForSingleVertex() throws IOException {
	DuctileDBVertex vertex = graph.addVertex();

	assertInTransaction(vertex);
	assertNotInGraph(vertex);

	graph.commit();

	assertInTransaction(vertex);
	assertInGraph(vertex);

	vertex.addLabel("label");

	assertInTransaction(vertex);
	assertUnequalInGraph(vertex);

	graph.commit();

	assertInTransaction(vertex);
	assertInGraph(vertex);

	vertex.removeLabel("label");

	assertInTransaction(vertex);
	assertUnequalInGraph(vertex);

	graph.commit();

	assertInTransaction(vertex);
	assertInGraph(vertex);

	vertex.setProperty("key", "value");

	assertInTransaction(vertex);
	assertUnequalInGraph(vertex);

	graph.commit();

	assertInTransaction(vertex);
	assertInGraph(vertex);

	vertex.removeProperty("key");

	assertInTransaction(vertex);
	assertUnequalInGraph(vertex);

	graph.commit();

	assertInTransaction(vertex);
	assertInGraph(vertex);
    }

    /**
     * Checks the simple creation of vertices and edges and the correct behavior
     * before and after commits.
     * 
     * @throws IOException
     */
    @Test
    public void testBasicCommit() throws IOException {
	DuctileDBVertex vertex1 = graph.addVertex();
	DuctileDBVertex vertex2 = graph.addVertex();
	DuctileDBEdge edge = graph.addEdge(vertex1, vertex2, "edge");

	assertInTransaction(vertex1);
	assertInTransaction(vertex2);
	assertInTransaction(edge);

	assertNotInGraph(vertex1);
	assertNotInGraph(vertex2);
	assertNotInGraph(edge);

	graph.commit();

	assertInGraph(vertex1);
	assertInGraph(vertex2);
	assertInGraph(edge);

	edge.remove();

	assertNotInTransaction(edge);

	assertInGraphWithDifferentEdges(vertex1);
	assertInGraphWithDifferentEdges(vertex2);
	assertInGraph(edge);

	graph.commit();

	assertNotInGraph(edge);

	vertex1.remove();
	vertex2.remove();

	assertNotInTransaction(vertex1);
	assertNotInTransaction(vertex2);
	assertNotInTransaction(edge);

	assertNotInGraph(edge);

	graph.commit();

	assertNotInGraph(vertex1);
	assertNotInGraph(vertex2);
	assertNotInGraph(edge);
    }

    /**
     * Checks that a rollback really drops the changes in the transaction and a
     * following commit will not change the graph.
     * 
     * @throws IOException
     */
    @Test
    public void testRollback() throws IOException {
	DuctileDBVertex vertex1 = graph.addVertex();
	DuctileDBVertex vertex2 = graph.addVertex();
	DuctileDBEdge edge = graph.addEdge(vertex1, vertex2, "edge");

	assertInTransaction(vertex1);
	assertInTransaction(vertex2);
	assertInTransaction(edge);

	assertNotInGraph(vertex1);
	assertNotInGraph(vertex2);
	assertNotInGraph(edge);

	graph.rollback();

	assertNotInTransaction(vertex1);
	assertNotInTransaction(vertex2);
	assertNotInTransaction(edge);

	assertNotInGraph(vertex1);
	assertNotInGraph(vertex2);
	assertNotInGraph(edge);

	graph.commit();

	assertNotInTransaction(vertex1);
	assertNotInTransaction(vertex2);
	assertNotInTransaction(edge);

	assertNotInGraph(vertex1);
	assertNotInGraph(vertex2);
	assertNotInGraph(edge);
    }

    /**
     * This test checks the behavior of changes on not committed elements.
     * 
     * @throws IOException
     */
    @Test
    public void testChangesOnNotCommittedElements() throws IOException {
	DuctileDBVertex vertex1 = graph.addVertex();
	DuctileDBVertex vertex2 = graph.addVertex();
	DuctileDBEdge edge = graph.addEdge(vertex1, vertex2, "edge");

	assertInTransaction(vertex1);
	assertInTransaction(vertex2);
	assertInTransaction(edge);

	assertNotInGraph(vertex1);
	assertNotInGraph(vertex2);
	assertNotInGraph(edge);

	/*
	 * The next operation needs to work...
	 */
	edge.setProperty("property1", "value1");
	assertEquals("value1", edge.getProperty("property1"));

	assertUnequalInTransaction(vertex1);
	assertUnequalInTransaction(vertex2);
	assertInTransaction(edge);

	assertNotInGraph(vertex1);
	assertNotInGraph(vertex2);
	assertNotInGraph(edge);

	graph.commit();

	assertInGraph(vertex1);
	assertInGraph(vertex2);
	assertInGraph(edge);
	/*
	 * The next operation needs to work...
	 */
	edge.removeProperty("property1");
	assertNull(edge.getProperty("property1"));

	assertUnequalInTransaction(vertex1);
	assertUnequalInTransaction(vertex2);
	assertInTransaction(edge);

	assertUnequalInGraph(vertex1);
	assertUnequalInGraph(vertex2);
	assertUnequalInGraph(edge);

	graph.commit();

	assertInGraph(vertex1);
	assertInGraph(vertex2);
	assertInGraph(edge);
    }

    /////////////////////////////////////////////////////////////////////

    @Test
    public void shouldAllowJustCommitOnlyWithAutoTransaction() {
	// not expecting any exceptions here
	graph.getCurrentTransaction().commit();
    }

    @Test
    public void shouldAllowJustRollbackOnlyWithAutoTransaction() {
	// not expecting any exceptions here
	graph.getCurrentTransaction().rollback();
    }

    @Test
    public void shouldAllowAutoTransactionToWorkWithoutMutationByDefault() {
	// expecting no exceptions to be thrown here
	graph.getCurrentTransaction().commit();
	graph.getCurrentTransaction().rollback();
	graph.getCurrentTransaction().commit();
    }

    @Test
    public void shouldNotifyTransactionListenersOnCommitSuccess() {
	final AtomicInteger count = new AtomicInteger(0);
	graph.getCurrentTransaction().addTransactionListener(s -> {
	    if (s == DuctileDBTransaction.Status.COMMIT)
		count.incrementAndGet();
	});
	graph.getCurrentTransaction().commit();

	assertEquals(1, count.get());
    }

    @Test
    public void shouldNotifyTransactionListenersInSameThreadOnlyOnCommitSuccess() throws Exception {
	final AtomicInteger count = new AtomicInteger(0);
	graph.getCurrentTransaction().addTransactionListener(s -> {
	    if (s == DuctileDBTransaction.Status.COMMIT)
		count.incrementAndGet();
	});

	final Thread t = new Thread(() -> graph.getCurrentTransaction().commit());
	t.start();
	t.join();

	assertEquals(0, count.get());
    }

    @Test
    public void shouldNotifyTransactionListenersOnRollbackSuccess() {
	final AtomicInteger count = new AtomicInteger(0);
	graph.getCurrentTransaction().addTransactionListener(s -> {
	    if (s == DuctileDBTransaction.Status.ROLLBACK)
		count.incrementAndGet();
	});
	graph.getCurrentTransaction().rollback();

	assertEquals(1, count.get());
    }

    @Test
    public void shouldNotifyTransactionListenersInSameThreadOnlyOnRollbackSuccess() throws Exception {
	final AtomicInteger count = new AtomicInteger(0);
	graph.getCurrentTransaction().addTransactionListener(s -> {
	    if (s == DuctileDBTransaction.Status.ROLLBACK)
		count.incrementAndGet();
	});

	final Thread t = new Thread(() -> graph.getCurrentTransaction().rollback());
	t.start();
	t.join();

	assertEquals(0, count.get());
    }

    @Test
    public void shouldCommitElementAutoTransactionByDefault() {
	final DuctileDBVertex v1 = graph.addVertex();
	final DuctileDBEdge e1 = v1.addEdge("l", v1);
	assertVertexEdgeCounts(1, 1);
	assertEquals(v1.getId(), graph.getVertex(v1.getId()).getId());
	assertEquals(e1.getId(), graph.getEdge(e1.getId()).getId());
	graph.getCurrentTransaction().commit();
	assertVertexEdgeCounts(1, 1);
	assertEquals(v1.getId(), graph.getVertex(v1.getId()).getId());
	assertEquals(e1.getId(), graph.getEdge(e1.getId()).getId());

	graph.getVertex(v1.getId()).remove();
	assertVertexEdgeCounts(0, 0);
	graph.getCurrentTransaction().rollback();
	assertVertexEdgeCounts(1, 1);

	graph.getVertex(v1.getId()).remove();
	assertVertexEdgeCounts(0, 0);
	graph.getCurrentTransaction().commit();
	assertVertexEdgeCounts(0, 0);
    }

    @Test
    public void shouldRollbackElementAutoTransactionByDefault() {
	final DuctileDBVertex v1 = graph.addVertex();
	final DuctileDBEdge e1 = v1.addEdge("l", v1);
	assertVertexEdgeCounts(1, 1);
	assertEquals(v1.getId(), graph.getVertex(v1.getId()).getId());
	assertEquals(e1.getId(), graph.getEdge(e1.getId()).getId());
	graph.getCurrentTransaction().rollback();
	assertVertexEdgeCounts(0, 0);
    }

    @Test
    public void shouldCommitPropertyAutoTransactionByDefault() {
	final DuctileDBVertex v1 = graph.addVertex();
	final DuctileDBEdge e1 = v1.addEdge("l", v1);
	graph.getCurrentTransaction().commit();
	assertVertexEdgeCounts(1, 1);
	assertEquals(v1.getId(), graph.getVertex(v1.getId()).getId());
	assertEquals(e1.getId(), graph.getEdge(e1.getId()).getId());

	v1.setProperty("name", "marko");
	assertEquals("marko", v1.<String> getProperty("name"));
	assertEquals("marko", graph.getVertex(v1.getId()).<String> getProperty("name"));
	graph.getCurrentTransaction().commit();

	assertEquals("marko", v1.<String> getProperty("name"));
	assertEquals("marko", graph.getVertex(v1.getId()).<String> getProperty("name"));

	v1.setProperty("name", "stephen");

	assertEquals("stephen", v1.<String> getProperty("name"));
	assertEquals("stephen", graph.getVertex(v1.getId()).<String> getProperty("name"));

	graph.getCurrentTransaction().commit();

	assertEquals("stephen", v1.<String> getProperty("name"));
	assertEquals("stephen", graph.getVertex(v1.getId()).<String> getProperty("name"));

	e1.setProperty("name", "xxx");

	assertEquals("xxx", e1.<String> getProperty("name"));
	assertEquals("xxx", graph.getEdge(e1.getId()).<String> getProperty("name"));

	graph.getCurrentTransaction().commit();

	assertEquals("xxx", e1.<String> getProperty("name"));
	assertEquals("xxx", graph.getEdge(e1.getId()).<String> getProperty("name"));

	assertVertexEdgeCounts(1, 1);
	assertEquals(v1.getId(), graph.getVertex(v1.getId()).getId());
	assertEquals(e1.getId(), graph.getEdge(e1.getId()).getId());
    }

    @Test
    public void shouldRollbackPropertyAutoTransactionByDefault() throws IOException {
	final DuctileDBVertex v1 = graph.addVertex(new HashSet<>(), toMap("name", "marko"));
	final DuctileDBEdge e1 = v1.addEdge("l", v1, toMap("name", "xxx"));
	assertVertexEdgeCounts(1, 1);
	assertEquals(v1.getId(), graph.getVertex(v1.getId()).getId());
	assertEquals(e1.getId(), graph.getEdge(e1.getId()).getId());
	assertEquals("marko", v1.<String> getProperty("name"));
	assertEquals("xxx", e1.<String> getProperty("name"));
	graph.commit();

	assertEquals("marko", v1.<String> getProperty("name"));
	assertEquals("marko", graph.getVertex(v1.getId()).<String> getProperty("name"));

	v1.setProperty("name", "stephen");

	assertEquals("stephen", v1.<String> getProperty("name"));
	assertEquals("stephen", graph.getVertex(v1.getId()).<String> getProperty("name"));

	graph.getCurrentTransaction().rollback();

	assertEquals("marko", v1.<String> getProperty("name"));
	assertEquals("marko", graph.getVertex(v1.getId()).<String> getProperty("name"));

	e1.setProperty("name", "yyy");

	assertEquals("yyy", e1.<String> getProperty("name"));
	assertEquals("yyy", graph.getEdge(e1.getId()).<String> getProperty("name"));

	graph.getCurrentTransaction().rollback();

	assertEquals("xxx", e1.<String> getProperty("name"));
	assertEquals("xxx", graph.getEdge(e1.getId()).<String> getProperty("name"));

	assertVertexEdgeCounts(1, 1);
    }

    @Test
    public void shouldRollbackOnCloseByDefault() throws Exception {
	final AtomicReference<Long> oid = new AtomicReference<>();
	final AtomicReference<DuctileDBVertex> vid = new AtomicReference<>();
	final Thread t = new Thread(() -> {
	    vid.set(graph.addVertex(new HashSet<>(), toMap("name", "stephen")));
	    try {
		graph.commit();
		try (DuctileDBTransaction ignored = graph.getCurrentTransaction()) {
		    final DuctileDBVertex v1 = graph.addVertex(new HashSet<>(), toMap("name", "marko"));
		    oid.set(v1.getId());
		}
	    } catch (Exception e) {
		throw new RuntimeException(e);
	    }
	});
	t.start();
	t.join();

	// this was committed
	assertNotNull(graph.getVertex(vid.get().getId()));

	// this was not
	assertNull("Vertex should not be found as close behavior was set to rollback", graph.getVertex(oid.get()));
    }

    @Test
    public void shouldExecuteWithCompetingThreads() {
	int totalThreads = 250;
	final AtomicInteger vertices = new AtomicInteger(0);
	final AtomicInteger edges = new AtomicInteger(0);
	final AtomicInteger completedThreads = new AtomicInteger(0);
	for (int i = 0; i < totalThreads; i++) {
	    new Thread() {
		@Override
		public void run() {
		    final Random random = new Random();
		    if (random.nextBoolean()) {
			final DuctileDBVertex a = graph.addVertex();
			final DuctileDBVertex b = graph.addVertex();
			final DuctileDBEdge e = a.addEdge("friend", b);

			vertices.getAndAdd(2);
			a.setProperty("test", this.getId());
			b.setProperty("blah", random.nextDouble());
			e.setProperty("bloop", random.nextInt());
			edges.getAndAdd(1);
			try {
			    graph.commit();
			} catch (IOException e1) {
			    throw new RuntimeException(e1);
			}
		    } else {
			final DuctileDBVertex a = graph.addVertex();
			final DuctileDBVertex b = graph.addVertex();
			final DuctileDBEdge e = a.addEdge("friend", b);

			a.setProperty("test", this.getId());
			b.setProperty("blah", random.nextDouble());
			e.setProperty("bloop", random.nextInt());

			if (random.nextBoolean()) {
			    try {
				graph.commit();
			    } catch (IOException e1) {
				throw new RuntimeException(e1);
			    }
			    vertices.getAndAdd(2);
			    edges.getAndAdd(1);
			} else {
			    try {
				graph.rollback();
			    } catch (IOException e1) {
				throw new RuntimeException(e1);
			    }
			}
		    }
		    completedThreads.getAndAdd(1);
		}
	    }.start();
	}

	while (completedThreads.get() < totalThreads) {
	}

	assertEquals(completedThreads.get(), 250);
	assertVertexEdgeCounts(vertices.get(), edges.get());
    }

    @Test
    public void shouldExecuteCompetingThreadsOnMultipleDbInstances() throws Exception {
	// the idea behind this test is to simulate a gremlin-server environment
	// where two graphs of the same type
	// are being mutated by multiple threads. originally replicated a bug
	// that was part of OrientDB.

	final DuctileDBGraph g1 = GraphFactory.createGraph(new BaseConfiguration());

	final Thread threadModFirstGraph = new Thread() {
	    @Override
	    public void run() {
		graph.addVertex();
		graph.getCurrentTransaction().commit();
	    }
	};

	threadModFirstGraph.start();
	threadModFirstGraph.join();

	final Thread threadReadBothGraphs = new Thread() {
	    @Override
	    public void run() {
		final long gCounter = DuctileDBTestHelper.count(graph.getVertices());
		assertEquals(1l, gCounter);

		final long g1Counter = DuctileDBTestHelper.count(g1.getVertices());
		assertEquals(0l, g1Counter);
	    }
	};

	threadReadBothGraphs.start();
	threadReadBothGraphs.join();
    }

    @Test
    public void shouldSupportTransactionIsolationCommitCheck() throws Exception {
	// the purpose of this test is to simulate gremlin server access to a
	// graph instance, where one thread modifies
	// the graph and a separate thread cannot affect the transaction of the
	// first
	final CountDownLatch latchCommittedInOtherThread = new CountDownLatch(1);
	final CountDownLatch latchCommitInOtherThread = new CountDownLatch(1);

	final AtomicBoolean noVerticesInFirstThread = new AtomicBoolean(false);

	// this thread starts a transaction then waits while the second thread
	// tries to commit it.
	final Thread threadTxStarter = new Thread() {
	    @Override
	    public void run() {
		graph.addVertex();
		latchCommitInOtherThread.countDown();

		try {
		    latchCommittedInOtherThread.await();
		} catch (InterruptedException ie) {
		    throw new RuntimeException(ie);
		}

		try {
		    graph.rollback();
		} catch (IOException e) {
		    throw new RuntimeException(e);
		}

		// there should be no vertices here
		noVerticesInFirstThread.set(!graph.getVertices().iterator().hasNext());
	    }
	};

	threadTxStarter.start();

	// this thread tries to commit the transaction started in the first
	// thread above.
	final Thread threadTryCommitTx = new Thread() {
	    @Override
	    public void run() {
		try {
		    latchCommitInOtherThread.await();
		} catch (InterruptedException ie) {
		    throw new RuntimeException(ie);
		}

		// try to commit the other transaction
		try {
		    graph.commit();
		} catch (IOException e) {
		    throw new RuntimeException(e);
		}

		latchCommittedInOtherThread.countDown();
	    }
	};

	threadTryCommitTx.start();

	threadTxStarter.join();
	threadTryCommitTx.join();

	assertTrue(noVerticesInFirstThread.get());
	assertVertexEdgeCounts(0, 0);
    }

    @Test
    public void shouldCountVerticesEdgesOnPreTransactionCommit() throws IOException {
	// see a more complex version of this test at
	// GraphTest.shouldProperlyCountVerticesAndEdgesOnAddRemove()
	DuctileDBVertex v1 = graph.addVertex();
	graph.commit();

	assertVertexEdgeCounts(1, 0);

	final DuctileDBVertex v2 = graph.addVertex();
	v1 = graph.getVertex(v1.getId());
	v1.addEdge("friend", v2);

	assertVertexEdgeCounts(2, 1);

	graph.commit();

	assertVertexEdgeCounts(2, 1);
    }

    @Test
    public void shouldAllowReferenceOfVertexOutsideOfOriginalTransactionalContextAuto() {
	final DuctileDBVertex v1 = graph.addVertex(new HashSet<>(), toMap("name", "stephen"));
	graph.getCurrentTransaction().commit();

	assertEquals("stephen", v1.getProperty("name"));

	graph.getCurrentTransaction().rollback();
	assertEquals("stephen", v1.getProperty("name"));

    }

    @Test
    public void shouldAllowReferenceOfEdgeOutsideOfOriginalTransactionalContextAuto() {
	final DuctileDBVertex v1 = graph.addVertex();
	final DuctileDBEdge e = v1.addEdge("self", v1, toMap("weight", 0.5d));
	graph.getCurrentTransaction().commit();

	assertEquals(0.5d, e.getProperty("weight"), 0.00001d);

	graph.getCurrentTransaction().rollback();
	assertEquals(0.5d, e.getProperty("weight"), 0.00001d);
    }

    @Test
    public void shouldAllowReferenceOfVertexIdOutsideOfOriginalThreadAuto() throws Exception {
	final DuctileDBVertex v1 = graph.addVertex(new HashSet<>(), toMap("name", "stephen"));

	final AtomicReference<Object> id = new AtomicReference<>();
	final Thread t = new Thread(() -> id.set(v1.getId()));
	t.start();
	t.join();

	assertEquals(v1.getId(), id.get());

	graph.getCurrentTransaction().rollback();
    }

    @Test
    public void shouldAllowReferenceOfEdgeIdOutsideOfOriginalThreadAuto() throws Exception {
	final DuctileDBVertex v1 = graph.addVertex();
	final DuctileDBEdge e = v1.addEdge("self", v1, toMap("weight", 0.5d));

	final AtomicReference<Object> id = new AtomicReference<>();
	final Thread t = new Thread(() -> id.set(e.getId()));
	t.start();
	t.join();

	assertEquals(e.getId(), id.get());

	graph.getCurrentTransaction().rollback();
    }

    @Test
    public void shouldNotShareTransactionCloseConsumersAcrossThreads() throws InterruptedException {
	final CountDownLatch latch = new CountDownLatch(1);

	final Thread manualThread = new Thread(() -> {
	    try {
		latch.await();
	    } catch (InterruptedException ie) {
		throw new RuntimeException(ie);
	    }
	});

	manualThread.start();

	final Thread autoThread = new Thread(() -> {
	    try {
		latch.countDown();
		graph.addVertex();
		graph.rollback();
	    } catch (Exception e) {
		throw new RuntimeException(e);
	    }
	});

	autoThread.start();

	manualThread.join();
	autoThread.join();

	assertEquals("Graph should be empty. autoThread transaction.onClose() should be ROLLBACK (default)", 0,
		DuctileDBTestHelper.count(graph.getVertices()));
    }

}
