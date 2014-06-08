/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure;

import com.cc.plugin.api.menu.MenuManager;
import com.cc.plugin.api.menu.MenuSlot;
import com.cc.plugin.api.menu.ShopCategoryMenu;
import com.sc.api.structure.commands.ConstructionCommandExecutor;
import com.sc.api.structure.commands.SettlerCraftCommandExecutor;
import com.sc.api.structure.entity.plan.StructurePlan;
import com.sc.api.structure.entity.plan.StructureSchematic;
import com.sc.api.structure.listener.PlayerListener;
import com.sc.api.structure.listener.PluginListener;
import com.sc.api.structure.listener.ShopListener;
import com.sc.api.structure.listener.StructureListener;
import com.sc.api.structure.persistence.HSQLServer;
import com.sc.api.structure.persistence.HibernateUtil;
import com.sc.api.structure.plan.StructurePlanLoader;
import com.sc.api.structure.util.CuboidUtil;
import com.sc.plugin.commands.StructureCommandExecutor;
import com.sk89q.worldedit.CuboidClipboard;
import java.io.File;
import java.util.logging.Level;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Chingo
 */
public class SettlerCraft extends JavaPlugin {

    public static final String PLAN_MENU_NAME = "Plan Menu";
    public static final String PLANSHOP = "Buy & Build";
    private static final int INFINITE_BLOCKS = -1;
    private static final Logger LOGGER = Logger.getLogger(SettlerCraft.class);
    private Plugin plugin;
    private static SettlerCraft instance;
    private ShopCategoryMenu planMenu;
    private ShopCategoryMenu planShop;

    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("WorldEdit") == null) {
            System.out.println("[SCStructureAPI]: WorldEdit NOT FOUND!!! Disabling...");
            this.setEnabled(false);
            return;
        }
        if (Bukkit.getPluginManager().getPlugin("AsyncWorldEdit") == null) {
            System.out.println("[SCStructureAPI]: AsyncWorldEdit NOT FOUND!!! Disabling...");
            this.setEnabled(false);
            return;
        }

        if (Bukkit.getPluginManager().getPlugin("WorldGuard") == null) {
            System.out.println("[SCStructureAPI]: WorldGuard NOT FOUND!!! Disabling...");
            this.setEnabled(false);
            return;
        }
        
        if (Bukkit.getPluginManager().getPlugin("HolographicDisplays") == null) {
            System.out.println("[SCStructureAPI]: HolographicDisplays NOT FOUND!!! Disabling...");
            this.setEnabled(false);
            return;
        }
        
        
        
        
        
        HibernateUtil.addAnnotatedClasses(
                Structure.class,
                StructurePlan.class,
                ConstructionProcess.class,
                StructureSchematic.class
        );

        if (!HSQLServer.getInstance().isRunning()) {
            HSQLServer.getInstance().start();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                java.util.logging.Logger.getLogger(SettlerCraft.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("[SCStructureAPI]: Checking invalid structures");
            new RestoreService().restore(); // only execute on server start, not on reload!

        }
        
        SettlerCraft.loadStructures(FileUtils.getFile(getDataFolder(), "Structures"));
        setupMenu();
        setupPlanShop();

        Bukkit.getPluginManager().registerEvents(new StructureListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), plugin);
                Bukkit.getPluginManager().registerEvents(new ShopListener(), this);
        Bukkit.getPluginManager().registerEvents(new PluginListener(), this);
        
        getCommand("sc").setExecutor(new SettlerCraftCommandExecutor(this));
        getCommand("cst").setExecutor(new ConstructionCommandExecutor(this));
        getCommand("stt").setExecutor(new StructureCommandExecutor());
    }

    public static SettlerCraft getSettlerCraft() {
        return (SettlerCraft) Bukkit.getPluginManager().getPlugin("SettlerCraft");
    }



    public boolean isPlanMenuEnabled() {
        return getConfig().getBoolean("menus.planmenu");
    }

    public boolean isPlanShopEnabled() {
        return getConfig().getBoolean("menus.planshop");
    }
/**
 * Loads structures from a directory
 *
 * @param structureDirectory The directory to search
 * @param executor The executor
 */
