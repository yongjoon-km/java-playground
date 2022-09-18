package com.example.javaplayground;

import com.lmax.disruptor.EventHandler;

import java.lang.reflect.Array;
import java.util.List;

public class SingleEventPrintConsumer {

    public EventHandler<ValueEvent>[] getEventHandler() {
        EventHandler<ValueEvent> eventHandler = (event, sequence, endOfBatch) -> print(event.getValue(), sequence);
        return new EventHandler[]{eventHandler};
    }

    private void print(int id, long sequence) {
        System.out.println("Id is " + id + "sequence id that was used is " + sequence);
    }
}
