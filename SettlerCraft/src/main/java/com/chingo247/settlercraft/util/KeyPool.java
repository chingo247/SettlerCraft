/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Chingo
 * @param <T>
 */
public class KeyPool<T> {

    private final HashMap<T, Queue<KeyRunnable>> tasks = new HashMap<>();
    private final ExecutorService executor;

    public KeyPool(ExecutorService executor) {
        this.executor = executor;
    }

    public Future execute(T t, Runnable runnable) {
        synchronized (tasks) {
            if (tasks.get(t) == null) {
                tasks.put(t, new LinkedList<KeyRunnable>());
            }
        }
        boolean execute;
        KeyRunnable keyRunnable = new KeyRunnable(t, runnable);
        synchronized (tasks.get(t)) {
            execute = tasks.get(t).peek() == null;
            tasks.get(t).add(keyRunnable);
        }

        if (execute) {
            return executor.submit(keyRunnable);
        } else {
            return keyRunnable.f;
        }
    }

    private class KeyRunnable implements Runnable {

        private final T t;
        private final Runnable r;
        private final Future f;

        public KeyRunnable(T t, Runnable r) {
            this.t = t;
            this.r = r;
            this.f = new FutureTask(r, new Object());
        }

        @Override
        public void run() {
            try {
                r.run();
            } finally {
                tasks.get(t).poll();
                synchronized (tasks.get(t)) {
                    Runnable nextTask = tasks.get(t).peek();
                    if (nextTask != null) {
                        executor.execute(nextTask);
                    }
                }
            }

        }
    }

    public void shutdown() {
        if (!executor.isShutdown()) {
            executor.shutdown();
        }
    }

    public boolean isShutdown() {
        return executor.isShutdown();
    }

    public boolean isTerminated() {
        return executor.isTerminated();
    }

    public void awaitTermination(long timeout, TimeUnit timeUnit) throws InterruptedException {
        executor.awaitTermination(timeout, timeUnit);
    }

//    public static void main(String... args) {
//        KeyPool<String> keyPool = new KeyPool<>(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
//
//        for (int j = 0; j < 3; j++) {
//            final int tasker = j;
//            for (int i = 0; i < 5; i++) {
//                final int count = i;
//                keyPool.execute("TASKER # "+ tasker +" ", new Runnable() {
//
//                    @Override
//                    public void run() {
//                        try {
//                            long random = Math.round(5000 * Math.random());
//                            Thread.sleep(random);
//                            System.out.println("TASKER # "+ tasker +" " + count + " done in " + random);
//                        } catch (InterruptedException ex) {
//                            Logger.getLogger(KeyPool.class.getName()).log(Level.SEVERE, null, ex);
//                        }
//                    }
//                });
//
//            }
//        }
//
//    }
}
