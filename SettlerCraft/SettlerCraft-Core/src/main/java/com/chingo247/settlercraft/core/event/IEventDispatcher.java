/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.core.event;

import com.google.common.eventbus.EventBus;

/**
 * EventDispatcher manages eventbusses. Events posted with {@link #dispatchEvent(java.lang.Object) } 
 * will be sent to all registered eventbusses
 * @author Chingo
 */
public interface IEventDispatcher {
    
    /**
     * Posts an event to all registered eventbusses
     * @param event The event
     */
    void dispatchEvent(Object event);
    
    /**
     * Registers an eventbus
     * @param eventBus The eventbus to register
     */
    void register(EventBus eventBus);
}
