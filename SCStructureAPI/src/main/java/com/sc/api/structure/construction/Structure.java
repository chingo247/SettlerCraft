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
package com.sc.api.structure.construction;

import com.avaje.ebean.validation.NotNull;
import com.google.common.base.Preconditions;
import com.sc.api.structure.entity.plan.StructurePlan;
import com.sc.api.structure.entity.world.SimpleCardinal;
import com.sc.api.structure.entity.world.WorldDimension;
import com.sc.api.structure.entity.world.WorldLocation;
import com.sc.api.structure.plan.PlanManager;
import com.sc.api.structure.util.WorldUtil;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Location;
import java.io.Serializable;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SC_STRUCTURE_ID")
    private Long id;

    /**
     * Only used for lookups, for authorized actions worldguard will be used
     */
    @NotNull
    private String owner; 

    @Embedded
    private StructurePlan plan;

    @Embedded
    private WorldLocation worldLocation;

    @Embedded
    private WorldDimension dimension;

    private SimpleCardinal cardinal;

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
    protected Structure() {
    }

    /**
     * Constructor.
     * @param owner The owner of this structure
     * @param target The start location of this structure
     * @param cardinal The player's direction on placement
     * @param plan The plan
     */
    Structure(String owner, Location target, SimpleCardinal cardinal, StructurePlan plan) {
        Preconditions.checkNotNull(plan);
        Preconditions.checkNotNull(target);
        Preconditions.checkNotNull(cardinal);
        this.plan = plan;
        this.owner = owner;
        this.cardinal = cardinal;
        this.worldLocation = new WorldLocation(target);
        CuboidClipboard structureSchematic = PlanManager.getInstance().getClipBoard(getPlan().getChecksum());
        this.dimension = WorldUtil.getWorldDimension(target, cardinal, structureSchematic);
    }

    public ConstructionProcess getProgress() {
        return progress;
    }
    
    void setConstructionProgress(ConstructionProcess progress) {
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

    public String getStructureRegion() {
        return structureRegion;
    }

    public boolean isOnLot(Location location) {
        return (location.getPosition().getBlockX() >= dimension.getMinX()&& location.getPosition().getBlockX() <= dimension.getMaxX()
                && location.getPosition().getBlockY() >= dimension.getMinY()&& location.getPosition().getBlockY() <= dimension.getMaxY()
                && location.getPosition().getBlockZ() >= dimension.getMinZ()&& location.getPosition().getBlockZ() <= dimension.getMaxZ());
    }

}
