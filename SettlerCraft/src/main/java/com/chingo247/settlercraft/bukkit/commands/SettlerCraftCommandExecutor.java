/*
 * Copyright (C) 2014 Chingo247
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.chingo247.settlercraft.bukkit.commands;

import com.chingo247.menu.CategoryMenu;
import com.chingo247.menu.util.EconomyUtil;
import com.chingo247.menu.util.ShopUtil;
import com.chingo247.settlercraft.bukkit.PermissionManager;
import com.chingo247.settlercraft.bukkit.SettlerCraftPlugin;
import com.chingo247.settlercraft.structure.PlayerOwnership;
import com.chingo247.settlercraft.structure.QPlayerOwnership;
import com.chingo247.settlercraft.structure.QStructure;
import com.chingo247.settlercraft.structure.Structure;
import com.chingo247.settlercraft.structure.Structure.State;
import com.chingo247.settlercraft.structure.exception.CommandException;
import com.chingo247.settlercraft.structure.persistence.hibernate.HibernateUtil;
import com.chingo247.settlercraft.structure.persistence.hibernate.StructureDAO;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import java.util.List;
import java.util.Set;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.hibernate.Session;

/**
 *
 * @author Chingo
 */
public class SettlerCraftCommandExecutor implements CommandExecutor {

    private static final int MAX_LINES = 10;
    private final SettlerCraftPlugin settlerCraft;
    private final StructureDAO structureDAO = new StructureDAO();
    
    public SettlerCraftCommandExecutor(SettlerCraftPlugin settlerCraft) {
        this.settlerCraft = settlerCraft;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {

        if (args.length == 0) {
            cs.sendMessage(ChatColor.RED + "Too few arguments");
            return true;
        }

        try {
            switch (args[0]) {
                case "menu":
                    openMenu(cs, args);
                    break;
                case "shop":
                    openShop(cs, args);
                    break;
                case "refund":
                    refund(cs, args);
                    break;
                case "reload":
                    reload(cs, args);
                    break;
                default:
                    String actionLast = "";
                    for (String s : args) {
                        actionLast += s + " ";
                    }
                    cs.sendMessage(ChatColor.RED + "No actions known for: " + actionLast);
                    return true;
            }
        } catch (CommandException e) {
            cs.sendMessage(e.getPlayerErrorMessage());
        }
        return true;
    }

    private void openMenu(CommandSender sender, String[] args) throws CommandException {
        if (args.length > 1) {
            throw new CommandException("Too many arguments");
        }

        if (!(sender instanceof Player)) {
            throw new CommandException("You are not a player!");
        }

        if (!settlerCraft.getConfigProvider().isPlanMenuEnabled()) {
            throw new CommandException("Planmenu is disabled");
        }

        Player player = (Player) sender;
        if (!PermissionManager.isAllowed(player, PermissionManager.Perms.OPEN_PLAN_MENU)) {
            throw new CommandException("You have no permission to open the planmenu");
        }

        CategoryMenu planmenu = SettlerCraftPlugin.getInstance().getPlanMenu();
        if (planmenu == null) {
            throw new CommandException("Planmenu is initialized yet, please wait...");
        }

        if (!planmenu.isEnabled()) {
            throw new CommandException("Planmenu is not ready yet, please wait");
        }
        planmenu.openMenu(player, true);
    }

    private void openShop(CommandSender sender, String[] args) throws CommandException {
        if (args.length > 1) {
            throw new CommandException("Too many arguments");
        }

        if (!(sender instanceof Player)) {
            throw new CommandException("You are not a player!");
        }

        if (!settlerCraft.getConfigProvider().isPlanShopEnabled()) {
            throw new CommandException("Planshop is disabled");
        }

        Player player = (Player) sender;
        if (!PermissionManager.isAllowed(player, PermissionManager.Perms.OPEN_PLAN_MENU)) {
            throw new CommandException("You have no permission to open the plan shop");
        }

        CategoryMenu planmenu = SettlerCraftPlugin.getInstance().getPlanMenu();
        if (planmenu == null) {
            throw new CommandException("Planmenu is initialized yet, please wait...");
        }

        if (!planmenu.isEnabled()) {
            throw new CommandException("Planmenu is not ready yet, please wait");
        }
        planmenu.openMenu(player, false);
    }

    private void refund(CommandSender sender, String[] args) throws CommandException {
        if (sender instanceof Player) {
            if (!((Player) sender).isOp()) {
                throw new CommandException("You are not OP");
            }
        }
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            throw new CommandException(ChatColor.RED + "Vault was not found...");
        }
        if (EconomyUtil.getInstance().getEconomy() == null) {
            throw new CommandException("No economy plugin was found...");
        }

        if (args.length == 1) {
            throw new CommandException(new String[]{"Too few arguments",
                "Usage: ",
                "refund list [index] " + ChatColor.RESET + "- List refundable structures of all players",
                "refund [id] " + ChatColor.RESET + "- refunds the structure to the owners",
                "refund all " + ChatColor.RESET + "- refunds all structures that were \"autoremoved\""
            });
        } else if (args.length >= 2 && args.length < 5) {
            if (args[1].equalsIgnoreCase("list")) {
                listRefundables(sender, args);
            } else if (args[1].equalsIgnoreCase("all")) {
                refundAll(sender);
            } else {
                long sid;
                try {
                    sid = Integer.parseInt(args[1]);
                } catch (NumberFormatException nfe) {
                    throw new CommandException("Invalid id");
                }
                Structure structure = structureDAO.find(sid);
                if (structure == null) {
                    throw new CommandException("No structure found with id #" + sid + "...");
                }

                if (structure.getState() != Structure.State.REMOVED) {
                    throw new CommandException("Structure isn't removed...");
                }

                if (!structure.getLog().isAutoremoved()) {
                    throw new CommandException("This structure can not be refunded...");
                }
                refundStructure(sender, structure);
            }
        } else {
            throw new CommandException(new String[]{
                "Too many arguments",
                "Usage: ",
                "refund list [index] " + ChatColor.RESET + "- List refundable structures of all players",
                "refund [id] " + ChatColor.RESET + "- refunds the structure to the owners",
                "refund all " + ChatColor.RESET + "- refunds all structures that were \"autoremoved\""
            });
        }
    }

