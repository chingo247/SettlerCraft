/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * @author Chingo
 */
public class PlayerListener implements Listener {
    


    @EventHandler
    public void onPlayerBuildEvent(PlayerInteractEvent pie) {
//        if (pie.getItem() != null
//                && pie.getClickedBlock() != null
//                && pie.getItem().getItemMeta() != null
//                && pie.getItem().getItemMeta().getDisplayName() != null
//                && pie.getItem().getItemMeta().getDisplayName().equals(Recipes.CONSTRUCTION_TOOL)) {
//            // Cancel default action which would destroy blocks
//            pie.setCancelled(true);
//            if (pie.getAction() != Action.LEFT_CLICK_BLOCK) {
//                return;
//            }
//            StructureService service = new StructureService();
//            Structure structure = service.getStructure(pie.getClickedBlock().getLocation());
//            if (structure != null && structure.getStatus() != StructureState.COMPLETE) {
//                PlayerAction.build(pie.getPlayer(),structure, possibleSkillAmount);
//            }
//        }
        
    }
}
