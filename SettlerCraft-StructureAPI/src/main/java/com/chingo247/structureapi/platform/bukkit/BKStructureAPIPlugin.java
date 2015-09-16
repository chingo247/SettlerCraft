package com.chingo247.structureapi.platform.bukkit;

/*
 * Copyright (C) 2015 Chingo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
import com.chingo247.backupapi.core.BackupAPI;
import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.xplatform.core.IPlugin;
import com.chingo247.settlercraft.core.exception.SettlerCraftException;
import com.chingo247.structureapi.platform.bukkit.listener.PlanListener;
import com.chingo247.settlercraft.core.exception.CommandException;
import com.chingo247.settlercraft.core.platforms.bukkit.BKPermissionRegistry;
import com.chingo247.settlercraft.core.platforms.services.IEconomyProvider;
import com.chingo247.structureapi.commands.StructureCommands;
import com.chingo247.structureapi.exception.StructureAPIException;
import com.chingo247.structureapi.platform.services.PermissionManager;
import com.chingo247.structureapi.platform.services.holograms.StructureHologramManager;
import com.chingo247.structureapi.StructureAPI;
import com.chingo247.structureapi.StructureInvalidator;
import com.chingo247.structureapi.plan.util.PlanGenerator;
import com.chingo247.xplatform.core.ICommandSender;
import com.chingo247.xplatform.platforms.bukkit.BukkitConsoleSender;
import com.chingo247.xplatform.platforms.bukkit.BukkitPlayer;
import com.chingo247.xplatform.platforms.bukkit.BukkitPlugin;
import com.chingo247.xplatform.platforms.bukkit.BukkitServer;
import java.io.File;

import java.util.logging.Level;
import java.util.logging.Logger;
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

    public static final Level LOG_LEVEL = Level.SEVERE;
    public static final String MSG_PREFIX = ChatColor.YELLOW + "[SettlerCraft]: ";
    private static final Logger LOGGER = Logger.getLogger(BKStructureAPIPlugin.class.getName());
    private IEconomyProvider economyProvider;
    private BKConfigProvider configProvider;
    private static BKStructureAPIPlugin instance;
    private StructureCommands structureCommands;
    private GraphDatabaseService graph;
    

    @Override
    public void onEnable() {
        instance = this;
        if(Bukkit.getPluginManager().getPlugin("SettlerCraft") != null) {
            Bukkit.getConsoleSender().sendMessage(new String[] {
                    ChatColor.RED + "[SettlerCraft]: Please remove the old jar of SettlerCraft!",
                    ChatColor.RED + "[SettlerCraft]: This should be named something like 'SettlerCraft-1.0-RC3.jar'" ,
                    ChatColor.RED + "[SettlerCraft]: Or something like 'SettlerCraft-1.0-RC4-1.jar'",
                    ChatColor.RED + "[SettlerCraft]: and needs to be removed!",
                }
            );
            return;
        }
        
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
        
        if(Bukkit.getPluginManager().getPlugin("SettlerCraft-BackupAPI") != null) {
            System.out.println("[SettlerCraft]: Found 'SettlerCraft-BackupAPI' Setting up backup options...");
            StructureAPI structureAPI = ((StructureAPI)StructureAPI.getInstance());
            try {
                structureAPI.registerBackupAPI(BackupAPI.getInstance());
                structureAPI.registerChunkManager(BackupAPI.getInstance().getChunkManager());
            } catch (StructureAPIException ex) {
                Logger.getLogger(BKStructureAPIPlugin.class.getName()).log(Level.SEVERE, null, ex);
            }
        } 
       
        
        // Get GraphDatabase
        graph = SettlerCraft.getInstance().getNeo4j();
        
        // Initialize Config
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
        try {
            structureAPI.registerStructureAPIPlugin(new BukkitPlugin(this));
        } catch (StructureAPIException ex) {
            this.setEnabled(false);
            System.out.println("[SettlerCraft]: Disabling SettlerCraft-StructureAPI");
            Logger.getLogger(BKStructureAPIPlugin.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        structureAPI.registerConfigProvider(configProvider);
        structureAPI.setLogLevel(LOG_LEVEL);
        
        
        
        economyProvider = SettlerCraft.getInstance().getEconomyProvider();
        
        // Run invalidation!
        StructureInvalidator invalidator = new StructureInvalidator(new BukkitServer(Bukkit.getServer()), SettlerCraft.getInstance().getExecutor(), graph, economyProvider);
        invalidator.invalidate();
        
        // Initialize the StructureAPI
        try {
            structureAPI.initialize();
        } catch (DocumentException | SettlerCraftException ex) {
            java.util.logging.Logger.getLogger(BKStructureAPIPlugin.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(MSG_PREFIX + "Disabling SettlerCraft-StructureAPI");
            return;
        }
        structureAPI.registerAsyncEditSesionFactoryProvider(new BKAsyncEditSessionFactoryProvider());
        
        // Register Listeners
        Bukkit.getPluginManager().registerEvents(new PlanListener(economyProvider), this);
        
        // Generate Plans 
        File generationDirectory = StructureAPI.getInstance().getGenerationDirectory();
        generationDirectory.mkdirs();
        PlanGenerator.generate(generationDirectory);
        
//        // Setup HolographicDisplays (if available)
        
        if(configProvider.useHolograms()) {
            StructureHologramManager.getInstance().inititialize(new BukkitPlugin(this));
        }
        
        // Register permissions
        PermissionManager.getInstance().registerPermissionRegistry(new BKPermissionRegistry());
        
        // Setup Commands
        structureCommands = new StructureCommands(StructureAPI.getInstance(), SettlerCraft.getInstance().getExecutor(), graph);
        
        
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
