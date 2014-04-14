/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.api.structure.listeners;

import com.settlercraft.core.manager.StructurePlanManager;
import com.settlercraft.core.model.entity.structure.Structure;
import com.settlercraft.core.model.entity.structure.construction.Builder;
import com.settlercraft.core.model.plan.StructurePlan;
import com.settlercraft.core.util.LocationUtil;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Chingo
 */
public class StructurePlanListener implements Listener {
    private final JavaPlugin settlerCraft;

    
    public StructurePlanListener(JavaPlugin settlerCraft) {
        this.settlerCraft = settlerCraft;
    }
  
    @EventHandler
    public void onPlayerBuild(PlayerInteractEvent pie) {
      System.out.println("PlayerInteractEvent!");
      if(pie.getItem() == null || pie.getItem().getType() != Material.PAPER) return;
      StructurePlan plan = StructurePlanManager.getInstance().getPlan(pie.getItem().getItemMeta().getDisplayName());
      if(plan != null 
              && pie.getClickedBlock() != null 
              && pie.getClickedBlock().getType() != Material.AIR) {
        System.out.println("Building");
        Builder.placeStructure(new Structure(
                pie.getPlayer(),                            // Who: Player
                pie.getClickedBlock().getLocation(),        // Where: Location
                LocationUtil.getDirection(pie.getPlayer()), // Direction?
                plan));                                     // What?
      }
    }
    
}
