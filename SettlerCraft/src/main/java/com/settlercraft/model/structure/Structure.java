/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.model.structure;

import com.avaje.ebean.validation.NotEmpty;
import com.avaje.ebean.validation.NotNull;
import com.google.common.base.Preconditions;
import com.settlercraft.main.StructurePlanRegister;
import com.settlercraft.util.LocationUtil.DIRECTION;
import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 *
 * @author Chingo
 */
@Entity
@Table(name = "sc_structure")

public class Structure implements Serializable  {

    @Id
    @GeneratedValue
    private int id;
    @NotNull
    private String owner;
    @NotNull
    @NotEmpty
    private String plan;
    @NotNull
    private DIRECTION direction;
    @NotNull
    private int x;
    @NotNull
    private int y;
    @NotNull
    private int z;
    @NotNull
    @NotEmpty
    private String world;
    @NotNull
    private int currentLayer;
    
    @OneToOne(fetch = FetchType.EAGER, mappedBy = "mainStructure", cascade = CascadeType.ALL)
    @Column(name = "structureChest")
    private StructureChest structureChest;
    
    @OneToOne(fetch = FetchType.EAGER, mappedBy = "mainStructure", cascade = CascadeType.ALL)
    @Column(name = "structureSign")
    private StructureSign structureSign;

    public Structure() {
    }

    public Structure(Player owner, Location target, DIRECTION direction, String plan) {
        Preconditions.checkNotNull(StructurePlanRegister.getPlan(plan));
        Preconditions.checkNotNull(target);
        this.owner = owner.getName();
        this.x = target.getBlockX();
        this.y = target.getBlockY();
        this.z = target.getBlockZ();
        this.world = target.getWorld().getName();
        this.plan = plan;
        this.direction = direction;
        this.currentLayer = 0;
    }

    public int getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public String getPlan() {
        return plan;
    }

    public final Location getLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z);
    }

    public DIRECTION getDirection() {
        return direction;
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

    public void setId(int id) {
        this.id = id;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setDirection(DIRECTION direction) {
        this.direction = direction;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public StructureChest getStructureChest() {
        return structureChest;
    }

    public StructureSign getStructureSign() {
        return structureSign;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public void setStructureChest(StructureChest structureChest) {
        this.structureChest = structureChest;
    }

    public void setStructureSign(StructureSign structureSign) {
        this.structureSign = structureSign;
    }

    public int getCurrentLayer() {
        return currentLayer;
    }

    public void setCurrentLayer(int currentLayer) {
        this.currentLayer = currentLayer;
    }

    @Override
    public String toString() {
        return "id:" + getId() + " owner:" + getOwner() + " plan:" + getPlan() + " x:" + getX() + " y:" + getY() + " z:" + getZ();
    }
    
    

    
    
}
