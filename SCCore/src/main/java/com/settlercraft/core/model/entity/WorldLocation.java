/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.core.model.entity;

import com.avaje.ebean.validation.NotEmpty;
import com.avaje.ebean.validation.NotNull;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 *
 * @author Chingo
 */
@Embeddable
public class WorldLocation implements Serializable {

    @NotNull
    @Column(name = "x")
    protected int x;

    @NotNull
    @Column(name = "y")
    protected int y;

    @NotNull
    @Column(name = "z")
    protected int z;

    @NotEmpty
    @NotNull
    @Column(name = "world")
    protected String world;

    public WorldLocation() {
    }

    public WorldLocation(Location location) {
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
        this.world = location.getWorld().getName();
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public Location getLocation() {
        Location location = new Location(Bukkit.getServer().getWorld(world), x, y, z);
        return location;
    }

}
