package com.example.javaplayground;

import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.util.concurrent.atomic.AtomicLong;

public class Disruptor2P1CDemo {

    private static long st;
    private static long et;
    private static long PRODUCER_MSG_NO = 10_000_000;
    private static AtomicLong sum = new AtomicLong(0);

    private static void handleEvent(LongEvent event, long sequence, boolean endOfBatch) {
        long l = event.value;
//        sum += l;
        sum.getAndAdd(l);
        System.out.println("Event Consumer 2: " + Thread.currentThread() + " " + sum.get());
    }

    public static class LongEvent {
        private long value;

        public void set(long value) {
            this.value = value;
        }
    }

    static class ProducerThread extends Thread {
        int count = 0;
        Disruptor<LongEvent> disruptor;

        public ProducerThread(Disruptor<LongEvent> disruptor) {
            this.disruptor = disruptor;
        }

        @Override
        public void run() {
            RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();
            while (count < PRODUCER_MSG_NO) {
                long sequence = ringBuffer.next();
                try {
                    LongEvent event = ringBuffer.get(sequence);
                    event.set(count);
                    count++;
                } finally {
                    ringBuffer.publish(sequence);
                }
            }
        }

    }

    public static void main(String[] args) throws InterruptedException {
        int bufferSize = (int) Math.pow(2, 20);
        Disruptor<LongEvent> disruptor = new Disruptor<>(LongEvent::new,
                bufferSize,
                DaemonThreadFactory.INSTANCE,
                ProducerType.MULTI,
                new BusySpinWaitStrategy());
        disruptor.handleEventsWith(Disruptor2P1CDemo::handleEvent)
                .then(((event, sequence, endOfBatch) -> System.out.println("Event Consumer 1: " + event.value)));
//        disruptor.handleEventsWith((event, sequence, endOfBatch) ->
//                System.out.println("Event Consumer 1: " + event.value));
        disruptor.start();


        Thread t1 = new ProducerThread(disruptor);
        Thread t2 = new ProducerThread(disruptor);

        PRODUCER_MSG_NO = 100_000;
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        PRODUCER_MSG_NO = 10_000;
        sum.set(0);
        Thread tt1 = new ProducerThread(disruptor);
        Thread tt2 = new ProducerThread(disruptor);

        st = System.currentTimeMillis();

        tt1.start();
        tt2.start();
        tt1.join();
        tt2.join();

        et = System.currentTimeMillis();
        System.out.printf("Sum %s, Process time: %s%n", sum, (et - st));

    }
}
