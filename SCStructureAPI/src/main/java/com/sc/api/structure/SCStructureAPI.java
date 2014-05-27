/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure;

import com.sc.api.menu.plugin.shop.ItemShopCategoryMenu;
import com.sc.api.menu.plugin.shop.MenuManager;
import com.sc.api.menu.plugin.shop.MenuSlot;
import com.sc.api.structure.commands.StructureCommands;
import com.sc.api.structure.construction.progress.ConstructionEntry;
import com.sc.api.structure.construction.progress.ConstructionTask;
import com.sc.api.structure.entity.Structure;
import com.sc.api.structure.entity.StructureJob;
import com.sc.api.structure.entity.plan.StructurePlan;
import com.sc.api.structure.entity.progress.StructureProgress;
import com.sc.api.structure.entity.progress.StructureProgressLayer;
import com.sc.api.structure.entity.progress.StructureProgressMaterialResource;
import com.sc.api.structure.io.StructurePlanLoader;
import com.sc.api.structure.listeners.StructurePlanListener;
import com.sc.api.structure.persistence.HSQLServer;
import com.sc.api.structure.persistence.HibernateUtil;
import com.sc.api.structure.persistence.MemDBUtil;
import com.sc.api.structure.persistence.service.StructurePlanService;
import com.sc.api.structure.util.CuboidUtil;
import com.sc.api.structure.util.plugins.SCAsyncWorldEditUtil;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.bukkit.WorldEditAPI;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Chingo
 */
public class SCStructureAPI extends JavaPlugin {

    private boolean restrictZones = false;
    public static final String ALIAS = "[STRUC]";
    public static final String PLAN_MENU_NAME = "Plan Menu";
    private StructurePlanListener spl;
    private static ItemShopCategoryMenu planMenu;
    public static final String PLANSHOP = "Buy & Build"; // Unique Identifier for shop
    private static ItemShopCategoryMenu planShop;

    public SCStructureAPI() {

    }

    /**
     * The amount of blocks placed between each commit to the database, this
     * value will be the same as the rendering.blocks node in the AsyncWorldEdit
     * config
     *
     * @return AsyncWorldEdit's config().getInt("rendering.blocks")
     */
    public static int getSaveThreshold() {
        return SCAsyncWorldEditUtil.getAsyncWorldEditPlugin().getConfig().getInt("rendering.blocks");
    }

    public boolean isRestrictZonesEnabled() {

        return restrictZones;
    }

    public void setRestrictZonesEnabled(boolean restrictZones) {
        this.restrictZones = restrictZones;
    }

    public static WorldEditAPI getWorldEditAPI() {
        return new WorldEditAPI((WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit"));
    }

    public static SCStructureAPI getSCStructureAPI() {
        return (SCStructureAPI) Bukkit.getPluginManager().getPlugin("SCStructureAPI");
    }

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

        if (Bukkit.getPluginManager().getPlugin("SCMenu") == null) {
            System.out.println("[SCStructureAPI]: SCMenu NOT FOUND!!! Disabling...");
            this.setEnabled(false);
            return;
        }

        Bukkit.getPluginManager().registerEvents(new StructurePlanListener(), this);
        HSQLServer.getInstance().start();
        initDB();

//        ConstructionRestoreService.getInstance().restoreProgress();

        new Thread(new Runnable() {

            @Override
            public void run() {
//                System.out.println("Loading Structures");
                loadStructures(FileUtils.getFile(getDataFolder(), "Structures"));
//                System.out.println("Loading plans into menu");
                setupMenu();
                if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
                    setupPlanShop();
                }
//                System.out.println("Structures loaded");
                getCommand("sc").setExecutor(new StructureCommands());
            }
        }).start();

    }

    public static WorldEditPlugin getWorldEditPlugin() {
        return (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
    }

    private static void initDB() {
        addClassesToDB(
                Structure.class,
                StructureProgress.class,
                StructureProgressLayer.class,
                StructureProgressMaterialResource.class,
                StructurePlan.class,
                StructureJob.class,
                ConstructionEntry.class,
                ConstructionTask.class
        );
    }

    private static void addClassesToDB(Class... clazzes) {
        MemDBUtil.addAnnotatedClasses(clazzes);
        HibernateUtil.addAnnotatedClasses(clazzes);
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
        try {
            spLoader.loadStructures(structureFolder);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SCStructureAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void setupMenu() {
        planMenu = new ItemShopCategoryMenu(PLAN_MENU_NAME, true, true, new ItemShopCategoryMenu.ShopCallback() {

            @Override
            public void onItemSold(final Player buyer, final ItemStack stack, final double price) {
                ItemMeta meta = stack.getItemMeta();
                List<String> lore = new ArrayList<>();
                lore.add("[Value]: " + ChatColor.GOLD + " " + price);
                lore.add("[Type]: " + ChatColor.GOLD + "PLAN");
                meta.setLore(lore);
                stack.setItemMeta(meta);
            }
        });

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
        planMenu.addActionSlot(9, new ItemStack(Material.BED_BLOCK), "Previous");
        planMenu.addActionSlot(17, new ItemStack(Material.BED_BLOCK), "Next");
        planMenu.setLocked(10, 11, 12, 13, 14, 15, 16);
        planMenu.setDefaultCategory("All");
        planMenu.setChooseDefaultCategory(true);

        StructurePlanService planService = new StructurePlanService();
        for (StructurePlan plan : planService.getPlans()) {
            ItemStack is = new ItemStack(Material.PAPER);
            MenuSlot slot = new MenuSlot(is, plan.getDisplayName(), MenuSlot.MenuSlotType.ITEM);
            CuboidClipboard cc = plan.getSchematic();
            int size = CuboidUtil.count(cc, true);
            String sizeString = size < 999 ? String.valueOf(size) : ((Math.round(size / 1000)) + "K");

            slot.setData("Size", cc.getLength() + "x" + cc.getWidth() + "x" + cc.getHeight(), ChatColor.GOLD);
            slot.setData("Blocks", sizeString, ChatColor.GOLD);
            planMenu.addItem(slot, plan.getCategory(), 0f); // FOR FREEEE
        }

        MenuManager.getInstance().register(planMenu);
    }

    private static void setupPlanShop() {
        planShop = new ItemShopCategoryMenu(PLANSHOP, true, true, new ItemShopCategoryMenu.ShopCallback() {

            @Override
            public void onItemSold(final Player buyer, final ItemStack stack, final double price) {
                ItemMeta meta = stack.getItemMeta();
                List<String> lore = new ArrayList<>();
                lore.add("[Value]: " + ChatColor.GOLD + " " + price);
                lore.add("[Type]: " + ChatColor.GOLD + "PLAN");
                meta.setLore(lore);
                stack.setItemMeta(meta);
            }
        });

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

        StructurePlanService planService = new StructurePlanService();
        for (StructurePlan plan : planService.getPlans()) {
            ItemStack is = new ItemStack(Material.PAPER);
            MenuSlot slot = new MenuSlot(is, plan.getDisplayName(), MenuSlot.MenuSlotType.ITEM);
            CuboidClipboard cc = plan.getSchematic();
            int size = CuboidUtil.count(cc, true);
            String sizeString = size < 999 ? String.valueOf(size) : ((Math.round(size / 1000)) + "K");

            slot.setData("Size", cc.getLength() + "x" + cc.getWidth() + "x" + cc.getHeight(), ChatColor.GOLD);
            slot.setData("Blocks", sizeString, ChatColor.GOLD);
            planShop.addItem(slot, plan.getCategory(), plan.getPrice());
        }

        MenuManager.getInstance().register(planShop);
    }

}
