/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure.commands;

import com.cc.plugin.scshop.CategoryShop;
import com.cc.plugin.scshop.ShopManager;
import com.sc.api.structure.construction.SCStructureAPI;
import com.sc.api.structure.construction.builders.StructureBuilder;
import com.settlercraft.core.model.entity.structure.Structure;
import com.settlercraft.core.model.entity.structure.StructureState;
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
            if (!player.isOp()) {
                player.sendMessage(ChatColor.RED + " YOU ARE NOT OP");
            }

            String arg = args[0];
            switch (arg) {
                case "structure":
                    return tellInfo(player);
                case "construct":
                    return construct(player, args);
                case "reload":
                    return reloadPlans();
                case "menu":
                    return openPlanMenu(player);
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
            player.sendMessage(ChatColor.RED + "[SCS] U are not above a structure");
            return false;
        } else {
            player.sendMessage(ChatColor.YELLOW + "[SCS] INFO: \n"
                    + "id: " + structure.getId() + "\n"
                    + "owner: " + structure.getOwner() + "\n"
                    + "plan: " + structure.getPlan().getName() + "\n"
                    + "orientation: " + structure.getDirection()
            );
            return true;
        }
    }

    private boolean construct(Player player, String[] args) {
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
        StructureBuilder.BuildDirection bd;
        if (args.length == 2) {
            switch (args[2].toLowerCase()) {
                case "down":
                    bd = StructureBuilder.BuildDirection.DOWN;
                    break;
                case "up":
                    bd = StructureBuilder.BuildDirection.UP;
                    break;
                default:
                    player.sendMessage(ChatColor.RED + SCStructureAPI.ALIAS + ": Dont know argument " + args[2]);
                    return false;
            }
        } else if (args.length > 2) {
            player.sendMessage(ChatColor.RED + SCStructureAPI.ALIAS + ": Too many arguments!");
            return false;
        } else {
            bd = StructureBuilder.BuildDirection.UP;
        }

        StructureService ss = new StructureService();
        if (ss.isOnStructure(player.getLocation())) {
            Structure structure = ss.getStructure(player.getLocation());
            if (structure.getStatus() != StructureState.FINISHING) {
                SCStructureAPI.build(structure).complete(bd, false, true);
                player.sendMessage(ChatColor.GREEN + "[SCS]: finishing " + structure.getPlan().getName());
                return true;
            } else {
                player.sendMessage(ChatColor.BLUE + "[SCS]: structure already trying to finish, wait for it to complete");
                return false;
            }

        } else {
            player.sendMessage(ChatColor.RED + "[SCS]: you must stand on a structure");
            return false;
        }
    }

    private boolean constructCurrent(Player player) {
        StructureService ss = new StructureService();
        if (ss.isOnStructure(player.getLocation())) {
            Structure structure = ss.getStructure(player.getLocation());
            if (structure.getStatus() != StructureState.COMPLETE) {
                SCStructureAPI.build(structure).layer(structure.getProgress().getLayer(), true);
                player.sendMessage(ChatColor.GREEN + "[SCS]: constructing current  " + structure.getPlan().getName());
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

    private boolean openPlanMenu(Player player) {
//        ShopSessionService sss = new ShopSessionService();
//        ShopSession session = sss.getShopSession(player, PlanShop.title);
//        if(session == null) {
//            session = new ShopSession(player, PlanShop.title, 0, 0);
//        } 

        CategoryShop cs = new CategoryShop("Shop!", true);
        cs.setCategoryRow(0, true);
        cs.addCategory("General", new ItemStack(Material.WORKBENCH));
        cs.addCategory("Houses", new ItemStack(Material.BED));
        cs.addCategory("Shops", new ItemStack(Material.GOLD_INGOT));
        cs.addCategory("Temples", new ItemStack(Material.QUARTZ));
        cs.addCategory("Castles", new ItemStack(Material.SMOOTH_BRICK));
        cs.addCategory("Tower", new ItemStack(Material.LADDER));
        cs.addCategory("Dungeons&Arenas", new ItemStack(Material.IRON_SWORD));
        cs.addCategory("Misc", new ItemStack(Material.RECORD_10));
        for (int i = 0; i < 10; i++) {
            cs.addItem(StructureCommandExecutor.setName(new ItemStack(Material.PAPER), "House : " + i), "Houses");
        }
        for (int i = 0; i < 30; i++) {
            cs.addItem(StructureCommandExecutor.setName(new ItemStack(Material.PAPER), "Tower : " + i), "Tower");
        }
        for (int i = 0; i < 5; i++) {
            cs.addItem(StructureCommandExecutor.setName(new ItemStack(Material.PAPER), "Arena : " + i), "Dungeons&Arenas");
        }
        ShopManager.getInstance().register(cs);

        cs.visit(player);
        return true;
    }

    public static ItemStack setName(ItemStack stack, String name) {
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(name);
        stack.setItemMeta(meta);
        return stack;
    }

}
