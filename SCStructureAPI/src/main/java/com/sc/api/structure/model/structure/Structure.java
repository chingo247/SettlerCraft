package com.sc.api.structure.model.structure;

import com.avaje.ebean.validation.NotNull;
import com.google.common.base.Preconditions;
import com.sc.api.structure.event.structure.StructureStateChangedEvent;
import com.sc.api.structure.model.structure.plan.StructurePlan;
import com.sc.api.structure.model.structure.progress.StructureProgress;
import com.sc.api.structure.model.structure.world.SimpleCardinal;
import com.sc.api.structure.model.structure.world.WorldDimension;
import com.sc.api.structure.model.structure.world.WorldLocation;
import com.sc.api.structure.util.WorldUtil;
import com.sk89q.worldedit.Location;
import java.io.Serializable;
import javax.annotation.Nullable;
import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.bukkit.Bukkit;

/**
 *
 * @author Chingo
 */
@Entity
@Table(name = "sc_structure")
public class Structure implements Serializable {

    @Id
    @GeneratedValue
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

    private SimpleCardinal direction;

    @Nullable
    @OneToOne(cascade = CascadeType.ALL)
    private StructureProgress progress;
    
    @Nullable
    private String parentRegion;
    
    @Nullable
    private String structureRegion;
    

    @Embedded
    private ReservedArea reserved;

    private StructureState status;

    /**
     * JPA Constructor
     */
    protected Structure() {
    }

    /**
     * Constructor.
     *
     * @param owner The owner of this structure
     * @param target The start location of this structure
     * @param direction The player's direction on placement
     * @param plan The plan
     */
    public Structure(String owner, Location target, SimpleCardinal direction, StructurePlan plan) {
        Preconditions.checkNotNull(plan);
        Preconditions.checkNotNull(target);
        Preconditions.checkNotNull(direction);
        this.plan = plan;
        this.owner = owner;
        this.setStatus(StructureState.CLEARING_SITE_OF_BLOCKS);
        this.direction = direction;
        this.worldLocation = new WorldLocation(target);
        this.reserved = new ReservedArea(this);
        this.dimension = WorldUtil.getWorldDimension(target, direction, plan.getSchematic());
//        this.progress = new StructureProgress(this, blockReport);
    }

    public void setStructureRegion(String structureRegion) {
        this.structureRegion = structureRegion;
    }

    public void setParentRegion(String parentRegion) {
        this.parentRegion = parentRegion;
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
     * Sets the owner of this structure Use a Structure to handle this as a
     * transaction!
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
    public SimpleCardinal getDirection() {
        return direction;
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

    public StructureState getStatus() {
        return status;
    }

    public final void setStatus(StructureState status) {
        Bukkit.getPluginManager().callEvent(new StructureStateChangedEvent(this, status));
        this.status = status;
    }

    public StructureProgress getProgress() {
        return progress;
    }

    public ReservedArea getReserved() {
        return reserved;
    }

    public boolean isOnLot(Location location) {
        return (location.getPosition().getBlockX() >= dimension.getStartX() && location.getPosition().getBlockX() <= dimension.getEndX()
                && location.getPosition().getBlockY() >= dimension.getStartY() && location.getPosition().getBlockY() <= dimension.getEndY()
                && location.getPosition().getBlockZ() >= dimension.getStartZ() && location.getPosition().getBlockZ() <= dimension.getEndZ());
    }

}
