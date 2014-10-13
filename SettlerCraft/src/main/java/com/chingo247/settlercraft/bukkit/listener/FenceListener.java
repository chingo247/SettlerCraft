/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.bukkit.listener;

import com.chingo247.settlercraft.persistence.StructureService;
import com.chingo247.settlercraft.structure.entities.structure.Structure;
import com.chingo247.settlercraft.structure.entities.world.Dimension;
import com.sk89q.worldedit.Vector;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

/**
 *
 * @author Chingo
 */
public class FenceListener implements Listener {
    
    private final StructureService service = new StructureService();
    
    @EventHandler
    public void onFenceBreak(BlockBreakEvent bbe) {
        if(bbe.getBlock().getType() == Material.IRON_FENCE) {
            Structure structure = service.getStructure(bbe.getBlock().getLocation());
            if(structure != null && structure.getState() != Structure.State.COMPLETE) {
                Location l = bbe.getBlock().getLocation();
                Vector v = structure.getRelativePosition(new Vector(l.getBlockX(), l.getBlockY(), l.getBlockZ()));
                Dimension dim = structure.getDimension();
                
                if(v.getBlockY() == 1 && (l.getBlockX() == dim.getMinX() || l.getBlockX() == dim.getMaxX() || l.getBlockZ() == dim.getMinZ() || l.getBlockZ() == dim.getMaxZ())) {
                    if(bbe.getPlayer() != null) {
                        bbe.getPlayer().sendMessage(ChatColor.RED + "Can't destroy fence....");
                    }
                    bbe.setCancelled(true);
                }
                
            }
        }
        
        
    }
    
}
