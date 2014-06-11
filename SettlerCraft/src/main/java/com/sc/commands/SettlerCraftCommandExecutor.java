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
package com.sc.commands;

import com.cc.plugin.api.menu.MenuManager;
import com.cc.plugin.api.menu.SCVaultEconomyUtil;
import com.cc.plugin.api.menu.ShopCategoryMenu;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.sc.entity.QStructure;
import com.sc.entity.Structure;
import com.sc.persistence.AbstractService;
import com.sc.persistence.HibernateUtil;
import com.sc.persistence.StructureService;
import com.sc.plugin.SettlerCraft;
import com.sc.structure.StructurePlanManager;
import com.sc.structure.construction.ConstructionProcess.State;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author Chingo
 */
public class SettlerCraftCommandExecutor implements CommandExecutor {

    private final SettlerCraft settlerCraft;
    private static final int MAX_LINES = 10;
    private static final String CMD = "/sc";

    public SettlerCraftCommandExecutor(SettlerCraft settlercraft) {
        this.settlerCraft = settlercraft;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
        if(! (cs instanceof Player)) {
            cs.sendMessage("You are not a player!"); // Command is issued from server console
            return true;
        }
        
        if (args.length == 0) {
            cs.sendMessage(ChatColor.RED + "Too few arguments");
            return true;
        } else {
            Player player = (Player) cs;
            switch (args[0]) {
                case "menu":
                    if (!settlerCraft.isPlanMenuEnabled()) {
                        cs.sendMessage(ChatColor.RED + "Planmenu is disabled");
                        return true;
                    }
                    if (!MenuManager.getInstance().hasMenu(StructurePlanManager.PLANSHOP)) {
                        cs.sendMessage(ChatColor.RED + "Planmenu is not loaded yet");
                        return true;
                    }

                    // HAS PERMISSION
                    if (args.length == 1) {
                        return openPlanMenu(player);
                    } else {
                        cs.sendMessage(ChatColor.RED + "Too many arguments!");
                        return true;
                    }
                case "shop":
                    if (!settlerCraft.isPlanShopEnabled()) {
                        cs.sendMessage(ChatColor.RED + "Planshop is disabled");
                        return true;
                    }
                    if (!MenuManager.getInstance().hasMenu(StructurePlanManager.PLAN_MENU)) {
                        cs.sendMessage(ChatColor.RED + "Planshop is not loaded yet");
                        return true;
                    }

                    // HAS PERMISSION
                    if (args.length == 1) {
                        return openShopMenu(player);
                    } else {
                        cs.sendMessage(ChatColor.RED + "Too many arguments!");
                        return false;
                    }

                case "refund":
                    return refund(player, args);
                default:
                    return false;
            }
        }
    }

    private boolean openPlanMenu(Player player) {
        ShopCategoryMenu menu = (ShopCategoryMenu) MenuManager.getInstance().getMenu(StructurePlanManager.PLAN_MENU);
        menu.onEnter(player);
        return true;
    }

