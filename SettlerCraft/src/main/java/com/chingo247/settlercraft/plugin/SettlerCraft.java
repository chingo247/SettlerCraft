/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.plugin;

import com.chingo247.settlercraft.bukkit.commands.ConstructionCommandExecutor;
import com.chingo247.settlercraft.bukkit.commands.SettlerCraftCommandExecutor;
import com.chingo247.settlercraft.bukkit.commands.StructureCommandExecutor;
import com.chingo247.settlercraft.bukkit.listener.PlanListener;
import com.chingo247.settlercraft.bukkit.listener.PluginListener;
import com.chingo247.settlercraft.exception.SettlerCraftException;
import com.chingo247.settlercraft.exception.StructureAPIException;
import com.chingo247.settlercraft.exception.StructureDataException;
import com.chingo247.settlercraft.persistence.HSQLServer;
import com.chingo247.settlercraft.persistence.HibernateUtil;
import com.chingo247.settlercraft.persistence.RestoreService;
import com.chingo247.settlercraft.plugin.PermissionManager.Perms;
import com.chingo247.settlercraft.structure.PlanMenuLoader;
import com.chingo247.settlercraft.structure.entities.structure.QStructure;
import com.chingo247.settlercraft.structure.entities.structure.Structure;
import com.chingo247.settlercraft.structure.plan.StructurePlan;
import com.chingo247.settlercraft.structure.plan.StructurePlanItem;
import com.chingo247.settlercraft.structure.plan.StructurePlanManager;
import com.chingo247.settlercraft.structure.plan.holograms.StructureHologramManager;
import com.chingo247.settlercraft.structure.plan.overview.StructureOverviewManager;
import com.mysema.query.jpa.hibernate.HibernateUpdateClause;
import com.sc.module.menuapi.menus.menu.CategoryMenu;
import com.sc.module.menuapi.menus.menu.MenuAPI;
import com.sk89q.worldedit.data.DataException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.dom4j.DocumentException;
import org.hibernate.Session;

/**
 *
 * @author Chingo
 */
public class SettlerCraft extends JavaPlugin {

    private static final Logger LOGGER = Logger.getLogger(SettlerCraft.class);
    private static SettlerCraft instance;
    private boolean plansLoaded = false;
    private boolean loadingplans = false;
    private static UUID PLANSHOP;

