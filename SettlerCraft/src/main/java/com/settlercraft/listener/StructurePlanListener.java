/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.listener;

import com.settlercraft.main.SettlerCraft;
import com.settlercraft.main.StructurePlanRegister;
import com.settlercraft.model.structure.StructurePlan;
import com.settlercraft.action.BuildAction;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author Chingo
 */
public class StructurePlanListener implements Listener {
    private final SettlerCraft settlerCraft;
    
    public StructurePlanListener(SettlerCraft settlerCraft) {
      this.settlerCraft = settlerCraft;
    }
  
    @EventHandler
    public void onPlayerBuild(PlayerInteractEvent pie) {
      System.out.println("PlayerInteractEvent!");
      if(pie.getItem() == null || pie.getItem().getType() != Material.PAPER) return;
      StructurePlan plan = StructurePlanRegister.getPlan(pie.getItem().getItemMeta().getDisplayName());
      if(plan != null 
              && pie.getClickedBlock() != null 
              && pie.getClickedBlock().getType() != Material.AIR) {
        System.out.println("Building");
        BuildAction ba = new BuildAction(settlerCraft);
        ba.placeStructure(pie.getPlayer(), pie.getClickedBlock().getLocation(),plan);
      }
    }
    
}
