/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.townapi.entities.container;

import com.chingo247.settlercraft.structure.entities.world.Location;
import javax.persistence.Embeddable;

/**
 *
 * @author Chingo
 */
@Embeddable
public class Chest {
    
    private Location location;

    /**
     * JPA Constructor
     */
    protected Chest() {
    }

    public Chest(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }
    
}
