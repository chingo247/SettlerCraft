/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.core.event;

import com.settlercraft.core.model.entity.structure.StructureState;
import org.bukkit.event.HandlerList;

/**
 * Automatically fired when the structure.setStatus() method is called
 * @author Chingo
 */
public class StructureStateChangedEvent extends SettlerCraftEvent {
    
   private final StructureState oldState;
   private final StructureState newState;

    public StructureStateChangedEvent(StructureState oldState, StructureState newState) {
        this.oldState = oldState;
        this.newState = newState;
    }

    public StructureState getNewState() {
        return newState;
    }

    public StructureState getOldState() {
        return oldState;
    }
    
        private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    
   
}
