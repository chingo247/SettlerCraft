/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure.model.structure.plan;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.EnumMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;


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
    @Lob
    private final File structureSchematic;
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
        this.id = null;
        this.structureSchematic = null;
    }

    public StructurePlan(String id, File schematic) throws IOException, DataException {
        this.id = id;
        this.displayName = id;
        this.structureSchematic = schematic;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public CuboidClipboard getSchematic()  {
        try {
            CuboidClipboard structure = SchematicFormat.getFormat(structureSchematic).load(structureSchematic);
            return structure;
        }
        catch (IOException | DataException ex) {
            Logger.getLogger(StructurePlan.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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
    
    public EnumMap<ReservedSide, Integer> getReserved() {
        EnumMap<ReservedSide, Integer> reserved = new EnumMap<>(ReservedSide.class);
        reserved.put(ReservedSide.UP, reservedUp);
        reserved.put(ReservedSide.DOWN, reservedDown);
        reserved.put(ReservedSide.EAST, reservedEast);
        reserved.put(ReservedSide.NORTH, reservedNorth);
        reserved.put(ReservedSide.SOUTH, reservedSouth);
        reserved.put(ReservedSide.WEST, reservedWest);
        return reserved;
    }

    public int getStartY() {
        return startY;
    }

}
