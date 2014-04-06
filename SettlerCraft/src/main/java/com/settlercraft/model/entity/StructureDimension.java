/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.model.entity;

import com.avaje.ebean.validation.NotNull;
import com.settlercraft.model.entity.structure.Structure;
import com.settlercraft.util.location.LocationUtil;
import com.settlercraft.util.schematic.SchematicObject;
import com.settlercraft.util.yaml.StructureConfig.RESERVED_SIDE;
import java.io.Serializable;
import java.util.EnumMap;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import org.bukkit.Location;

/**
 * Dimension is the total space a building requires the start location may not be the actual start
 * of the building the Start location and end location include the reserved spots of the building.
 *
 * @author Chingo
 */
@Embeddable
public class StructureDimension implements Serializable {

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

    public StructureDimension() {}

    public StructureDimension(Structure structure) {
        Location start = getStart(structure.getStructureLocation(), structure);
        this.startX = start.getBlockX();
        this.startY = start.getBlockY();
        this.startZ = start.getBlockZ();

        Location end = getEnd(structure.getStructureLocation(), structure);
        this.endX = end.getBlockX();
        this.endY = end.getBlockY();
        this.endZ = end.getBlockZ();
    }

    private Location getStart(Location location, Structure structure) {
        EnumMap<RESERVED_SIDE, Integer> reserved = structure.getPlan().getConfig().getReserved();
        LocationUtil.DIRECTION direction = structure.getDirection();
        Location target = new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());

        // Calculate Building Start + reserved Spots
        int[] mods = LocationUtil.getModifiers(direction);
        int xMod = mods[0];
        int zMod = mods[1];
        Location loc;
        if (direction == LocationUtil.DIRECTION.NORTH || direction == LocationUtil.DIRECTION.SOUTH) {
            loc = target.clone().add(-reserved.get(RESERVED_SIDE.WEST) * xMod, 0, -reserved.get(RESERVED_SIDE.SOUTH) * zMod);
        } else {
            loc = target.clone().add(-reserved.get(RESERVED_SIDE.SOUTH) * zMod, 0, -reserved.get(RESERVED_SIDE.WEST) * xMod);
        }
        return loc;
    }

    private Location getEnd(Location location, Structure structure) {
        EnumMap<RESERVED_SIDE, Integer> reserved = structure.getPlan().getConfig().getReserved();
        LocationUtil.DIRECTION direction = structure.getDirection();
        SchematicObject schem = structure.getPlan().getSchematic();

        // Calculate Building end
        Location target = new Location(location.getWorld(),
                location.getBlockX(), // x Structure End
                location.getBlockY(),
                location.getBlockZ());   // z Structure End

        // Calculate Building end + Reserved Spots
        int[] mods = LocationUtil.getModifiers(direction);
        int xMod = mods[0];
        int zMod = mods[1];
        Location loc;
        if (direction == LocationUtil.DIRECTION.NORTH || direction == LocationUtil.DIRECTION.SOUTH) {
            loc = target.clone().add((schem.width-1) * xMod, (schem.height -1) , (schem.length-1) * zMod);
        } else {
            loc = target.clone().add((schem.length-1) * zMod, (schem.height -1) , (schem.width-1) * xMod);
        }
        System.out.println("\ngetEnd():" + loc + "\n");

        return loc;
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
