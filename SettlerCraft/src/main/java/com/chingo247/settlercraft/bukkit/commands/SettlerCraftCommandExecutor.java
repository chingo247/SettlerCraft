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

import com.chingo247.settlercraft.exception.SettlerCraftException;
import com.chingo247.settlercraft.persistence.HibernateUtil;
import com.chingo247.settlercraft.persistence.StructureService;
import com.chingo247.settlercraft.plugin.ConfigProvider;
import com.chingo247.settlercraft.plugin.PermissionManager;
import com.chingo247.settlercraft.plugin.SettlerCraft;
import com.chingo247.settlercraft.structure.entities.structure.PlayerOwnership;
import com.chingo247.settlercraft.structure.entities.structure.QPlayerOwnership;
import com.chingo247.settlercraft.structure.entities.structure.QStructure;
import com.chingo247.settlercraft.structure.entities.structure.Structure;
import com.chingo247.settlercraft.structure.entities.structure.Structure.State;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.sc.module.menuapi.menus.menu.CategoryMenu;
import com.sc.module.menuapi.menus.menu.util.EconomyUtil;
import com.sc.module.menuapi.menus.menu.util.ShopUtil;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    private final SettlerCraft settlerCraft;
    private static final int MAX_LINES = 10;
    private static final String CMD = "/sc";
    private final ChatColor CCC = ChatColor.DARK_PURPLE;

    public SettlerCraftCommandExecutor(SettlerCraft settlercraft) {
        this.settlerCraft = settlercraft;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {

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
                    CategoryMenu planmenu = SettlerCraft.getInstance().getPlanMenu();
                    if(planmenu == null) {
                        cs.sendMessage(ChatColor.RED + " Planmenu is initialized yet, please wait...");
                        return true;
                    }

                    if (!planmenu.isEnabled()) {
                        cs.sendMessage(ChatColor.RED + " Planmenu is not ready yet, please wait");
                        return true;
                    }


                    // HAS PERMISSION
                    if (args.length == 1) {
                        planmenu.openMenu(player, true);
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
                    
                    if(EconomyUtil.getInstance().getEconomy() == null) {
                         cs.sendMessage(ChatColor.RED + "This command requires an Economy plugin");
                    }

                    if (!ConfigProvider.getInstance().isPlanShopEnabled()) {
                        cs.sendMessage(ChatColor.RED + "Planshop is disabled");
                        return true;
                    }

                 

                    CategoryMenu planmenu2 = SettlerCraft.getInstance().getPlanMenu();

                    if(planmenu2 == null) {
                        cs.sendMessage(ChatColor.RED + " Planmenu is initialized yet, please wait...");
                        return true;
                    }

                    if (!planmenu2.isEnabled()) {
                        cs.sendMessage(ChatColor.RED + " Planmenu is not ready yet, please wait");
                        return true;
                    }

                    // HAS PERMISSION
                    if (args.length == 1) {
                        planmenu2.openMenu(player2);
                        return true;
                    } else {
                        cs.sendMessage(ChatColor.RED + "Too many arguments!");
                        return true;
                    }
                case "list":
                    listRefundables(cs, args);
                    return true;

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
                    // Check if should reload plans
                    if (args.length == 2 && args[1].equalsIgnoreCase("plans")) {
                        //TODO FIX PLANS
//                        SettlerCraft.getInstance().reloadPlans();
                        return true;
                    }

                    if (args.length == 2 && args[1].equalsIgnoreCase("config")) {
                        try {
                            ConfigProvider.getInstance().load();
                            cs.sendMessage(SettlerCraft.MSG_PREFIX + "Config reloaded");
                        } catch (SettlerCraftException ex) {
                            Logger.getLogger(SettlerCraftCommandExecutor.class.getName()).log(Level.SEVERE, null, ex);
                            cs.sendMessage(ChatColor.RED + ex.getMessage());
                        }
                        return true;
                    }
                    String action = "";
                    for (String s : args) {
                        action += s + " ";
                    }
                    cs.sendMessage(ChatColor.RED + "No actions known for: " + action);
                    return true;

                default:
                    String actionLast = "";
                    for (String s : args) {
                        actionLast += s + " ";
                    }
                    cs.sendMessage(ChatColor.RED + "No actions known for: " + actionLast);
                    return true;
            }
        }
    }

    private boolean listRefundables(CommandSender sender, String[] args) {
        int index;
        String ply = null;
        if (args.length == 2) {
            index = 1;
        } else if (args.length == 3) {
            try {
                index = Integer.parseInt(args[2]);
            } catch (NumberFormatException nfe) {
                sender.sendMessage("Invalid index");
                sender.sendMessage("Usage: " + CCC + CMD + "refund list [index] - List all refundable structures of all players");
                return true;
            }
        } else if (args.length == 4) {
            try {
                index = Integer.parseInt(args[2]);
            } catch (NumberFormatException nfe) {
                sender.sendMessage("Invalid index");
                sender.sendMessage("Usage: " + CCC + CMD + "refund list [index][player] - List all refundable structures of a player");
                return true;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Too many arguments!");
            sender.sendMessage(new String[]{
                "Usage: ",
                CCC + CMD + "refund list [index] - List all refundable structures of all players",
                CCC + CMD + "refund list [index][player] - List all refundable structures of a player",});
            return true;
        }
        List<Structure> structures;
        // List all
        if(ply != null) {
            Player player = Bukkit.getPlayer(ply.trim());
            structures = getRefundables(player);
        } else {
            structures = getRefundables();
        }

        if (structures.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "There are currently no structures that need to be refunded");
            return true;
        } else {
            int amountOfRefundables = structures.size();
            String[] message = new String[MAX_LINES];
            int pages = (amountOfRefundables / (MAX_LINES - 1)) + 1;
            if (index > pages || index <= 0) {
                sender.sendMessage(ChatColor.RED + "Page " + index + " out of " + pages + "...");
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
            sender.sendMessage(message);
            return true;
        }

    }

    private void refundAll(CommandSender sender) {
        // Note: only gets the structures with a refundValue > 0
        List<Structure> structures = getRefundables();

        if (structures.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "No refundable structures found");
            return;
        }
        
        if(Bukkit.getPluginManager().getPlugin("Vault") == null) {
            sender.sendMessage(ChatColor.RED + "Vault was not found...");
            return;
        }
        
        if(EconomyUtil.getInstance().getEconomy() == null) {
            sender.sendMessage(ChatColor.RED + "No economy plugin was found...");
            return;
        }

        for (Structure s : structures) {
            makeDeposit(sender, s, false);
        }

    }
    
    private void refund(CommandSender sender, Structure structure) {
        
        if(Bukkit.getPluginManager().getPlugin("Vault") == null) {
            sender.sendMessage(ChatColor.RED + "Vault was not found...");
            return;
        }
        
        if(EconomyUtil.getInstance().getEconomy() == null) {
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
                    new StructureService().save(structure);
                }
                if (talk) {
                    sender.sendMessage("Deposited " + ChatColor.GOLD + ShopUtil.valueString(Math.floor(structure.getRefundValue() / structure.getOwnerships(PlayerOwnership.Type.FULL).size())) + ChatColor.RESET + " to all owners");
                }
            
        
    }

    private boolean refund(CommandSender sender, String[] args) {
        if (args.length == 1) {
            sender.sendMessage(ChatColor.RED + "Too few arguments!");
            sender.sendMessage(new String[]{
                "Usage: ",
                CCC + CMD + " refund list [index] " + ChatColor.RESET + "- List refundable structures of all players",
                CCC + CMD + " refund [id] " + ChatColor.RESET + "- refunds the structure to the owners",
                CCC + CMD + " refund all " + ChatColor.RESET + "- refunds all structures that were \"autoremoved\""
            });
            return true;
        } else if (args.length >= 2 && args.length < 5) {
            if (args[1].equalsIgnoreCase("list")) {
                return listRefundables(sender, args);
            } else if(args[1].equalsIgnoreCase("all")){
                refundAll(sender);
                return true;
            } else {
                long sid;
                try {
                    sid = Integer.parseInt(args[1]);
                } catch (NumberFormatException nfe) {
                    sender.sendMessage(ChatColor.RED + "Invalid id");
                    return true;
                }
                StructureService service = new StructureService();
                Structure structure = service.getStructure(sid);
                if(structure == null) {
                    sender.sendMessage(ChatColor.RED + "No structure found with id #" + sid + "...");
                    return true;
                }
                
                if(structure.getState() != Structure.State.REMOVED) {
                    sender.sendMessage(ChatColor.RED + "Structure isn't removed...");
                    return true;
                }
                
                if(!structure.getLog().isAutoremoved()) {
                    sender.sendMessage(ChatColor.RED + "This structure can not be refunded...");
                    return true;
                }
                refund(sender, structure);
                return true;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Too many arguments");
            sender.sendMessage(new String[]{
                "Usage: ",
                CCC + CMD + " refund list [index] " + ChatColor.RESET + "- List refundable structures of all players",
                CCC + CMD + " refund [id] " + ChatColor.RESET + "- refunds the structure to the owners",
                CCC + CMD + " refund all " + ChatColor.RESET + "- refunds all structures that were \"autoremoved\""
            });
            return true;
        }
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

//    /**
//     * Gets all the tasks that have been removed, but haven't been refunded (requires vault to
//     * refund) from speficied owner
//     *
//     * @param owner The owner
//     * @return A list of unrefunded tasks
//     */
//    public List<Structure> getRefundables(UUID owner) {
//        Session session = HibernateUtil.getSession();
//        JPQLQuery query = new HibernateQuery(session);
//        QPlayerOwnership qpo = QPlayerOwnership.playerOwnership;
//        List<Structure> structures = query.from(qpo).orderBy(qpo.structure().logEntry().removedAt.desc())
//                .where(
//                        qpo.structure().state.eq(State.REMOVED)
//                        .and(qpo.player.eq(owner))
//                        .and(qpo.structure().refundValue.gt(0))
//                        .and(qpo.structure().logEntry().autoremoved.eq(Boolean.TRUE))
//                ).list(qpo.structure());
//        session.close();
//        return structures;
//    }
}
