/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.model.entity;

import com.avaje.ebean.validation.NotEmpty;
import com.avaje.ebean.validation.NotNull;
import com.settlercraft.model.entity.structure.Structure;
import com.settlercraft.util.schematic.model.SchematicObject;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * Dimension is the total space a building requires the start location may not be the actual start of the building
 * the Start location and end location include the reserved spots of the building
 * 
 * To get the actual start use the @Link{WorldLocation.class} of the structure instead
 * @author Chingo
 */
@Embeddable
public class WorldDimension implements Serializable {

    @NotNull
    @Column(name = "startX")
    protected int startX;

    @NotNull
    @Column(name = "startY")
    protected int startY;

    @NotNull
    @Column(name = "startZ")
    protected int startZ;

    @NotNull
    @Column(name = "endX")
    protected int endX;

    @NotNull
    @Column(name = "endY")
    protected int endY;

    @NotNull
    @Column(name = "endZ")
    protected int endZ;

    @NotEmpty
    @NotNull
    @Column(name = "world")
    protected String world;

    public WorldDimension() {
    }
    
    public WorldDimension(Location target, Structure structure) {
        this.startX = target.getBlockX();
        this.startY = target.getBlockY();
        this.startZ = target.getBlockZ();
        SchematicObject schem = structure.getPlan().getSchematic();
        this.endX = startX + (structure.getxMod() * schem.width);
        this.endY = startY +  schem.height;
        this.endZ = startZ + (structure.getzMod() * schem.length);
        this.world = target.getWorld().getName();
    }



    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public Location getStartLocation() {
        Location location = new Location(Bukkit.getServer().getWorld(world), startX, startY, startZ);
        return location;
    }

    public Location getEndLocation() {
        Location location = new Location(Bukkit.getServer().getWorld(world), endX, endY, endZ);
        return location;
    }

    public int getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public int getStartY() {
        return startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public int getStartZ() {
        return startZ;
    }

    public void setStartZ(int startZ) {
        this.startZ = startZ;
    }

    public int getEndX() {
        return endX;
    }

    public void setEndX(int endX) {
        this.endX = endX;
    }

    public int getEndY() {
        return endY;
    }

    public void setEndY(int endY) {
        this.endY = endY;
    }

    public int getEndZ() {
        return endZ;
    }

    public void setEndZ(int endZ) {
        this.endZ = endZ;
    }
    
    

}
