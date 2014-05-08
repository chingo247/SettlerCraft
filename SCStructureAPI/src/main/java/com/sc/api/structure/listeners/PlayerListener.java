///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.sc.api.structure.listeners;
//
//import com.sc.api.structure.construction.player.PlayerAction;
//import com.sc.api.structure.recipe.Recipes;
//import com.settlercraft.core.model.entity.structure.Structure;
//import com.settlercraft.core.model.entity.structure.StructureState;
//import com.settlercraft.core.persistence.StructureService;
//import org.bukkit.event.EventHandler;
//import org.bukkit.event.Listener;
//import org.bukkit.event.block.Action;
//import org.bukkit.event.block.BlockPlaceEvent;
//import org.bukkit.event.player.PlayerInteractEvent;
//
///**
// * @author Chingo
// */
//public class PlayerListener implements Listener {
//    
//    private int possibleSkillAmount = 5;
//
//    @EventHandler
//    public void onPlayerPlacedBlock(BlockPlaceEvent bpe) {
//        System.out.println(
//                "Player placed: " + bpe.getBlock().getType()
//                + " data: " + bpe.getBlock().getData()
//                + " durability: " + bpe.getBlock().getType().getMaxDurability()
//                + " material data: " + bpe.getBlock().getType().getData()
//        );
//    }
//
//    @EventHandler
//    public void onPlayerBuildEvent(PlayerInteractEvent pie) {
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
//        
//    }
//}
