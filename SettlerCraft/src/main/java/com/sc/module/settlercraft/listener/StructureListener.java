/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.module.settlercraft.listener;

import com.sc.module.structureapi.event.StructureDemolisionEvent;
import com.sc.module.structureapi.event.structure.StructureCompleteEvent;
import com.sc.module.structureapi.event.structure.StructureConstructionEvent;
import com.sc.module.structureapi.event.structure.StructureRemovedEvent;
import com.sc.module.structureapi.structure.Structure;
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
//        List<PlayerOwnership> stakeholders = structure.getInhabitants(); // people who wanna know
//
//        for (PlayerOwnership i : stakeholders) {
//            if (i.isPlayer()) {
//                Player player = Bukkit.getPlayer(i.getUUID());
//                if (player != null && player.isOnline()) {
//                    player.sendMessage("Construction Complete #" + ChatColor.GOLD + structure.getId() + " " + ChatColor.BLUE + structure.getPlan().getDisplayName());
//                }
//            }
//        }

    }

    @EventHandler
    public void onStructureRemove(StructureRemovedEvent sre) {
        Structure structure = sre.getStructure();
//        List<PlayerOwnership> stakeholders = structure.getInhabitants(); // people who wanna know

//        for (PlayerOwnership i : stakeholders) {
//            if (i.isPlayer()) {
//                Player player = Bukkit.getPlayer(i.getUUID());
//                if (player != null && player.isOnline()) {
//                    player.sendMessage("Removed #" + ChatColor.GOLD + structure.getId() + " " + ChatColor.BLUE + structure.getPlan().getDisplayName());
//                }
//            }
//        }

    }

    @EventHandler
    public void onStructureDemolision(StructureDemolisionEvent sde) {
        Structure structure = sde.getStructure();
//        List<PlayerOwnership> stakeholders = structure.getInhabitants(); // people who wanna know
//
//        for (PlayerOwnership i : stakeholders) {
//            if (i.isPlayer()) {
//                Player player = Bukkit.getPlayer(i.getUUID());
//                if (player != null && player.isOnline()) {
//                    player.sendMessage("Demolishing #" + ChatColor.GOLD + structure.getId() + " " + ChatColor.BLUE + structure.getPlan().getDisplayName());
//                }
//            }
//        }

    }

    @EventHandler
    public void onStructureConstruction(StructureConstructionEvent sce) {
//        Structure structure = sce.getStructure();
//        List<PlayerOwnership> stakeholders = structure.getInhabitants(); // people who wanna know
//
//        for (PlayerOwnership i : stakeholders) {
//            if (i.isPlayer()) {
//                Player player = Bukkit.getPlayer(i.getUUID());
//                if (player != null && player.isOnline()) {
//                    player.sendMessage("Building: #" + ChatColor.GOLD + " " + structure.getId() + " " + ChatColor.BLUE + structure.getPlan().getDisplayName());
//                }
//            }
//        }
    }

}
