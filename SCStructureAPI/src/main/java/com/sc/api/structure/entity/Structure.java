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
package com.sc.api.structure.entity;

import com.avaje.ebean.validation.NotNull;
import com.google.common.base.Preconditions;
import com.sc.api.structure.entity.plan.StructurePlan;
import com.sc.api.structure.entity.progress.ConstructionTask;
import com.sc.api.structure.entity.progress.MaterialProgress;
import com.sc.api.structure.entity.world.SimpleCardinal;
import com.sc.api.structure.entity.world.WorldDimension;
import com.sc.api.structure.entity.world.WorldLocation;
import com.sc.api.structure.util.WorldUtil;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Location;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 *
 * @author Chingo
 */
@Entity
@Table(name = "sc_structure")
public class Structure implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "STRUCTURE_ID")
    private Long id;

    @NotNull
    private String owner; //TODO Create Owner class

    @Embedded
    private StructurePlan plan;

    @Embedded
    private WorldLocation worldLocation;

    @Embedded
    private WorldDimension dimension;

    private SimpleCardinal cardinal;

    @Nullable
    @OneToOne
    private MaterialProgress progress;
    
    @Nullable
    private String structureRegion;

    @OneToOne
    @Cascade(CascadeType.ALL)
    @PrimaryKeyJoinColumn(name = "STRUCTURE_ID", referencedColumnName = "TASK_ID")
    private ConstructionTask task;
    
    
    private File areaBefore;

    /**
     * JPA Constructor
     */
    protected Structure() {
    }

    /**
     * Constructor.
     * @param owner The owner of this structure
     * @param target The start location of this structure
     * @param cardinal The player's direction on placement
     * @param plan The plan
     */
    public Structure(String owner, Location target, SimpleCardinal cardinal, StructurePlan plan) {
        Preconditions.checkNotNull(plan);
        Preconditions.checkNotNull(target);
        Preconditions.checkNotNull(cardinal);
        this.plan = plan;
        this.owner = owner;
        this.cardinal = cardinal;
        this.worldLocation = new WorldLocation(target);
        this.dimension = WorldUtil.getWorldDimension(target, cardinal, plan.getSchematic());
    }

    public void setAreaBefore(CuboidClipboard backup) {
        try {
            areaBefore = File.createTempFile(owner + "_", "str_" + id + "_" + getPlan().getDisplayName().replaceAll("\\s", "_"));
            SchematicFormat.MCEDIT.save(backup, areaBefore);
        } catch (IOException | DataException ex) {
            Logger.getLogger(Structure.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public CuboidClipboard getAreaBefore() {
        CuboidClipboard area = null;
        try {
            area = SchematicFormat.MCEDIT.load(areaBefore);
        } catch (IOException | DataException ex) {
            Logger.getLogger(Structure.class.getName()).log(Level.SEVERE, null, ex);
        }
        return area;
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

    public ConstructionTask getTask() {
        return task;
    }
    
    

    /**
     * Gets the name of the owner of this structure Owner may be a Player or NPC
     *
     * @return The owner of this structure
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Sets the owner of this structure Use a Structure to handle this as a transaction!
     *
     * @param owner The new owner of this structure
     */
    public void setOwner(String owner) {
        this.owner = owner;
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

    public WorldDimension getDimension() {
        return dimension;
    }

    public MaterialProgress getProgress() {
        return progress;
    }

    public String getStructureRegion() {
        return structureRegion;
    }

    public boolean isOnLot(Location location) {
        return (location.getPosition().getBlockX() >= dimension.getMinX()&& location.getPosition().getBlockX() <= dimension.getMaxX()
                && location.getPosition().getBlockY() >= dimension.getMinY()&& location.getPosition().getBlockY() <= dimension.getMaxY()
                && location.getPosition().getBlockZ() >= dimension.getMinZ()&& location.getPosition().getBlockZ() <= dimension.getMaxZ());
    }

}
