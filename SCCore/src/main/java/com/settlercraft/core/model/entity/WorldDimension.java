/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.core.model.entity;

import com.avaje.ebean.validation.NotNull;
import com.settlercraft.core.model.entity.structure.Structure;
import com.settlercraft.core.model.plan.StructurePlan;
import com.settlercraft.core.model.plan.schematic.SchematicObject;
import com.settlercraft.core.model.plan.yaml.StructureConfig;
import com.settlercraft.core.util.LocationUtil;
import com.settlercraft.core.util.LocationUtil.DIRECTION;
import java.util.EnumMap;
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

    /**
     * Constructor when using a structure
     * @param structure The structure
     */
    public WorldDimension(Structure structure) {
        DIRECTION direction = structure.getDirection();
        Location start = structure.getStructureStartLocation();
        StructurePlan plan = structure.getPlan();
        
        Location s = getStart(start, plan, direction);
        Location e = getEnd(start, plan, direction);
        this.startX = s.getBlockX();
        this.startY = s.getBlockY();
        this.startZ = s.getBlockZ();
        this.endX = e.getBlockX();
        this.endY = e.getBlockY();
        this.endZ = e.getBlockZ();
        
    }
    
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

    private Location getStart(Location start, StructurePlan plan, LocationUtil.DIRECTION direction) {
        EnumMap<StructureConfig.RESERVED_SIDE, Integer> reserved = plan.getConfig().getReserved();
        Location target = new Location(start.getWorld(), start.getBlockX(), start.getBlockY(), start.getBlockZ());

        // Calculate Building Start + reserved Spots
        int[] mods = LocationUtil.getModifiers(direction);
        int xMd = mods[0];
        int zMd = mods[1];
        Location loc;
        if (direction == LocationUtil.DIRECTION.NORTH || direction == LocationUtil.DIRECTION.SOUTH) {
            loc = target.clone().add(-reserved.get(StructureConfig.RESERVED_SIDE.WEST) * xMd, 0, -reserved.get(StructureConfig.RESERVED_SIDE.SOUTH) * zMd);
        } else {
            loc = target.clone().add(-reserved.get(StructureConfig.RESERVED_SIDE.SOUTH) * zMd, 0, -reserved.get(StructureConfig.RESERVED_SIDE.WEST) * xMd);
        }
        return loc;
    }

    private Location getEnd(Location start, StructurePlan plan, LocationUtil.DIRECTION direction) {
        EnumMap<StructureConfig.RESERVED_SIDE, Integer> reserved = plan.getConfig().getReserved();
        SchematicObject schem = plan.getSchematic();

        // Calculate Building end
        Location target = new Location(start.getWorld(),
                start.getBlockX(), // x Structure End
                start.getBlockY(),
                start.getBlockZ());   // z Structure End

        // Calculate Building end + Reserved Spots
        int[] mods = LocationUtil.getModifiers(direction);
        int xMd = mods[0];
        int zMd = mods[1];
        Location loc;
        if (direction == LocationUtil.DIRECTION.NORTH || direction == LocationUtil.DIRECTION.SOUTH) {
            loc = target.clone().add((schem.width - 1) * xMd, (schem.layers - 1), (schem.length - 1) * zMd);
        } else {
            loc = target.clone().add((schem.length - 1) * zMd, (schem.layers - 1), (schem.width - 1) * xMd);
        }
        return loc;
    }

    
}
