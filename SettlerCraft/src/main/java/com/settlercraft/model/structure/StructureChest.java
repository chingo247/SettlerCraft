/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.model.structure;

import com.avaje.ebean.validation.NotEmpty;
import com.avaje.ebean.validation.NotNull;
import com.google.common.base.Preconditions;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

/**
 *
 * @author Chingo
 */
@Embeddable
public class StructureChest {
    
    @NotNull
    private final int x;
    
    @NotNull
    private final int y;
    
    @NotNull
    private final int z;
    
    @NotNull
    private final float pitch;
    
    @NotNull
    private final float yaw;
    
    @NotEmpty
    private final String world;
    
    public StructureChest(Location chestLocation) {
        Preconditions.checkArgument(chestLocation.getBlock().getType() == Material.CHEST);
        this.x = chestLocation.getBlockX();
        this.y = chestLocation.getBlockY();
        this.z = chestLocation.getBlockZ();
        this.pitch = chestLocation.getPitch();
        this.yaw = chestLocation.getYaw();
        this.world = chestLocation.getWorld().getName();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public String getWorld() {
        return world;
    }
    
    public Location getLocation() {
        Location location = new Location(Bukkit.getServer().getWorld(world), x, y, z);
        location.setPitch(pitch);
        location.setYaw(yaw);
        return location;
    }
    
    
    
    
    
    
    
}
