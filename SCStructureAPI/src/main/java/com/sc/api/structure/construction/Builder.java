/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure.construction;

import com.google.common.base.Preconditions;
import com.sc.api.structure.Messages;
import com.settlercraft.core.model.entity.structure.Structure;
import com.settlercraft.core.model.plan.StructurePlan;
import com.settlercraft.core.model.plan.schematic.SchematicBlockData;
import com.settlercraft.core.model.plan.schematic.SchematicObject;
import com.settlercraft.core.model.world.Direction;
import com.settlercraft.core.model.world.WorldDimension;
import com.settlercraft.core.persistence.StructureService;
import com.settlercraft.core.util.WorldUtil;
import java.util.Iterator;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 *
 * @author Chingo
 */
public class Builder {

    private final StructureService structureService;

    private final Structure structure;

    public enum FOUNDATION_STRATEGY {

        DEFAULT,
        PROVIDED,
    }

    public enum FRAME_STRATEGY {
        DEFAULT,
        FANCY,
        ANIMATED_DEFAULT,
        ANIMATED_FANCY
    }

    Builder(Structure structure) {
        this.structure = structure;
        this.structureService = new StructureService();
    }

    /**
     * Will try to place the structure, the operation is succesful if the
     * structure doesn't "overlap" any other structure
     *
     * @return True if operation was succesful, otherwise false
     */
    public boolean place() {
        StructureService ss = new StructureService();
        if (ss.overlaps(structure)) {
            Player player = Bukkit.getServer().getPlayer(structure.getOwner());
            if (player != null && player.isOnline()) {
                player.sendMessage(Messages.STRUCTURE_OVERLAPS_ANOTHER);
            }
            return false;
        }
        ss.save(structure);
        return true;
    }

    /**
     * Clears all blocks at the location of the structure
     */
    public void clear() {
        Direction direction = structure.getDirection();
        Location location = structure.getLocation();
        StructurePlan sp = structure.getPlan();
        SchematicObject schematic = sp.getStructureSchematic();

        int[] mods = WorldUtil.getModifiers(direction);
        int xMod = mods[0];
        int zMod = mods[1];

        for (int y = 0; y < schematic.layers; y++) {
            for (int z = schematic.length - 1; z >= 0; z--) {
                for (int x = 0; x < schematic.width; x++) {
                    Block b;
                    if (direction == Direction.NORTH || direction == Direction.SOUTH) {
                        b = location.clone().add(x * xMod, y, z * zMod).getBlock();
                    } else {
                        b = location.clone().add(z * zMod, y, x * xMod).getBlock();
                    }
                    b.setType(Material.AIR);
                }
            }
        }
    }

    /**
     * Constructs a foundation with the given strategy,
     *
     * @param strategy The foundation strategy
     */
    public void foundation(FOUNDATION_STRATEGY strategy) {
        switch (strategy) {
            case DEFAULT:
                placeDefaultFoundation();
                break;
            case PROVIDED:
                placeProvidedFoundation();
                break;
            default:
                throw new UnsupportedOperationException("no strategy implemented for: " + strategy);
        }
    }

    /**
     * Places a foundation for the structure. If the structure doesnt have a
     * foundation schematic provided, the default strategy will be executed
     */
    public void foundation() {
        if (structure.getPlan().getFoundationSchematic() != null) {
            foundation(FOUNDATION_STRATEGY.PROVIDED);
        } else {
            foundation(FOUNDATION_STRATEGY.DEFAULT);
        }
    }

    private void placeDefaultFoundation() {
        SchematicObject schematic = structure.getPlan().getStructureSchematic();
        Direction direction = structure.getDirection();
        Location target = structure.getLocation();

        int[] mods = WorldUtil.getModifiers(direction);
        int xMod = mods[0];
        int zMod = mods[1];
        for (int z = schematic.length - 1; z >= 0; z--) {
            for (int x = 0; x < schematic.width; x++) {
                Location l;
                if (direction == Direction.NORTH || direction == Direction.SOUTH) {
                    l = target.clone().add(x * xMod, 0, z * zMod);
                } else {
                    l = target.clone().add(z * zMod, 0, x * xMod);
                }
                l.getBlock().setType(Material.COBBLESTONE);
            }
        }
    }

