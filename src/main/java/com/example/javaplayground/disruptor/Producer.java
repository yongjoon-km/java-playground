package com.example.javaplayground.disruptor;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

public class Producer extends Thread {
    private int count = 0;
    private final RingBuffer<MyEvent> ringBuffer;
    private final int produceCount;

    public Producer(Disruptor<MyEvent> disruptor, int produceCount) {
        this.ringBuffer = disruptor.getRingBuffer();
        this.produceCount = produceCount;
    }

    @Override
    public void run() {
        while (count < produceCount) {
            long sequence = this.ringBuffer.next();
            try {
                MyEvent event = this.ringBuffer.get(sequence);
                event.set(1);
                count++;
            } finally {
                this.ringBuffer.publish(sequence);
//                System.out.println("produced " + Thread.currentThread() + " " + sequence);
            }
        }
    }
}
