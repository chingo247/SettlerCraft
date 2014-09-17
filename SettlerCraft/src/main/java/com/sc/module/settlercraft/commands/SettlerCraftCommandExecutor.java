/*
 * Copyright (C) 2014 Chingo
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
package com.sc.module.settlercraft.commands;

import com.sc.module.menuapi.menus.menu.util.ShopUtil;
import com.sc.module.settlercraft.plugin.SettlerCraft;
import com.sc.module.structureapi.persistence.StructureService;
import com.sc.module.structureapi.structure.Structure;
import com.sc.plugin.ConfigProvider;
import com.sc.plugin.PermissionManager;
import com.sc.plugin.SettlerCraftException;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Chingo
 */
public class SettlerCraftCommandExecutor implements CommandExecutor {

    private final SettlerCraft settlerCraft;
    private static final int MAX_LINES = 10;
    private static final String CMD = "/sc";
    private final ChatColor CCC = ChatColor.DARK_PURPLE;

    public SettlerCraftCommandExecutor(SettlerCraft settlercraft) {
        this.settlerCraft = settlercraft;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {

        if (!(cs instanceof Player)) {
            cs.sendMessage("You are not a player!"); // Command is issued from server console
            return true;
        }

        if (args.length == 0) {
            cs.sendMessage(ChatColor.RED + "Too few arguments");
            return true;
        } else {

            switch (args[0]) {
                case "menu":
                    if (!(cs instanceof Player)) {
                        cs.sendMessage("You are not a player!"); // Command is issued from server console
                        return true;
                    }
                    Player player = (Player) cs;
                    if (!PermissionManager.isAllowed(player, PermissionManager.Perms.OPEN_PLAN_MENU)) {
                        player.sendMessage(ChatColor.RED + "You have no permission to open this menu");
                        return true;
                    }
                    if (!ConfigProvider.getInstance().isPlanMenuEnabled()) {
                        cs.sendMessage(ChatColor.RED + "Planmenu is disabled");
                        return true;
                    }
                    if(SettlerCraft.getInstance().getPlanMenu() == null || !SettlerCraft.getInstance().getPlanMenu().isEnabled()) {
                        cs.sendMessage(ChatColor.RED + " Planmenu is not enabled");
                        return true;
                    }

                    // HAS PERMISSION
                    if (args.length == 1) {
                        SettlerCraft.getInstance().getPlanMenu().openMenu(player);
                        return true;
                    } else {
                        cs.sendMessage(ChatColor.RED + "Too many arguments!");
                        return true;
                    }
                case "shop":
                    if (!(cs instanceof Player)) {
                        cs.sendMessage("You are not a player!"); // Command is issued from server console
                        return true;
                    }
                    Player player2 = (Player) cs;
                    if (!PermissionManager.isAllowed(player2, PermissionManager.Perms.OPEN_PLAN_SHOP)) {
                        player2.sendMessage(ChatColor.RED + "You have no permission to open this menu");
                        return true;
                    }

                    if (!ConfigProvider.getInstance().isPlanShopEnabled()) {
                        cs.sendMessage(ChatColor.RED + "Planshop is disabled");
                        return true;
                    }


                    // HAS PERMISSION
                    if (args.length == 1) {
//                        return openShopMenu(player2);
                    } else {
                        cs.sendMessage(ChatColor.RED + "Too many arguments!");
                        return true;
                    }

                case "refund":

                    if (cs instanceof Player) {
                        Player player3 = (Player) cs;
                        if (!player3.isOp()) {
                            player3.sendMessage(ChatColor.RED + "You are not OP");
                            return true;
                        }
                    }
                    return refund(cs, args);
                case "reload":
                    if (cs instanceof Player) {
                        Player player3 = (Player) cs;
                        if (!player3.isOp()) {
                            player3.sendMessage(ChatColor.RED + "You are not OP");
                            return true;
                        }
                    }

                    try {
                        ConfigProvider.getInstance().load();
                        cs.sendMessage(ChatColor.GOLD + "[SettlerCraft]:" + ChatColor.RESET + " Config reloaded");
                    } catch (SettlerCraftException ex) {
                        Logger.getLogger(SettlerCraftCommandExecutor.class.getName()).log(Level.SEVERE, null, ex);
                        cs.sendMessage(ChatColor.RED + ex.getMessage());
                    }

                    return true;

                case "test1":
                    Player player3 = (Player) cs;
                    Chunk chunk = player3.getLocation().getChunk();
                    Comparator<Block> comp = new Comparator<Block>() {

                @Override
                public int compare(Block o1, Block o2) {
                    return new Integer(o1.getTypeId()).compareTo(o2.getTypeId());
                }
            };
                    Queue<Block> queue = new PriorityQueue<>(comp);
                    long start = System.currentTimeMillis();
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 16; z++) {
                            for (int y = 0; y < 256; y++) {
                                Block block = chunk.getBlock(x, y, z);
                                if(block.getType() != Material.ANVIL){
                                    queue.add(block);
                                }
                            }
                        }
                    }
                    
                    long end = System.currentTimeMillis();
                    System.out.println("Get in: " + (end - start));
                    System.out.println(queue.size());
                    return true;
                case "test2":
                    Player player4 = (Player) cs;
                    Chunk chunk2 = player4.getLocation().getChunk();
                    long start2 = System.currentTimeMillis();
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 16; z++) {
                            for (int y = 0; y < 256; y++) {
                                Block block = chunk2.getBlock(x, y, z);
                                block.setType(Material.COBBLESTONE);
                            }
                        }
                    }
                    long end2 = System.currentTimeMillis();
                    System.out.println("Set in: " + (end2 - start2));
                    return true;
                default:
                    cs.sendMessage(ChatColor.RED + "No actions known for: " + args);
                    return true;
            }
        }
    }

