/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure;

import com.sc.api.structure.entity.SCSession;
import com.sc.api.structure.entity.Structure;
import com.sc.api.structure.entity.StructureJob;
import com.sc.api.structure.entity.plan.StructurePlan;
import com.sc.api.structure.entity.progress.ConstructionEntry;
import com.sc.api.structure.entity.progress.ConstructionTask;
import com.sc.api.structure.entity.progress.MaterialLayerProgress;
import com.sc.api.structure.entity.progress.MaterialProgress;
import com.sc.api.structure.entity.progress.MaterialResourceProgress;
import com.sc.api.structure.io.StructurePlanLoader;
import com.sc.api.structure.listener.StructurePlanListener;
import com.sc.api.structure.persistence.HSQLServer;
import com.sc.api.structure.persistence.HibernateUtil;
import com.sc.api.structure.persistence.MemDBUtil;
import com.sk89q.worldedit.bukkit.WorldEditAPI;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Chingo
 */
public class SCStructureAPI extends JavaPlugin {

    private boolean restrictZones = false;
    private StructurePlanListener spl;

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

        Bukkit.getPluginManager().registerEvents(new StructurePlanListener(), this);
        HSQLServer.getInstance().start();
        initDB();
        
        RestoreService service = new RestoreService();
        service.restore();

        loadStructures(FileUtils.getFile(getDataFolder(), "Structures"));
        
        
    }

    public static WorldEditPlugin getWorldEditPlugin() {
        return (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
    }

    private static void initDB() {
        MemDBUtil.addAnnotatedClasses(
                SCSession.class,
                StructurePlan.class);
        HibernateUtil.addAnnotatedClasses(
                Structure.class,
                MaterialProgress.class,
                MaterialLayerProgress.class,
                MaterialResourceProgress.class,
                StructurePlan.class,
                StructureJob.class,
                ConstructionEntry.class,
                ConstructionTask.class);
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

}
