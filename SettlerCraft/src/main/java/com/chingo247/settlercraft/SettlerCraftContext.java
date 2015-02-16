/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.util.org.apache.commons.io.IOUtils;

/**
 *
 * @author Chingo
 */
public class SettlerCraftContext {
    
    private static SettlerCraftContext instance;
    
    private String structureDirectory;
    private String structurePlanDirectory;
    private String pluginDirectory;
    private String schematicToPlanDirectory;
    private String plugin;
    private String platform;
    
    private SettlerCraftContext() {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("com//chingo247//structureapi//resources//config.properties");
        Properties properties = new Properties();
        try {
            properties.load(stream);
            
            this.pluginDirectory = properties.getProperty("directory.plugin");
            this.structureDirectory = properties.getProperty("directory.structures");
            this.schematicToPlanDirectory = properties.getProperty("directory.schematic.to.plan");
            this.structurePlanDirectory = properties.getProperty("directory.plans");
            this.plugin = properties.getProperty("main.plugin");
            this.platform = properties.getProperty("main.platform");
            
        } catch (IOException ex) {
            Logger.getLogger(SettlerCraftContext.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    public String getPlatform() {
        return platform;
    }
    
    public static SettlerCraftContext getContext() {
        if(instance == null) {
            instance = new SettlerCraftContext();
        }
        return instance;
    }
    
    public File getStructureDirectory() {
        return new File(structureDirectory);
    }
    
    public File getPlanDirectory() {
        return new File(structurePlanDirectory);
    }
    
    public File getPluginDirectory() {
        return new File(pluginDirectory);
    }
    
    public File getSchematicToPlanDirectory() {
        return new File(schematicToPlanDirectory);
    }

    public String getPluginName() {
        return plugin;
    }
    
    
    
}
