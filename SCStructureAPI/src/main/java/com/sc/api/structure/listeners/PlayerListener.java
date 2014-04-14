/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * DEBUG. Check byte and material values
 *
 * @author Chingo
 */
public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerPlacedBlock(BlockPlaceEvent bpe) {
        System.out.println(
                "Player placed: " + bpe.getBlock().getType()
                + " data: " + bpe.getBlock().getData()
                + " durability: " + bpe.getBlock().getType().getMaxDurability()
                + " material data: " + bpe.getBlock().getType().getData()
        );
    }

    @EventHandler
    public void onPlayerBuildEvent(PlayerInteractEvent pie) {
//        if (pie.getItem() != null
//                && pie.getClickedBlock() != null
//                && pie.getItem().getItemMeta() != null
//                && pie.getItem().getItemMeta().getDisplayName() != null
//                && pie.getItem().getItemMeta().getDisplayName().equals(SettlerCraftTools.CONSTRUCTION_TOOL)) {
//            System.out.println("CONSTRUCTION TOOL!");
//            pie.setCancelled(true);                             // Cancel default action which will destroy blocks
//            if (pie.getAction() != Action.LEFT_CLICK_BLOCK) {
//                return;
//            }
//            StructureService service = new StructureService();
//            boolean onStructure = service.isOnStructure(pie.getClickedBlock().getLocation());
//            System.out.println("ON STRUCTURE: " + onStructure);
//            if (onStructure) {
//                // BUILD!
//            }
//        }
        
    }
}
