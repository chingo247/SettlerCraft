/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.api.structure.event;

import com.settlercraft.core.event.SettlerCraftEvent;
import com.settlercraft.core.model.entity.structure.Structure;
import org.bukkit.event.HandlerList;

/**
 *
 * @author Chingo
 */
public class StructureStateChangedEvent extends SettlerCraftEvent {
    
    private final Structure structure;

    /**
     * Constructor.
     * @param structure The structure involved in this event
     */
    public StructureStateChangedEvent(final Structure structure) {
        this.structure = structure;
    }

    /**
     * Gets the structure involved in this event.
     * @return The structure involved in this event
     */
    public Structure getStructure() {
        return structure;
    }
    
    private static final HandlerList handlers = new HandlerList();
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
