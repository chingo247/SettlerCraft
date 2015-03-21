/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.bukkit;

import com.chingo247.settlercraft.SettlerCraft;
import com.chingo247.settlercraft.bukkit.util.BKWorldEditUtil;
import com.chingo247.xcore.platforms.PlatformFactory;
import com.chingo247.xcore.platforms.bukkit.BukkitPlugin;
import com.sk89q.worldedit.entity.Player;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import org.bukkit.Bukkit;

/**
 *
 * @author Chingo
 */
public class BKSettlerCraft extends SettlerCraft {
    
    
    BKSettlerCraft(ExecutorService executorService, SettlerCraftPlugin plugin) {
        super(executorService, PlatformFactory.getPlatform("bukkit"), new BKStructureAPI(plugin, executorService), new BukkitPlugin(plugin));
    }

    @Override
    protected void load() {
        super.load();
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
