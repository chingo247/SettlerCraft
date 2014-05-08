/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure.model.structure.plan;

import java.io.File;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 * @author Chingo
 */
@Entity
public class StructurePlan implements Serializable {
    @Id
    private final String id;
    private String category = "default";
    private String faction = "default";
    private String displayName; 
    private String description;
    private int startY = 0;
    private final String structureSchematic;
    private double price = 0.0;
    private int reservedNorth = 0;
    private int reservedEast = 0;
    private int reservedSouth = 0;
    private int reservedWest = 0;
    private int reservedUp = 0;
    private int reservedDown = 0;

    /**
     * JPA Constructor
     */
    protected StructurePlan() {
        this.structureSchematic = null;
        this.id = null;
    }

    public StructurePlan(String id, File schematic) {
        this.id = id;
        this.displayName = id;
        this.structureSchematic = schematic.getAbsolutePath();
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public File getSchematic() {
        return new File(structureSchematic);
    }


    public double getPrice() {
        return price;
    }

    public void setPrice(double cost) {
        this.price = cost;
    }

    public int getReservedNorth() {
        return reservedNorth;
    }

    public void setReservedNorth(int reservedNorth) {
        this.reservedNorth = reservedNorth;
    }

    public int getReservedEast() {
        return reservedEast;
    }

    public void setReservedEast(int reservedEast) {
        this.reservedEast = reservedEast;
    }

    public int getReservedSouth() {
        return reservedSouth;
    }

    public void setReservedSouth(int reservedSouth) {
        this.reservedSouth = reservedSouth;
    }

    public int getReservedWest() {
        return reservedWest;
    }

    public void setReservedWest(int reservedWest) {
        this.reservedWest = reservedWest;
    }

    public int getReservedUp() {
        return reservedUp;
    }

    public void setReservedUp(int reservedUp) {
        this.reservedUp = reservedUp;
    }

    public int getReservedDown() {
        return reservedDown;
    }

    public void setReservedDown(int reservedDown) {
        this.reservedDown = reservedDown;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setFaction(String faction) {
        this.faction = faction;
    }

    public String getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getFaction() {
        return faction;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public int getStartY() {
        return startY;
    }

    public File getStructureSchematic() {
        return new File(structureSchematic);
    }
    
}