//    private boolean openPlanMenu(Player player) {
//        ShopCategoryMenu menu = (ShopCategoryMenu) CategoryMenuManager.getInstance().getMenu(StructurePlanManagerV2.PLAN_MENU);
//        menu.onEnter(player);
//        return true;
//    }
//
//    private boolean openShopMenu(Player player) {
//        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
//            player.sendMessage(ChatColor.RED + " Planshop requires Vault");
//            return true;
//        }
//        ShopCategoryMenu menu = (ShopCategoryMenu) CategoryMenuManager.getInstance().getMenu(StructurePlanManagerV2.PLANSHOP);
//        menu.onEnter(player);
//        return true;
//    }

    private boolean listRefundables(CommandSender player, String[] args) {
        int index;
        String ply;
        if (args.length == 2) {
            index = 1;
            ply = null;
        } else if (args.length == 3) {
            try {
                index = Integer.parseInt(args[2]);
            } catch (NumberFormatException nfe) {
                player.sendMessage(ChatColor.RED + "Invalid index");
                return true;
            }
            ply = null;
        } else if (args.length == 4) {
            try {
                index = Integer.parseInt(args[2]);
            } catch (NumberFormatException nfe) {
                player.sendMessage(ChatColor.RED + "No valid index");
                return true;
            }
            ply = args[3];
        } else {
            player.sendMessage(ChatColor.RED + "Too many arguments!");
            player.sendMessage(new String[]{
                "Usage: ",
                CCC + CMD + " list [index] - List all refundable structures of all players",
                CCC + CMD + " list [index][player] - List all refundable structures of a player",});
            return true;
        }
        List<Structure> structures;
        // List all
        if (ply == null) {
            structures = getRefundables();
        } else {
            if (Bukkit.getPlayer(ply) == null) {
                player.sendMessage(ChatColor.RED + "Player doesn't exist!");
                return true;
            }

            // List from player
            structures = getRefundables(ply);
            if (structures.isEmpty()) {
                player.sendMessage(ChatColor.GREEN + ply + ChatColor.RED + " has no tasks that need to be refunded");
                return true;
            }
        }

        if (structures.isEmpty()) {
            player.sendMessage(ChatColor.RED + "There are currently no tasks that need to be refunded");
            return true;
        } else {
            int amountOfRefundables = structures.size();
            String[] message = new String[MAX_LINES];
            int pages = (amountOfRefundables / (MAX_LINES - 1)) + 1;
            if (index > pages || index <= 0) {
                player.sendMessage(ChatColor.RED + "Page " + index + " out of " + pages + "...");
                return true;
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
            player.sendMessage(message);
            return true;
        }

    }

    private void makeDeposit(CommandSender sender, Structure structure, boolean talk) {
//        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
//            Economy economy = SCVaultEconomyUtil.getInstance().getEconomy();
//            if (economy != null) {
//                Player player = Bukkit.getPlayer(structure.getOwner());
//                if (structure.getRefundValue() > 0) {
//                    economy.depositPlayer(structure.getOwner(), structure.getRefundValue());
//                    if (player != null && player.isOnline() && talk) {
//                        player.sendMessage(new String[]{
//                            "Refunded #" + ChatColor.GOLD + structure.getId() + " "
//                            + ChatColor.BLUE + structure.getPlan().getDisplayName() + " "
//                            + ChatColor.GOLD + SettlerCraftUtil.valueString(structure.getRefundValue()),
//                            ChatColor.RESET + "Your new balance: " + ChatColor.GOLD + SettlerCraftUtil.valueString(economy.getBalance(player.getName()))
//                        });
//                    }
//                }
//                if (talk) {
//                    sender.sendMessage("Deposited " + ChatColor.GOLD + SettlerCraftUtil.valueString(structure.getRefundValue())
//                            + ChatColor.RESET + " to " + ChatColor.GREEN + structure.getOwner());
//                }
//            } else {
//                sender.sendMessage(ChatColor.RED + "No economy plugin was found...");
//            }
//        } else {
//            sender.sendMessage(ChatColor.RED + "Vault not found...");
//        }
    }

    private void refundAll(CommandSender sender, String refunder) {
//        // Note: only gets the structures with a refundValue > 0
//        List<Structure> structures = getRefundables(refunder);
//
//        if (structures.isEmpty()) {
//            sender.sendMessage(ChatColor.RED + "No refundable structures found for " + refunder);
//            return;
//        }
//        Session session = null;
//        Transaction tx = null;
//        try {
//            session = HibernateUtil.getSession();
//            tx = session.beginTransaction();
//            double totalRefunded = 0;
//            for (Structure s : structures) {
//                makeDeposit(sender, s, false);
//                totalRefunded += s.getRefundValue();
//                s.setPrice(0d);
//                session.merge(s.getProgress());
//            }
//
//            sender.sendMessage("Deposited " + ChatColor.GOLD + SettlerCraftUtil.valueString(totalRefunded) + ChatColor.RESET + " to " + ChatColor.GREEN + refunder);
//            Player player = Bukkit.getPlayer(refunder);
//            if (player != null && player.isOnline()) {
//                sender.sendMessage("You have been refunded " + ChatColor.GOLD + SettlerCraftUtil.valueString(totalRefunded));
//            }
//
//            tx.commit();
//        } catch (HibernateException e) {
//            try {
//                tx.rollback();
//            } catch (HibernateException rbe) {
//                Logger.getLogger(AbstractService.class.getName()).log(Level.SEVERE, "Couldn’t roll back transaction", rbe);
//            }
//            throw e;
//        } finally {
//            if (session != null) {
//                session.close();
//            }
//        }
    }

    private boolean refundPlayer(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Too few arguments!");
            sender.sendMessage(new String[]{
                "Usage: ",
                CCC + CMD + " refund [id] - refund the structure",
                CCC + CMD + " refund [player] - refund all refundable structures of the player that were autoremoved"
            });
            return true;
        } else if (args.length == 2) {
            Long id;
            try {
                id = Long.parseLong(args[1]);

            } catch (NumberFormatException nfe) {
                String refunder = args[1];
                refundAll(sender, refunder);
                return true;
            }
            StructureService ss = new StructureService();
            Structure structure = ss.getStructure(id);
            if (structure == null) {
                sender.sendMessage(ChatColor.RED + " Structure #" + ChatColor.GOLD + id + ChatColor.RED + " doesn't exist");
                return true;
            } else {
                makeDeposit(sender, structure, true); // auto talks
                structure.setPrice(0d);
                ss.save(structure);
            }
        }

        return true;
    }

    private boolean refund(CommandSender player, String[] args) {
        if (args.length == 1) {
            player.sendMessage(ChatColor.RED + "Too few arguments!");
            player.sendMessage(new String[]{
                "Usage: ",
                CCC + CMD + " list [index] " + ChatColor.RESET + "- List refundable structures of all players",
                CCC + CMD + " list [index][player] " + ChatColor.RESET + "- List all refundable structures of the player",
                CCC + CMD + " refund [id] " + ChatColor.RESET + "- refunds the structure to the owner, whether they are auto-removed or not",
                CCC + CMD + " refund [player] " + ChatColor.RESET + "- refunds all refundable structure of the player that were auto-removed",});
            return true;
        } else if (args.length >= 2 && args.length < 5) {
            if (args[1].equals("list")) {
                return listRefundables(player, args);
            } else {
                return refundPlayer(player, args);
            }
        } else {
            player.sendMessage(ChatColor.RED + "Too many arguments");
            player.sendMessage(new String[]{
                "Usage: ",
                CCC + CMD + " list [index] " + ChatColor.RESET + "- List refundable structures of all players",
                CCC + CMD + " list [index][player] " + ChatColor.RESET + "- List all refundable structures of the player",
                CCC + CMD + " refund [id] " + ChatColor.RESET + "- refunds the structure to the owner, whether they are auto-removed or not",
                CCC + CMD + " refund [player] " + ChatColor.RESET + "- refunds all refundable structure of the player that were auto-removed",});
            return true;
        }
    }

