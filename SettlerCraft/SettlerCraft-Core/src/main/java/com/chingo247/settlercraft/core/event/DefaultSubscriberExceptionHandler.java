/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.core.event;

import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Chingo
 */
public class DefaultSubscriberExceptionHandler implements SubscriberExceptionHandler{

    @Override
    public void handleException(Throwable exception, SubscriberExceptionContext context) {
        Class c = context.getSubscriber().getClass();
        Logger log = Logger.getLogger(c.getName());
        log.log(Level.SEVERE, exception.getMessage(), exception);
    }
    
}
