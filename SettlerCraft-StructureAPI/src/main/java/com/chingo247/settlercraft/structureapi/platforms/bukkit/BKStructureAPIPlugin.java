package com.chingo247.settlercraft.structureapi.platforms.bukkit;

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
import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.core.event.EventManager;
import com.chingo247.xplatform.core.IPlugin;
import com.chingo247.settlercraft.core.exception.SettlerCraftException;
import com.chingo247.settlercraft.structureapi.platforms.bukkit.listener.PlanListener;
import com.chingo247.settlercraft.core.exception.CommandException;
import com.chingo247.settlercraft.core.platforms.services.IEconomyProvider;
import com.chingo247.settlercraft.structureapi.commands.StructureCommands;
import com.chingo247.settlercraft.structureapi.persistence.dao.StructureDAO;
import com.chingo247.settlercraft.structureapi.platforms.bukkit.services.holograms.HolographicDisplaysHologramProvider;
import com.chingo247.settlercraft.structureapi.platforms.bukkit.services.holograms.StructureHologramManager;
import com.chingo247.settlercraft.structureapi.platforms.bukkit.services.worldguard.WorldGuardHelper;
import com.chingo247.settlercraft.structureapi.platforms.bukkit.services.worldguard.WorldGuardStructureListener;
import com.chingo247.settlercraft.structureapi.platforms.bukkit.structure.restriction.WorldGuardRestriction;
import com.chingo247.settlercraft.structureapi.structure.StructureAPI;
import com.chingo247.settlercraft.structureapi.structure.plan.util.PlanGenerator;
import com.chingo247.xplatform.core.ICommandSender;
import com.chingo247.xplatform.platforms.bukkit.BukkitConsoleSender;
import com.chingo247.xplatform.platforms.bukkit.BukkitPlayer;
import com.chingo247.xplatform.platforms.bukkit.BukkitPlugin;
import java.io.File;

import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.dom4j.DocumentException;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 *
 * @author Chingo
 */
public class BKStructureAPIPlugin extends JavaPlugin implements IPlugin {

    public static final String MSG_PREFIX = ChatColor.YELLOW + "[SettlerCraft]: ";
    private static final Logger LOGGER = Logger.getLogger(BKStructureAPIPlugin.class);
    private IEconomyProvider economyProvider;
    private BKConfigProvider configProvider;
    private static BKStructureAPIPlugin instance;
    private StructureCommands structureCommands;
    private GraphDatabaseService graph;
    

    @Override
    public void onEnable() {
        instance = this;
        
        if(Bukkit.getPluginManager().getPlugin("SettlerCraft-Core") == null) {
           System.out.println(MSG_PREFIX + " SettlerCraft-Core NOT FOUND!!! Disabling...");
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
        
        

        // Register plugin & ConfigProvider
        StructureAPI structureAPI = (StructureAPI) StructureAPI.getInstance();
        
        structureAPI.registerStructureAPIPlugin(new BukkitPlugin(this));
        structureAPI.registerConfigProvider(configProvider);
        
        
        try {
            structureAPI.initialize();
        } catch (DocumentException | SettlerCraftException ex) {
            java.util.logging.Logger.getLogger(BKStructureAPIPlugin.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(MSG_PREFIX + "Disabling SettlerCraft-StructureAPI");
            return;
        }
        
        economyProvider = SettlerCraft.getInstance().getEconomyProvider();
        
        Bukkit.getPluginManager().registerEvents(new PlanListener(economyProvider), this);
        
        File generationDirectory = StructureAPI.getInstance().getGenerationDirectory();
        generationDirectory.mkdirs();
        PlanGenerator.generate(generationDirectory);

        graph = SettlerCraft.getInstance().getNeo4j();
        
        // Enable WorldGuard
        if(Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
            WorldGuardHelper worldGuardHelper = new WorldGuardHelper(graph, new StructureDAO(graph));
            EventManager.getInstance().getEventBus().register(new WorldGuardStructureListener(worldGuardHelper));
            StructureAPI.getInstance().addRestriction(new WorldGuardRestriction());
            worldGuardHelper.processStructuresWithoutRegion();
        }
        
        if(Bukkit.getPluginManager().getPlugin("HolographicDisplays") != null) {
            StructureHologramManager.getInstance().setHologramProvider(new HolographicDisplaysHologramProvider());
            EventManager.getInstance().getEventBus().register(StructureHologramManager.getInstance());
        }
        
        structureCommands = new StructureCommands(StructureAPI.getInstance(), new BKPermissionManager(), SettlerCraft.getInstance().getExecutor(), graph);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ICommandSender s = (sender instanceof Player) ? new BukkitPlayer((Player) sender) : new BukkitConsoleSender(sender);
            try {
                switch(command.getName()) {
                    case "stt": return structureCommands.handle(s, command.getName(), args);
                    default:
                        sender.sendMessage("No action known for " + command.getName());
                        break;
                }
                return true; //To change body of generated methods, choose Tools | Templates.
            } catch (CommandException ex) {
                sender.sendMessage(ex.getPlayerErrorMessage());
                
            }
        
        return false;
    }

    @Override
    public void onDisable() {
        super.onDisable(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
    

  
    public static BKStructureAPIPlugin getInstance() {
        return instance;
    }

    public BKConfigProvider getConfigProvider() {
        return configProvider;
    }

  

}