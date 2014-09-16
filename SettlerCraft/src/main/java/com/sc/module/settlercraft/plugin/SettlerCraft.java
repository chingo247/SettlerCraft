/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.module.settlercraft.plugin;

import com.sc.module.menuapi.menus.menu.exception.DuplicateMenuException;
import com.sc.module.settlercraft.commands.ConstructionCommandExecutor;
import com.sc.module.settlercraft.commands.SettlerCraftCommandExecutor;
import com.sc.module.settlercraft.commands.StructureCommandExecutor;
import com.sc.module.settlercraft.listener.PlayerListener;
import com.sc.module.settlercraft.listener.PluginListener;
import com.sc.module.settlercraft.listener.ShopListener;
import com.sc.module.settlercraft.listener.StructureListener;
import com.sc.module.structureapi.menu.CategoryMenu;
import com.sc.module.structureapi.menu.MenuManager;
import com.sc.module.structureapi.plan.StructurePlan;
import com.sc.module.structureapi.plan.StructurePlanItem;
import com.sc.module.structureapi.plan.StructurePlanManager;
import com.sc.module.structureapi.plan.StructurePlanManager.Callback;
import com.sc.module.structureapi.plan.concurrent.StructurePlanItemTask;
import com.sc.persistence.HSQLServer;
import com.sc.plugin.ConfigProvider;
import com.sc.plugin.PermissionManager.Perms;
import com.sc.plugin.SettlerCraftException;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Chingo
 */
public class SettlerCraft extends JavaPlugin {

    private static final int INFINITE_BLOCKS = -1;
    private static final Logger LOGGER = Logger.getLogger(SettlerCraft.class);
    private Plugin plugin;
    private static SettlerCraft instance;
    private boolean plansLoaded = false;
    public static final String PLANSHOP = "Buy & Build";

    @Override
    public void onEnable() {
        instance = this;

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
//            System.out.println("[SettlerCraft]: Checking for invalid structures");
//            new RestoreService().restore(); // only execute on server start, not on reload!
        }

        try {
            ConfigProvider.getInstance().load();
        } catch (SettlerCraftException ex) {
            java.util.logging.Logger.getLogger(SettlerCraft.class.getName()).log(Level.SEVERE, null, ex);
        }

        setupPlanMenu();
        loadStructures(FileUtils.getFile(getDataFolder(), "Structures"));

        Bukkit.broadcastMessage(ChatColor.GOLD + "[SettlerCraft]: " + ChatColor.RESET + "Structure plans loaded");
//        StructureConstructionManager.getInstance().init();

        Bukkit.getPluginManager().registerEvents(new StructureListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new ShopListener(), this);
        Bukkit.getPluginManager().registerEvents(new PluginListener(), this);

        getCommand("sc").setExecutor(new SettlerCraftCommandExecutor(this));
        getCommand("cst").setExecutor(new ConstructionCommandExecutor(this));
        getCommand("stt").setExecutor(new StructureCommandExecutor());

        printPerms();
    }

    private void printPerms() {
        File printedFile = new File(SettlerCraft.getInstance().getDataFolder(), "SettlerCraftPermissions.yml");
        if (!printedFile.exists()) {
            try {
                printedFile.createNewFile();
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(SettlerCraft.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(printedFile);

        for (Perms perm : Perms.values()) {
            Permission p = perm.getPermission();
            config.createSection(p.getName());
            config.set(p.getName() + ".default", p.getDefault().toString());
            config.set(p.getName() + ".description", p.getDescription());
        }

        try {
            config.save(printedFile);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(SettlerCraft.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static SettlerCraft getInstance() {
        return instance;
    }

    public boolean isLoaded() {
        return plansLoaded;
    }

    /**
     * Loads structures from a directory
     *
     * @param structureDirectory The directory to search
     */
    private static void loadStructures(File structureDirectory) {
        File structureFolder = new File(structureDirectory.getAbsolutePath());
        if (!structureFolder.exists()) {
            structureFolder.mkdirs();
        }
        StructurePlanManager.getInstance().load(structureFolder, new Callback() {

            @Override
            public void onComplete() {
                loadPlansIntoMenu();
            }
        });
    }

    private static void setupPlanMenu() {
        if (MenuManager.getInstance().getMenu(PLANSHOP) == null) {
            CategoryMenu planMenu = new CategoryMenu(PLANSHOP, 54);
            planMenu.putCategorySlot(1, "General", Material.WORKBENCH);
            planMenu.putCategorySlot(2, "Industry", Material.ANVIL, "Industrial", "Industries");
            planMenu.putCategorySlot(3, "Housing", Material.BED, "Residence", "Residencial", "Houses", "House");
            planMenu.putCategorySlot(4, "Economy", Material.GOLD_INGOT, "Economical", "Shops", "Shop", "Market", "Markets");
            planMenu.putCategorySlot(5, "Temples", Material.QUARTZ, "Temple", "Church", "Sacred", "Holy");
            planMenu.putCategorySlot(6, "Fortifications", Material.SMOOTH_BRICK, "Fort", "Fortification", "Wall", "Fortress", "Fortresses", "Keep", "Castle", "Castles", "Military");
            planMenu.putCategorySlot(7, "Dungeons&Arenas", Material.IRON_SWORD);
            planMenu.putCategorySlot(8, "Misc", Material.BUCKET, "Misc");
            planMenu.putActionSlot(9, "Previous", Material.COAL_BLOCK);
            planMenu.putActionSlot(17, "Next", Material.COAL_BLOCK);
            planMenu.putLocked(10, 11, 12, 13, 14, 15, 16);
            
            try {
                MenuManager.getInstance().register(planMenu);
            } catch (DuplicateMenuException ex) {
                java.util.logging.Logger.getLogger(SettlerCraft.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }
    
    private static void loadPlansIntoMenu() {

        final Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        final List<StructurePlan> plans = StructurePlanManager.getInstance().getPlans();
        final int total = plans.size();
        final Iterator<StructurePlan> planIterator = plans.iterator();
        final AtomicInteger count = new AtomicInteger(0);
        final CategoryMenu planMenu = MenuManager.getInstance().getMenu(PLANSHOP);

        System.out.println("Loading plans into shop...");
        System.out.println("Total: " + total);

        while (planIterator.hasNext()) {
            final StructurePlan plan = planIterator.next();
            executor.execute(new StructurePlanItemTask(plan) {

                @Override
                public void onComplete(StructurePlanItem item) {

                    // Add item to planmenu
                    System.out.println("adding item");
                    planMenu.addItem(item);
                    
                    // Enable planmenu if this was the last item
                    if (count.incrementAndGet() == total) {
                        planMenu.setEnabled(true);
                        return;
                    }
                    System.out.println(count.get() + "/" + total);
                }
            });
        }
    }

}
