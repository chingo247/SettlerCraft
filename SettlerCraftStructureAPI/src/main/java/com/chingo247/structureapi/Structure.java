/*
 * Copyright (C) 2014 Chingo247
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
package com.chingo247.structureapi;

import static com.chingo247.structureapi.Direction.EAST;
import static com.chingo247.structureapi.Direction.NORTH;
import static com.chingo247.structureapi.Direction.SOUTH;
import static com.chingo247.structureapi.Direction.WEST;
import com.chingo247.structureapi.PlayerOwnership.Type;
import com.chingo247.structureapi.event.StructureStateChangeEvent;
import com.chingo247.structureapi.plan.schematic.Schematic;
import static com.chingo247.structureapi.util.SchematicUtil.calculateDimension;
import com.chingo247.structureapi.util.WorldUtil;
import com.google.common.base.Preconditions;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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

    @Column(name = "Status")
    private State state = State.INITIALIZING;

    @Id
    @GeneratedValue
    @Column(name = "SC_STRUCTURE_ID")
    private Long id;

    @Embedded
    private StructureLog logEntry;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<PlayerOwnership> ownerships;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<PlayerMembership> memberships;

    @Embedded
    @Column(updatable = false)
    private Location location;

    @Embedded
    @Column(updatable = false)
    private Dimension dimension;

    @Column(updatable = false)
    private Direction direction;

    private Double refundValue;

    private String structureRegion;

    private String name;
    
    private Long checksum;

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
     * @param direction The player's direction on placement
     * @param plan The plan
     * @param structureschematic
     */
    Structure(World world, Vector pos, Direction direction, Schematic schematic) {
        Preconditions.checkNotNull(pos);
        Preconditions.checkNotNull(direction);
        Preconditions.checkNotNull(schematic);
        Preconditions.checkNotNull(world);
        this.direction = direction;
        this.location = new Location(world, pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
        this.dimension = calculateDimension(schematic, pos, direction);
        this.memberships = new HashSet<>();
        this.ownerships = new HashSet<>();
        this.logEntry = new StructureLog();
        this.checksum = schematic.getCheckSum();
    }

    public Long getChecksum() {
        return checksum;
    }
    
    void setChecksum(long checksum) {
        this.checksum = checksum;
    }
    

    /**
     * Gets the id of this structure
     *
     * @return The id
     */
    public Long getId() {
        return id;
    }
    
    public StructureLog getLog() {
        return logEntry;
    }

    public void setRefundValue(Double refundValue) {
        this.refundValue = refundValue;
    }
    
    public Set<PlayerOwnership> getOwnerships() {
        return getOwnerships(null);
    }
    
    public Set<PlayerOwnership> getOwnerships(Type type) {
        if(type == null) {
            return new HashSet<>(ownerships);
        } else {
            Set<PlayerOwnership> owners = new HashSet<>();
            for(PlayerOwnership ownership : ownerships) {
                if(ownership.getOwnerType() == type) {
                    owners.add(ownership);
                }
            }
            return owners;
        }
    }

    public State getState() {
        return state;
    }

    public synchronized void setState(State newState) {
        if (this.state != newState) {
            State oldState = state;
            this.state = newState;
            Bukkit.getPluginManager().callEvent(new StructureStateChangeEvent(this, oldState));
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    

    public boolean isOwner(Player player) {
        return isOwner(player, null);
    }
    
    public boolean isOwner(Player player, Type type) {
        for (PlayerOwnership pos : ownerships) {
            if(type == null && pos.getPlayerUUID().equals(player.getUniqueId())) {
                return true;
            }
            
            if (pos.getOwnerType() == type && pos.getPlayerUUID().equals(player.getUniqueId())) {
                return true;
            }
        }
        return false;
    }
    

    public boolean isMember(Player player) {
        for (PlayerOwnership pos : ownerships) {
            if (pos.getPlayerUUID().equals(player.getUniqueId())) {
                return true;
            }
        }
        return false;
    }



    public boolean addOwner(Player player, Type ownerType) {
        if(!isOwner(player)) {
            ownerships.add(new PlayerOwnership(player, this, ownerType));
            return true;
        }
        return false;
    }

    public boolean removeOwner(Player player) {
        Iterator<PlayerOwnership> it = ownerships.iterator();
        while (it.hasNext()) {
            PlayerOwnership pm = it.next();
            if (pm.getPlayerUUID().equals(player.getUniqueId())) {
                it.remove();
                return true;
            }

        }
        return false;
    }

    public boolean addMember(Player player) {
        if(!isMember(player)) {
            memberships.add(new PlayerMembership(player, this));
            return true;
        }
        return false;
        
    }

    public boolean removeMember(Player playerMember) {
        Iterator<PlayerMembership> it = memberships.iterator();
        while (it.hasNext()) {
            PlayerMembership pm = it.next();
            if (pm.getUUID().equals(playerMember.getUniqueId())) {
                it.remove();
                return true;
            }

        }
        return false;
    }

    public Double getRefundValue() {
        return refundValue;
    }

    public void setStructureRegionId(String structureRegion) {
        this.structureRegion = structureRegion;
    }

    /**
     * Gets the direction (NORTH|EAST|SOUTH|WEST) of this structure
     *
     * @return The direction
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Gets the actual location of the start of this structure
     *
     * @return The building location
     */
    public org.bukkit.Location getLocation() {
        return new org.bukkit.Location(Bukkit.getWorld(location.getWorldName()), location.getX(), location.getY(), location.getZ());
    }

    public String getWorldName() {
        return location.getWorldName();
    }

    public UUID getWorldUUID() {
        return location.getWorldUUID();
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
    public Vector getRelativePosition(Vector location) {
        switch (direction) {
            case NORTH:
                return new BlockVector(
                        location.getBlockX() - this.getLocation().getX(),
                        location.getBlockY() - this.getLocation().getY(),
                        this.getLocation().getZ() - location.getBlockZ()
                );
            case SOUTH:
                return new BlockVector(
                        this.getLocation().getX() - location.getBlockX(),
                        location.getBlockY() - this.getLocation().getY(),
                        location.getBlockZ() - this.getLocation().getZ()
                );
            case EAST:
                return new Vector(
                        location.getBlockZ() - this.getLocation().getZ(),
                        location.getBlockY() - this.getLocation().getY(),
                        location.getBlockX() - this.getLocation().getX()
                );
            case WEST:
                return new Vector(
                        this.getLocation().getZ() - location.getBlockZ(),
                        location.getBlockY() - this.getLocation().getY(),
                        this.getLocation().getX() - location.getBlockX()
                );
            default:
                throw new AssertionError("Unreachable");
        }
    }

    /**
     * Will add the offset to the structure's origin, which is always the front left corner of a
     * structure.
     *
     * @param offset The offset
     * @return the location
     */
    public org.bukkit.Location translateRelativeLocation(Vector offset) {
        Vector p = WorldUtil.translateLocation(getPosition(), direction, offset.getX(), offset.getY(), offset.getZ());
        World world = Bukkit.getWorld(getWorldName());
        return new org.bukkit.Location(world, p.getBlockX(), p.getBlockY(), p.getBlockZ());
    }

    public org.bukkit.Location translateRelativeLocation(double x, double y, double z) {
        return translateRelativeLocation(new Vector(x, y, z));
    }

    @Override
    public String toString() {
        return ChatColor.RESET + "#" + ChatColor.GOLD + id + " " + ChatColor.BLUE + name + ChatColor.RESET;

    }
    
    public String stringValue() {
        return "#"+ id + " " + name;
    }

}
