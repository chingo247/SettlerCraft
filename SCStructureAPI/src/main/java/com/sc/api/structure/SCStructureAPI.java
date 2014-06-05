/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure;

import com.sc.api.structure.plan.StructurePlanLoader;
import java.io.File;
import java.io.FileNotFoundException;
import org.apache.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Chingo
 */
public class SCStructureAPI extends JavaPlugin {
    
    
    private static final int INFINITE_BLOCKS = -1;
    private static final Logger LOGGER = Logger.getLogger(SCStructureAPI.class);
    
    public static Plugin getMainPlugin() {
        return Bukkit.getPluginManager().getPlugin("SettlerCraft");
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
            LOGGER.error(ex);
        }
    }

    
   

    
    
    

    
    
}
