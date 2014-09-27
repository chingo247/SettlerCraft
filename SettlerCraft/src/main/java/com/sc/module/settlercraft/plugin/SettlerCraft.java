/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.module.settlercraft.plugin;

import com.sc.module.menuapi.menus.menu.CategoryMenu;
import com.sc.module.menuapi.menus.menu.MenuAPI;
import com.sc.module.settlercraft.commands.ConstructionCommandExecutor;
import com.sc.module.settlercraft.commands.SettlerCraftCommandExecutor;
import com.sc.module.settlercraft.commands.StructureCommandExecutor;
import com.sc.module.settlercraft.listener.PlanListener;
import com.sc.module.structureapi.persistence.RestoreService;
import com.sc.module.structureapi.structure.concurrent.StructurePlanItemTask;
import com.sc.module.structureapi.structure.construction.ConstructionManager;
import com.sc.module.structureapi.structure.plan.StructurePlan;
import com.sc.module.structureapi.structure.plan.StructurePlanItem;
import com.sc.module.structureapi.structure.plan.StructurePlanManager;
import com.sc.module.structureapi.structure.plan.StructurePlanManager.Callback;
import com.sc.persistence.HSQLServer;
import com.sc.plugin.ConfigProvider;
import com.sc.plugin.PermissionManager.Perms;
import com.sc.plugin.SettlerCraftException;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
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
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Chingo
 */
public class SettlerCraft extends JavaPlugin {

    private static final int INFINITE_BLOCKS = -1;
    private static final Logger LOGGER = Logger.getLogger(SettlerCraft.class);
    private static SettlerCraft instance;
    private boolean plansLoaded = false;
    public static final String PLANSHOP_NAME = "Buy & Build";
    public static final String PLAN_FOLDER = "Structures";
    public static final String MSG_PREFIX = ChatColor.GOLD + "[SettlerCraft]: ";
    private static UUID PLANSHOP;

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
            System.out.println("[SettlerCraft]: Checking for invalid structures");
            new RestoreService().restore(); // only execute on server start, not on reload!
        }
        
        

        try {
            ConfigProvider.getInstance().load();
        } catch (SettlerCraftException ex) {
            java.util.logging.Logger.getLogger(SettlerCraft.class.getName()).log(Level.SEVERE, null, ex);
        }

        
        setupPlanMenu();
        loadStructures(FileUtils.getFile(getDataFolder(), PLAN_FOLDER));
        
        
        ConstructionManager.getInstance().init();

        Bukkit.getPluginManager().registerEvents(new PlanListener(), this);

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

    public boolean isPlansLoaded() {
        return plansLoaded;
    }
    
    public CategoryMenu getPlanMenu() {
        return MenuAPI.getInstance().getMenu(instance, PLANSHOP);
    }

    /**
     * Loads structures from a directory
     *
     * @param structureDirectory The directory to search
     */
    private void loadStructures(File structureDirectory) {
        File structureFolder = new File(structureDirectory.getAbsolutePath());
        if (!structureFolder.exists()) {
            structureFolder.mkdirs();
        }
        StructurePlanManager.getInstance().load(structureFolder, new Callback() {

            @Override
            public void onComplete() {
                plansLoaded = true;
                Bukkit.broadcastMessage(ChatColor.GOLD + "[SettlerCraft]: " + ChatColor.WHITE + "Structure plans loaded");
                loadPlansIntoMenu();
            }
        });
    }

    private void setupPlanMenu() {
            CategoryMenu planMenu = MenuAPI.createMenu(instance, PLANSHOP_NAME, 54);
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
            
            PLANSHOP = planMenu.getId();
    }
    
    private static void loadPlansIntoMenu() {
        final Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        final List<StructurePlan> plans = StructurePlanManager.getInstance().getPlans();
        final int total = plans.size();
        final Iterator<StructurePlan> planIterator = plans.iterator();
        final AtomicInteger count = new AtomicInteger(0);
        final CategoryMenu planMenu = getInstance().getPlanMenu();

        while (planIterator.hasNext()) {
            final StructurePlan plan = planIterator.next();
            executor.execute(new StructurePlanItemTask(plan) {

                @Override
                public void onComplete(StructurePlanItem item) {

                    // Add item to planmenu
                    planMenu.addItem(item);
                    
                    // Enable planmenu if this was the last item
                    if (count.incrementAndGet() == total) {
                        planMenu.setEnabled(true);
                        getInstance().plansLoaded = true;
                    }
                }
            });
        }
    }

}
