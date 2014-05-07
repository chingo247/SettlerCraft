/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure;

import com.sc.api.menu.plugin.shop.ItemShopCategoryMenu;
import com.sc.api.menu.plugin.shop.MenuManager;
import com.sc.api.structure.commands.StructureCommandExecutor;
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
import com.settlercraft.core.model.plan.StructurePlan;
import com.settlercraft.recipe.CShapedRecipe;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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
