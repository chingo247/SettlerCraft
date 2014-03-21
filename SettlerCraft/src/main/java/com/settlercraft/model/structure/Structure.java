/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.model.structure;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 *
 * @author Chingo
 */
public class Structure {
    private Player owner;
    private Location location;
    private final StructurePlan plan;

    public Structure(Player owner, Location location, StructurePlan plan) {
        this.owner = owner;
        this.location = location;
        this.plan = plan;
    }
    
    
    
}
