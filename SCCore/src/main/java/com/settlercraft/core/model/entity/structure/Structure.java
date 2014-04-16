package com.settlercraft.core.model.entity.structure;

import com.avaje.ebean.validation.NotEmpty;
import com.avaje.ebean.validation.NotNull;
import com.google.common.base.Preconditions;
import com.settlercraft.core.manager.StructurePlanManager;
import com.settlercraft.core.model.plan.StructurePlan;
import com.settlercraft.core.model.plan.schematic.SchematicObject;
import com.settlercraft.core.model.world.Direction;
import com.settlercraft.core.model.world.WorldDimension;
import com.settlercraft.core.model.world.WorldLocation;
import com.settlercraft.core.util.WorldUtil;
import java.io.Serializable;
import java.util.Date;
import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

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
    private String owner; //FIXME Create Owner class

    @NotNull
    @NotEmpty
    private String plan;

    @NotNull
    private int xMod;

    @NotNull
    private int zMod;

    @Embedded
    private WorldLocation worldLocation;

    @Embedded
    private WorldDimension dimension;
    
    @Nullable
    @OneToOne(cascade = CascadeType.ALL)
    private StructureProgress progress;


    public enum STATE {

        /**
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
         * Players/NPC may build now
         */
        BUILDING_IN_PROGRESS,
        /**
         * ConstructionSite is complete
         */
        COMPLETE
    }

    private STATE status;

    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

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
        setStatus(STATE.CLEARING_SITE_OF_BLOCKS);
        this.created = new Date();
        this.worldLocation = new WorldLocation(target);
        this.dimension = new WorldDimension(this);
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
    public Location getStructureStartLocation() {
        return new Location(Bukkit.getWorld(worldLocation.getWorld()), worldLocation.getX(), worldLocation.getY(), worldLocation.getZ());
    }

    public Location getStructureEndLocation() {
        SchematicObject schem = getPlan().getSchematic();
        Location target = getStructureStartLocation();
        Direction direction = WorldUtil.getDirection(xMod, zMod);

        int[] mods = WorldUtil.getModifiers(direction);
        int xMd = mods[0];
        int zMd = mods[1];
        Location loc;
        if (direction == Direction.NORTH || direction == Direction.SOUTH) {
            loc = target.clone().add((schem.width - 1) * xMd, (schem.layers - 1), (schem.length - 1) * zMd);
        } else {
            loc = target.clone().add((schem.length - 1) * zMd, (schem.layers - 1), (schem.width - 1) * xMd);
        }
        return loc;
    }

    /**
     * Gets the start location the structure dimension
     *
     * @return The startlocation of this structure
     */
    public Location getDimensionStartLocation() {
        return new Location(Bukkit.getWorld(worldLocation.getWorld()), dimension.getStartX(), dimension.getStartY(), dimension.getStartZ());
    }

    /**
     * Gets the end location of the structure dimension
     *
     * @return
     */
    public Location getDimensionEndLocation() {
        return new Location(Bukkit.getWorld(worldLocation.getWorld()), dimension.getEndX(), dimension.getEndY(), dimension.getEndZ());
    }

    public WorldDimension getDimension() {
        return dimension;
    }

    public STATE getStatus() {
        return status;
    }

    public final void setStatus(STATE status) {
        System.out.println(this + " changed state: " + status);
        this.status = status;
    }

    public Date getCreated() {
        return created;
    }

    public StructureProgress getProgress() {
        return progress;
    }
    

    @Override
    public String toString() {
        return "id:" + getId() + " owner:" + getOwner() + " plan:" + getPlan();
    }

}