    private boolean openShopMenu(Player player) {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            player.sendMessage(ChatColor.RED + " Planshop requires Vault to work");
            return false;
        }
        ShopCategoryMenu menu = (ShopCategoryMenu) MenuManager.getInstance().getMenu(StructurePlanManager.PLANSHOP);
        menu.onEnter(player);
        return true;
    }
    
    private boolean listRefundables(Player player, String[] args) {
        int index;
            Player ply = null;
            if (args.length == 2) {
                index = 1;
                ply = null;
            } else if (args.length == 3) {
                try {
                    index = Integer.parseInt(args[2]);
                } catch (NumberFormatException nfe) {
                    ply = Bukkit.getPlayer(args[2]);
                    if (ply == null) {
                        player.sendMessage(ChatColor.RED + "Third argument needs to be an index or player");
                        return true;
                    }
                    index = 1;
                }
            } else if (args.length == 4) {
                ply = Bukkit.getPlayer(args[2]);
                if (ply == null) {
                    player.sendMessage(ChatColor.RED + "Player not found: " + args[1]);
                    return true;
                }
                try {
                    index = Integer.parseInt(args[3]);
                } catch (NumberFormatException nfe) {
                    player.sendMessage(ChatColor.RED + "No valid index");
                    return true;
                }
            } else {
                player.sendMessage(ChatColor.RED + "Too many arguments!");
                return true;
            }
            List<Structure> structures;
            if (ply == null) {
                structures = getRefundable();
            } else {
                structures = getRefundable(ply);
            }

            if (structures.isEmpty()) {
                player.sendMessage(ChatColor.RED + "There are currently no tasks that need to be refunded");
                return true;
            } else {
                int amountOfRefundables = structures.size();
                String[] message = new String[MAX_LINES];
                int pages = (amountOfRefundables / (MAX_LINES - 1)) + 1;
                if (index > pages) {
                    player.sendMessage(ChatColor.RED + "Max page is " + pages);
                    return true;
                }

                message[0] = "-----------(Page: " + (index) + "/" + ((amountOfRefundables / (MAX_LINES - 1)) + 1) + " Structures: " + amountOfRefundables + ")-----------";
                int line = 1;
                int startIndex = (index - 1) * (MAX_LINES - 1);
                for (int i = startIndex; i < startIndex + (MAX_LINES - 1) && i < amountOfRefundables; i++) {
                    Structure structure = structures.get(i);
                    String value = toPriceString(structure.getRefundValue());
                    String l = "#" + ChatColor.GOLD + structure.getId() + ChatColor.RESET + " value: " + ChatColor.GOLD + value + ChatColor.RESET + " owned by: " + ChatColor.GREEN + structure.getOwner();
                    message[line] = l;
                    line++;
                }
                player.sendMessage(message);
                return true;
            }

        
    }
    
    private void refund(Structure structure, boolean talk) {
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            Economy economy = SCVaultEconomyUtil.getInstance().getEconomy();
            if (economy != null) {
                Player player = Bukkit.getPlayer(structure.getOwner());
                if (player != null) {
                    economy.depositPlayer(structure.getOwner(), structure.getRefundValue());
                    if (player.isOnline() && talk) {
                        player.sendMessage(new String[]{
                            "Refunded " + ChatColor.BLUE + structure.getPlan().getDisplayName() + ChatColor.GOLD + structure.getRefundValue(),
                            ChatColor.RESET + "Your new balance: " + ChatColor.GOLD + economy.getBalance(player.getName())
                        });
                    }
                }
            }
        }
    }
    
    private void refundAll(Player player) {
        List<Structure> structures = SettlerCraftCommandExecutor.this.getRefundable(player);
        
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSession();
            tx = session.beginTransaction();
            double totalRefunded = 0;
            for(Structure s : structures) {
                refund(s, false);
                totalRefunded += s.getRefundValue();
                s.setRefundValue(0d);
                session.merge(s.getProgress());
            }
            
            if(player.isOnline()) {
                player.sendMessage("You have been refunded " + ChatColor.GOLD + totalRefunded);
            }
            
            
            tx.commit();
        } catch (HibernateException e) {
            try {
                tx.rollback();
            } catch (HibernateException rbe) {
                Logger.getLogger(AbstractService.class.getName()).log(Level.SEVERE, "Couldn’t roll back transaction", rbe);
            }
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
    
    private boolean refundPlayer(Player player, String[] args) {
        if(args.length <= 2) {
            player.sendMessage(ChatColor.RED + "Too few arguments!");
            return true;
        } else if (args.length == 3) {
            Player ply = Bukkit.getPlayer(args[1]);
            if(ply == null) {
                player.sendMessage(ChatColor.RED + "Player unknown: " + args[1]);
                return true;
            }
            if(args[2].equalsIgnoreCase("all")) {
                refundAll(player);
            } else {
                Long id;
                try {
                    id = Long.parseLong(args[2]);
                    
                } catch(NumberFormatException nfe) {
                    player.sendMessage(new String[]{
                        ChatColor.RED + "Invalid use of command, should be: ",
                        ChatColor.RED + CMD + " refund " + args[1] + " [id]",
                        ChatColor.RED + "or: " + CMD + " refund all"
                    });
                    return true;
                }
                StructureService ss = new StructureService();
                Structure structure = ss.getStructure(id);
                if(structure == null) {
                    player.sendMessage(ChatColor.RED + " Structure #" + ChatColor.GOLD + id + ChatColor.RED + " doesn't exist");
                    return true;
                } else {
                    if(ply.getName().equals(structure.getOwner())) {
                    refund(structure, true); // auto talks
                    structure.setRefundValue(0d);
                    ss.save(structure);
                    } else {
                        player.sendMessage(ChatColor.RED + ply.getName() + " is not the owner of this structure");
                    }
                }
                
            }
            
        }
        
        return true;
    }

    private boolean refund(Player player, String[] args) {
        if (!player.isOp()) {
            player.sendMessage(ChatColor.RED + "You are not OP");
            return true;
        }
        
        if(args.length == 1) {
            player.sendMessage(ChatColor.RED + "Too few arguments!");
            return true;
        } else if (args.length >= 2 && args.length < 5) {
            if(args[1].equals("list")) {
                return listRefundables(player, args);
            } else {
               String ply = args[1];
               if(Bukkit.getPlayer(ply) == null) {
                   player.sendMessage(ChatColor.RED + "Unknown player: " + args[1]);
                   return true;
               } else {
                   return refundPlayer(player, args);
               }
            }
        } else {
            player.sendMessage(ChatColor.RED + "Too many arguments");
            return true;
        }
    }


    private String toPriceString(double value) {
        if (value < 1000) {
            return String.valueOf(value);
        } else if (value < 1E6) {
            return String.valueOf(Math.round(value / 1E3)) + "K";
        } else {
            return String.valueOf(Math.round(value / 1E6)) + "M";
        }
    }

    /**
     * Gets all the tasks that have been removed, but haven't been refunded (requires vault to
     * refund)
     *
     * @return A list of unrefunded tasks
     */
    public List<Structure> getRefundable() {
        Session session = HibernateUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);
        QStructure qs = QStructure.structure;
        List<Structure> structures = query.from(qs).orderBy(qs.progress().removedAt.desc())
                .where(
                        qs.progress().progressStatus.eq(State.REMOVED)
                        .and(qs.refundValue.gt(0))
                        .and(qs.progress().autoRemoved.eq(Boolean.TRUE)))
                .list(qs);
        session.close();
        return structures;
    }

    /**
     * Gets all the tasks that have been removed, but haven't been refunded (requires vault to
     * refund) from speficied owner
     *
     * @param owner The owner
     * @return A list of unrefunded tasks
     */
    public List<Structure> getRefundable(Player owner) {
        Session session = HibernateUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);
        QStructure qs = QStructure.structure;
        List<Structure> structures = query.from(qs).orderBy(qs.progress().removedAt.desc())
                .where(
                        qs.progress().progressStatus.eq(State.REMOVED)
                        .and(qs.owner.eq(owner.getName()))
                        .and(qs.refundValue.gt(0))
                        .and(qs.progress().autoRemoved.eq(Boolean.TRUE))
                ).list(qs);
        session.close();
        return structures;
    }

}
