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
import com.chingo247.settlercraft.core.platforms.bukkit.BKPermissionRegistry;
import com.chingo247.settlercraft.core.platforms.services.IEconomyProvider;
import com.chingo247.structureapi.StructureAPIException;
import com.chingo247.structureapi.platform.permission.PermissionManager;
import com.chingo247.structureapi.platform.services.holograms.StructureHologramManager;
import com.chingo247.structureapi.StructureAPI;
import com.chingo247.structureapi.StructureInvalidator;
import com.chingo247.structureapi.commands.SchematicCommands;
import com.chingo247.structureapi.commands.SettlerCommands;
import com.chingo247.structureapi.commands.StructureCommands;
import com.chingo247.structureapi.commands.StructurePlanCommands;
import com.chingo247.settlercraft.core.commands.util.PluginCommandManager;
import com.chingo247.structureapi.plan.util.PlanGenerator;
import com.chingo247.xplatform.platforms.bukkit.BukkitConsoleSender;
import com.chingo247.xplatform.platforms.bukkit.BukkitPlayer;
import com.chingo247.xplatform.platforms.bukkit.BukkitPlugin;
import com.chingo247.xplatform.platforms.bukkit.BukkitServer;
import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;
import com.sk89q.minecraft.util.commands.CommandUsageException;
import com.sk89q.minecraft.util.commands.MissingNestedCommandException;
import com.sk89q.minecraft.util.commands.WrappedCommandException;
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
    private GraphDatabaseService graph;
    private PluginCommandManager commands;

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
        registerCommands();
    }
    
    private void registerCommands() {
        this.commands = new PluginCommandManager(SettlerCraft.getInstance().getExecutor(), SettlerCraft.getInstance().getPlatform());
        CommandsManagerRegistration cmdRegister = new CommandsManagerRegistration(this, commands);
        cmdRegister.register(SchematicCommands.class);
        cmdRegister.register(StructureCommands.class);
        cmdRegister.register(SettlerCommands.class);
        cmdRegister.register(StructurePlanCommands.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            if(sender instanceof Player) {
               this.commands.execute(command.getName(), args, new BukkitPlayer((Player) sender), new BukkitPlayer((Player) sender), StructureAPI.getInstance());
            } else {
               this.commands.execute(command.getName(), args, new BukkitConsoleSender(sender), new BukkitConsoleSender(sender), StructureAPI.getInstance());
            }
        } catch (CommandPermissionsException e) {
            sender.sendMessage(ChatColor.RED + "You don't have permission.");
        } catch (MissingNestedCommandException e) {
            sender.sendMessage(ChatColor.RED + e.getUsage());
        } catch (CommandUsageException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
            sender.sendMessage(ChatColor.RED + e.getUsage());
        } catch (WrappedCommandException e) {
            if (e.getCause() instanceof NumberFormatException) {
                sender.sendMessage(ChatColor.RED + "Number expected, string received instead.");
            } else {
                sender.sendMessage(ChatColor.RED + "An error has occurred. See console.");
                e.printStackTrace();
            }
        } catch (com.sk89q.minecraft.util.commands.CommandException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
        }
 
        return true;
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
