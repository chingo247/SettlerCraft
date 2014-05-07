/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure.construction.player;

import com.sc.api.structure.construction.builder.BuildCallback;
import com.sc.api.structure.construction.builder.StructureBuilder;
import com.sc.api.structure.event.build.PlayerBuildEvent;
import com.settlercraft.core.model.entity.structure.Structure;
import com.settlercraft.core.persistence.StructureService;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Chingo
 */
public class PlayerAction {

    public static void build(final Player player, final Structure structure, int amount) {
        StructureBuilder.build(structure, player.getInventory(), amount, new BuildCallback() {

            @Override
            public void onSucces(Structure structure, ItemStack deposit) {
                player.sendMessage("Removed " + deposit.getAmount() + " " + deposit.getType().name() + " from your inventory");
                player.updateInventory();
                Bukkit.getPluginManager().callEvent(new PlayerBuildEvent(structure, player, deposit));
            }

            @Override
            public void onNotInBuildState(Structure structure) {
                switch (structure.getStatus()) {
                    case FINISHING:
                        player.sendMessage(ChatColor.GOLD + " Structure is currently finishing and doesn't require anymore resources");
                        break;
                    case COMPLETE:
                        player.sendMessage(ChatColor.GOLD + " Structure already Complete!");
                        break;
                    default:
                        player.sendMessage(ChatColor.RED + " Structure currently in progress, wait for it to be done...");
                        break;
                }

            }

            @Override
            public void onResourcesNotRequired(Structure structure) {
                player.sendMessage(ChatColor.RED + " Structure currently needs: \n" + structure.getProgress().toString());
            }
        });
    }

    /**
     * Will try to place the structure, the operation is succesful if the
     * structure doesn't "overlap" any other structure.
     *
     * @param structure The structure to place
     * @return True if operation was succesful, otherwise false
     */
    public static boolean place(Player player, Structure structure) {
        StructureService ss = new StructureService();
        CuboidSelection cs = new CuboidSelection(structure.getDimension().getWorld(), structure.getDimension().getStart(), structure.getDimension().getEnd());

//        if (ss.overlaps(structure)) {
//            if (player.isOnline()) {
//                player.sendMessage(ChatColor.RED + SCStructureAPIPlugin.ALIAS + ": Structure overlaps another structure");
//            }
//            return false;
//        } 
//        ss.save(structure);
        return true;
    }

}
