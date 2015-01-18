/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercrafttownapi.entities.container;

import com.chingo247.settlercraft.structure.entities.world.Location;
import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

/**
 *
 * @author Chingo
 */
@Embeddable
public class Chest implements Serializable {
    
    @Embedded
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
