/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cc.plugin.scdivision;

import com.sc.construction.structure.Structure;
import com.sc.persistence.StructureService;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerEggThrowEvent;

/**
 *
 * @author Chingo
 */
public class StructureListener {
    
    private StructureService ss;
    
    @EventHandler
    public void onChickenEggThrown(PlayerEggThrowEvent event) {
        Location location = event.getPlayer().getLocation();
        Structure structure = ss.getStructure(location);
        if(structure != null) {
            
        }
    }
    
}
