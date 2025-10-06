package org.itmo;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class QueueBalancer<T> {
    private Integer nextQueue;
    private final List<Queue<T>> queues;

    QueueBalancer(Integer size) {
        nextQueue = 0;
        queues = Stream.<Queue<T>>generate(ArrayDeque<T>::new).limit(size).collect(Collectors.toList());
    }

    void add(T value) {
        nextQueue = (nextQueue + 1) % queues.size();
        queues.get(nextQueue).add(value);
    }

    List<Queue<T>> getQueues() {
        return queues;
    }
}
