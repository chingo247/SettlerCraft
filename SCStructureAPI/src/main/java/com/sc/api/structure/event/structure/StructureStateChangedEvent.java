/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure.event.structure;

import com.sc.api.structure.model.structure.Structure;
import com.sc.api.structure.model.structure.StructureState;
import org.bukkit.event.HandlerList;

/**
 * Automatically fired when the structure.setStatus() method is called
 *
 * @author Chingo
 */
public class StructureStateChangedEvent extends StructureEvent {

    private final StructureState oldState;

    public StructureStateChangedEvent(Structure structure, StructureState oldState) {
        super(structure);
        this.oldState = oldState;
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
