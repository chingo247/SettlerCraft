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
import com.sc.api.structure.entity.progress.MaterialProgress;
import com.sc.api.structure.entity.world.SimpleCardinal;
import com.sc.api.structure.entity.world.WorldDimension;
import com.sc.api.structure.entity.world.WorldLocation;
import com.sc.api.structure.util.WorldUtil;
import com.sk89q.worldedit.Location;
import java.io.Serializable;
import javax.annotation.Nullable;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;

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

    @Lob
    private StructurePlan plan;

    @Embedded
    @AttributeOverride(name = "world", column = @Column(name = "loc_world"))
    private WorldLocation worldLocation;

    @Embedded
    @AttributeOverride(name = "world", column = @Column(name = "dim_world"))
    private WorldDimension dimension;

    private SimpleCardinal cardinal;

    @Nullable
    @OneToOne
    private MaterialProgress progress;
    
    @Nullable
    private String structureRegion;

    @Embedded
    private ReservedArea reserved;

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
        this.reserved = new ReservedArea(this);
        this.dimension = WorldUtil.getWorldDimension(target, cardinal, plan.getSchematic());
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

    public ReservedArea getReserved() {
        return reserved;
    }

    public String getStructureRegion() {
        return structureRegion;
    }

    public boolean isOnLot(Location location) {
        return (location.getPosition().getBlockX() >= dimension.getStartX() && location.getPosition().getBlockX() <= dimension.getEndX()
                && location.getPosition().getBlockY() >= dimension.getStartY() && location.getPosition().getBlockY() <= dimension.getEndY()
                && location.getPosition().getBlockZ() >= dimension.getStartZ() && location.getPosition().getBlockZ() <= dimension.getEndZ());
    }

}
