/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.model.structure;

import com.settlercraft.util.schematic.model.SchematicObject;
import java.util.EnumMap;

/**
 *
 * @author Chingo
 */
public class Structure {
    private boolean isFinished = false;
    
    public enum RESERVED_SIDES {
        NORTH,
        EAST,
        SOUTH,
        WEST
    }
    
    private final EnumMap<RESERVED_SIDES,Boolean> reserved;
    private final String name;
    private final SchematicObject structure;
    private final int layersBeneathGround;
    
    public Structure(String name, SchematicObject structure, int layersBeneathGround) {
        this.structure = structure;
        this.name = name;
        this.reserved = new EnumMap<>(RESERVED_SIDES.class);
        this.layersBeneathGround = layersBeneathGround;
    }
    
    public void setReserved(RESERVED_SIDES side, boolean reserved) {
        this.reserved.put(side, reserved);
    }

    public String getName() {
        return name;
    }

    public EnumMap<RESERVED_SIDES, Boolean> getReserved() {
        return reserved;
    }

    public SchematicObject getSchematic() {
        return structure;
    }

    public int getLayersBeneathGround() {
        return layersBeneathGround;
    }
    
    
   
}
