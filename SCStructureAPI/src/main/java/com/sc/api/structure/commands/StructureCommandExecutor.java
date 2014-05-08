/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure.commands;

import com.sc.api.structure.model.structure.Structure;
import com.sc.api.structure.model.structure.StructureState;
import com.sc.api.structure.persistence.StructureService;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Chingo
 */
public class StructureCommandExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
        if (cs instanceof Player) {
            Player player = (Player) cs;

            String arg = args[0];
            switch (arg) {
                case "structure":
                    return tellInfo(player);
                case "construct":
                    return construct(player, args);
                default:
                    player.sendMessage("no action known for " + cmnd.getName() + " " + arg);
                    return false;
            }
        } else {
            return false;
        }
    }

    private boolean tellInfo(Player player) {
        StructureService ss = new StructureService();
        Structure structure = ss.getStructure(player.getLocation());
        if (structure == null) {
            player.sendMessage(ChatColor.RED + "[SC] U are not above a structure");
            return false;
        } else {
            player.sendMessage(ChatColor.YELLOW + "[SC] INFO: \n"
                    + "id: " + structure.getId() + "\n"
                    + "owner: " + structure.getOwner() + "\n"
//                    + "plan: " + structure.getPlan().getName() + "\n"
                    + "orientation: " + structure.getDirection()
            );
            return true;
        }
    }

    private boolean construct(Player player, String[] args) {
        if (!player.isOp()) {
            player.sendMessage(ChatColor.RED + " YOU ARE NOT OP");
            return false;
        }

        switch (args[1]) {
            case "current":
                return constructCurrent(player);
            case "complete":
                return constructComplete(player, args);
            default:
                return false;
        }
    }

    private boolean constructComplete(Player player, String[] args) {
//        StructureBuilder.BuildDirection bd;
//        if (args.length == 2) {
//            switch (args[2].toLowerCase()) {
//                case "down":
//                    bd = StructureBuilder.BuildDirection.DOWN;
//                    break;
//                case "up":
//                    bd = StructureBuilder.BuildDirection.UP;
//                    break;
//                default:
//                    player.sendMessage(ChatColor.RED + SCStructureAPI.ALIAS + ": Dont know argument " + args[2]);
//                    return false;
//            }
//        } else if (args.length > 2) {
//            player.sendMessage(ChatColor.RED + SCStructureAPI.ALIAS + ": Too many arguments!");
//            return false;
//        } else {
//            bd = StructureBuilder.BuildDirection.UP;
//        }
//
//        StructureService ss = new StructureService();
//        if (ss.isOnStructure(player.getLocation())) {
//            Structure structure = ss.getStructure(player.getLocation());
//            if (structure.getStatus() != StructureState.FINISHING) {
//                StructureBuilder.complete(structure, bd, false, true);
////                player.sendMessage(ChatColor.GREEN + "Finishing " + structure.getPlan().getName());
//                return true;
//            } else {
//                player.sendMessage(ChatColor.BLUE + "Structure already trying to finish, wait for it to complete");
//                return false;
//            }
//
//        } else {
//            player.sendMessage(ChatColor.RED + "You must stand on a structure");
            return false;
//        }
    }

    private boolean constructCurrent(Player player) {
        StructureService ss = new StructureService();
        if (ss.isOnStructure(player.getLocation())) {
            Structure structure = ss.getStructure(player.getLocation());
            if (structure.getStatus() != StructureState.COMPLETE) {
//                StructureBuilder.layer(structure, structure.getProgress().getLayer(), true);
//                player.sendMessage(ChatColor.GREEN + "constructing current  " + structure.getPlan().getName());
                return true;
            } else {
                player.sendMessage(ChatColor.BLUE + "structure already complete...");
                return false;
            }

        } else {
            player.sendMessage(ChatColor.RED + "you must stand on a structure");
            return false;
        }
    }


    public static ItemStack setName(ItemStack stack, String name) {
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(name);
        stack.setItemMeta(meta);
        return stack;
    }

}
