/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.api.structure.event.structure;

import com.sc.api.structure.model.structure.Structure;
import org.bukkit.event.HandlerList;

/**
 *
 * @author Chingo
 */
public class StructureLayerCompleteEvent extends StructureEvent {
    
    private final int layer;

    public StructureLayerCompleteEvent(Structure structure, int layer) {
        super(structure);
        this.layer = layer;
    }

    public int getLayer() {
        return layer;
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
