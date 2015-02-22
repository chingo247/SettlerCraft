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

import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author Chingo
 */
public class FireNextQueue {

    private final ExecutorService executor;
    private final Queue<Runnable> queue;

    public FireNextQueue(ExecutorService service) {
        this.executor = service;
        this.queue = new LinkedBlockingQueue<>();
    }

    public Future submit(Runnable runnable) {
        boolean execute;
        
        FireNextRunnable fnr = new FireNextRunnable(runnable);
        synchronized (queue) {
            execute = queue.isEmpty();
            queue.add(fnr);
        }
        
        if(execute) {
            return executor.submit(fnr);
        } else {
            return fnr.futureTask;
        }

    }

    private class FireNextRunnable implements Runnable {

        private FutureTask futureTask;

        public FireNextRunnable(Runnable r) {
            this.futureTask = new FutureTask(r, null);
        }

        @Override
        public void run() {
            try {
                futureTask.run();
            } finally {
                queue.poll(); // remove yourself
                synchronized (queue) {
                    Runnable r = queue.peek();
                    if (r != null) {
                        executor.execute(r);
                    }
                }
            }
        }

    }

}
