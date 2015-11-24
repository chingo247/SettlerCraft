/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.core.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Chingo
 */
public class ThreadPoolFactory {

    /**
     * Creates a cached threadpool which will spawn thread when needed. Idle threads will be terminated after
     * a specific amount of time. As this is a cached threadpool, threads will be reused when they are idle.
     * 
     * @param coreSize The amount of core threads
     * @param maxSize The max size
     * @param timeOut The amount of time a thread may be idle before it gets terminated
     * @param unit The time unit
     * @return the threadpool instance
     */
    public ExecutorService newCachedThreadPool(int coreSize, int maxSize, long timeOut, TimeUnit unit) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                coreSize, // core size
                maxSize, // max size
                timeOut, // idle timeout
                unit,
                new LinkedBlockingQueue<Runnable>()); // queue with a size
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }
    
    public ExecutorService newCachedThreadPool(int coreSize, int maxSize) {
        return newCachedThreadPool(coreSize, maxSize, 30L, TimeUnit.SECONDS);
    }
    
    

}
