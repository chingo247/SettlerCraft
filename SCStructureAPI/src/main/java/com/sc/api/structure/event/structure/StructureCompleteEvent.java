/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure.event.structure;

import com.settlercraft.core.event.SettlerCraftEvent;
import com.settlercraft.core.model.entity.structure.Structure;
import org.bukkit.event.HandlerList;

/**
 *
 * @author Chingo
 */
public class StructureCompleteEvent extends SettlerCraftEvent {

    private final Structure structure;

    public StructureCompleteEvent(Structure structure) {
        this.structure = structure;
    }

    public Structure getStructure() {
        return structure;
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
