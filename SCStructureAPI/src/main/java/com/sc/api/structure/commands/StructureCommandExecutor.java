/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure.commands;

import com.sc.api.structure.construction.SCStructureAPI;
import com.settlercraft.core.manager.StructurePlanManager;
import com.settlercraft.core.model.entity.structure.Structure;
import com.settlercraft.core.model.entity.structure.StructureState;
import com.settlercraft.core.model.plan.StructurePlan;
import com.settlercraft.core.persistence.StructureService;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
            if(!player.isOp()) {
                player.sendMessage(ChatColor.RED + " YOU ARE NOT OP");
            }
            
            String arg = args[0];
            switch (arg) {
                case "structure":
                    return tellInfo(player);
                case "construct":
                    return construct(player, args);
                case "plan":
                    return getPlan(player, args);
                case "reload":
                    return reloadPlans();
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
        if(structure == null) {
            player.sendMessage(ChatColor.RED + "[SCS] U are not above a structure");
            return false;
        } else {
            player.sendMessage(ChatColor.YELLOW + "[SCS] INFO: \n" + 
                    "id: " + structure.getId() + "\n" +
                    "owner: " + structure.getOwner() + "\n" +
                    "plan: " + structure.getPlan().getConfig().getName() + "\n" +
                    "orientation: " + structure.getDirection()
            );
            return true;
        }
    }

    private boolean construct(Player player, String[] args) {
        switch (args[1]) {
            case "current":
                return constructCurrent(player);
            case "complete":
                return constructComplete(player);
            default:
                return false;
        }
    }

    private boolean getPlan(Player player, String[] args) {
        if(args.length != 2) {
            player.sendMessage(ChatColor.RED + "[SCS]: excpected 2 arguments");
            return false;
        }
        if(player.isOnline()) {
            StructurePlan plan = StructurePlanManager.getInstance().getPlan(args[2]);
            if(plan == null) {
                player.sendMessage(ChatColor.RED + "[SCS]: Don't know any plan called " + plan.getConfig().getName());
                return false;
            }
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(plan.getConfig().getName());
            item.setItemMeta(meta);
            player.getInventory().addItem(item);
            return true;
        } else {
            return false;
        }
    }

    private boolean constructComplete(Player player) {
        StructureService ss = new StructureService();
        if(ss.isOnStructure(player.getLocation())){
            Structure structure = ss.getStructure(player.getLocation());
            if(structure.getStatus() != StructureState.COMPLETE) {
                SCStructureAPI.build(structure).finish();
                player.sendMessage(ChatColor.GREEN + "[SCS]: finishing " + structure.getPlan().getConfig().getName());
                return true;
            } else {
                player.sendMessage(ChatColor.BLUE + "[SCS]: structure already complete...");
                return false;
            }
            
        } else {
            player.sendMessage(ChatColor.RED + "[SCS]: you must stand on a structure");
            return false;
        }
    }

    private boolean constructCurrent(Player player) {
        StructureService ss = new StructureService();
        if(ss.isOnStructure(player.getLocation())){
            Structure structure = ss.getStructure(player.getLocation());
            if(structure.getStatus() != StructureState.COMPLETE) {
                SCStructureAPI.build(structure).layer(structure.getProgress().getLayer(), true);
                player.sendMessage(ChatColor.GREEN + "[SCS]: constructing current  " + structure.getPlan().getConfig().getName());
                return true;
            } else {
                player.sendMessage(ChatColor.BLUE + "[SCS]: structure already complete...");
                return false;
            }
            
        } else {
            player.sendMessage(ChatColor.RED + "[SCS]: you must stand on a structure");
            return false;
        }
    }

    private boolean reloadPlans() {
        SCStructureAPI.reloadPlans();
        return true;
    }

}
