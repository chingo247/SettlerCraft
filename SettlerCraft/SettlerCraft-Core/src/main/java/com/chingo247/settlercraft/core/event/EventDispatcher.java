/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.core.event;

import com.google.common.eventbus.EventBus;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link IEventDispatcher}
 * @author Chingo
 */
public class EventDispatcher {
    
    private List<EventBus> eventBusses;

    public EventDispatcher() {
        this.eventBusses = new ArrayList<>();
    }
    

    public void dispatchEvent(Object event) {
        for(EventBus e : eventBusses) {
            e.post(event);
        }
    }

    public void register(EventBus eventBus) {
        this.eventBusses.add(eventBus);
    }
    
}
