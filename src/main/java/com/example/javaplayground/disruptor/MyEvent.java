package com.example.javaplayground.disruptor;

public class MyEvent {
    private long value;

    public void set(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }
}
