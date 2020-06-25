package com.example.customviewstuff;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {
    private static volatile ThreadPool pool;
    private static final Object lock = new Object();
    private ExecutorService cache;

    private ThreadPool() {
        cache = Executors.newCachedThreadPool();
    }

    public static ThreadPool instance() {
        synchronized (lock) {
            if (pool == null) {
                pool = new ThreadPool();
            }
        }
        return pool;
    }

    public static ExecutorService cache() {
        return instance().cache;
    }
}