    private void placeProvidedFoundation() {
        SchematicObject schematic = structure.getPlan().getFoundationSchematic();
        Direction direction = structure.getDirection();
        Location target = structure.getLocation();
        Iterator<SchematicBlockData> it = structure.getPlan().getFoundationSchematic().getBlocksSorted().iterator();

        int[] mods = WorldUtil.getModifiers(direction);
        int xMod = mods[0];
        int zMod = mods[1];
        for (int z = schematic.length - 1; z >= 0; z--) {
            for (int x = 0; x < schematic.width; x++) {
                SchematicBlockData sbd = it.next();
                Location l;
                if (direction == Direction.NORTH || direction == Direction.SOUTH) {
                    l = target.clone().add(x * xMod, 0, z * zMod);
                } else {
                    l = target.clone().add(z * zMod, 0, x * xMod);
                }
                l.getBlock().setType(sbd.getMaterial());
                l.getBlock().setData(sbd.getData());
            }
        }
    }

    /**
     * Moves all entites from structure within the structure
     */
    public void evacuate() {
        Set<Entity> entities = WorldUtil.getEntitiesWithin(structure.getDimension().getStart(), structure.getDimension().getEnd());
        for (Entity e : entities) {
            if (e instanceof LivingEntity) {
                moveEntityFromLot((LivingEntity) e, 5, structure);
            }
        }
    }

    private void moveEntityFromLot(LivingEntity entity, int distance, Structure targetStructure) {
        Preconditions.checkArgument(distance > 0);

        WorldDimension dimension = targetStructure.getDimension();
        Location start = dimension.getStart();
        Location end = dimension.getEnd();
        if (entity.getLocation().distance(start) < entity.getLocation().distance(end)) {
            Location xMinus = new Location(start.getWorld(),
                    start.getBlockX() - distance, // X
                    start.getWorld().getHighestBlockYAt(start.getBlockX() - distance, entity.getLocation().getBlockZ()), // Y
                    entity.getLocation().getBlockZ() // Z
            );
            Location zMinus = new Location(start.getWorld(),
                    entity.getLocation().getBlockX(),
                    start.getWorld().getHighestBlockYAt(entity.getLocation().getBlockX() - distance, start.getBlockZ() - distance),
                    start.getBlockZ() - distance
            );
            if (entity.getLocation().distance(xMinus) < entity.getLocation().distance(zMinus)) {
                moveEntity(entity, xMinus);
            } else {
                moveEntity(entity, zMinus);
            }
        } else {
            Location xPlus = new Location(end.getWorld(),
                    end.getBlockX() + distance, // X
                    end.getWorld().getHighestBlockYAt(end.getBlockX() + distance, entity.getLocation().getBlockZ()), // Y
                    entity.getLocation().getBlockZ()
            );                                                                      // Z

            Location zPlus = new Location(end.getWorld(),
                    entity.getLocation().getBlockX(),
                    end.getWorld().getHighestBlockYAt(entity.getLocation().getBlockX() + distance, end.getBlockZ() + distance),
                    end.getBlockZ() + distance
            );
            if (entity.getLocation().distance(xPlus) < entity.getLocation().distance(zPlus)) {
                moveEntity(entity, xPlus);
            } else {
                moveEntity(entity, zPlus);
            }
        }
    }

    private void moveEntity(LivingEntity entity, Location target) {
        if (target.getBlock().getType() == Material.LAVA) {
            // Alternative?
        } else if (structureService.isOnStructure(target)) {
            moveEntityFromLot(entity, 5, structureService.getStructure(target));
        }
    }

    /**
     * Build frame for target structure
     */
    public void frame() {
        frame(FRAME_STRATEGY.DEFAULT);
    }

    /**
     * Contructs a frame for this structure
     * @param strategy The strategy to place this frame
     */
    public void frame(FRAME_STRATEGY strategy) {
        switch (strategy) {
            case DEFAULT:
                placeDefaultFrame();
            case FANCY:
                placeFancyFrame();
            case ANIMATED_DEFAULT:
                placeDefaultAnimatedFrame();
            case ANIMATED_FANCY:
                placeFancyAnimatedFrame();
            default:
                throw new UnsupportedOperationException("no strategy implemented for " + strategy);
        }
    }

