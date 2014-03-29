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
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

/**
 *
 * @author Chingo
 */
@Entity
public class StructureChest {
    
    @Id
    private long id;
    
    @NotNull
    private final int x;
    
    @NotNull
    private final int y;
    
    @NotNull
    private final int z;
    
    @NotEmpty
    private final String world;
    
    StructureChest(Location chestLocation) {
        Preconditions.checkArgument(chestLocation.getBlock().getType() == Material.CHEST);
        this.x = chestLocation.getBlockX();
        this.y = chestLocation.getBlockY();
        this.z = chestLocation.getBlockZ();
        this.world = chestLocation.getWorld().getName();
    }

    public long getId() {
        return id;
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

    public String getWorld() {
        return world;
    }
    
    public Location getLocation() {
        Location location = new Location(Bukkit.getServer().getWorld(world), x, y, z);
        return location;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof StructureChest)) return false;
        StructureChest sc = (StructureChest) obj;
        return sc.id == this.id;
    }
    
    
    
    
    
    
    
    
}
