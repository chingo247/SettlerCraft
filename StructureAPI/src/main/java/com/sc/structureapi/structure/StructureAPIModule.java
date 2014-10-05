/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.structureapi.structure;

import com.mysema.query.jpa.hibernate.HibernateUpdateClause;
import com.sc.module.menuapi.menus.menu.CategoryMenu;
import com.sc.module.menuapi.menus.menu.MenuAPI;
import com.sc.structureapi.bukkit.ConfigProvider;
import com.sc.structureapi.exception.StructureAPIException;
import com.sc.structureapi.exception.StructureDataException;
import com.sc.structureapi.listener.PlanListener;
import com.sc.structureapi.persistence.HibernateUtil;
import com.sc.structureapi.structure.construction.ConstructionManager;
import com.sc.structureapi.structure.entities.structure.QStructure;
import com.sc.structureapi.structure.entities.structure.Structure;
import com.sc.structureapi.structure.plan.StructurePlan;
import com.sc.structureapi.structure.plan.StructurePlanItem;
import com.sc.structureapi.structure.plan.StructurePlanManager;
import com.sc.structureapi.structure.plan.holograms.StructureHologramManager;
import com.sc.structureapi.structure.plan.overview.StructureOverviewManager;
import com.sc.structureapi.util.FileUtil;
import com.sk89q.worldedit.data.DataException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
    private static final Logger STRUCTURE_API_LOG = Logger.getLogger(MODULE_NAME);
    private static final String MAIN_PLUGIN_NAME = "SettlerCraft";
    public static final String MSG_PREFIX = ChatColor.YELLOW + "[" + MAIN_PLUGIN_NAME + "]" + ChatColor.GOLD + "[" + MODULE_NAME + "]: " + ChatColor.RESET;
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

            try {
                // Setup Menu
                CategoryMenu menu = PlanMenuLoader.load();
                PLANSHOP = menu.getId();

            } catch (DocumentException | StructureAPIException ex) {
                Logger.getLogger(StructureAPIModule.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }

            // Make dirs if needed
            StructurePlanManager.getInstance().init();
//            getModuleFolder();
            getStructureDataFolder(); // Creates module folder and Structure folder
            writeResources();

            try {
                // Load Config
                ConfigProvider.getInstance().load();
            } catch (StructureAPIException ex) {
                Logger.getLogger(StructureAPIModule.class.getName()).log(Level.SEVERE, null, ex);
            }

            // Generate Plans in 'SchematicToPlan' Folder
            StructurePlanManager.getInstance().generate();

            // Load Plans into menu
            loadPlans();

            // Register listeners
            Bukkit.getPluginManager().registerEvents(new PlanListener(), MAIN_PLUGIN);
            Bukkit.getPluginManager().registerEvents(ConstructionManager.getInstance(), MAIN_PLUGIN);

            // Reset Structure states 
            setStates();

            // Init extra data
            boolean useHolograms = MAIN_PLUGIN.getConfig().getBoolean("structure.use-holograms");
            Plugin plugin = Bukkit.getPluginManager().getPlugin("HolographicDisplays");
            if (useHolograms && plugin != null && plugin.isEnabled()) {
                StructureOverviewManager overviewManager = StructureOverviewManager.getInstance();
                StructureHologramManager hologramManager = StructureHologramManager.getInstance();

                Bukkit.getPluginManager().registerEvents(overviewManager, plugin);
                Bukkit.getPluginManager().registerEvents(hologramManager, plugin);

                overviewManager.init();
                hologramManager.init();
            }

            // IMPORTANT STUFF ON RELOAD! SHUTDOWN CONNECTIONS AND END THREADS!
            Bukkit.getPluginManager().registerEvents(this, MAIN_PLUGIN);

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

        final long start = System.currentTimeMillis();
        StructurePlanManager.getInstance().load(new StructurePlanManager.Callback() {

            @Override
            public void onComplete() {
                Bukkit.broadcastMessage(MSG_PREFIX + ChatColor.WHITE + "Structure plans loaded");

                int plans = StructurePlanManager.getInstance().getPlans().size();
                Bukkit.getConsoleSender().sendMessage(MSG_PREFIX + plans + " plans loaded in " + (System.currentTimeMillis() - start) + "ms");

                loadPlansIntoMenu();

            }
        });
    }

    /**
     * Reload the plans
     */
    public synchronized void reload() {
        if (!loadingplans) {
            loadingplans = true;
            getPlanMenu().makeAllLeave();
            getPlanMenu().clearItems();

            final long start = System.currentTimeMillis();
            StructurePlanManager.getInstance().load(new StructurePlanManager.Callback() {

                @Override
                public void onComplete() {
                    int plans = StructurePlanManager.getInstance().getPlans().size();
                    Bukkit.getConsoleSender().sendMessage(MSG_PREFIX + plans + " plans loaded in " + (System.currentTimeMillis() - start) + "ms");
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
        File structureDirectory = new File(getModuleFolder(), "Structures");
        if (!structureDirectory.exists()) {
            structureDirectory.mkdirs();
        }
        return structureDirectory;
    }

    public CategoryMenu getPlanMenu() {
        return MenuAPI.getInstance().getMenu(MAIN_PLUGIN, PLANSHOP);
    }

    private void loadPlansIntoMenu() {
        final List<StructurePlan> plans = StructurePlanManager.getInstance().getPlans();
        final Iterator<StructurePlan> planIterator = plans.iterator();
        final CategoryMenu planMenu = getInstance().getPlanMenu();


        final long start = System.currentTimeMillis();

        while (planIterator.hasNext()) {
            final StructurePlan plan = planIterator.next();

//            executor.submit(new Runnable() {
//
//                @Override
//                public void run() {
            try {
                // Add item to planmenu
                StructurePlanItem planItem = StructurePlanItem.load(plan);
                planMenu.addItem(planItem);
            } catch (IOException | DataException | DocumentException | StructureDataException ex) {
//                        java.util.logging.Logger.getLogger(StructureAPIModule.class.getName()).log(Level.SEVERE, null, ex);
//                    } finally {
                // Enable planmenu if this was the last item
//                        count.incrementAndGet();
                
//                        if (count.intValue() == total) {

//                new Thread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        executor.shutdownNow();
//                        try {
//                            executor.awaitTermination(10, TimeUnit.MILLISECONDS);
//                        } catch (InterruptedException ex) {
//                            java.util.logging.Logger.getLogger(StructureAPIModule.class.getName()).log(Level.SEVERE, null, ex);
//                        }
//                    }
//                }).start();
            }
        }
        planMenu.setEnabled(true);
        getInstance().plansLoaded = true;
        getInstance().loadingplans = false;

        Bukkit.getConsoleSender().sendMessage(MSG_PREFIX + "PlanMenu loaded in " + (System.currentTimeMillis() - start) + "ms");

//                }
//            });
//        }
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
        if (pde.getPlugin().getName().equals(MAIN_PLUGIN_NAME)) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[" + MODULE_NAME + "]: " + ChatColor.GRAY + " Shutting down...");
            shutdown();
        }
    }

    public void shutdown() {
        AsyncStructureAPI.getInstance().shutdown();
        StructurePlanManager.getInstance().shutdown();
        HibernateUtil.shutdown();
    }

    public File getModuleFolder() {
        File dataFolder = new File(MAIN_PLUGIN.getDataFolder(), MODULE_NAME);
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        return dataFolder;

    }

    public Logger getLogger() {
        return STRUCTURE_API_LOG;
    }

    private void writeResources() {
        File config = new File(getModuleFolder(), "config.yml");

        if (!config.exists()) {
            InputStream i = this.getClass().getClassLoader().getResourceAsStream("structureapi/config.yml");
            FileUtil.write(i, config);
        }
    }

}
