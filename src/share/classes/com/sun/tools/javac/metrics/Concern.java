package com.sun.tools.javac.metrics;

public enum Concern {
    ATOMIC_TYPES("Atomic data types"),
    THREAD_EXEC("Management and execution of threads"),
    MUTEX("Mutual exclusion"),
    BLOCKING_QUEUE("Blocking queue"),
    CONDITION_SIGN("Conditional signalling"),
    DATA_STRUCTURE("Thread-safe data structure");
    public String name;
    Concern(String name) {
        this.name = name;
    }
}
