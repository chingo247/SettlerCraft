package com.chingo247.structureapi.platforms.bukkit;

/*
 * The MIT License
 *
 * Copyright 2015 Chingo.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
import com.chingo247.menuapi.menu.BKEconomyProvider;
import com.chingo247.menuapi.menu.CategoryMenu;
import com.chingo247.proxyplatform.core.IPlugin;
import com.chingo247.settlercraft.core.exception.SettlerCraftException;
import com.chingo247.structureapi.platforms.bukkit.commands.SettlerCraftCommandExecutor;
import com.chingo247.structureapi.platforms.bukkit.listener.PlanListener;
import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.structureapi.exception.StructureAPIException;
import com.chingo247.structureapi.structure.StructureAPI;
import com.chingo247.structureapi.menu.StructurePlanMenuReader;
import java.io.File;

import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.dom4j.DocumentException;

/**
 *
 * @author Chingo
 */
public class BKStructureAPIPlugin extends JavaPlugin implements IPlugin {

    public static final String MSG_PREFIX = "[SettlerCraft]: ";
    private static final Logger LOGGER = Logger.getLogger(BKStructureAPIPlugin.class);
   
    private BKEconomyProvider economyProvider;
    private final SettlerCraft settlerCraft = SettlerCraft.getInstance();

    private BKConfigProvider configProvider;
    private static BKStructureAPIPlugin instance;
    
    

    @Override
    public void onEnable() {
        instance = this;
        
        if(Bukkit.getPluginManager().getPlugin("SettlerCraft-Core") == null) {
           System.out.println(MSG_PREFIX + " SettlerCraft-MenuAPI NOT FOUND!!! Disabling...");
            this.setEnabled(false);
            return; 
        }
        if(Bukkit.getPluginManager().getPlugin("SettlerCraft-MenuAPI") == null) {
           System.out.println(MSG_PREFIX + " SettlerCraft-MenuAPI NOT FOUND!!! Disabling...");
            this.setEnabled(false);
            return; 
        }
        if (Bukkit.getPluginManager().getPlugin("WorldEdit") == null) {
            System.out.println(MSG_PREFIX + " WorldEdit NOT FOUND!!! Disabling...");
            this.setEnabled(false);
            return;
        }
        if (Bukkit.getPluginManager().getPlugin("AsyncWorldEdit") == null) {
            System.out.println(MSG_PREFIX + " AsyncWorldEdit NOT FOUND!!! Disabling...");
            this.setEnabled(false);
            return;
        }
        
        // Register Config
        configProvider = new BKConfigProvider();
        try {
            configProvider.load();
        } catch (SettlerCraftException ex) {
            java.util.logging.Logger.getLogger(BKStructureAPIPlugin.class.getName()).log(Level.SEVERE, null, ex);
            setEnabled(false);
            return;
        }
        
        StructureAPI.getInstance().load();
        
        StructurePlanMenuReader reader = new StructurePlanMenuReader();
        CategoryMenu menu;
        try {
            menu = reader.read(new File(getDataFolder(), "menu.xml"));
            StructureAPI.getInstance().registerMenu(menu);
        } catch (DocumentException | SettlerCraftException ex) {
            java.util.logging.Logger.getLogger(BKStructureAPIPlugin.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Disabling SettlerCraft!");
            this.setEnabled(false);
            return;
        } catch (StructureAPIException ex) {
            java.util.logging.Logger.getLogger(BKStructureAPIPlugin.class.getName()).log(Level.SEVERE, null, ex);
            this.setEnabled(false);
            return;
        }

        Bukkit.getPluginManager().registerEvents(new PlanListener(economyProvider), this);
//
        getCommand("sc").setExecutor(new SettlerCraftCommandExecutor(this));
//        getCommand("cst").setExecutor(new ConstructionCommandExecutor(structureAPI));
//        getCommand("stt").setExecutor(new StructureCommandExecutor(structureAPI));

    }

  
    public static BKStructureAPIPlugin getInstance() {
        return instance;
    }

    public BKConfigProvider getConfigProvider() {
        return configProvider;
    }

  

}
