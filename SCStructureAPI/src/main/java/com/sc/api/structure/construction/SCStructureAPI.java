/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure.construction;

import com.sc.api.structure.construction.builders.StructureBuilder;
import com.google.common.base.Preconditions;
import com.sc.api.structure.commands.StructureCommandExecutor;
import com.sc.api.structure.exception.InvalidStructurePlanException;
import com.sc.api.structure.exception.NoStructureSchematicNodeException;
import com.sc.api.structure.exception.SchematicFileNotFoundException;
import com.sc.api.structure.io.StructurePlanLoader;
import com.sc.api.structure.listeners.PlayerListener;
import com.sc.api.structure.listeners.StructureListener;
import com.sc.api.structure.listeners.StructurePlanListener;
import com.sc.api.structure.recipe.Recipes;
import com.settlercraft.core.SettlerCraftModule;
import com.settlercraft.core.model.entity.structure.Structure;
import com.settlercraft.core.model.world.WorldDimension;
import com.settlercraft.core.persistence.StructureService;
import com.settlercraft.core.util.WorldUtil;
import com.settlercraft.recipe.CShapedRecipe;
import java.io.File;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Chingo
 */
public class SCStructureAPI extends SettlerCraftModule {

    public static final String MAIN_PLUGIN_NAME = "SettlerCraft";
    public static final String ALIAS = "[STRUC]";
    
    public static Plugin getSettlerCraft() {
        return Bukkit.getPluginManager().getPlugin(MAIN_PLUGIN_NAME);
    }

    public SCStructureAPI() {
        super("SCStructureAPI");
    }

    @Override
    protected void setupListeners(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(new StructurePlanListener(plugin), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new StructureListener(), plugin);
    }

    @Override
    protected void setupRecipes(JavaPlugin plugin) {
        for (CShapedRecipe r : Recipes.getRecipes()) {
            plugin.getServer().addRecipe(r.getRecipe());
        }
    }
    
   

    @Override
    public void init(JavaPlugin plugin) {
        loadStructures(plugin.getDataFolder().getAbsoluteFile());
        setupListeners(plugin);
        setupRecipes(plugin);
        plugin.getCommand("scs").setExecutor(new StructureCommandExecutor());
    }
    
    public static void reloadPlans() {
        loadStructures(getSettlerCraft().getDataFolder().getAbsoluteFile());
    }

   
    public static StructureBuilder build(Structure structure) {
        return new StructureBuilder(structure);
    }

    /**
     * Moves all entities from structure within the structure to the borders of this structure if
     * the new location is on another structure, the entity will be moved again
     *
     * @param structure The structure
     */
    public static void evacuate(Structure structure) {
        Set<Entity> entities = WorldUtil.getEntitiesWithin(structure.getDimension().getStart(), structure.getDimension().getEnd());
        for (Entity e : entities) {
            if (e instanceof LivingEntity) {
                moveEntityFromLot((LivingEntity) e, 5, structure);
            }
        }
    }

    /**
     * Moves the given entity from the given target structure, the entity will be moved beyond the
     * closest border. If the entity isnt within the structure, no actions will be taken. If the new
     * location of the entity is a location on another structure, then this method will call itself
     * recursively To prevent a stackoverflow the distance value will be doubled each recursive call
     *
     * @param entity
     * @param distance
     * @param targetStructure
     */
    public static void moveEntityFromLot(LivingEntity entity, int distance, Structure targetStructure) {
        Preconditions.checkArgument(distance > 0);
        if (targetStructure.isOnLot(entity.getLocation())) {
            return;
        }

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
                moveEntity(entity, distance, xMinus);
            } else {
                moveEntity(entity, distance, zMinus);
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
                moveEntity(entity, distance, xPlus);
            } else {
                moveEntity(entity, distance, zPlus);
            }
        }
    }

    public static PlayerAction player(Player player) {
        return new PlayerAction(player);
    }
    
    /**
     * Read and loads all structures in the datafolder of the plugin
     *
     * @param baseFolder The datafolder of the plugin
     */
    private static void loadStructures(File baseFolder) {
        if (!baseFolder.exists()) {
            baseFolder.mkdir();
        }
        try {
            StructurePlanLoader spLoader = new StructurePlanLoader();
            spLoader.load(baseFolder);
        } catch (InvalidStructurePlanException | SchematicFileNotFoundException | NoStructureSchematicNodeException ex) {
            Logger.getLogger(SCStructureAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void moveEntity(LivingEntity entity, int distance, Location target) {
        StructureService structureService = new StructureService();
        if (target.getBlock().getType() == Material.LAVA) {
            // Alternative?
            // TODO use alternative

        } else if (structureService.isOnStructure(target)) {
            moveEntityFromLot(entity, distance * 2, structureService.getStructure(target));
        }
    }

}
