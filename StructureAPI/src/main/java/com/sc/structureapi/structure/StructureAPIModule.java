/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.structureapi.structure;

import com.mysema.query.jpa.hibernate.HibernateUpdateClause;
import com.sc.module.menuapi.menus.menu.CategoryMenu;
import com.sc.module.menuapi.menus.menu.MenuAPI;
import com.sc.structureapi.exception.StructureDataException;
import com.sc.structureapi.listener.PlanListener;
import com.sc.structureapi.persistence.HibernateUtil;
import com.sc.structureapi.structure.entities.structure.Structure;
import com.sc.structureapi.structure.plan.StructurePlan;
import com.sc.structureapi.structure.plan.StructurePlanItem;
import com.sc.structureapi.structure.plan.StructurePlanManager;
import com.sc.structureapi.structure.plan.holograms.StructureHologramManager;
import com.sc.structureapi.structure.plan.overview.StructureOverviewManager;
import com.sk89q.worldedit.data.DataException;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.dom4j.DocumentException;
import org.hibernate.Session;

/**
 *
 * @author Chingo
 */
public class StructureAPIModule implements Listener {

    private static final String MODULE_NAME = "StructureAPI";
    private static final String PLANSHOP_NAME = "Buy & Build";
    private static final String PLAN_FOLDER = "Plans";
    private static final Logger STRUCTURE_API_LOG = Logger.getLogger(MODULE_NAME);
    private final String MAIN_PLUGIN_NAME = "SettlerCraft";
    private final Plugin MAIN_PLUGIN = Bukkit.getPluginManager().getPlugin(MAIN_PLUGIN_NAME);
    private boolean initialized = false;
    private boolean plansLoaded = false;
    private boolean loadingplans = false;
    private static StructureAPIModule instance;

    private static UUID PLANSHOP;

    private StructureAPIModule() {
    }

    public static StructureAPIModule getInstance() {
        if (instance == null) {
            instance = new StructureAPIModule();
        }
        return instance;
    }

    public Plugin getMainPlugin() {
        return MAIN_PLUGIN;
    }

    public void initialize() {
        if (!initialized) {
            // Setup Menu
            setupMenu();

            // Generate Plans in 'SchematicToPlan' Folder
            StructurePlanManager.getInstance().generate();

            // Load Plans into menu
            loadPlans();

            // Register listener for plans, so that plans can be used
            Bukkit.getPluginManager().registerEvents(new PlanListener(), MAIN_PLUGIN);

            // Reset Structure states 
            setStates();

            // Init extra data
            boolean useHolograms = MAIN_PLUGIN.getConfig().getBoolean("structure.use-holograms");
            Plugin plugin = Bukkit.getPluginManager().getPlugin("HolographicDisplays");
            if (useHolograms && plugin != null && plugin.isEnabled()) {
                StructureOverviewManager overviewManager = new StructureOverviewManager();
                StructureHologramManager hologramManager = new StructureHologramManager();

                Bukkit.getPluginManager().registerEvents(overviewManager, plugin);
                Bukkit.getPluginManager().registerEvents(hologramManager, plugin);

                overviewManager.init();
                hologramManager.init();
            }
            
            
            initialized = true;
        }
    }
    
    

    /**
     * Loads structures from a directory
     *
     * @param structureDirectory The directory to search
     */
    private void loadPlans() {
        loadingplans = true;
        File structureFolder = StructurePlanManager.getInstance().getPlanFolder();
        if (!structureFolder.exists()) {
            structureFolder.mkdirs();
        }
        final long start = System.currentTimeMillis();
        StructurePlanManager.getInstance().load(new StructurePlanManager.Callback() {

            @Override
            public void onComplete() {
                plansLoaded = true;
                Bukkit.broadcastMessage(ChatColor.GOLD + "[SettlerCraft]: " + ChatColor.WHITE + "Structure plans loaded");

                long end = System.currentTimeMillis();

                Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[SettlerCraft]: " + ChatColor.GRAY + "Plans loaded in " + (end - start) + "ms");

                loadPlansIntoMenu();

            }
        });
    }