    public static final String MSG_PREFIX = ChatColor.YELLOW + "[SettlerCraft]: " + ChatColor.RESET;

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

//        if (Bukkit.getPluginManager().getPlugin("HolographicDisplays") == null) {
//            System.out.println("[SettlerCraft]: HolographicDisplays NOT FOUND!!! Disabling...");
//            this.setEnabled(false);
//            return;
//        }
        try {
            // Setup Menu
            CategoryMenu menu = PlanMenuLoader.load();
            PLANSHOP = menu.getId();

        } catch (DocumentException | StructureAPIException ex) {
            java.util.logging.Logger.getLogger(SettlerCraft.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        // Init HSQL Server
        HSQLServer hsqls = HSQLServer.getInstance();
        if (!hsqls.isRunning()) {
            Bukkit.getConsoleSender().sendMessage(MSG_PREFIX + "Starting HSQL Server");
            hsqls.start();
            new RestoreService().restore();
        }

        resetStates();

        // Init StructurePlanManager
        StructurePlanManager.getInstance().init();
        reloadPlans();
        

        // Write Changelog & Config if not exist!
        writeResources();

        try {
            ConfigProvider.getInstance().load();
        } catch (SettlerCraftException ex) {
            java.util.logging.Logger.getLogger(SettlerCraft.class.getName()).log(Level.SEVERE, null, ex);
        }

        Bukkit.getPluginManager().registerEvents(new PluginListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlanListener(), this);

        boolean useHolograms = getConfig().getBoolean("structure.use-holograms");
        Plugin plugin = Bukkit.getPluginManager().getPlugin("HolographicDisplays");
        if (useHolograms && plugin != null && plugin.isEnabled()) {
            StructureOverviewManager overviewManager = StructureOverviewManager.getInstance();
            StructureHologramManager hologramManager = StructureHologramManager.getInstance();

            Bukkit.getPluginManager().registerEvents(overviewManager, plugin);
            Bukkit.getPluginManager().registerEvents(hologramManager, plugin);

            overviewManager.init();
            hologramManager.init();
        }

        getCommand("sc").setExecutor(new SettlerCraftCommandExecutor(this));
        getCommand("cst").setExecutor(new ConstructionCommandExecutor(this));
        getCommand("stt").setExecutor(new StructureCommandExecutor());

        printPerms();
    }

    /**
     * Gets the datafolder for the StructureAPI or creates them if none exists
     *
     * @return The datafolder
     */
    public final File getStructureDataFolder() {
        File structureDirectory = new File(getDataFolder(), "Structures");
        if (!structureDirectory.exists()) {
            structureDirectory.mkdirs();
        }
        return structureDirectory;
    }

    private void printPerms() {
        File printedFile = new File(SettlerCraft.getInstance().getDataFolder(), "permissions.yml");
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

    public boolean isLoadingplans() {
        return loadingplans;
    }

    private void writeResources() {
        File config = new File(getDataFolder(), "config.yml");
        File changelog = new File(getDataFolder(), "changelog.txt");

        if (!config.exists()) {

            InputStream i = this.getClassLoader().getResourceAsStream("com/chingo247/settlercraft/resources/config.yml");
            System.out.println(i);
            write(i, config);
        }
        if (!changelog.exists()) {
            InputStream i = this.getClassLoader().getResourceAsStream("com/chingo247/settlercraft/resources/changelog.txt");
            System.out.println(i);
            write(i, changelog);
        }
    }

    private void write(InputStream inputStream, File file) {
        OutputStream outputStream = null;

        try {

            // write the inputStream to a FileOutputStream
            outputStream = new FileOutputStream(file);

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }

        } catch (IOException e) {
            Logger.getLogger(SettlerCraft.class).error(e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Logger.getLogger(SettlerCraft.class).error(e.getMessage());
                }
            }
            if (outputStream != null) {
                try {
                    // outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    Logger.getLogger(SettlerCraft.class).error(e.getMessage());
                }

            }
        }
    }

    public CategoryMenu getPlanMenu() {
        return MenuAPI.getInstance().getMenu(this, PLANSHOP);
    }

    /**
     * Reload the plans
     */
    public void reloadPlans() {
        synchronized (this) {
            if (loadingplans) {
                return;
            }
        }

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

    private void loadPlansIntoMenu() {
        final List<StructurePlan> plans = StructurePlanManager.getInstance().getPlans();
        final Iterator<StructurePlan> planIterator = plans.iterator();
        final CategoryMenu planMenu = getInstance().getPlanMenu();

        final long start = System.currentTimeMillis();

        while (planIterator.hasNext()) {
            final StructurePlan plan = planIterator.next();

            try {
                // Add item to planmenu
                StructurePlanItem planItem = StructurePlanItem.load(plan);
                planMenu.addItem(planItem);
            } catch (IOException | DataException | DocumentException | StructureDataException ex) {
                LOGGER.error(ex.getMessage());
            }
        }
        planMenu.setEnabled(true);
        getInstance().plansLoaded = true;
        getInstance().loadingplans = false;
        Bukkit.getConsoleSender().sendMessage(MSG_PREFIX + "PlanMenu loaded in " + (System.currentTimeMillis() - start) + "ms");
    }

    private void resetStates() {
        Session session = HibernateUtil.getSession();
        QStructure qs = QStructure.structure;

        new HibernateUpdateClause(session, qs).where(qs.state.ne(Structure.State.COMPLETE).and(qs.state.ne(Structure.State.REMOVED)))
                .set(qs.state, Structure.State.STOPPED)
                .execute();
        session.close();
    }

    public static void print(String[] messages) {
        for (String s : messages) {
            print(s);
        }
    }

    public static void print(String message) {
        Bukkit.getConsoleSender().sendMessage(MSG_PREFIX + message);
    }

}
