/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure;

import com.sc.api.structure.io.StructurePlanLoader;
import com.sc.api.structure.listeners.PlayerListener;
import com.sc.api.structure.listeners.StructurePlanListener;
import com.sc.api.structure.model.structure.Structure;
import com.sc.api.structure.model.structure.StructureJob;
import com.sc.api.structure.model.structure.plan.StructurePlan;
import com.sc.api.structure.model.structure.progress.StructureProgress;
import com.sc.api.structure.model.structure.progress.StructureProgressLayer;
import com.sc.api.structure.model.structure.progress.StructureProgressMaterialResource;
import com.sc.api.structure.util.HibernateUtil;
import com.sc.api.structure.util.MaterialUtil;
import com.sc.api.structure.util.MemDBUtil;
import com.sk89q.worldedit.bukkit.WorldEditAPI;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Chingo
 */
public class SCStructureAPI extends JavaPlugin {

    private boolean restrictZones = false;
    public static final String ALIAS = "[STRUC]";
    public static final String PLAN_SHOP_NAME = "Buy & Build";
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
        initDB();
        Bukkit.getPluginManager().registerEvents(new StructurePlanListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        
        for(Material m : Material.values()) {
            MaterialUtil.isAttachable(m, (byte)0);
        }
        
        
    }
    
    public static WorldEditPlugin getWorldEditPlugin() {
        return (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
    }

// TODO SHADING  
//    public static void init(JavaPlugin plugin) {
//        initDB();
//        Bukkit.getPluginManager().registerEvents(new StructurePlanListener(plugin), plugin);
//        if(instance == null) {
//            instance = new SCStructureAPI();
//        }
//        instance.getCommand("sc").setExecutor(new StructureCommandExecutor());
//    }
    
    private static void initDB() {
        addClassesToDB(
                Structure.class,
                StructureProgress.class,
                StructureProgressLayer.class,
                StructureProgressMaterialResource.class,
                StructurePlan.class,
                StructureJob.class
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
            structureFolder.mkdir();
        }
        StructurePlanLoader spLoader = new StructurePlanLoader();
        try {
            spLoader.loadStructures(structureFolder);
        }
        catch (FileNotFoundException ex) {
            Logger.getLogger(SCStructureAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
