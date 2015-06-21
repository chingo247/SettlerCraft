/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.towny.plugin;

import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.structureapi.structure.StructureAPI;
import com.chingo247.settlercraft.towny.listener.TownListener;
import com.chingo247.settlercraft.towny.listener.TownyDebugListener;
import com.chingo247.settlercraft.towny.restriction.TownyRestriction;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.object.Coord;
import com.sk89q.worldedit.Vector2D;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Chingo
 */
public class SettlerCraftTowny extends JavaPlugin {

    @Override
    public void onEnable() {
        if(Bukkit.getPluginManager().getPlugin("Towny") == null) {
            System.out.println("[SettlerCraft]: Couldn't find towny!");
            System.out.println("[SettlerCraft]: Disabling");
            return;
        }
        
        if(Bukkit.getPluginManager().getPlugin("SettlerCraft-StructureAPI") == null) {
            System.out.println("[SettlerCraft]: Couldn't find SettlerCraft-StructureAPI!");
            System.out.println("[SettlerCraft]: Disabling");
            return;
        }
        
        // Towny Restriction
        StructureAPI.getInstance().addRestriction(new TownyRestriction());
        
        SettlerCraft settlerCraft = SettlerCraft.getInstance();
        
        Bukkit.getPluginManager().registerEvents(new TownyDebugListener(), this);
        Bukkit.getPluginManager().registerEvents(new TownListener(settlerCraft.getNeo4j(), settlerCraft.getExecutor()), this);
        
    }
    
    public static Vector2D translate(Coord coord) {
        int blockSize = TownySettings.getTownBlockSize();
        int x = coord.getX() * blockSize;
        int z = coord.getZ() * blockSize;
        return new Vector2D(x,z);
    }
    
    
    
    
}
