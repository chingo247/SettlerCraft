/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.model.entity;

import com.avaje.ebean.validation.NotNull;
import javax.persistence.Column;
import org.bukkit.Location;

/**
 *
 * @author Chingo
 */
public class WorldDimension {

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

    
    /**
     * JPA Constructor.
     */
    protected WorldDimension() {}

    public WorldDimension(Location start, Location end) {
        this.startX = start.getBlockX();
        this.startY = start.getBlockY();
        this.startZ = start.getBlockZ();
        this.endX = end.getBlockX();
        this.endY = end.getBlockY();
        this.endZ = end.getBlockZ();
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