    /**
     * Reload the plans
     */
    public synchronized void reload()  {
        if (!loadingplans) {
            loadingplans = true;
            getPlanMenu().makeAllLeave();
            getPlanMenu().clearItems();
            
            final long start = System.currentTimeMillis();
            StructurePlanManager.getInstance().load(new StructurePlanManager.Callback() {

                @Override
                public void onComplete() {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[SettlerCraft]: " + ChatColor.GRAY + "Plans loaded in " + (System.currentTimeMillis() - start) + "ms");
                    loadPlansIntoMenu();
                }
            });
        } 
    }

    public boolean isPlansLoaded() {
        return plansLoaded;
    }

    public boolean isLoadingplans() {
        return loadingplans;
    }
    
        /**
     * Gets the datafolder for the StructureAPI or creates them if none exists
     *
     * @return The datafolder
     */
    public final File getStructureDataFolder() {
        File structureDirectory = new File(MAIN_PLUGIN.getDataFolder(), "Data//Structures");
        if (!structureDirectory.exists()) {
            structureDirectory.mkdirs();
        }
        return structureDirectory;
    }
    

    public CategoryMenu getPlanMenu() {
        return MenuAPI.getInstance().getMenu(MAIN_PLUGIN, PLANSHOP);
    }

    private void setupMenu() {
        CategoryMenu planMenu = MenuAPI.createMenu(MAIN_PLUGIN, PLANSHOP_NAME, 54);
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

    private void loadPlansIntoMenu() {
        final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        final List<StructurePlan> plans = StructurePlanManager.getInstance().getPlans();
        final int total = plans.size();
        final Iterator<StructurePlan> planIterator = plans.iterator();
        final AtomicInteger count = new AtomicInteger(0);
        final CategoryMenu planMenu = getInstance().getPlanMenu();

        final long start = System.currentTimeMillis();

        while (planIterator.hasNext()) {
            final StructurePlan plan = planIterator.next();

            executor.submit(new Runnable() {

                @Override
                public void run() {
                    try {
                        // Add item to planmenu
                        StructurePlanItem planItem = StructurePlanItem.load(plan);
                        planMenu.addItem(planItem);
                    } catch (IOException | DataException | DocumentException | StructureDataException ex) {
                        java.util.logging.Logger.getLogger(StructureAPIModule.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        count.incrementAndGet();
                        // Enable planmenu if this was the last item
                        if (count.incrementAndGet() == total) {
                            planMenu.setEnabled(true);
                            getInstance().plansLoaded = true;
                            getInstance().loadingplans = false;

                            Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[SettlerCraft]: " + ChatColor.GRAY + "Items loaded in " + (System.currentTimeMillis() - start) + "ms");

                            new Thread(new Runnable() {

                                @Override
                                public void run() {
                                    executor.shutdownNow();
                                    try {
                                        executor.awaitTermination(1000, TimeUnit.MILLISECONDS);
                                    } catch (InterruptedException ex) {
                                        java.util.logging.Logger.getLogger(StructureAPIModule.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            }).start();

                        }
                    }
                }
            });

        }
    }

    private void setStates() {
        Session session = HibernateUtil.getSession();
        QStructure qs = QStructure.structure;

        new HibernateUpdateClause(session, qs).where(qs.state.ne(Structure.State.COMPLETE).and(qs.state.ne(Structure.State.REMOVED)))
                .set(qs.state, Structure.State.STOPPED)
                .execute();
        session.close();
    }

    @EventHandler
    public void shutdown(PluginDisableEvent pde) {
        if(pde.getPlugin().getName().equals(MAIN_PLUGIN_NAME)) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + MODULE_NAME + ": " +  ChatColor.GRAY +" Shutting down...");
            shutdown();
        }
    }
    
    public void shutdown() {
        AsyncStructureAPI.getInstance().shutdown();
        StructurePlanManager.getInstance().shutdown();
        HibernateUtil.shutdown();
    }

    public File getDataFolder() {
        File dataFolder = new File(MAIN_PLUGIN.getDataFolder(), MODULE_NAME);
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        return dataFolder;

    }

    public Logger getLogger() {
        return STRUCTURE_API_LOG;
    }

}
