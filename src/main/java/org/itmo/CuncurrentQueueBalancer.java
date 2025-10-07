package org.itmo;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class CuncurrentQueueBalancer<T> {
    private AtomicInteger nextQueue;
    private final List<Queue<T>> queues;

    CuncurrentQueueBalancer(Integer size) {
        nextQueue = new AtomicInteger(0);
        queues = Stream.<Queue<T>>generate(ConcurrentLinkedQueue<T>::new).limit(size).collect(Collectors.toList());
    }

    void add(T value) {
        Integer nextQueueIdx = nextQueue.getAndUpdate((val) -> (val + 1) % queues.size());
        queues.get(nextQueueIdx).add(value);
    }

    List<Queue<T>> getQueues() {
        return queues;
    }
}