//    private String toPriceString(double value) {
//        if (value < 1000) {
//            return String.valueOf(value);
//        } else if (value < 1E6) {
//            return String.valueOf(Math.round(value / 1E3)) + "K";
//        } else {
//            return String.valueOf(Math.round(value / 1E6)) + "M";
//        }
//    }
    /**
     * Gets all the tasks that have been removed, but haven't been refunded (requires vault to
     * refund)
     *
     * @return A list of unrefunded tasks
     */
    public List<Structure> getRefundables() {
//        Session session = HibernateUtil.getSession();
//        JPQLQuery query = new HibernateQuery(session);
//        QStructure qs = QStructure.structure;
//        List<Structure> structures = query.from(qs).orderBy(qs.progress().removedAt.desc())
//                .where(
//                        qs.progress().progressStatus.eq(State.REMOVED)
//                        .and(qs.refundValue.gt(0))
//                        .and(qs.progress().autoRemoved.eq(Boolean.TRUE)))
//                .list(qs);
//        session.close();
        return null;
    }

    /**
     * Gets all the tasks that have been removed, but haven't been refunded (requires vault to
     * refund) from speficied owner
     *
     * @param owner The owner
     * @return A list of unrefunded tasks
     */
    public List<Structure> getRefundables(String owner) {
//        Session session = HibernateUtil.getSession();
//        JPQLQuery query = new HibernateQuery(session);
//        QStructure qs = QStructure.structure;
//        List<Structure> structures = query.from(qs).orderBy(qs.progress().removedAt.desc())
//                .where(
//                        qs.progress().progressStatus.eq(State.REMOVED)
//                        .and(qs.owner.eq(owner))
//                        .and(qs.refundValue.gt(0))
//                        .and(qs.progress().autoRemoved.eq(Boolean.TRUE))
//                ).list(qs);
//        session.close();
        return null;
    }

}
