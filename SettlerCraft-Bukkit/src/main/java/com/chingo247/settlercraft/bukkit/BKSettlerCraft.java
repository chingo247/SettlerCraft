/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.bukkit;

import com.chingo247.settlercraft.SCWorld;
import com.chingo247.settlercraft.SettlerCraft;
import com.chingo247.settlercraft.bukkit.util.BKWorldEditUtil;
import com.chingo247.xcore.core.IWorld;
import com.chingo247.xcore.platforms.PlatformFactory;
import com.sk89q.worldedit.entity.Player;
import java.io.File;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import org.bukkit.Bukkit;

/**
 *
 * @author Chingo
 */
public class BKSettlerCraft extends SettlerCraft {
    
    private final SettlerCraftPlugin plugin;
    
    BKSettlerCraft(ExecutorService executorService, SettlerCraftPlugin plugin) {
        super(executorService, PlatformFactory.getPlatform("bukkit"));
        this.plugin = plugin;
    }

    @Override
    protected SCWorld handle(IWorld world) {
        return new BKWorld(EXECUTOR, this, world);
    }
    
    @Override
    protected void load() {
        super.load();
    }

    

    @Override
    public File getStructureDirectory() {
        File folder = new File(plugin.getDataFolder(), "Structures");
        folder.mkdirs();
        return folder;
    }

    @Override
    public File getPlanDirectory() {
        File folder =  new File(plugin.getDataFolder(), "StructurePlans");
        folder.mkdirs();
        return folder;
    }

    @Override
    public File getPluginDirectory() {
        File folder = plugin.getDataFolder();
        folder.mkdirs();
        return folder;
    }

    @Override
    public File getSchematicToPlanDirectory() {
        File folder = new File(plugin.getDataFolder(), "SchematicToPlan");
        folder.mkdirs();
        return folder;
    }

    @Override
    protected Player getPlayer(UUID player) {
        org.bukkit.entity.Player ply = Bukkit.getPlayer(player);
        if(ply != null) {
            return BKWorldEditUtil.wrapPlayer(ply);
        }
        return null;
    }
    
}
