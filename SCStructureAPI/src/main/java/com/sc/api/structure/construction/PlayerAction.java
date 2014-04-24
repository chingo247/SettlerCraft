/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.api.structure.construction;

import com.google.common.base.Preconditions;
import com.sc.api.structure.event.build.PlayerBuildEvent;
import com.settlercraft.core.model.entity.structure.Structure;
import com.settlercraft.core.persistence.StructureService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Chingo
 */
public class PlayerAction  {
    
    private final Player player;
    
    PlayerAction(Player player) {
        Preconditions.checkNotNull(player);
        this.player = player;
    }

    public void build(Structure structure, int amount) {
        BuildAction action = new BuildAction(structure);
        action.build(player.getInventory(), amount, new BuildAction.BuildCallback() {

            @Override
            public void onSucces(Structure structure, ItemStack deposit) {
                player.sendMessage(SCStructureAPI.ALIAS + ": Removed " + deposit.getAmount() + " " + deposit.getType().name() + " from your inventory");
                player.updateInventory();
                Bukkit.getPluginManager().callEvent(new PlayerBuildEvent(structure, player, deposit));
            }

            @Override
            public void onNotInBuildState(Structure structure) {
                switch(structure.getStatus()) {
                    case FINISHING:
                        player.sendMessage(ChatColor.GOLD + SCStructureAPI.ALIAS + ": Structure is currently finishing and doesn't require anymore resources");
                        break;
                    case COMPLETE:
                        player.sendMessage(ChatColor.GOLD + SCStructureAPI.ALIAS + ": Structure already Complete!");
                        break;
                    default:player.sendMessage(ChatColor.RED + SCStructureAPI.ALIAS + ": Structure currently in progress, wait for it to be done...");
                        break;
                }
               
            }

            @Override
            public void onResourcesNotRequired(Structure structure) {
                 player.sendMessage(ChatColor.RED + "[SCS]: Structure currently needs: \n" + structure.getProgress().toString());
            }
        });
    }
    
        /**
     * Will try to place the structure, the operation is succesful if the structure doesn't
     * "overlap" any other structure.
     *
     * @param structure The structure to place
     * @return True if operation was succesful, otherwise false
     */
    public boolean place(Structure structure) {
        StructureService ss = new StructureService();
        if (ss.overlaps(structure)) {
            if (player.isOnline()) {
                player.sendMessage(ChatColor.RED + SCStructureAPI.ALIAS + ": Structure overlaps another structure");
            }
            return false;
        } 
        ss.save(structure);
        return true;
    }
    
}
