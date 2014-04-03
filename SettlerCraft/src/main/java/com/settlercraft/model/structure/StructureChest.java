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
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

/**
 *
 * @author Chingo
 */
@Entity
public class StructureChest implements Serializable {
    
    @Id
    @GeneratedValue
    private Long id;
    
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
    
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "structure")
    private Structure mainStructure;

    public StructureChest() {
        
    }

    StructureChest(Location chestLocation, Structure structure) {
        Preconditions.checkArgument(chestLocation.getBlock().getType() == Material.CHEST);
        this.x = chestLocation.getBlockX();
        this.y = chestLocation.getBlockY();
        this.z = chestLocation.getBlockZ();
        this.world = chestLocation.getWorld().getName();
        this.mainStructure = structure;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Structure getStructure() {
        return mainStructure;
    }

    public void setStructure(Structure structure) {
        this.mainStructure = structure;
    }

    
    
}
