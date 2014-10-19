/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.townapi.entities.field;

import java.io.Serializable;
import java.util.UUID;
import org.bukkit.World;

/**
 *
 * @author Chingo
 */
public class Field implements Serializable {
    
    protected final int minX;
    protected final int minZ;
    protected final int maxX;
    protected final int maxZ;
    protected final int height;
    protected final String world;
    protected final UUID worldUUID;

    public Field(World world, int minX, int minZ, int maxX, int maxZ, int height) {
        this.minX = minX;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxZ = maxZ;
        this.height = height;
        this.world = world.getName();
        this.worldUUID = world.getUID();
    }

    public Integer getMinX() {
        return minX;
    }

    public Integer getMinZ() {
        return minZ;
    }

    public Integer getMaxX() {
        return maxX;
    }

    public Integer getMaxZ() {
        return maxZ;
    }

    public Integer getHeight() {
        return height;
    }

    public String getWorld() {
        return world;
    }

    public UUID getWorldUUID() {
        return worldUUID;
    }
    
    

    
    
    
    
}
