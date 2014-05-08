package com.sc.api.structure.model.structure;

import com.avaje.ebean.validation.NotNull;
import com.google.common.base.Preconditions;
import com.sc.api.structure.event.structure.StructureStateChangedEvent;
import com.sc.api.structure.model.structure.plan.StructurePlan;
import com.sc.api.structure.model.structure.progress.StructureProgress;
import com.sc.api.structure.model.structure.schematic.SchematicBlockReport;
import com.sc.api.structure.model.structure.world.Direction;
import com.sc.api.structure.model.structure.world.WorldDimension;
import com.sc.api.structure.model.structure.world.WorldLocation;
import com.sc.api.structure.util.WorldUtil;
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
public class Structure implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private String owner; //TODO Create Owner class

    private String planId;

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
     * @param blockReport
     * @param plan
     */
    public Structure(Player owner, SchematicBlockReport blockReport, StructurePlan plan, Location target ) {
        Preconditions.checkNotNull(plan);
        Preconditions.checkNotNull(target);
        this.planId = plan.getId();
        this.owner = owner.getName();
//        int[] modifiers = WorldUtil.getModifiers(direction);
//        this.xMod = modifiers[0];
//        this.zMod = modifiers[1];
        setStatus(StructureState.CLEARING_SITE_OF_BLOCKS);
        this.worldLocation = new WorldLocation(target);
//        this.dimension = new WorldDimension(this);
        this.reserved = new ReservedArea(this);
        this.progress = new StructureProgress(this, blockReport);
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
        Bukkit.getPluginManager().callEvent(new StructureStateChangedEvent(this,status));
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
    
   

}
