package com.example.javaplayground.disruptor;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;

public class Consumer extends Thread {
    private int sum = 0;
    private final Sequence sequence;
    private final RingBuffer<MyEvent> ringBuffer;
    private final SequenceBarrier sequenceBarrier;

    private final int consumeCount;

    public Consumer(Disruptor<MyEvent> disruptor, int consumeCount) {
        this.ringBuffer = disruptor.getRingBuffer();
        this.sequenceBarrier = this.ringBuffer.newBarrier();
        this.sequence = new Sequence();
        this.consumeCount = consumeCount;
        this.ringBuffer.addGatingSequences(this.sequence);
    }

    @Override
    public void run() {
        while (sequence.get() < consumeCount) {
            try {
                this.sequenceBarrier.waitFor(sequence.get());
                MyEvent e = this.ringBuffer.get(sequence.get());
                sum += e.getValue();
                if (sequence.get() > -1 && e.getValue() == 0) {
                    System.out.println("[ERROR]" + sequence.get());
                    System.exit(-1);
                }
//                System.out.println(e.getValue());
                sequence.addAndGet(1);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public int getSum() {
        return sum;
    }
}
