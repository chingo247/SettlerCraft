/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure.listeners;

import static com.sc.api.structure.util.WorldEditUtil.getLocalSession;
import com.sk89q.worldedit.LocalSession;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * @author Chingo
 */
public class PlayerListener implements Listener {
    
//    private int possibleSkillAmount = 5;

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent joinEvent) {
        Player player = joinEvent.getPlayer();
        LocalSession session = getLocalSession(player);
        if(!session.hasCUISupport()) {
            player.sendMessage("Hey there " + ChatColor.GOLD +player.getName()+ "!");
            player.sendMessage(ChatColor.RESET+"You are "+ ChatColor.RED +" NOT " + ChatColor.RESET + " using "+ChatColor.GOLD+ "WorldEditCUI!");
            player.sendMessage("To get an "+ChatColor.GOLD+"AWESOME "+ ChatColor.RESET+ "Client User Interface");
//            player.sendMessage("Check out: "+ ChatColor.GOLD +" http://www.minecraftforum.net/topic/2171206-172-worldeditcui/");
        }
    }

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
