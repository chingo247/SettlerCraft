package com.chingo247.settlercraft.bukkit.commands;

/*
 * The MIT License
 *
 * Copyright 2015 Chingo.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import com.chingo247.settlercraft.bukkit.BKPermissionManager;
import com.chingo247.settlercraft.bukkit.SettlerCraftPlugin;
import com.chingo247.settlercraft.persistence.dao.StructureDAO;
import com.chingo247.settlercraft.exception.CommandException;
import com.chingo247.settlercraft.menu.CategoryMenu;
import com.chingo247.xcore.platforms.bukkit.BukkitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
                    checkIsPlayer(cs);
                    openMenu(cs, args);
                    break;
                case "shop":
                    checkIsPlayer(cs);
                    openShop(cs, args);
                    break;
                case "refund":
//                    refund(cs, args);
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
            cs.sendMessage(e.getMessage());
        }
        return true;
    }
    
    private void notifyDebug(String command) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Debug command " + command + " is still active in /sc");
    }
    
    private void checkIsPlayer(CommandSender sender) throws CommandException {
        if(!(sender instanceof Player)) {
           throw new CommandException("This command is for player's only...");
        }
    } 

    private void openMenu(CommandSender sender, String[] args) throws CommandException {
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
        if (!BKPermissionManager.getInstance().isAllowed(player, BKPermissionManager.Perms.OPEN_PLAN_MENU)) {
            throw new CommandException("You have no permission to open the plan shop");
        }

        CategoryMenu planmenu = settlerCraft.getSettlerCraft().createPlanMenu();
        if (planmenu == null) {
            throw new CommandException("Planmenu is initialized yet, please wait...");
        }

        if (!planmenu.isEnabled()) {
            throw new CommandException("Planmenu is not ready yet, please wait");
        }
        planmenu.setNoCosts(true);
        planmenu.openMenu(new BukkitPlayer(player));
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
        if (!BKPermissionManager.getInstance().isAllowed(player, BKPermissionManager.Perms.OPEN_PLAN_MENU)) {
            throw new CommandException("You have no permission to open the plan shop");
        }

        CategoryMenu planmenu = settlerCraft.getSettlerCraft().createPlanMenu();
        if (planmenu == null) {
            throw new CommandException("Planmenu is initialized yet, please wait...");
        }

        if (!planmenu.isEnabled()) {
            throw new CommandException("Planmenu is not ready yet, please wait");
        }
        planmenu.openMenu(new BukkitPlayer(player));
    }

//    private void refund(CommandSender sender, String[] args) throws CommandException {
//        if (sender instanceof Player) {
//            if (!((Player) sender).isOp()) {
//                throw new CommandException("You are not OP");
//            }
//        }
//        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
//            throw new CommandException(ChatColor.RED + "Vault was not found...");
//        }
//        if (EconomyUtil.getInstance().getEconomy() == null) {
//            throw new CommandException("No economy plugin was found...");
//        }
//
//        if (args.length == 1) {
//            throw new CommandException(new String[]{"Too few arguments",
//                "Usage: ",
//                "refund list [index] " + ChatColor.RESET + "- List refundable structures of all players",
//                "refund [id] " + ChatColor.RESET + "- refunds the structure to the owners",
//                "refund all " + ChatColor.RESET + "- refunds all structures that were \"autoremoved\""
//            });
//        } else if (args.length >= 2 && args.length < 5) {
//            if (args[1].equalsIgnoreCase("list")) {
//                listRefundables(sender, args);
//            } else if (args[1].equalsIgnoreCase("all")) {
//                refundAll(sender);
//            } else {
//                long sid;
//                try {
//                    sid = Integer.parseInt(args[1]);
//                } catch (NumberFormatException nfe) {
//                    throw new CommandException("Invalid id");
//                }
//                Structure structure = settlerCraft.getSettlerCraft().getStructure(sid);
//                if (structure == null) {
//                    throw new CommandException("No structure found with id #" + sid + "...");
//                }
//
//                if (structure.getState() != StructureState.REMOVED) {
//                    throw new CommandException("Structure isn't removed...");
//                }
//
////                if (!structure.getLog().isAutoremoved()) {
////                    throw new CommandException("This structure can not be refunded...");
////                }
//                refundStructure(sender, structure);
//            }
//        } else {
//            throw new CommandException(new String[]{
//                "Too many arguments",
//                "Usage: ",
//                "refund list [index] " + ChatColor.RESET + "- List refundable structures of all players",
//                "refund [id] " + ChatColor.RESET + "- refunds the structure to the owners",
//                "refund all " + ChatColor.RESET + "- refunds all structures that were \"autoremoved\""
//            });
//        }
//    }

//    private void listRefundables(CommandSender sender, String[] args) throws CommandException {
//        int index;
//        String ply = null;
//        if (args.length == 2) {
//            index = 1;
//        } else if (args.length == 3) {
//            try {
//                index = Integer.parseInt(args[2]);
//            } catch (NumberFormatException nfe) {
//                throw new CommandException(new String[]{"Invalid index", "Usage: refund list [index] - List all refundable structures of all players"});
//            }
//        } else if (args.length == 4) {
//            try {
//                index = Integer.parseInt(args[2]);
//            } catch (NumberFormatException nfe) {
//                throw new CommandException(new String[]{"Invalid index", "Usage: refund list [index] - List all refundable structures of all players"});
//            }
//        } else {
//            throw new CommandException(new String[]{
//                "Too many arguments!",
//                "Usage: ",
//                "refund list [index] - List all refundable structures of all players",
//                "refund list [index][player] - List all refundable structures of a player",});
//        }
//        List<Structure> structures;
//        // List all
//        if (ply != null) {
//            Player player = Bukkit.getPlayer(ply.trim());
//            structures = getRefundables(player);
//        } else {
//            structures = getRefundables();
//        }
//
//        if (structures.isEmpty()) {
//            sender.sendMessage(SettlerCraftPlugin.MSG_PREFIX + "There are currently no structures that need to be refunded");
//        } else {
//            int amountOfRefundables = structures.size();
//            String[] message = new String[MAX_LINES];
//            int pages = (amountOfRefundables / (MAX_LINES - 1)) + 1;
//            if (index > pages || index <= 0) {
//                throw new CommandException(ChatColor.RED + "Page " + index + " out of " + pages + "...");
//            }
//
//            message[0] = "-----------(Page: " + (index) + "/" + ((amountOfRefundables / (MAX_LINES - 1)) + 1) + " Structures: " + amountOfRefundables + ")-----------";
//            int line = 1;
//            int startIndex = (index - 1) * (MAX_LINES - 1);
//            for (int i = startIndex; i < startIndex + (MAX_LINES - 1) && i < amountOfRefundables; i++) {
//                Structure structure = structures.get(i);
//                String value = ShopUtil.valueString(structure.getValue());
//                String l = "#" + ChatColor.GOLD + structure.getId() + ChatColor.RESET + " value: " + ChatColor.GOLD + value;
//                message[line] = l;
//                line++;
//            }
//            sender.sendMessage(message);
//        }
//
//    }

//    private void refundAll(CommandSender sender) {
//        // Note: only gets the structures with a refundValue > 0
//        List<Structure> structures = getRefundables();
//
//        if (structures.isEmpty()) {
//            sender.sendMessage(SettlerCraftPlugin.MSG_PREFIX + "No structures that need to be refunded...");
//            return;
//        }
//
//        for (Structure s : structures) {
//            makeDeposit(sender, s, false);
//        }
//
//    }

//    private void refundStructure(CommandSender sender, Structure structure) {
//
//        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
//            sender.sendMessage(ChatColor.RED + "Vault was not found...");
//            return;
//        }
//
//        if (EconomyUtil.getInstance().getEconomy() == null) {
//            sender.sendMessage(ChatColor.RED + "No economy plugin was found...");
//            return;
//        }
//
//        makeDeposit(sender, structure, true);
//
//    }

//    private void makeDeposit(CommandSender sender, Structure structure, boolean talk) {
//
//        Set<PlayerOwnership> ownerships = structure.getOwnerships(PlayerOwnership.Type.FULL);
//        if (structure.getRefundValue() > 0) {
//            double refunding = Math.floor(structure.getRefundValue() / ownerships.size());
//
//            for (PlayerOwnership po : ownerships) {
//                Player player = Bukkit.getPlayer(po.getPlayerUUID());
//                if (player != null) {
//                    Economy economy = EconomyUtil.getInstance().getEconomy();
//                    economy.depositPlayer(player.getName(), refunding);
//                    if (player.isOnline()) {
//                        player.sendMessage(new String[]{
//                            "Refunded #" + ChatColor.GOLD + structure.getId() + " "
//                            + ChatColor.BLUE + structure.getName() + " "
//                            + ChatColor.GOLD + ShopUtil.valueString(structure.getRefundValue()),
//                            ChatColor.RESET + "Your new balance: " + ChatColor.GOLD + ShopUtil.valueString(economy.getBalance(player.getName()))
//                        });
//                    }
//                }
//            }
//            structure.setRefundValue(0d);
//            structureDAO.save(structure);
//        }
//        if (talk) {
//            sender.sendMessage("Deposited " + ChatColor.GOLD + ShopUtil.valueString(Math.floor(structure.getRefundValue() / structure.getOwnerships(PlayerOwnership.Type.FULL).size())) + ChatColor.RESET + " to all owners");
//        }
//
//    }

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

//    /**
//     * Gets all the tasks that have been removed, but haven't been refunded
//     * (requires vault to refund)
//     *
//     * @return A list of unrefunded tasks
//     */
//    public List<Structure> getRefundables() {
//        Session session = HibernateUtil.getSession();
//        JPQLQuery query = new HibernateQuery(session);
//        QStructure qs = QStructure.structure;
//
//        List<Structure> structures = query.from(qs).orderBy(qs.logEntry().removedAt.desc())
//                .where(
//                        qs.state.eq(State.REMOVED)
//                        .and(qs.refundValue.gt(0))
//                        .and(qs.logEntry().autoremoved.eq(Boolean.TRUE))
//                ).list(qs);
//        session.close();
//        return structures;
//    }

//    public List<Structure> getRefundables(Player player) {
//        Session session = HibernateUtil.getSession();
//        JPQLQuery query = new HibernateQuery(session);
//        QPlayerOwnership qpo = QPlayerOwnership.playerOwnership;
//
//        List<Structure> structures = query.from(qpo).orderBy(qpo.structure().logEntry().removedAt.desc())
//                .where(
//                        qpo.structure().state.eq(State.REMOVED)
//                        .and(qpo.structure().refundValue.gt(0))
//                        .and(qpo.structure().logEntry().autoremoved.eq(Boolean.TRUE))
//                ).list(qpo.structure());
//        session.close();
//        return structures;
//    }

   

}
