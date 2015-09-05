/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.backupapi.bukkit;

import com.chingo247.backupapi.core.IChunkLoader;
import com.chingo247.backupapi.core.IChunkManager;
import com.chingo247.backupapi.core.IChunkSaver;
import org.bukkit.Bukkit;
import org.bukkit.World;

/**
 *
 * @author Chingo
 */
public class BKChunkManager implements IChunkManager {


    private static void print(String s) {
        
    }

    @Override
    public IChunkLoader getLoader(String world) {
        World w = Bukkit.getWorld(world);
        return w == null ? null : new BKChunkLoader(w);
    }

    @Override
    public IChunkSaver getSaver(String world) {
        World w = Bukkit.getWorld(world);
        return w == null ? null : new BKChunkSaver(w);
    }

}
