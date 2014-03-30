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
public class StructureSign implements Serializable {
    
    @NotNull
    @Column(name = "stsign_x")
    private int x;

    @NotNull
    @Column(name = "stsign_y")
    private int y;

    @NotNull
    @Column(name = "stsign_z")
    private int z;

    @NotEmpty
    @NotNull
    @Column(name = "stsign_world")
    private String world;

    public StructureSign() {
    }

    public StructureSign(Location signLocation, Structure structure) {
        Preconditions.checkArgument(signLocation.getBlock().getType() == Material.SIGN_POST);
        this.x = signLocation.getBlockX();
        this.y = signLocation.getBlockY();
        this.z = signLocation.getBlockZ();
        this.world = signLocation.getWorld().getName();
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

    public Location getLocation() {
        Location location = new Location(Bukkit.getServer().getWorld(world), x, y, z);
        return location;
    }

}
