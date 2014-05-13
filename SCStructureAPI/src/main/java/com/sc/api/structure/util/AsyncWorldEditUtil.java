/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.api.structure.util;

import static com.sc.api.structure.util.WorldEditUtil.getLocalWorld;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.primesoft.asyncworldedit.PluginMain;
import org.primesoft.asyncworldedit.blockPlacer.BlockPlacer;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSession;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSessionFactory;

/**
 *
 * @author Chingo
 */
public class AsyncWorldEditUtil {
    
    public static PluginMain getAsyncWorldEditPlugin() {
        return (PluginMain) Bukkit.getPluginManager().getPlugin("AsyncWorldEdit");
    }
    
    public static BlockPlacer getBlockPlacer() {
        return getAsyncWorldEditPlugin().getBlockPlacer();
    }
    
    public static AsyncEditSessionFactory getAsyncSessionFactory() {
        return new AsyncEditSessionFactory(getAsyncWorldEditPlugin());
    }

    public static AsyncEditSession createAsyncEditSession(Player player, int maxblocks) {
        return new AsyncEditSession(getAsyncSessionFactory(), getAsyncWorldEditPlugin(), player.getName(), getLocalWorld(player), maxblocks);
    }
    
 
    
}
