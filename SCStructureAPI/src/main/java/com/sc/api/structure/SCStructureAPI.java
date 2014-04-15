/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure;

import com.google.common.base.Preconditions;
import com.sc.api.structure.exception.InvalidStructurePlanException;
import com.sc.api.structure.listeners.PlayerListener;
import com.sc.api.structure.listeners.StructurePlanListener;
import com.sc.api.structure.recipe.tools.Recipes;
import com.settlercraft.core.SettlerCraftAPI;
import com.settlercraft.core.model.entity.structure.Structure;
import com.settlercraft.core.model.entity.structure.Structure.STATE;
import com.settlercraft.core.model.plan.StructurePlan;
import com.settlercraft.core.model.plan.schematic.SchematicBlockData;
import com.settlercraft.core.model.plan.schematic.SchematicObject;
import com.settlercraft.core.persistence.StructureService;
import com.settlercraft.core.util.LocationUtil;
import com.settlercraft.recipe.CShapedRecipe;
import java.io.File;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Chingo
 */
public class SCStructureAPI extends SettlerCraftAPI {

    public SCStructureAPI() {
        super("SCStructureAPI");
    }

    @Override
    public void setupListeners(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(new StructurePlanListener(plugin), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), plugin);
    }

    @Override
    public void setupRecipes(JavaPlugin plugin) {
        for(CShapedRecipe r : Recipes.getRecipes()) {
            plugin.getServer().addRecipe(r.getRecipe());
        }
    }

    /**
     * Read and loads all structures in the datafolder of the plugin
     * @param baseFolder The datafolder of the plugin
     */
    private void loadStructures(File baseFolder) {
        if (!baseFolder.exists()) {
            baseFolder.mkdir();
        }
        try {
            StructurePlanLoader spLoader = new StructurePlanLoader();
            spLoader.load(baseFolder);
        } catch (InvalidStructurePlanException ex) {
            Logger.getLogger(SCStructureAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void init(JavaPlugin plugin) {
        loadStructures(plugin.getDataFolder().getAbsoluteFile());
        setupListeners(plugin);
        setupRecipes(plugin);
    }

    /**
     * Removes all blocks (replace with air) that stand in the way of this building.
     *
     * @param structure THe structure
     */
    public static void clearSiteFromBlocks(Structure structure) {
        Preconditions.checkArgument(structure.getStatus() == Structure.STATE.CLEARING_SITE_OF_BLOCKS);
        LocationUtil.DIRECTION direction = structure.getDirection();
        Location target = structure.getDimensionStartLocation();
        StructurePlan sp = structure.getPlan();
        SchematicObject schematic = sp.getSchematic();

        int[] mods = LocationUtil.getModifiers(direction);
        int xMod = mods[0];
        int zMod = mods[1];

        for (int y = 0; y < schematic.layers; y++) {
            for (int z = schematic.length - 1; z > 0; z--) {
                for (int x = 0; x < schematic.width; x++) {
                    Block b;
                    if (direction == LocationUtil.DIRECTION.NORTH || direction == LocationUtil.DIRECTION.SOUTH) {
                        b = target.clone().add(x * xMod, y, z * zMod).getBlock();
                    } else {
                        b = target.clone().add(z * zMod, y, x * xMod).getBlock();
                    }
                    b.setType(Material.AIR);
                }
            }
        }
        structure.setStatus(STATE.CLEARING_SITE_OF_ENTITIES);
    }

    /**
     * Creates a default foundation for given structure, this foundation will be immediately
     * generated at the buildings target location
     *
     * @param structure The structure
     */
    public static void placeDefaultFoundation(Structure structure) {
        Preconditions.checkArgument(structure.getStatus() == STATE.PLACING_FOUNDATION);
        SchematicObject schematic = structure.getPlan().getSchematic();
        LocationUtil.DIRECTION direction = structure.getDirection();
        Location target = structure.getStructureStartLocation();

        int[] mods = LocationUtil.getModifiers(direction);
        int xMod = mods[0];
        int zMod = mods[1];
        for (int z = schematic.length - 1; z >= 0; z--) {
            for (int x = 0; x < schematic.width; x++) {
                Location l;
                if (direction == LocationUtil.DIRECTION.NORTH || direction == LocationUtil.DIRECTION.SOUTH) {
                    l = target.clone().add(x * xMod, 0, z * zMod);
                } else {
                    l = target.clone().add(z * zMod, 0, x * xMod);
                }
                l.getBlock().setType(Material.COBBLESTONE);
            }
        }
        structure.setStatus(STATE.PLACING_FRAME);
    }

    /**
     * Clears the lot from entities
     *
     * @param structure
     */
    public static void clearSiteFromEntities(Structure structure) {
        Preconditions.checkArgument(structure.getStatus() == STATE.CLEARING_SITE_OF_ENTITIES);
        Set<Entity> entities = LocationUtil.getEntitiesWithin(structure.getStructureStartLocation(), structure.getStructureEndLocation());
        System.out.println(entities.size());
        for (Entity e : entities) {
            System.out.println("on lot!");
            if (e instanceof LivingEntity) {
                System.out.println("moving: " + e);
                moveEntityFromLot(structure, (LivingEntity) e);
            }
        }
        structure.setStatus(STATE.PLACING_FOUNDATION);
    }

    private static void moveEntityFromLot(Structure structure, LivingEntity entity) {
        Location entLoc = entity.getLocation();
        int threshold = 2;
        int[] mods = LocationUtil.getModifiers(structure.getDirection());
        int xMod = mods[0];
        int zMod = mods[1];
        
        System.out.println("xMod: " + xMod + " zMod: " + zMod);
        System.out.println(structure.getDimensionStartLocation() + " : " + structure.getDimensionEndLocation());
        
        // TODO Improve target location
        Location l;
        if (structure.getDirection() == LocationUtil.DIRECTION.NORTH || structure.getDirection() == LocationUtil.DIRECTION.SOUTH) {
            if (entLoc.distance(structure.getDimensionStartLocation()) < entLoc.distance(structure.getDimensionEndLocation())) {
                l = structure.getDimensionStartLocation().clone().add(xMod * -threshold, 0, zMod * -threshold);
            } else {
                l = structure.getDimensionEndLocation().clone().add(xMod * threshold, 0, zMod * threshold);
            }
        } else {
            if (entLoc.distance(structure.getDimensionStartLocation()) < entLoc.distance(structure.getDimensionEndLocation())) {
                l = structure.getDimensionStartLocation().clone().add(zMod * -threshold, 0, xMod * -threshold);
            } else {
                l = structure.getDimensionEndLocation().clone().add(zMod * threshold, 0, xMod * threshold);
            }
        }
        
        StructureService ss = new StructureService();
        Structure s = ss.getStructure(l);
        if (s != null && !s.getId().equals(structure.getId())) {
            moveEntityFromLot(s, entity);
        } else {
            Location target = l.getWorld().getHighestBlockAt(l.getBlockX(), l.getBlockY()).getLocation();
            if (target.getBlock().getType() == Material.LAVA) {
                //TODO moveToAlternative location
                System.out.println("TARGET BLOCK WAS LAVA!!!");
            } else {
                entity.teleport(target.clone().add(0, 1, 0));
                System.out.println("target: " + l);
                l.getBlock().setType(Material.STONE);
            }
        }
    }

    public static void placeFrame(Structure structure) {
        Preconditions.checkArgument(structure.getStatus() == STATE.PLACING_FRAME);
        SchematicObject schematic = structure.getPlan().getSchematic();
        Preconditions.checkArgument(schematic.layers >= 2);
        LocationUtil.DIRECTION direction = structure.getDirection();
        Location target = structure.getStructureStartLocation();
        int[] mods = LocationUtil.getModifiers(direction);
        int xMod = mods[0];
        int zMod = mods[1];

        for (int y = 0; y < schematic.layers; y++) {
            for (int z = schematic.length - 1; z >= 0; z--) {
                for (int x = 0; x < schematic.width; x++) {
                    if (y != 0 && (y == schematic.layers - 1 || z == 0 || x == 0 || z == schematic.length - 1 || x == schematic.width - 1)) {
                        Block b;
                        if (direction == LocationUtil.DIRECTION.NORTH || direction == LocationUtil.DIRECTION.SOUTH) {
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
        structureService.setStatus(structure, STATE.BUILDING_IN_PROGRESS);
//        structure.setStatus(STATE.BUILDING_IN_PROGRESS);
    }

    /**
     * Places an unfinished building at target location. The orientation must be given
     *
     * @param structure
     */
    public static void placeStructure(Structure structure) {
        StructureService ss = new StructureService();
        if (ss.overlaps(structure)) {
            Player player = Bukkit.getServer().getPlayer(structure.getOwner());
            if (player != null && player.isOnline()) {
                player.sendMessage(ChatColor.RED + "[SC]: Structure overlaps"); // TODO BETTER FEEDBACK
            }
            return;
        }
        ss.save(structure); // CLAIMS Ground (Dimension)!
        progress(structure);
    }

    /**
     * Instantly constructs a structure
     *
     * @param structure The structure
     */
    public static void instantBuildStructure(Structure structure) {
        Preconditions.checkArgument(structure.getStatus() != STATE.COMPLETE);
        SchematicObject schematic = structure.getPlan().getSchematic();
        Preconditions.checkArgument(schematic.layers >= 2);
        Iterator<SchematicBlockData> it = schematic.getBlocksSorted().iterator();
        LocationUtil.DIRECTION direction = structure.getDirection();
        Location target = structure.getStructureStartLocation();
        int[] mods = LocationUtil.getModifiers(direction);
        int xMod = mods[0];
        int zMod = mods[1];

        for (int y = 0; y < schematic.layers; y++) {
            for (int z = schematic.length - 1; z >= 0; z--) {
                for (int x = 0; x < schematic.width; x++) {
                    Block b;
                    if (direction == LocationUtil.DIRECTION.NORTH || direction == LocationUtil.DIRECTION.SOUTH) {
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
        structureService.setStatus(structure, STATE.COMPLETE);
    }

    public static void progress(Structure structure) {
        switch (structure.getStatus()) {
            case BUILDING_IN_PROGRESS:
                return; // Nothing to do here
            case COMPLETE:
                return; // Structure Complete Event!
            case CLEARING_SITE_OF_BLOCKS:
                SCStructureAPI.clearSiteFromBlocks(structure);
            case CLEARING_SITE_OF_ENTITIES:
                SCStructureAPI.clearSiteFromEntities(structure);
            case PLACING_FOUNDATION:
                SCStructureAPI.placeDefaultFoundation(structure);
            case PLACING_FRAME:
                SCStructureAPI.placeFrame(structure);
                break;
            default:
                throw new AssertionError("Unreachable");
        }
    }

}
