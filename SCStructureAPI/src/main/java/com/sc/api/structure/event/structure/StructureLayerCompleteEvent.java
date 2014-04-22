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
public class StructureLayerCompleteEvent extends SettlerCraftEvent {
    
    private final Structure structure;
    private final int layer;

    public StructureLayerCompleteEvent(Structure structure, int layer) {
        this.structure = structure;
        this.layer = layer;
    }

    public int getLayer() {
        return layer;
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
