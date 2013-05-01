package com.sun.tools.javac.metrics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;

public class ConcurrentElements {

    private static final ArgType ARGTYPE_LONG = new ArgType(
            Arrays.asList("long", "java.lang.Long", "int", "java.lang.Integer"));
    private static final ArgType ARGTYPE_INT = new ArgType(
            Arrays.asList("int", "java.lang.Integer"));

    public static class ArgType {
        public List<String> acceptedTypes;
        public ArgType(List<String> acceptedTypes) {
            this.acceptedTypes = acceptedTypes;
        }
        public ArgType(String acceptedType) {
            this.acceptedTypes = Arrays.asList(acceptedType);
        }
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ArgType) {
                ArgType argType = (ArgType) obj;
                return this.acceptedTypes.containsAll(argType.acceptedTypes) ||
                        argType.acceptedTypes.containsAll(this.acceptedTypes);
            } else {
                return false;
            }
        }
        @Override
        public String toString() {
            return acceptedTypes.toString();
        }
    }

    public static class Method {
        public String name;
        public List<ArgType> typeArgs;
        public Method(String name, List<ArgType> typeArgs) {
            this.name = name;
            this.typeArgs = typeArgs;
        }
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Method) {
                Method meth = (Method) obj;
                return this.name.equals(meth.name) &&
                        this.typeArgs.equals(meth.typeArgs);
            } else {
                return false;
            }
        }
        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }

    public static HashMap<String, Concern> classes = new HashMap<String, Concern>() {{
        put("java.util.concurrent.atomic.AtomicBoolean", Concern.ATOMIC_TYPES);
        put("java.util.concurrent.atomic.AtomicInteger", Concern.ATOMIC_TYPES);
        put("java.util.concurrent.atomic.AtomicIntegerArray", Concern.ATOMIC_TYPES);
        put("java.util.concurrent.atomic.AtomicIntegerFieldUpdater", Concern.ATOMIC_TYPES);
        put("java.util.concurrent.atomic.AtomicLong", Concern.ATOMIC_TYPES);
        put("java.util.concurrent.atomic.AtomicLongArray", Concern.ATOMIC_TYPES);
        put("java.util.concurrent.atomic.AtomicLongFieldUpdater", Concern.ATOMIC_TYPES);
        put("java.util.concurrent.atomic.AtomicMarkableReference", Concern.ATOMIC_TYPES);
        put("java.util.concurrent.atomic.AtomicReference", Concern.ATOMIC_TYPES);
        put("java.util.concurrent.atomic.AtomicReferenceArray", Concern.ATOMIC_TYPES);
        put("java.util.concurrent.atomic.AtomicReferenceFieldUpdater", Concern.ATOMIC_TYPES);
        put("java.util.concurrent.atomic.AtomicStampedReferenc", Concern.ATOMIC_TYPES);
        put("java.util.concurrent.ArrayBlockingQueue", Concern.BLOCKING_QUEUE);
        put("java.util.concurrent.DelayQueue", Concern.BLOCKING_QUEUE);
        put("java.util.concurrent.LinkedBlockingQueue", Concern.BLOCKING_QUEUE);
        put("java.util.concurrent.LinkedBlockingDeque", Concern.BLOCKING_QUEUE);
        put("java.util.concurrent.PriorityBlockingQueue", Concern.BLOCKING_QUEUE);
        put("java.util.concurrent.SynchronousQueue", Concern.BLOCKING_QUEUE);
        put("java.util.concurrent.CyclicBarrier", Concern.CONDITION_SIGN);
        put("java.util.concurrent.CountDownLatch", Concern.CONDITION_SIGN);
        put("java.util.concurrent.Exchanger", Concern.CONDITION_SIGN);
        put("java.util.concurrent.ConcurrentHashMap", Concern.DATA_STRUCTURE);
        put("java.util.concurrent.ConcurrentSkipListMap", Concern.DATA_STRUCTURE);
        put("java.util.concurrent.locks.ReentrantLock", Concern.MUTEX);
        put("java.util.concurrent.Semaphore", Concern.MUTEX);
        put("java.util.concurrent.locks.ReentrantReadWriteLock", Concern.MUTEX);
        put("java.lang.Thread", Concern.THREAD_EXEC);
        put("java.util.concurrent.Executors", Concern.THREAD_EXEC);
        put("java.util.concurrent.ThreadPoolExecutor", Concern.THREAD_EXEC);
    }};

    public static HashMap<String, Concern> interfaces = new HashMap<String, Concern>() {{
        put("java.util.concurrent.BlockingDeque", Concern.BLOCKING_QUEUE);
        put("java.util.concurrent.BlockingQueue", Concern.BLOCKING_QUEUE);
        put("java.util.concurrent.locks.Condition", Concern.CONDITION_SIGN);
        put("java.util.concurrent.ConcurrentMap", Concern.DATA_STRUCTURE);
        put("java.util.concurrent.ConcurrentNavigableMap", Concern.DATA_STRUCTURE);
        put("java.lang.Runnable", Concern.THREAD_EXEC);
        put("java.util.concurrent.ExecutorService", Concern.THREAD_EXEC);
        put("java.util.concurrent.ScheduledExecutorService", Concern.THREAD_EXEC);
        put("java.util.concurrent.Executor", Concern.THREAD_EXEC);
        put("java.util.concurrent.locks.Lock", Concern.MUTEX);
        put("java.util.concurrent.locks.ReadWriteLock", Concern.MUTEX);
    }};

    public static HashMap<Method, Concern> methods = new HashMap<Method, Concern>() {{
        put(new Method("wait", new ArrayList<ArgType>()), Concern.CONDITION_SIGN);
        put(new Method("wait", Arrays.asList(ARGTYPE_LONG)), Concern.CONDITION_SIGN);
        put(new Method("wait", Arrays.asList(ARGTYPE_LONG, ARGTYPE_INT)), Concern.CONDITION_SIGN);
        put(new Method("notify", new ArrayList<ArgType>()), Concern.CONDITION_SIGN);
        put(new Method("notifyAll", new ArrayList<ArgType>()), Concern.CONDITION_SIGN);
    }};

}
