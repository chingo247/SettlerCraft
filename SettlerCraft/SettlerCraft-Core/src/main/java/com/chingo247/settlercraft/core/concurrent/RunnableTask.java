/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.core.concurrent;

import com.google.common.util.concurrent.Monitor;

/**
 *
 * @author ching
 */
public abstract class RunnableTask implements Runnable {
    
    private Runnable runnable;

    public RunnableTask(Runnable runnable) {
        this.runnable = runnable;
    }
    
    @Override
    public void run() {
        runnable.run();
        onComplete();
    }
    
    public abstract void onComplete();
    
    
    
}
