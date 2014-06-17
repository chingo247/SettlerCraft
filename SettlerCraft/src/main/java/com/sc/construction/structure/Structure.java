/*
 * Copyright (C) 2014 Chingo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.sc.construction.structure;

import com.avaje.ebean.validation.NotNull;
import com.google.common.base.Preconditions;
import com.sc.construction.asyncworldEdit.ConstructionProcess;
import com.sc.construction.plan.StructurePlan;
import com.sc.construction.plan.StructureSchematic;
import com.sc.util.SCWorldEditUtil;
import com.sc.util.WorldUtil;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import java.io.Serializable;
import java.util.UUID;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 *
 * @author Chingo
 */

@Table(name = "SC_STRUCTURE")
@Entity
public class Structure implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "SC_STRUCTURE_ID")
    private Long id;
    
    private UUID worldUUID;

    /**
     * Only used for lookups, for authorized actions worldguard will be used
     */
    @NotNull
    private UUID ownerID; 
    
    @NotNull
    private String owner;

    @Embedded
    private StructurePlan plan;

    @Embedded
    private WorldLocation worldLocation;

    @Embedded
    private WorldDimension dimension;

    private SimpleCardinal cardinal;
    
    private Double refundValue;

    @OneToOne
    @Cascade(CascadeType.ALL)
    private ConstructionProcess progress;
    
    @Nullable
    private String structureRegion;

//    @OneToOne
//    @Cascade(CascadeType.ALL)
////    @PrimaryKeyJoinColumn(name = "STRUCTURE_ID", referencedColumnName = "TASK_ID")
//    private ConstructionTask task;
    
    
    /**
     * JPA Constructor
     */
    protected Structure() {}
    
    
    /**
     * Dummy constructor for overlaps()
     * @param target The location
     * @param cardinal The cardinal
     * @param plan The plan
     * @param schematic The schematic
     */
    Structure(World world, Vector target, SimpleCardinal cardinal, StructurePlan plan, StructureSchematic schematic) {
        this.worldLocation = new WorldLocation(new Location(world, target));
        this.cardinal = cardinal;
        Vector pos2 = WorldUtil.getPoint2Right(target, cardinal, new BlockVector(
                schematic.getWidth(), 
                schematic.getHeight(), 
                schematic.getLength())
        );
        this.dimension = new WorldDimension(world, target, pos2);
        this.worldUUID = Bukkit.getWorld(world.getName()).getUID();
    }
    
    Structure(org.bukkit.World world, Vector target, SimpleCardinal cardinal, StructurePlan plan, StructureSchematic schematic) {
        this(SCWorldEditUtil.getWorld(world.getName()), target, cardinal, plan, schematic);
    }

    /**
     * Constructor.
     * @param owner The ownerID of this structure
     * @param target The start location of this structure
     * @param cardinal The player's direction on placement
     * @param plan The plan
     * @param structureschematic
     */
    Structure(World world, Player owner, Vector target, SimpleCardinal cardinal, StructurePlan plan, StructureSchematic structureschematic) {
        Preconditions.checkNotNull(plan);
        Preconditions.checkNotNull(target);
        Preconditions.checkNotNull(cardinal);
        // Sanity check
        Preconditions.checkArgument(plan.getSchematicChecksum().equals(structureschematic.getCheckSum()));
        this.plan = plan;
        this.ownerID = owner.getUniqueId();
        this.owner = owner.getName();
        this.cardinal = cardinal;
        this.worldLocation = new WorldLocation(new Location(world, target));
        this.worldUUID = worldLocation.getWorld().getUID();
        this.refundValue = plan.getPrice();
        Vector pos2 = WorldUtil.getPoint2Right(target, cardinal, new BlockVector(
                structureschematic.getWidth(), 
                structureschematic.getHeight(), 
                structureschematic.getLength())
        );
        this.dimension = new WorldDimension(world, target, pos2);
    }

    public void setRefundValue(Double refundValue) {
        this.refundValue = refundValue;
    }

    public Double getRefundValue() {
        return refundValue;
    }
    
    public UUID getWorldUUID() {
        return worldUUID;
    }
    
    public String getWorld() {
        return worldLocation.getWorldName();
    }
   
    public ConstructionProcess getProgress() {
        return progress;
    }
    
    public void setConstructionProgress(ConstructionProcess progress) {
        this.progress = progress;
    }
    


    public void setStructureRegionId(String structureRegion) {
        this.structureRegion = structureRegion;
    }

    /**
     * Gets the id of this structure
     *
     * @return The id
     */
    public Long getId() {
        return id;
    }

    public StructurePlan getPlan() {
        return plan;
    }

