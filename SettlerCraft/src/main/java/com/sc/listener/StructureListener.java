/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.listener;

import com.sc.construction.structure.Structure;
import com.sc.event.structure.StructureCompleteEvent;
import com.sc.event.structure.StructureConstructionEvent;
import com.sc.event.structure.StructureDemolisionEvent;
import com.sc.event.structure.StructureRemovedEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 *
 * @author Chingo
 */
public class StructureListener implements Listener {
    
    
    @EventHandler
    public void onStructureComplete(StructureCompleteEvent sce) {
        Structure structure = sce.getStructure();
        Player player = Bukkit.getPlayer(sce.getStructure().getOwner());
        if(player != null && player.isOnline()) {
            player.sendMessage("Construction Complete #" + ChatColor.GOLD + structure.getId() + " " + ChatColor.BLUE + structure.getPlan().getDisplayName());
        }
    }
    
    @EventHandler
    public void onStructureRemove(StructureRemovedEvent sre) {
        Structure structure = sre.getStructure();
        Player player = Bukkit.getPlayer(sre.getStructure().getOwner());
        if(player != null && player.isOnline()) {
            player.sendMessage("Removed #" + ChatColor.GOLD + structure.getId() + " " + ChatColor.BLUE + structure.getPlan().getDisplayName());
        }
    }
    
    @EventHandler
    public void onStructureDemolision(StructureDemolisionEvent sde) {
        Structure structure = sde.getStructure();
        Player player = Bukkit.getPlayer(sde.getStructure().getOwner());
        if(player != null && player.isOnline()) {
            player.sendMessage("Demolishing #" + ChatColor.GOLD + structure.getId() + " " + ChatColor.BLUE + structure.getPlan().getDisplayName());
        }
    }
    
    @EventHandler
    public void onStructureConstruction(StructureConstructionEvent sce) {
        Structure structure = sce.getStructure();
        Player player = Bukkit.getPlayer(sce.getStructure().getOwner());
        if(player != null && player.isOnline()) {
            player.sendMessage("Building: #" + ChatColor.GOLD + " " + structure.getId() + " " + ChatColor.BLUE + structure.getPlan().getDisplayName());
        }
    }
    
}
