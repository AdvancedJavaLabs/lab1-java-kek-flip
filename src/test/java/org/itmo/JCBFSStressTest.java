package org.itmo;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import org.openjdk.jcstress.annotations.Actor;
import org.openjdk.jcstress.annotations.Arbiter;
import org.openjdk.jcstress.annotations.Expect;
import org.openjdk.jcstress.annotations.JCStressTest;
import org.openjdk.jcstress.annotations.Outcome;
import org.openjdk.jcstress.annotations.State;
import org.openjdk.jcstress.infra.results.L_Result;

@JCStressTest
@Outcome(id = "10", expect = Expect.ACCEPTABLE, desc = "Все вершины были обойдены")
@Outcome(expect = Expect.ACCEPTABLE_INTERESTING, desc = "Гонка данных: часть вершин не была обойдена")
@State
public class JCBFSStressTest {
    private static final int CORES_NUM = 12;
    private static final int VERTICES_NUM = 10;

    Graph graph = new Graph(VERTICES_NUM);
    AtomicBoolean[] visited = new AtomicBoolean[VERTICES_NUM];
    ConcurrentQueueBalancer queueBalancer = new ConcurrentQueueBalancer(CORES_NUM);
    ConcurrentQueueBalancer nextBalancer = new ConcurrentQueueBalancer(CORES_NUM);

    public JCBFSStressTest() {
        graph.addEdge(0, 1);
        graph.addEdge(0, 2);
        graph.addEdge(0, 3);
        graph.addEdge(0, 4);

        graph.addEdge(1, 5);
        graph.addEdge(3, 6);
        graph.addEdge(2, 7);
        graph.addEdge(4, 8);
        graph.addEdge(4, 9);

        for (int i = 0; i < VERTICES_NUM; ++i) {
            visited[i] = new AtomicBoolean(false);
        }

        for (int i = 0; i < 5; ++i) {
            visited[i].set(true);
        }
    }

    @Actor
    void actor1() {
        Queue<Integer> q = new ConcurrentLinkedQueue<>();
        q.add(1);
        q.add(3);
        step(q);
    }

    @Actor
    void actor2() {
        Queue<Integer> q = new ConcurrentLinkedQueue<>();
        q.add(2);
        q.add(4);
        step(q);
    }

    void step(Queue<Integer> vertecies) {
        List<Integer>[] adjList = graph.getAdjList();
        while (!vertecies.isEmpty()) {
            Integer vertex = vertecies.poll();

            for (int n : adjList[vertex]) {
                Boolean isVisited = visited[n].getAndSet(true);
                if (!isVisited) {
                    nextBalancer.add(n);
                }
            }
        }
    }

    @Arbiter
    public void arbiter(L_Result r) {
        r.r1 = Stream.of(visited).filter(AtomicBoolean::get).count();
    }
}
