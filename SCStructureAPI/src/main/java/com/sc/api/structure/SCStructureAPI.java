/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure;

import com.sc.api.structure.entity.plan.StructurePlan;
import com.sc.api.structure.entity.plan.StructureSchematic;
import com.sc.api.structure.listener.PlayerListener;
import com.sc.api.structure.listener.StructureListener;
import com.sc.api.structure.persistence.HSQLServer;
import com.sc.api.structure.persistence.HibernateUtil;
import com.sc.api.structure.plan.StructurePlanLoader;
import java.io.File;
import org.apache.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Chingo
 */
public class SCStructureAPI {
    
    
    private static final int INFINITE_BLOCKS = -1;
    private static final Logger LOGGER = Logger.getLogger(SCStructureAPI.class);
    private Plugin plugin;
    private static SCStructureAPI instance;
    private boolean initialized = false;
    
    public Plugin getMainPlugin() {
        return plugin;
    }
    
    private SCStructureAPI() {
    
    }
    
    public static SCStructureAPI getInstance() {
        if(instance == null) {
            System.out.println("Instantiated");
            instance = new SCStructureAPI();
        }
        return instance;
    }
    
    public void init(Plugin plugin) {
        if(!HSQLServer.getInstance().isRunning()) {
            HSQLServer.getInstance().start();
            new RestoreService().restore(); // only execute on server start, not on reload!
        } 
        if(!initialized) {
            HibernateUtil.addAnnotatedClasses(
                Structure.class,
                StructurePlan.class,
                ConstructionProcess.class,
                StructureSchematic.class
        );
        Bukkit.getPluginManager().registerEvents(new StructureListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), plugin);
        initialized = true;
        }
        
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

    
   

    
    
    

    
    
}
