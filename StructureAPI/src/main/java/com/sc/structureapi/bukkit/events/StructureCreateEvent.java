/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.structureapi.bukkit.events;

import com.sc.structureapi.structure.entities.structure.Structure;

/**
 *
 * @author Chingo
 */
public class StructureCreateEvent extends StructureEvent {

    public StructureCreateEvent(Structure structure) {
        super(structure);
    }
    
    
}
