package com.settlercraft.core.model.entity.structure;

import com.avaje.ebean.validation.NotEmpty;
import com.avaje.ebean.validation.NotNull;
import com.google.common.base.Preconditions;
import com.settlercraft.core.manager.StructurePlanManager;
import com.settlercraft.core.model.entity.SettlerCraftEntity;
import com.settlercraft.core.model.plan.StructurePlan;
import com.settlercraft.core.model.world.Direction;
import com.settlercraft.core.model.world.WorldDimension;
import com.settlercraft.core.model.world.WorldLocation;
import com.settlercraft.core.util.WorldUtil;
import java.io.Serializable;
import javax.annotation.Nullable;
import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
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
public class Structure extends SettlerCraftEntity implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private String owner; //TODO Create Owner class

    @NotNull
    @NotEmpty
    private String plan;

    @NotNull
    private int xMod;

    @NotNull
    private int zMod;

    @Embedded
    @AttributeOverride(name = "world", column = @Column(name = "loc_world"))
    private WorldLocation worldLocation;

    @Embedded
    @AttributeOverride(name = "world", column = @Column(name = "dim_world"))
    private WorldDimension dimension;
    
    @Nullable
    @OneToOne(cascade = CascadeType.ALL)
    private StructureProgress progress;

    @Embedded
    private ReservedArea reserved;

    public enum StructureState {

        /**
         * All blocks on structure location will be removed.
         *//**
         * All blocks on structure location will be removed.
         */
        CLEARING_SITE_OF_BLOCKS,
        /**
         * All blocks on structure location will be removed.
         */
        CLEARING_SITE_OF_ENTITIES,
        /**
         * Placeing Foundation
         */
        PLACING_FOUNDATION,
        /**
         * Frame will be placed, all players will be removed from the foundation
         */
        PLACING_FRAME,
        
        /**
         * When the Complete() was called on this structure
         */
        FINISHING,
        /**
         * A layer is being constructed
         */
        ADVANCING_TO_NEXT_LAYER,
        /**
         * Players/NPC may build now
         */
        READY_TO_BE_BUILD,
        /**
         * ConstructionSite is complete
         */
        COMPLETE
    }

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
     * @param direction The direction of this structure
     * @param plan
     */
    public Structure(Player owner, Location target, Direction direction, StructurePlan plan) {
        Preconditions.checkNotNull(plan);
        Preconditions.checkNotNull(target);
        this.owner = owner.getName();
        this.plan = plan.getConfig().getName();
        int[] modifiers = WorldUtil.getModifiers(direction);
        this.xMod = modifiers[0];
        this.zMod = modifiers[1];
        setStatus(StructureState.CLEARING_SITE_OF_BLOCKS);
        this.worldLocation = new WorldLocation(target);
        this.dimension = new WorldDimension(this);
        this.reserved = new ReservedArea(this);
        this.progress = new StructureProgress(this);
    }

    /**
     * Gets the id of this structure
     *
     * @return The id
     */
    public Long getId() {
        return id;
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
     * Retrieves the plan of this structure
     *
     * @return The structure plan
     */
    public StructurePlan getPlan() {
        return StructurePlanManager.getInstance().getPlan(plan);
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
     * Gets the xMod of this building to determine the direction
     *
     * @return The xMod
     */
    public int getxMod() {
        return xMod;
    }

    /**
     * Gets the zMod of this building to determine the direction
     *
     * @return The zMod of this building
     */
    public int getzMod() {
        return zMod;
    }

    /**
     * Gets the direction (NORTH|EAST|SOUTH|WEST) of this building
     *
     * @return The direction
     */
    public Direction getDirection() {
        return WorldUtil.getDirection(xMod, zMod);
    }

    /**
     * Gets the actual location of the start of this building
     *
     * @return The building location
     */
    public Location getLocation() {
        return new Location(Bukkit.getWorld(worldLocation.getWorld()), worldLocation.getX(), worldLocation.getY(), worldLocation.getZ());
    }

    public WorldDimension getDimension() {
        return dimension;
    }

    public StructureState getStatus() {
        return status;
    }

    public final void setStatus(StructureState status) {
        this.status = status;
    }


    public StructureProgress getProgress() {
        return progress;
    }
    
    

    public ReservedArea getReserved() {
        return reserved;
    }
    
    public boolean isOnLot(Location location) {
        return (location.getBlockX() >= dimension.getStartX() && location.getBlockX() <= dimension.getEndX()
                && location.getBlockY() >= dimension.getStartY() && location.getBlockY() <= dimension.getEndY()
                && location.getBlockZ() >= dimension.getStartZ() && location.getBlockZ() <= dimension.getEndZ());
    }
    
    

    @Override
    public String toString() {
        return "id:" + getId() + " owner:" + getOwner() + " plan:" + getPlan() + "direction: " + getDirection();
    }

}
