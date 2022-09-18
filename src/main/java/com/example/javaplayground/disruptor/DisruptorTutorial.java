package com.example.javaplayground.disruptor;

import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;

public class DisruptorTutorial {

    private static final int EVENT_COUNT = 10_000_000;
    private static final int RING_BUFFER_SIZE = 2048;

    public static void main(String[] args) throws InterruptedException {

        Disruptor<MyEvent> disruptor = new Disruptor<>(
                MyEvent::new,
                RING_BUFFER_SIZE,
                DaemonThreadFactory.INSTANCE
        );

        Thread p1 = new Producer(disruptor, EVENT_COUNT);
        Thread p2 = new Producer(disruptor, EVENT_COUNT);

        Consumer c1 = new Consumer(disruptor, EVENT_COUNT * 2);
        Consumer c2 = new Consumer(disruptor, EVENT_COUNT * 2);

        p1.start();
        p2.start();
        c1.start();
        c2.start();

        p1.join();
        p2.join();
        c1.join();
        c2.join();

        System.out.println("result of consumer 1: " + c1.getSum());
        System.out.println("result of consumer 2: " + c2.getSum());
    }
}