//    public ConstructionTask getTask() {
//        return task;
//    }
    
    

    /**
     * Gets the name of the ownerID of this structure Owner may be a Player or NPC
     *
     * @return The ownerID of this structure
     */
    public UUID getOwnerUUID() {
        return ownerID;
    }
    
    public String getOwner() {
        return owner;
    }

    /**
     * Sets the ownerID of this structure Use a Structure to handle this as a transaction!
     *
     * @param owner The new ownerID of this structure
     */
    public void setOwner(Player owner) {
        this.ownerID = owner.getUniqueId();
    }

    /**
     * Gets the direction (NORTH|EAST|SOUTH|WEST) of this structure
     *
     * @return The direction
     */
    public SimpleCardinal getCardinal() {
        return cardinal;
    }

    /**
     * Gets the actual location of the start of this structure
     *
     * @return The building location
     */
    public Location getLocation() {
        return worldLocation.getLocation();
    }
    
    public Vector getPosition() {
        return new BlockVector(worldLocation.getX(), worldLocation.getY(), worldLocation.getZ());
    }

    public WorldDimension getDimension() {
        return dimension;
    }

    public String getStructureRegion() {
        return structureRegion;
    }

    public boolean isOnLot(Location location) {
        return (location.getBlockX() >= dimension.getMinX()&& location.getBlockX() <= dimension.getMaxX()
                && location.getBlockY() >= dimension.getMinY()&& location.getBlockY() <= dimension.getMaxY()
                && location.getBlockZ() >= dimension.getMinZ()&& location.getBlockZ() <= dimension.getMaxZ());
    }
    
    /**
     * Gets the relative position
     * @param location The location
     * @return the relative position from this structure
     */
    public Vector getRelativePosition(Location location) {
        switch(cardinal) {
            case NORTH: return new BlockVector(
                    location.getBlockX() - worldLocation.getX(), 
                    location.getBlockY() - worldLocation.getY(), 
                    worldLocation.getZ() - location.getBlockZ()
            );
            case SOUTH: return new BlockVector(
                    worldLocation.getX() - location.getBlockX(), 
                    location.getBlockY() - worldLocation.getY(), 
                    location.getBlockZ() - worldLocation.getZ()
            );
            case EAST: return new Vector(
                    location.getBlockZ() - worldLocation.getZ(), 
                    location.getBlockY() - worldLocation.getY(), 
                    location.getBlockX() - worldLocation.getX()
            );
            case WEST: return new Vector(
                    worldLocation.getZ() - location.getBlockZ(), 
                    location.getBlockY() - worldLocation.getY(), 
                    worldLocation.getX() - location.getBlockX()
            );
            default: throw new AssertionError("Unreachable");
        }
    }
    
    /**
     * Gets the location with a given offset from this structure
     * @param offset The offset
     * @return the location
     */
    public Location getLocation(Vector offset) {
        Vector p = WorldUtil.addOffset(getPosition(), cardinal, offset.getX(), offset.getY(), offset.getZ());
        return new Location(SCWorldEditUtil.getWorld(worldLocation.getWorldName()), p);
    }

}