    private void placeDefaultFrame() {
        SchematicObject schematic = structure.getPlan().getStructureSchematic();
        Direction direction = structure.getDirection();
        Location target = structure.getLocation();
        int[] mods = WorldUtil.getModifiers(direction);
        int xMod = mods[0];
        int zMod = mods[1];

        for (int y = 0; y < schematic.layers; y++) {
            for (int z = schematic.length - 1; z >= 0; z--) {
                for (int x = 0; x < schematic.width; x++) {
                    if (y != 0 && (y == schematic.layers - 1 || z == 0 || x == 0 || z == schematic.length - 1 || x == schematic.width - 1)) {
                        Block b;
                        if (direction == Direction.NORTH || direction == Direction.SOUTH) {
                            b = target.clone().add(x * xMod, y, z * zMod).getBlock();
                        } else {
                            b = target.clone().add(z * zMod, y, x * xMod).getBlock();
                        }
                        b.setType(Material.FENCE);
                    }
                }
            }
        }
    }

    private void placeFancyFrame() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void placeDefaultAnimatedFrame() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void placeFancyAnimatedFrame() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Instantly constructs a the structure
     */
    public void instant() {
        SchematicObject schematic = structure.getPlan().getStructureSchematic();
        Iterator<SchematicBlockData> it = schematic.getBlocksSorted().iterator();
        Direction direction = structure.getDirection();
        Location target = structure.getLocation();
        int[] mods = WorldUtil.getModifiers(direction);
        int xMod = mods[0];
        int zMod = mods[1];

        for (int y = 0; y < schematic.layers; y++) {
            for (int z = schematic.length - 1; z >= 0; z--) {
                for (int x = 0; x < schematic.width; x++) {
                    Block b;
                    if (direction == Direction.NORTH || direction == Direction.SOUTH) {
                        b = target.clone().add(x * xMod, y, z * zMod).getBlock();
                    } else {
                        b = target.clone().add(z * zMod, y, x * xMod).getBlock();
                    }
                    SchematicBlockData d = it.next();
                    b.setType(d.getMaterial());
                    b.setData(d.getData());
                }
            }
        }
    }

    /**
     * Builds the corresponding layer of this structure, whether the
     * precoditions are met or not
     *
     * @param layer The layer to build
     * @param hasFrame Determines if this should keep the fence at the borders
     */
    public void layer(int layer, boolean hasFrame) {
        StructurePlan sp = structure.getPlan();
        if (layer > sp.getStructureSchematic().layers || layer < 0) {
            throw new IndexOutOfBoundsException("layer doesnt exist");
        }

        Iterator<SchematicBlockData> it = sp.getStructureSchematic().getBlocksFromLayer(layer).iterator();
        SchematicObject schematic = sp.getStructureSchematic();
        Direction direction = structure.getDirection();
        Location target = structure.getLocation();
        int[] mods = WorldUtil.getModifiers(direction);
        int xMod = mods[0];
        int zMod = mods[1];

        for (int z = schematic.length - 1; z >= 0; z--) {
            for (int x = 0; x < schematic.width; x++) {
                Block b;
                SchematicBlockData d = it.next();
                if (hasFrame && d.getMaterial() == Material.AIR
                        && (z == 0 || z == schematic.length - 1 || x == 0 || x == schematic.width - 1)) {
                    continue;
                }
                if (direction == Direction.NORTH || direction == Direction.SOUTH) {
                    b = target.clone().add(x * xMod, layer, z * zMod).getBlock();
                } else {
                    b = target.clone().add(z * zMod, layer, x * xMod).getBlock();
                }
                b.setType(d.getMaterial());
                b.setData(d.getData());
            }
        }
    }

    public void layers(int layer, boolean hasFrame) {
        for (int i = 0; i < layer + 1; i++) {
            layer(layer, hasFrame);
        }
    }
    
}
