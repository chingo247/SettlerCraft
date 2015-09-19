/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.backupapi.bukkit;

import com.chingo247.backupapi.core.IChunkLoader;
import com.chingo247.backupapi.core.IChunkManager;
import net.minecraft.server.v1_8_R1.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;

/**
 *
 * @author Chingo
 */
public class BKChunkManager implements IChunkManager  {

    @Override
    public IChunkLoader getHandler(String world) {
        World w = Bukkit.getWorld(world);
        return w == null ? null : new BKChunkLoader(w);
    }


    @Override
    public void writeToDisk(String world) {
        // Get the world
        World w = Bukkit.getWorld(world);
        if(w == null) {
            throw new NullPointerException("No world found for '" + world + "'");
        }
        // Write to disk
        WorldServer ws = ((CraftWorld) w).getHandle();
        
        boolean oldSave = ws.savingDisabled;
        ws.savingDisabled = false;
        ws.flushSave();
        ws.savingDisabled = oldSave;
    }

}
