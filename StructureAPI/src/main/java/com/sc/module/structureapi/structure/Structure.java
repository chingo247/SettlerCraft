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
package com.sc.module.structureapi.structure;

import com.google.common.base.Preconditions;
import com.sc.module.structureapi.structure.plan.StructurePlan;
import com.sc.module.structureapi.structure.schematic.Schematic;
import static com.sc.module.structureapi.util.SchematicUtil.calculateDimension;
import com.sc.module.structureapi.util.WorldUtil;
import com.sc.module.structureapi.world.Cardinal;
import static com.sc.module.structureapi.world.Cardinal.EAST;
import static com.sc.module.structureapi.world.Cardinal.NORTH;
import static com.sc.module.structureapi.world.Cardinal.SOUTH;
import static com.sc.module.structureapi.world.Cardinal.WEST;
import com.sc.module.structureapi.world.Dimension;
import com.sc.module.structureapi.world.Location;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import construction.exception.StructureDataException;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 *
 * @author Chingo
 */
@Table(name = "SC_STRUCTURE")
@Entity
public class Structure implements Serializable {

    public enum State {
        INITIALIZING,

        /**
         * Structure has been added to AsyncWorldEdit's blockplacer's Queue
         */
        QUEUED,
        /**
         * Schematic is being loaded
         */
        LOADING_SCHEMATIC,
        /**
         * Fence is being placed
         */
        PLACING_FENCE,
        /**
         * Structure is being build
         */
        BUILDING,
        /**
         * Structure is being demolished
         */
        DEMOLISHING,
        /**
         * Progress has been completed
         */
        COMPLETE,
        /**
         * Structure has been removed
         */
        REMOVED,
        /**
         * Progress has stopped
         */
        STOPPED
    }
    
    private State state = State.INITIALIZING;

    @Id
    @GeneratedValue
    @Column(name = "SC_STRUCTURE_ID")
    private Long id;
    
    @Embedded
    private StructureLogEntry logEntry;


    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//    @JoinColumn(name = "OWNER_ID", columnDefinition = "STRUCTURE_ID")
    private Set<PlayerOwnership> ownerships;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//    @JoinColumn(name = "MEMBER_ID", columnDefinition = "STRUCTURE_ID")
    private Set<PlayerMembership> memberships;

//    @Embedded
//    private StructurePlan plan;
    @Embedded
    @Column(updatable = false)
    private Location location;

    @Embedded
    @Column(updatable = false)
    private Dimension dimension;

    @Column(updatable = false)
    private Cardinal cardinal;

    private Double refundValue;

    @Nullable
    private String structureRegion;

    private String name;
    
    

    /**
     * JPA Constructor
     */
    protected Structure() {
    }

    /**
     * Constructor.
     *
     * @param owner The ownerID of this structure
     * @param target The start location of this structure
     * @param cardinal The player's direction on placement
     * @param plan The plan
     * @param structureschematic
     */
    Structure(World world, Vector pos, Cardinal cardinal, Schematic schematic) {
        Preconditions.checkNotNull(pos);
        Preconditions.checkNotNull(cardinal);
        this.cardinal = cardinal;
        this.location = new Location(world, pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
        this.dimension = calculateDimension(schematic, pos, cardinal);
//        this.memberships = new HashSet<>();
//        this.ownerships = new HashSet<>();
        this.logEntry = new StructureLogEntry();
    }

    public StructureLogEntry getLogEntry() {
        return logEntry;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    
    
    

    public Long getSchematicChecksum() throws IOException, StructureDataException {
        long checksum = FileUtils.checksumCRC32(getSchematicFile());
        return checksum;
    }

    public StructurePlan getPlan() throws StructureDataException {
        File file = new File(getDataFolder(), "Config.xml");
        StructurePlan plan = StructurePlan.load(file);
        return plan;
    }

    public File getSchematicFile() throws StructureDataException {
        return getPlan().getSchematic();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public File getDataFolder() {
        return new File(StructureAPI.getDataFolder(), getWorldName() + "//" + id);
    }

    public boolean isOwner(Player player) {
        for (PlayerOwnership pos : ownerships) {
            if (pos.getUUID().equals(player.getUniqueId())) {
                return true;
            }
        }
        return false;
    }

    public List<PlayerOwnership> getOwnerships() {
        return new ArrayList<>(ownerships);
    }
    

    boolean addOwner(PlayerOwnership playerOwner) {
        return ownerships.add(playerOwner);
    }

    boolean removeOwner(PlayerOwnership playerOwner) {
        return ownerships.remove(playerOwner);
    }

    boolean addMember(PlayerMembership playerMember) {
        return memberships.add(playerMember);
    }

    boolean removeMember(PlayerMembership playerMember) {
        return memberships.remove(playerMember);
    }

    public void setPrice(Double refundValue) {
        this.refundValue = refundValue;
    }

    public Double getRefundValue() {
        return refundValue;
    }

    /**
     * Gets the world of this structure. Calls location.getWorldName()
     *
     * @return The world of this structure
     */
    public String getWorldName() {
        return location.getWorldName();
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

    public UUID getWorldUUID() {
        return location.getWorldUUID();
    }

    /**
     * Gets the direction (NORTH|EAST|SOUTH|WEST) of this structure
     *
     * @return The direction
     */
    public Cardinal getCardinal() {
        return cardinal;
    }

    /**
     * Gets the actual location of the start of this structure
     *
     * @return The building location
     */
    public org.bukkit.Location getLocation() {
        return new org.bukkit.Location(Bukkit.getWorld(getWorldName()), location.getX(), location.getY(), location.getZ());
    }

    public Vector getPosition() {
        return new BlockVector(location.getX(), location.getY(), location.getZ());
    }

    public Dimension getDimension() {
        return dimension;
    }

    public String getStructureRegion() {
        return structureRegion;
    }

    /**
     * Gets the relative position
     *
     * @param location The location
     * @return the relative position from this structure
     */
    public Vector getRelativePosition(com.sk89q.worldedit.util.Location location) {
        switch (cardinal) {
            case NORTH:
                return new BlockVector(
                        location.getBlockX() - location.getX(),
                        location.getBlockY() - location.getY(),
                        location.getZ() - location.getBlockZ()
                );
            case SOUTH:
                return new BlockVector(
                        location.getX() - location.getBlockX(),
                        location.getBlockY() - location.getY(),
                        location.getBlockZ() - location.getZ()
                );
            case EAST:
                return new Vector(
                        location.getBlockZ() - location.getZ(),
                        location.getBlockY() - location.getY(),
                        location.getBlockX() - location.getX()
                );
            case WEST:
                return new Vector(
                        location.getZ() - location.getBlockZ(),
                        location.getBlockY() - location.getY(),
                        location.getX() - location.getBlockX()
                );
            default:
                throw new AssertionError("Unreachable");
        }
    }

    /**
     * Adds the offset to the location of this structure and returns the world location.
     *
     * @param offset The offset
     * @return the location
     */
    public org.bukkit.Location getLocationForOffset(Vector offset) {
        Vector p = WorldUtil.addOffset(getPosition(), cardinal, offset.getX(), offset.getY(), offset.getZ());
        World world = Bukkit.getWorld(getWorldName());
        return new org.bukkit.Location(world, p.getBlockX(), p.getBlockY(), p.getBlockZ());
    }

    @Override
    public String toString() {
        return "#" + ChatColor.GOLD + id + " " + ChatColor.BLUE + name + ChatColor.RESET;

    }

}
