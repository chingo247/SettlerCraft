/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.plugin;

import com.cc.plugin.api.menu.MenuManager;
import com.cc.plugin.api.menu.MenuSlot;
import com.cc.plugin.api.menu.ShopCategoryMenu;
import com.sc.api.structure.RestoreService;
import com.sc.api.structure.StructureManager;
import com.sc.api.structure.StructurePlanManager;
import com.sc.api.structure.entity.plan.StructurePlan;
import com.sc.api.structure.entity.plan.StructureSchematic;
import com.sc.api.structure.plan.StructurePlanLoader;
import com.sc.commands.ConstructionCommandExecutor;
import com.sc.commands.SettlerCraftCommandExecutor;
import com.sc.commands.StructureCommandExecutor;
import com.sc.listener.PlayerListener;
import com.sc.listener.PluginListener;
import com.sc.listener.ShopListener;
import com.sc.listener.StructureListener;
import com.sc.persistence.HSQLServer;
import com.sc.util.CuboidUtil;
import com.sk89q.worldedit.CuboidClipboard;
import java.io.File;
import java.util.Iterator;
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
    private boolean plansLoaded;
    private double refundPercentage;
    private boolean menuEnabled;
    private boolean shopEnabled;

    @Override
    public void onEnable() {

        if (Bukkit.getPluginManager().getPlugin("WorldEdit") == null) {
            System.out.println("[SettlerCraft]: WorldEdit NOT FOUND!!! Disabling...");
            this.setEnabled(false);
            return;
        }
        if (Bukkit.getPluginManager().getPlugin("AsyncWorldEdit") == null) {
            System.out.println("[SettlerCraft]: AsyncWorldEdit NOT FOUND!!! Disabling...");
            this.setEnabled(false);
            return;
        }

        if (Bukkit.getPluginManager().getPlugin("WorldGuard") == null) {
            System.out.println("[SettlerCraft]: WorldGuard NOT FOUND!!! Disabling...");
            this.setEnabled(false);
            return;
        }

        if (Bukkit.getPluginManager().getPlugin("HolographicDisplays") == null) {
            System.out.println("[SettlerCraft]: HolographicDisplays NOT FOUND!!! Disabling...");
            this.setEnabled(false);
            return;
        }
        if (!HSQLServer.getInstance().isRunning()) {
            HSQLServer.getInstance().start();
            System.out.println("[SettlerCraft]: Checking for invalid structures");
            new RestoreService().restore(); // only execute on server start, not on reload!
        }

        loadStructures(FileUtils.getFile(getDataFolder(), "Structures"));
        setupMenu();
        setupPlanShop();
        Bukkit.broadcastMessage(ChatColor.GOLD + "[SettlerCraft]: " + ChatColor.RESET + "Structure plans loaded");
        StructureManager.getInstance().init();

        Bukkit.getPluginManager().registerEvents(new StructureListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new ShopListener(), this);
        Bukkit.getPluginManager().registerEvents(new PluginListener(), this);
        initConfig();

        getCommand("sc").setExecutor(new SettlerCraftCommandExecutor(this));
        getCommand("cst").setExecutor(new ConstructionCommandExecutor(this));
        getCommand("stt").setExecutor(new StructureCommandExecutor());

    }

    public static SettlerCraft getSettlerCraft() {
        return (SettlerCraft) Bukkit.getPluginManager().getPlugin("SettlerCraft");
    }

    /**
     * Stops all processes
     */
    public void stop() {
        StructureManager.getInstance().shutdown();
        MenuManager.getInstance().clearVisitors();
    }

    private void initConfig() {
        try {
            this.menuEnabled = getConfig().getBoolean("menus.planmenu");
            this.shopEnabled = getConfig().getBoolean("menus.planshop");
            this.refundPercentage = getConfig().getDouble("structure.refund");
            if (refundPercentage < 0) {
                throw new SettlerCraftException("refund node in config was negative");
            }
        } catch (SettlerCraftException ex) {
            java.util.logging.Logger.getLogger(SettlerCraft.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean isPlanMenuEnabled() {
        return menuEnabled;
    }

    public boolean isPlanShopEnabled() {
        return shopEnabled;
    }

    public double getRefundPercentage() {
        return refundPercentage;
    }

    public boolean isPlansLoaded() {
        return plansLoaded;
    }

    /**
     * Loads structures from a directory
     *
     * @param structureDirectory The directory to search
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
        ShopCategoryMenu planMenu = new ShopCategoryMenu(PLAN_MENU_NAME, true, true);

        // Add Plan Categories
        planMenu.addCategory(0, new ItemStack(Material.NETHER_STAR), "All");
        planMenu.addCategory(1, new ItemStack(Material.WORKBENCH), "General");
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
        planMenu.accepts("Plan");
        planMenu.setChooseDefaultCategory(true);

        Iterator<StructurePlan> pit = StructurePlanManager.getInstance().getPlans().iterator();

        while (pit.hasNext()) {
            StructurePlan plan = pit.next();
            ItemStack is = new ItemStack(Material.PAPER);
            MenuSlot slot = new MenuSlot(is, plan.getDisplayName(), MenuSlot.MenuSlotType.ITEM);
            CuboidClipboard cc = StructurePlanManager.getInstance().getClipBoard(plan.getChecksum());
            int size = CuboidUtil.count(cc, true);
            String sizeString = valueString(size);
            slot.setData("Size", cc.getLength() + "x" + cc.getWidth() + "x" + cc.getHeight(), ChatColor.GOLD);
            slot.setData("Blocks", sizeString, ChatColor.GOLD);
            slot.setData("Type", "Plan", ChatColor.GOLD);
            slot.setData("Id", plan.getId(), ChatColor.GOLD);
            planMenu.addItem(slot, plan.getCategory()); //Dont fill in these slots
        }

        MenuManager.getInstance().register(planMenu);
    }

    private void setupPlanShop() {
        ShopCategoryMenu planShop = new ShopCategoryMenu(PLANSHOP, true, true);

        // Add Plan Categories
        planShop.addCategory(0, new ItemStack(Material.NETHER_STAR), "All");
        planShop.addCategory(1, new ItemStack(Material.WORKBENCH), "General");
        planShop.addCategory(2, new ItemStack(Material.ANVIL), "Industry", "Industrial", "Industries");
        planShop.addCategory(3, new ItemStack(Material.BED), "Residency", "Residence", "Residencial", "Houses", "House");
        planShop.addCategory(4, new ItemStack(Material.GOLD_INGOT), "Economy", "Economical", "Shops", "Shop", "Market", "Markets");
        planShop.addCategory(5, new ItemStack(Material.QUARTZ), "Temples", "Temple", "Church", "Sacred", "Holy");
        planShop.addCategory(6, new ItemStack(Material.SMOOTH_BRICK), "Fortifications", "Fort", "Fortification", "Wall", "Fortress", "Fortresses", "Keep", "Castle", "Castles", "Military");
        planShop.addCategory(7, new ItemStack(Material.IRON_SWORD), "Dungeons&Arenas", "Arena", "Arenas", "Dungeon", "Dungeons");
        planShop.addCategory(8, new ItemStack(Material.BUCKET), "Misc");
        planShop.addActionSlot(9, new ItemStack(Material.BED_BLOCK), "Previous");
        planShop.addActionSlot(17, new ItemStack(Material.BED_BLOCK), "Next");
        planShop.setLocked(10, 11, 12, 13, 14, 15, 16); // //Dont fill in these slots
        planShop.setDefaultCategory("All");
        planShop.accepts("Plan");
        planShop.setChooseDefaultCategory(true);

        Iterator<StructurePlan> pit = StructurePlanManager.getInstance().getPlans().iterator();
        while (pit.hasNext()) {
            StructurePlan plan = pit.next();
            ItemStack is = new ItemStack(Material.PAPER);
            MenuSlot slot = new MenuSlot(is, plan.getDisplayName(), MenuSlot.MenuSlotType.ITEM);
            StructureSchematic ss = StructurePlanManager.getInstance().getSchematic(plan.getChecksum());
            int size = ss.getBlocks();
            String sizeString = valueString(size);
            slot.setData("Size", ss.getLength() + "x" + ss.getWidth() + "x" + ss.getHeight(), ChatColor.GOLD);
            slot.setData("Type", "Plan", ChatColor.GOLD);
            slot.setData("Blocks", sizeString, ChatColor.GOLD);
            slot.setData("Id", plan.getId(), ChatColor.GOLD);
            planShop.addItem(slot, plan.getCategory(), plan.getPrice());
        }

        MenuManager.getInstance().register(planShop);
    }

    /**
     * Creates a string from a value e.g. value > 1E3 = value/1E3 + "K" e.g. value > 1E6 = value/1E6
     * + "M"
     *
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