    private void listRefundables(CommandSender sender, String[] args) throws CommandException {
        int index;
        String ply = null;
        if (args.length == 2) {
            index = 1;
        } else if (args.length == 3) {
            try {
                index = Integer.parseInt(args[2]);
            } catch (NumberFormatException nfe) {
                throw new CommandException(new String[]{"Invalid index", "Usage: refund list [index] - List all refundable structures of all players"});
            }
        } else if (args.length == 4) {
            try {
                index = Integer.parseInt(args[2]);
            } catch (NumberFormatException nfe) {
                throw new CommandException(new String[]{"Invalid index", "Usage: refund list [index] - List all refundable structures of all players"});
            }
        } else {
            throw new CommandException(new String[]{
                "Too many arguments!",
                "Usage: ",
                "refund list [index] - List all refundable structures of all players",
                "refund list [index][player] - List all refundable structures of a player",});
        }
        List<Structure> structures;
        // List all
        if (ply != null) {
            Player player = Bukkit.getPlayer(ply.trim());
            structures = getRefundables(player);
        } else {
            structures = getRefundables();
        }

        if (structures.isEmpty()) {
            sender.sendMessage(SettlerCraftPlugin.MSG_PREFIX + "There are currently no structures that need to be refunded");
        } else {
            int amountOfRefundables = structures.size();
            String[] message = new String[MAX_LINES];
            int pages = (amountOfRefundables / (MAX_LINES - 1)) + 1;
            if (index > pages || index <= 0) {
                throw new CommandException(ChatColor.RED + "Page " + index + " out of " + pages + "...");
            }

            message[0] = "-----------(Page: " + (index) + "/" + ((amountOfRefundables / (MAX_LINES - 1)) + 1) + " Structures: " + amountOfRefundables + ")-----------";
            int line = 1;
            int startIndex = (index - 1) * (MAX_LINES - 1);
            for (int i = startIndex; i < startIndex + (MAX_LINES - 1) && i < amountOfRefundables; i++) {
                Structure structure = structures.get(i);
                String value = ShopUtil.valueString(structure.getRefundValue());
                String l = "#" + ChatColor.GOLD + structure.getId() + ChatColor.RESET + " value: " + ChatColor.GOLD + value;
                message[line] = l;
                line++;
            }
            sender.sendMessage(message);
        }

    }

