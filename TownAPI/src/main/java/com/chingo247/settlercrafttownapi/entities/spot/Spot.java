/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercrafttownapi.entities.spot;

import com.chingo247.settlercraft.structure.entities.world.Location;
import java.io.Serializable;
import javax.persistence.Embeddable;

/**
 *
 * @author Chingo
 */
@Embeddable
public class Spot implements Serializable {
    
    private Location location;
    
    private String taskName;
    
    /**
     * JPA Constructor
     */
    protected Spot() {
    }

    public Spot(String name, Location location) {
        this.location = location;
        this.taskName = name;
    }

    public Location getLocation() {
        return location;
    }

    public String getTaskName() {
        return taskName;
    }
    
    
    
}
