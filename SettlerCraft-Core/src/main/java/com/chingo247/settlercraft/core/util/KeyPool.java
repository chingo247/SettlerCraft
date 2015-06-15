
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
package com.chingo247.settlercraft.core.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chingo
 * @param <T>
 */
public class KeyPool<T> {

    private final HashMap<T, Queue<KeyRunnable>> tasks = new HashMap<>();
    private final ExecutorService executor;
    private final Lock lock;

    public KeyPool(ExecutorService executor) {
        this.executor = executor;
        this.lock = new ReentrantLock();
    }

    public Future execute(T key, Runnable task) {
        lock.lock();
        try {
            // Create a new Queue if there is now existing one for this key
            if (tasks.get(key) == null) {
                tasks.put(key, new LinkedList<KeyRunnable>());
            }
            // Determines if the runnable should be submitted to the executor
            boolean execute;
            KeyRunnable keyRunnable = new KeyRunnable(key, task);

            // If this is this first runnable in the queue, then this runnable should be sumbitted to the executor
            // Otherwise it will wait until the Runnable that is one place next in the queue has finished
            execute = tasks.get(key).peek() == null;
            tasks.get(key).add(keyRunnable);

        
            // Return the Future for this Runnable
            if (execute) {
                return executor.submit(keyRunnable);
            } else {
                return keyRunnable.task;
            }
        } finally {
            lock.unlock();
        }
    }

    private class KeyRunnable implements Runnable {

        private final T key;
        private final FutureTask task;

        public KeyRunnable(T t, Runnable r) {
            this.key = t;
            this.task = new FutureTask(r, t);
        }

        @Override
        public void run() {
            try {
                // Execute the runnable
                task.run();
                task.get(); // Will throw the exception which should be catched
            } catch(Exception e) {
               Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            } finally {
                synchronized (tasks.get(key)) {
                    // Remove yourself from the Queue
                    tasks.get(key).poll();
                    
                    // Check if there is another one waiting at your queue
                    Runnable nextTask = tasks.get(key).peek();
                    if (nextTask != null) {
                        // Submit this task to the executor, so it's scheduled to be executed
                        executor.execute(nextTask);
                    } else {
                        // remove the whole queue if im the last one 
                        tasks.remove(key);
                    }
                }
            }

        }
    }

    public ExecutorService getExecutorService() {
        return executor;
    }

}
