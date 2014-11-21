/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structure.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

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

    public ExecutorService getExecutorService() {
        return executor;
    }

}
