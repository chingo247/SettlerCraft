/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure;

import com.google.common.base.Preconditions;
import com.settlercraft.core.model.entity.structure.Structure;
import com.settlercraft.core.model.plan.StructurePlan;
import com.settlercraft.core.model.plan.schematic.SchematicBlockData;
import com.settlercraft.core.model.plan.schematic.SchematicObject;
import com.settlercraft.core.model.world.Direction;
import com.settlercraft.core.persistence.StructureService;
import com.settlercraft.core.util.WorldUtil;
import java.util.Iterator;
import java.util.Set;
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

    Builder() {
    }

    /**
     * Places an unfinished building at target location. The orientation must be
     * given
     *
     * @param player The player
     * @param direction The direction
     * @param target The target location of the structure
     * @param plan The StructurePlan of the structure that will be placed
     * @return True if the building was succesfully placed and stored in the
     * database
     */
    public boolean placeStructure(Player player, Location target, Direction direction, StructurePlan plan) {
        StructureService ss = new StructureService();
        Structure structure = new Structure(player, target, direction, plan);
        if (ss.overlaps(structure)) {
            if (player != null && player.isOnline()) {
                player.sendMessage(Messages.STRUCTURE_OVERLAPS_ANOTHER); // TODO BETTER FEEDBACK
            }
            return false;
        }
        ss.save(structure); // CLAIMS Ground (Dimension)!
        progress(structure);
        return true;
    }

    /**
     * Removes all blocks (replace them with air) that stand in the way of this
     * building.
     *
     * @param structure The structure
     */
    public void clearSiteFromBlocks(Structure structure) {
        Direction direction = structure.getDirection();
        Location location = structure.getLocation();
        StructurePlan sp = structure.getPlan();
        SchematicObject schematic = sp.getSchematic();

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
     * Creates a default foundation for given structure, this foundation will be
     * immediately generated at the buildings target location
     *
     * @param structure The structure
     */
    public void placeDefaultFoundation(Structure structure) {
        Preconditions.checkArgument(structure.getStatus() == Structure.STATE.PLACING_FOUNDATION);
        SchematicObject schematic = structure.getPlan().getSchematic();
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
        structure.setStatus(Structure.STATE.PLACING_FRAME);
    }

    /**
     * Clears the lot from entities
     *
     * @param structure
     */
    public void clearSiteFromEntities(Structure structure) {
        Preconditions.checkArgument(structure.getStatus() == Structure.STATE.CLEARING_SITE_OF_ENTITIES);
        Set<Entity> entities = WorldUtil.getEntitiesWithin(structure.getDimension().getStart(), structure.getDimension().getEnd());
        System.out.println(entities.size());
        for (Entity e : entities) {
            System.out.println("on lot!");
            if (e instanceof LivingEntity) {
                System.out.println("moving: " + e);
                moveEntityFromLot(structure, (LivingEntity) e);
            }
        }
        structure.setStatus(Structure.STATE.PLACING_FOUNDATION);
    }

    private void moveEntityFromLot(Structure structure, LivingEntity entity) {
        Location entLoc = entity.getLocation();
        int threshold = 2;
        int[] mods = WorldUtil.getModifiers(structure.getDirection());
        int xMod = mods[0];
        int zMod = mods[1];

        
    }

    public void placeFrame(Structure structure) {
        Preconditions.checkArgument(structure.getStatus() == Structure.STATE.PLACING_FRAME);
        SchematicObject schematic = structure.getPlan().getSchematic();
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
        StructureService structureService = new StructureService();
        structureService.setStatus(structure, Structure.STATE.BUILDING_IN_PROGRESS);
    }

    /**
     * Instantly constructs a structure
     *
     * @param structure The structure
     */
    public void instantBuildStructure(Structure structure) {
        Preconditions.checkArgument(structure.getStatus() != Structure.STATE.COMPLETE);
        SchematicObject schematic = structure.getPlan().getSchematic();
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
        StructureService structureService = new StructureService();
        structureService.setStatus(structure, Structure.STATE.COMPLETE);
    }

    public void progress(Structure structure) {
        switch (structure.getStatus()) {
            case BUILDING_IN_PROGRESS:
                return; // Nothing to do here
            case COMPLETE:
                return; // Structure Complete Event!
            case CLEARING_SITE_OF_BLOCKS:
                clearSiteFromBlocks(structure);
                structure.setStatus(Structure.STATE.CLEARING_SITE_OF_ENTITIES);
            case CLEARING_SITE_OF_ENTITIES:
                clearSiteFromEntities(structure);
            case PLACING_FOUNDATION:
                placeDefaultFoundation(structure);
            case PLACING_FRAME:
                placeFrame(structure);
                break;
            default:
                throw new AssertionError("Unreachable");
        }
    }

    /**
     * Builds the corresponding layer of this structure, whether the
     * precoditions are met or not
     *
     * @param structure The structure
     * @param layer The layer to build
     * @param keepFrame Determines if this should keep the fence at the borders
     */
    public void buildLayer(Structure structure, int layer, boolean keepFrame) {
        StructurePlan sp = structure.getPlan();
        if (layer > sp.getSchematic().layers || layer < 0) {
            throw new IndexOutOfBoundsException("layer doesnt exist");
        }

        Iterator<SchematicBlockData> it = sp.getSchematic().getBlocksFromLayer(layer).iterator();
        SchematicObject schematic = sp.getSchematic();
        Direction direction = structure.getDirection();
        Location target = structure.getLocation();
        int[] mods = WorldUtil.getModifiers(direction);
        int xMod = mods[0];
        int zMod = mods[1];

        for (int z = schematic.length - 1; z >= 0; z--) {
            for (int x = 0; x < schematic.width; x++) {
                Block b;
                SchematicBlockData d = it.next();
                if (keepFrame && d.getMaterial() == Material.AIR
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
    
    public void buildToLayer(Structure structure, int layer, boolean keepFrame) {
        for(int i = 0; i < layer+1; i++) {
            buildLayer(structure, layer, keepFrame);
        }
    }
}
