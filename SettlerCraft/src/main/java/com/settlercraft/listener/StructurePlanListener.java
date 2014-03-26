/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.listener;

import com.settlercraft.building.Builder;
import com.settlercraft.main.SettlerCraft;
import com.settlercraft.main.StructurePlanRegister;
import com.settlercraft.model.structure.StructurePlan;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author Chingo
 */
public class StructurePlanListener implements Listener {
    private SettlerCraft settlerCraft;
    private StructurePlanRegister spRegister;
    
    public StructurePlanListener(SettlerCraft settlerCraft) {
      this.settlerCraft = settlerCraft;
      this.spRegister = settlerCraft.getDefaultStructurePlanRegister();
    }
  
    @EventHandler
    public void onPlayerBuild(PlayerInteractEvent pie) {
      System.out.println("PlayerInteractEvent!");
      if(pie.getItem().getType() != Material.PAPER) return;
      StructurePlan plan = spRegister.getPlan(pie.getItem().getItemMeta().getDisplayName());
      System.out.println("Plan: " + plan.getConfig().getName());
      if(plan != null 
              && pie.getClickedBlock() != null 
              && pie.getClickedBlock().getType() != Material.AIR) {
        System.out.println("Building");
        Builder.buildStructure(pie.getPlayer(), plan, pie.getClickedBlock().getLocation());
      }
      
      
    }
    
}