    private void refundAll(CommandSender sender) {
        // Note: only gets the structures with a refundValue > 0
        List<Structure> structures = getRefundables();

        if (structures.isEmpty()) {
            sender.sendMessage(SettlerCraftPlugin.MSG_PREFIX + "No structures that need to be refunded...");
            return;
        }

        for (Structure s : structures) {
            makeDeposit(sender, s, false);
        }

    }

    private void refundStructure(CommandSender sender, Structure structure) {

        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            sender.sendMessage(ChatColor.RED + "Vault was not found...");
            return;
        }

        if (EconomyUtil.getInstance().getEconomy() == null) {
            sender.sendMessage(ChatColor.RED + "No economy plugin was found...");
            return;
        }

        makeDeposit(sender, structure, true);

    }

    private void makeDeposit(CommandSender sender, Structure structure, boolean talk) {

        Set<PlayerOwnership> ownerships = structure.getOwnerships(PlayerOwnership.Type.FULL);
        if (structure.getRefundValue() > 0) {
            double refunding = Math.floor(structure.getRefundValue() / ownerships.size());

            for (PlayerOwnership po : ownerships) {
                Player player = Bukkit.getPlayer(po.getPlayerUUID());
                if (player != null) {
                    Economy economy = EconomyUtil.getInstance().getEconomy();
                    economy.depositPlayer(player.getName(), refunding);
                    if (player.isOnline()) {
                        player.sendMessage(new String[]{
                            "Refunded #" + ChatColor.GOLD + structure.getId() + " "
                            + ChatColor.BLUE + structure.getName() + " "
                            + ChatColor.GOLD + ShopUtil.valueString(structure.getRefundValue()),
                            ChatColor.RESET + "Your new balance: " + ChatColor.GOLD + ShopUtil.valueString(economy.getBalance(player.getName()))
                        });
                    }
                }
            }
            structure.setRefundValue(0d);
            structureDAO.save(structure);
        }
        if (talk) {
            sender.sendMessage("Deposited " + ChatColor.GOLD + ShopUtil.valueString(Math.floor(structure.getRefundValue() / structure.getOwnerships(PlayerOwnership.Type.FULL).size())) + ChatColor.RESET + " to all owners");
        }

    }

    private void reload(CommandSender sender, String[] args) throws CommandException {
        if (sender instanceof Player) {
            if (((Player) sender).isOp()) {
                throw new CommandException("You are not OP");
            }
        }
        SettlerCraftPlugin.getInstance().reloadConfig();
//        // Check if should reload plans
//        if (args.length == 2 && args[1].equalsIgnoreCase("plans")) {
//          SettlerCraftPlugin.getInstance().reloadPlans();
//            return true;
//        }
//
//        if (args.length == 2 && args[1].equalsIgnoreCase("config")) {
//            try {
//                ConfigProvider.getInstance().load();
//                sender.sendMessage(SettlerCraftPlugin.MSG_PREFIX + "Config reloaded");
//            } catch (SettlerCraftException ex) {
//                Logger.getLogger(SettlerCraftCommandExecutor.class.getName()).log(Level.SEVERE, null, ex);
//                sender.sendMessage(ChatColor.RED + ex.getMessage());
//            }
//            return true;
//        }
//        String action = "";
//        for (String s : args) {
//            action += s + " ";
//        }
//        sender.sendMessage(ChatColor.RED + "No actions known for: " + action);
    }

    /**
     * Gets all the tasks that have been removed, but haven't been refunded (requires vault to
     * refund)
     *
     * @return A list of unrefunded tasks
     */
    public List<Structure> getRefundables() {
        Session session = HibernateUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);
        QStructure qs = QStructure.structure;

        List<Structure> structures = query.from(qs).orderBy(qs.logEntry().removedAt.desc())
                .where(
                        qs.state.eq(State.REMOVED)
                        .and(qs.refundValue.gt(0))
                        .and(qs.logEntry().autoremoved.eq(Boolean.TRUE))
                ).list(qs);
        session.close();
        return structures;
    }

    public List<Structure> getRefundables(Player player) {
        Session session = HibernateUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);
        QPlayerOwnership qpo = QPlayerOwnership.playerOwnership;

        List<Structure> structures = query.from(qpo).orderBy(qpo.structure().logEntry().removedAt.desc())
                .where(
                        qpo.structure().state.eq(State.REMOVED)
                        .and(qpo.structure().refundValue.gt(0))
                        .and(qpo.structure().logEntry().autoremoved.eq(Boolean.TRUE))
                ).list(qpo.structure());
        session.close();
        return structures;
    }

}
