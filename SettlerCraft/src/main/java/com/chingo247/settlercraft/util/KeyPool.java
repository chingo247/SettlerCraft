
/*
 * The MIT License
 *
 * Copyright 2015 Chingo.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.chingo247.settlercraft.util;

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
