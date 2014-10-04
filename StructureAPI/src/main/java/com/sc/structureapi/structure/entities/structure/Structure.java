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
package com.sc.structureapi.structure.entities.structure;

import com.google.common.base.Preconditions;
import com.sc.structureapi.bukkit.events.StructureCreateEvent;
import com.sc.structureapi.bukkit.events.StructureStateChangeEvent;
import com.sc.structureapi.exception.StructureDataException;
import com.sc.structureapi.exception.StructureException;
import com.sc.structureapi.persistence.StructureService;
import com.sc.structureapi.structure.StructureAPI;
import static com.sc.structureapi.structure.StructureAPI.overlapsStructures;
import com.sc.structureapi.structure.StructureAPIModule;
import com.sc.structureapi.structure.entities.world.Dimension;
import com.sc.structureapi.structure.entities.world.Direction;
import static com.sc.structureapi.structure.entities.world.Direction.EAST;
import static com.sc.structureapi.structure.entities.world.Direction.NORTH;
import static com.sc.structureapi.structure.entities.world.Direction.SOUTH;
import static com.sc.structureapi.structure.entities.world.Direction.WEST;
import com.sc.structureapi.structure.entities.world.Location;
import com.sc.structureapi.structure.plan.StructurePlan;
import com.sc.structureapi.structure.schematic.Schematic;
import com.sc.structureapi.structure.schematic.SchematicManager;
import com.sc.structureapi.util.SchematicUtil;
import static com.sc.structureapi.util.SchematicUtil.calculateDimension;
import com.sc.structureapi.util.WorldUtil;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.dom4j.DocumentException;

/**
 *
 * @author Chingo
 */
@Table(name = "SC_STRUCTURE")
@Entity
public class Structure implements Serializable {

    private static final String PREFIX = "SCREG#";

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
    private Structure(World world, Vector pos, Direction direction, Schematic schematic) {
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
    }

    /**
     * Creates a structure.
     *
     * @param plan The StructurePlan
     * @param world The world
     * @param pos The position
     * @param direction The direction / direction
     * @return The structure that was placed
     */
    public static Structure create(StructurePlan plan, World world, Vector pos, Direction direction) {
        return create(null, plan, world, pos, direction);
    }

    /**
     * Creates a structure
     *
     * @param player The player, which will also be added as an owner of the structure
     * @param plan The StructurePlan
     * @param world The world
     * @param pos The position
     * @param direction The direction / direction
     * @return The structure that was placed
     */
    public static Structure create(Player player, StructurePlan plan, World world, Vector pos, Direction direction) {
        // Retrieve schematic
        Schematic schematic;
        try {
            schematic = SchematicManager.getInstance().getSchematic(plan.getSchematic());
        } catch (DataException | IOException ex) {
            java.util.logging.Logger.getLogger(StructureAPI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            return null;
        }

        Dimension dimension = SchematicUtil.calculateDimension(schematic, pos, direction);

        // Check if structure overlapsStructures another structure
        if (overlapsStructures(world, dimension)) {
            if (player != null) {
                player.sendMessage(ChatColor.RED + "Structure overlaps another structure");
            }
            return null;
        }

        // Create structure
        Structure structure = new Structure(world, pos, direction, schematic);
        structure.setName(plan.getName() == null ? "Structure #" + structure.getId() : plan.getName());
        structure.setPrice(plan.getPrice());

        // Save structure
        StructureService ss = new StructureService();
        structure.setStructureRegionId(PREFIX + structure.getId());
        structure = ss.save(structure);
        if (player != null) {
            try {
                StructureAPI.makeOwner(player, structure);
            } catch (StructureException ex) {
                Logger.getLogger(Structure.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        try {
            final File STRUCTURE_DIR = structure.getFolder();
            if (!STRUCTURE_DIR.exists()) {
                STRUCTURE_DIR.mkdirs();
            }

            File config = plan.getConfigXML();
            File schematicFile = plan.getSchematic();

            FileUtils.copyFile(config, new File(STRUCTURE_DIR, "StructurePlan.xml"));
            FileUtils.copyFile(schematicFile, new File(STRUCTURE_DIR, schematicFile.getName()));
        } catch (IOException ex) {
            if (player != null) {
                player.sendMessage(ChatColor.RED + "Couldn't copy data for structure");
            }
            java.util.logging.Logger.getLogger(StructureAPI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            return null;
        }

        ProtectedRegion structureRegion = StructureAPI.claimGround(player, structure);
        if (structureRegion == null) {
            structure.getFolder().delete();
            ss.delete(structure);
            if (player != null) {
                player.sendMessage(ChatColor.RED + "Failed to claim region for structure");
            } else {
                System.out.println("[SettlerCraft]: Failed to claim region for structure");
            }

            return null;
        }

        Bukkit.getPluginManager().callEvent(new StructureCreateEvent(structure));
        return structure;
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

    public State getState() {
        return state;
    }

    public void setState(State newState) {
        if (this.state != newState) {
            State oldState = state;
            this.state = newState;
            Bukkit.getPluginManager().callEvent(new StructureStateChangeEvent(this, oldState));
        }
    }

    public Long getSchematicChecksum() throws IOException, StructureDataException, DocumentException {
        long checksum = FileUtils.checksumCRC32(getSchematicFile());
        return checksum;
    }

    public StructurePlan getPlan() throws StructureDataException, DocumentException, IOException {
        File file = getConfig();
        StructurePlan plan = new StructurePlan(file);
        plan.load();
        return plan;
    }

    public File getConfig() {
        return new File(getFolder(), "StructurePlan.xml");
    }

    public File getSchematicFile() throws StructureDataException, DocumentException, IOException {
        StructurePlan plan = getPlan();
        return plan.getSchematic();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public File getFolder() {
        return new File(StructureAPIModule.getInstance().getStructureDataFolder(), getWorldName() + "//" + id);
    }

    public boolean isOwner(Player player) {
        for (PlayerOwnership pos : ownerships) {
            if (pos.getUUID().equals(player.getUniqueId())) {
                return true;
            }
        }
        return false;
    }

    public boolean isMember(Player player) {
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

    public boolean addOwner(Player player) {
        return ownerships.add(new PlayerOwnership(player, this));
    }

    public boolean removeOwner(Player player) {
        Iterator<PlayerOwnership> it = ownerships.iterator();
        while (it.hasNext()) {
            PlayerOwnership pm = it.next();
            if (pm.getUUID().equals(player.getUniqueId())) {
                it.remove();
                return true;
            }

        }
        return false;
    }

    public boolean addMember(Player player) {
        return memberships.add(new PlayerMembership(player.getUniqueId(), this));
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

    public void setPrice(Double refundValue) {
        this.refundValue = refundValue;
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
    public Vector getRelativePosition(com.sk89q.worldedit.util.Location location) {
        switch (direction) {
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
        return "#" + ChatColor.GOLD + id + " " + ChatColor.BLUE + name + ChatColor.RESET;

    }

}
