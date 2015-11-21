/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.core.concurrent;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 *
 * @author ching
 */
public class SingleProcessingQueue {
    
    private KeyPool<String> keyPool;

    public SingleProcessingQueue(ExecutorService executorService) {
        this.keyPool = new KeyPool<>(executorService);
    }
    
    public Future submit(Runnable runnable) {
        return keyPool.execute("", runnable);
    }
    
}
