package org.itmo;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ConcurrentQueueBalancer {
    private AtomicInteger nextQueue;
    private final List<Queue<Integer>> queues;

    ConcurrentQueueBalancer(int size) {
        nextQueue = new AtomicInteger(0);
        queues = Stream.<Queue<Integer>>generate(ConcurrentLinkedQueue<Integer>::new).limit(size)
                .collect(Collectors.toList());
    }

    void add(Integer value) {
        Integer nextQueueIdx = nextQueue.getAndUpdate((val) -> (val + 1) % queues.size());
        queues.get(nextQueueIdx).add(value);
    }

    List<Queue<Integer>> getQueues() {
        return queues;
    }
}
