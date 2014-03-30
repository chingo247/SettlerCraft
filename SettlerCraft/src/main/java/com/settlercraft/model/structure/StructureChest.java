/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.model.structure;

import com.avaje.ebean.validation.NotEmpty;
import com.avaje.ebean.validation.NotNull;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

/**
 *
 * @author Chingo
 */
@Embeddable
public class StructureChest implements Serializable {
    
    @NotNull
    @Column(name = "stchest_x")
    private int x;

    @NotNull
    @Column(name = "stchest_y")
    private int y;

    @NotNull
    @Column(name = "stchest_z")
    private int z;

    @NotEmpty
    @NotNull
    @Column(name = "stchest_world")
    private String world;

    public StructureChest() {
    }

    StructureChest(Location chestLocation, Structure structure) {
        Preconditions.checkArgument(chestLocation.getBlock().getType() == Material.CHEST);
        this.x = chestLocation.getBlockX();
        this.y = chestLocation.getBlockY();
        this.z = chestLocation.getBlockZ();
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

    public String getWorld() {
        return world;
    }

    public Location getLocation() {
        Location location = new Location(Bukkit.getServer().getWorld(world), x, y, z);
        return location;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public void setWorld(String world) {
        this.world = world;
    }

}
