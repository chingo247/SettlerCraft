/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure.construction;

import com.google.common.base.Preconditions;
import com.sc.api.menu.plugin.shop.ItemShopCategoryMenu;
import com.sc.api.menu.plugin.shop.MenuManager;
import com.sc.api.structure.commands.StructureCommandExecutor;
import com.sc.api.structure.construction.builders.StructureBuilder;
import com.sc.api.structure.exception.InvalidStructurePlanException;
import com.sc.api.structure.exception.NoStructureSchematicNodeException;
import com.sc.api.structure.exception.SchematicFileNotFoundException;
import com.sc.api.structure.io.StructurePlanLoader;
import com.sc.api.structure.listeners.InventoryListener;
import com.sc.api.structure.listeners.PlayerListener;
import com.sc.api.structure.listeners.StructureListener;
import com.sc.api.structure.listeners.StructurePlanListener;
import com.sc.api.structure.recipe.Recipes;
import com.settlercraft.core.SCVaultEconomyUtil;
import com.settlercraft.core.SettlerCraftModule;
import com.settlercraft.core.manager.StructurePlanManager;
import com.settlercraft.core.model.entity.structure.Structure;
import com.settlercraft.core.model.plan.StructurePlan;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Chingo
 */
public class SCStructureAPI extends SettlerCraftModule {

    public static final String ALIAS = "[STRUC]";
    public static final String PLAN_SHOP_NAME = "Buy & Build";
    private final StructurePlanListener spl = new StructurePlanListener(this);
    private final PlayerListener pl = new PlayerListener();
    private final StructureListener sl = new StructureListener();
    private final InventoryListener il = new InventoryListener();
    
    public static SCStructureAPI getStructureAPI() {
        return (SCStructureAPI) Bukkit.getServer().getPluginManager().getPlugin("SCStructureAPI");
    }
    
    @Override
    public void onEnable() {
        if(!SCVaultEconomyUtil.getInstance().hasEconomy()) {
            //TODO CHECK CONFIG FOR VENDOR
            System.out.println("Disabling SCStructureAPI, NO Economy FOUND");
            this.setEnabled(false);
            return;
        }
        getCommand("sc").setExecutor(new StructureCommandExecutor());
        init();
    }
    
    private void setupListeners(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(spl, this);
        Bukkit.getPluginManager().registerEvents(pl, this);
        Bukkit.getPluginManager().registerEvents(sl, this);
        Bukkit.getPluginManager().registerEvents(il, this);
    }

    private void setupRecipes(JavaPlugin plugin) {
        for (CShapedRecipe r : Recipes.getRecipes()) {
            this.getServer().addRecipe(r.getRecipe());
        }
    }

    @Override
    public void init() {
        loadStructures(getDataFolder().getAbsoluteFile());
        setupListeners(this);
        setupRecipes(this);
        initPlanShop();
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
        File structureFolder = new File(baseFolder.getAbsolutePath() + "/Structures");
        if (!structureFolder.exists()) {
            structureFolder.mkdir();
        }
        try {
            StructurePlanLoader spLoader = new StructurePlanLoader();
            spLoader.load(structureFolder);
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
    
    private void initPlanShop() {
        ItemShopCategoryMenu iscm = new ItemShopCategoryMenu("Buy & Build", true, true);
        
        // Add Plan Categories
        iscm.addCategory(0,new ItemStack(Material.NETHER_STAR), "All");
        iscm.addCategory(1, new ItemStack(Material.WORKBENCH), "General", "Town Center");
        iscm.addCategory(2, new ItemStack(Material.ANVIL),"Industry", "Industrial", "Industries");
        iscm.addCategory(3, new ItemStack(Material.BED), "Residency", "Residence", "Residencial" ,"Houses", "House");
        iscm.addCategory(4, new ItemStack(Material.GOLD_INGOT), "Economy", "Economical",  "Shops", "Shop", "Market", "Markets");
        iscm.addCategory(5, new ItemStack(Material.QUARTZ), "Temples", "Temple", "Church", "Sacred", "Holy");
        iscm.addCategory(6, new ItemStack(Material.SMOOTH_BRICK), "Castles", "Fort", "Fortification", "Wall", "Fortress", "Fortresses", "Keep");
        iscm.addCategory(7, new ItemStack(Material.IRON_SWORD), "Dungeons&Arenas", "Arena", "Arenas", "Dungeon", "Dungeons");
        iscm.addCategory(8, new ItemStack(Material.BUCKET), "Misc");
        iscm.addActionSlot(9, new ItemStack(Material.BED_BLOCK), "Previous");
        iscm.addActionSlot(17, new ItemStack(Material.BED_BLOCK), "Next");
        iscm.setLocked(10,11,12, 13,14,15,16);
        iscm.setDefaultCategory("All");
        iscm.setChooseDefaultCategory(true);
        
        for(StructurePlan plan : StructurePlanManager.getInstance().getPlans()) {
            iscm.addItem(new ItemStack(Material.PAPER), plan.getName(), plan.getCost(), plan.getCategory());
        }
        
        MenuManager.getInstance().register(iscm);
    }

}
