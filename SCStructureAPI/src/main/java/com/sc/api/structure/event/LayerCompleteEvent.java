/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.api.structure.event;

import com.settlercraft.core.event.SettlerCraftEvent;
import com.settlercraft.core.model.entity.structure.Structure;

/**
 *
 * @author Chingo
 */
public class LayerCompleteEvent extends SettlerCraftEvent {
    
    private final Structure structure;
    private final int layer;

    public LayerCompleteEvent(Structure structure, int layer) {
        this.structure = structure;
        this.layer = layer;
    }

    public int getLayer() {
        return layer;
    }

    public Structure getStructure() {
        return structure;
    }
    
    
    
}