public static void loadStructures(File structureDirectory) {
        File structureFolder = new File(structureDirectory.getAbsolutePath());
        if (!structureFolder.exists()) {
            structureFolder.mkdirs();
        }
        StructurePlanLoader spLoader = new StructurePlanLoader();
        spLoader.loadStructures(structureFolder);
    }
    
    private void setupMenu() {
        planMenu = new ShopCategoryMenu(PLAN_MENU_NAME, true, true);

        // Add Plan Categories
        planMenu.addCategory(0, new ItemStack(Material.NETHER_STAR), "All");
        planMenu.addCategory(1, new ItemStack(Material.WORKBENCH), "General", "Town Center");
        planMenu.addCategory(2, new ItemStack(Material.ANVIL), "Industry", "Industrial", "Industries");
        planMenu.addCategory(3, new ItemStack(Material.BED), "Residency", "Residence", "Residencial", "Houses", "House");
        planMenu.addCategory(4, new ItemStack(Material.GOLD_INGOT), "Economy", "Economical", "Shops", "Shop", "Market", "Markets");
        planMenu.addCategory(5, new ItemStack(Material.QUARTZ), "Temples", "Temple", "Church", "Sacred", "Holy");
        planMenu.addCategory(6, new ItemStack(Material.SMOOTH_BRICK), "Fortifications", "Fort", "Fortification", "Wall", "Fortress", "Fortresses", "Keep", "Castle", "Castles", "Military");
        planMenu.addCategory(7, new ItemStack(Material.IRON_SWORD), "Dungeons&Arenas", "Arena", "Arenas", "Dungeon", "Dungeons");
        planMenu.addCategory(8, new ItemStack(Material.BUCKET), "Misc");
        planMenu.addActionSlot(9, new ItemStack(Material.COAL_BLOCK), "Previous");
        planMenu.addActionSlot(17, new ItemStack(Material.COAL_BLOCK), "Next");
        planMenu.setLocked(10, 11, 12, 13, 14, 15, 16);
        planMenu.setDefaultCategory("All");
        planMenu.setChooseDefaultCategory(true);

        for (StructurePlan plan : StructurePlanManager.getInstance().getPlans()) {
            ItemStack is = new ItemStack(Material.PAPER);
            MenuSlot slot = new MenuSlot(is, plan.getDisplayName(), MenuSlot.MenuSlotType.ITEM);
            CuboidClipboard cc = StructurePlanManager.getInstance().getClipBoard(plan.getChecksum());
            int size = CuboidUtil.count(cc, true);
            String sizeString = valueString(size);
            slot.setData("Size", cc.getLength() + "x" + cc.getWidth() + "x" + cc.getHeight(), ChatColor.GOLD);
            slot.setData("Blocks", sizeString, ChatColor.GOLD);
            slot.setData("Type", "Plan", ChatColor.GOLD);
            slot.setData("Id", plan.getId(), ChatColor.GOLD);
            planMenu.addItem(slot, plan.getCategory()); // FOR FREEEE
        }

        MenuManager.getInstance().register(planMenu);
    }

    private void setupPlanShop() {
        planShop = new ShopCategoryMenu(PLANSHOP, true, true);

        // Add Plan Categories
        planShop.addCategory(0, new ItemStack(Material.NETHER_STAR), "All");
        planShop.addCategory(1, new ItemStack(Material.WORKBENCH), "General", "Town Center");
        planShop.addCategory(2, new ItemStack(Material.ANVIL), "Industry", "Industrial", "Industries");
        planShop.addCategory(3, new ItemStack(Material.BED), "Residency", "Residence", "Residencial", "Houses", "House");
        planShop.addCategory(4, new ItemStack(Material.GOLD_INGOT), "Economy", "Economical", "Shops", "Shop", "Market", "Markets");
        planShop.addCategory(5, new ItemStack(Material.QUARTZ), "Temples", "Temple", "Church", "Sacred", "Holy");
        planShop.addCategory(6, new ItemStack(Material.SMOOTH_BRICK), "Fortifications", "Fort", "Fortification", "Wall", "Fortress", "Fortresses", "Keep", "Castle", "Castles", "Military");
        planShop.addCategory(7, new ItemStack(Material.IRON_SWORD), "Dungeons&Arenas", "Arena", "Arenas", "Dungeon", "Dungeons");
        planShop.addCategory(8, new ItemStack(Material.BUCKET), "Misc");
        planShop.addActionSlot(9, new ItemStack(Material.BED_BLOCK), "Previous");
        planShop.addActionSlot(17, new ItemStack(Material.BED_BLOCK), "Next");
        planShop.setLocked(10, 11, 12, 13, 14, 15, 16);
        planShop.setDefaultCategory("All");
        planShop.setChooseDefaultCategory(true);

        for (StructurePlan plan : StructurePlanManager.getInstance().getPlans()) {
            ItemStack is = new ItemStack(Material.PAPER);
            MenuSlot slot = new MenuSlot(is, plan.getDisplayName(), MenuSlot.MenuSlotType.ITEM);
            StructureSchematic ss = StructurePlanManager.getInstance().getSchematic(plan.getChecksum());
            int size = ss.getBlocks();
            String sizeString = valueString(size);
            slot.setData("Size", ss.getLength() + "x" + ss.getWidth() + "x" + ss.getHeight(), ChatColor.GOLD);
            slot.setData("Blocks", sizeString, ChatColor.GOLD);
            planShop.addItem(slot, plan.getCategory(), plan.getPrice());
        }

        MenuManager.getInstance().register(planShop);
    }
    
    /**
     * Creates a string from a value 
     * e.g. value > 1E3 = value/1E3 + "K" 
     * e.g. value > 1E6 = value/1E6 + "M"
     * @param value
     * @return 
     */
    public static String valueString(double value) {
        if (value < 1000) {
            return String.valueOf(value);
        } else if (value < 1E6) {
            return String.valueOf(Math.round(value / 1E3)) + "K";
        } else {
            return String.valueOf(Math.round(value / 1E6)) + "M";
        }
    }

}
