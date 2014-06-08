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
import com.cc.plugin.api.menu.ShopCategoryMenu;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.sc.api.structure.ConstructionProcess.State;
import com.sc.api.structure.QStructure;
import com.sc.plugin.SettlerCraft;
import com.sc.api.structure.Structure;
import com.sc.persistence.HibernateUtil;
import java.util.List;
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

    public SettlerCraftCommandExecutor(SettlerCraft settlercraft) {
        this.settlerCraft = settlercraft;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
        if (args.length == 0) {
            cs.sendMessage(ChatColor.RED + "Too few arguments");
            return false;
        } else {
            Player player = (Player) cs;
            switch (args[0]) {
                case "menu":
                    if (!settlerCraft.isPlanMenuEnabled()) {
                        cs.sendMessage(ChatColor.RED + "Planmenu is disabled");
                        return true;
                    }
                    if(!MenuManager.getInstance().hasMenu(SettlerCraft.PLANSHOP)) {
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
                    if(!MenuManager.getInstance().hasMenu(SettlerCraft.PLANSHOP)) {
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

                case "bin":
                    if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
                        cs.sendMessage(ChatColor.RED + " refund is not possible without vault");
                        return false;
                    }
                    return recycleBin(player, args);
                default:
                    return false;
            }
        }
    }

    private boolean openPlanMenu(Player player) {
        ShopCategoryMenu menu = (ShopCategoryMenu) MenuManager.getInstance().getMenu(SettlerCraft.PLAN_MENU_NAME);
        menu.onEnter(player);
        return true;
    }

    private boolean openShopMenu(Player player) {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            player.sendMessage(ChatColor.RED + " Planshop requires Vault to work");
            return false;
        }
        ShopCategoryMenu menu = (ShopCategoryMenu) MenuManager.getInstance().getMenu(SettlerCraft.PLANSHOP);
        menu.onEnter(player);
        return true;
    }

    private boolean recycleBin(Player player, String[] args) {
        if (!player.isOp()) {
            player.sendMessage(ChatColor.RED + "You are not OP");
            return true;
        }
        int index;
        Player ply = null;
        if (args.length == 1) {
            index = 1;
            ply = null;
        } else if (args.length == 2) {
            try {
                index = Integer.parseInt(args[1]);
            } catch (NumberFormatException nfe) {
                ply = Bukkit.getPlayer(args[1]);
                if (ply == null) {
                    player.sendMessage(ChatColor.RED + "Second argument needs to be an index or player");
                    return true;
                }
                index = 1;
            }
        } else if (args.length == 3) {
            ply = Bukkit.getPlayer(args[1]);
            if (ply == null) {
                player.sendMessage(ChatColor.RED + "Player not found: " + args[1]);
                return true;
            }
            try {
                index = Integer.parseInt(args[2]);
            } catch (NumberFormatException nfe) {
                player.sendMessage(ChatColor.RED + "No valid index");
                return true;
            }
        } else {
            player.sendMessage(ChatColor.RED + "Too many arguments!");
            return true;
        }
        List<Structure> structures;
        if(ply == null) {
            structures = getRemoved();
        } else {
            structures = getRemoved(ply);
        }
        
        if(structures.isEmpty()) {
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

            message[0] = "-----------(Page: " + (index) + "/" + ((amountOfRefundables / (MAX_LINES - 1)) + 1) + " Structures: "+amountOfRefundables+")-----------";
            int line = 1;
            int startIndex = (index - 1) * (MAX_LINES - 1);
            for (int i = startIndex; i < startIndex + (MAX_LINES - 1) && i < amountOfRefundables; i++) {
                Structure structure = structures.get(i);
                String value = toPriceString(structure.getProgress().getRefundValue());
                String l = "#" + ChatColor.GOLD + structure.getId() + ChatColor.RESET + " value: " + ChatColor.GOLD + value + ChatColor.RESET + " owned by: " + ChatColor.GREEN + structure.getOwner();
                message[line] = l;
                line++;
            }
            player.sendMessage(message);
            return true;
        }
        
    

        
    }
    
    private String toPriceString(double value) {
        if(value < 1000) {
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
    public List<Structure> getRemoved() {
        Session session = HibernateUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);
        QStructure qs = QStructure.structure;
        List<Structure> structures = query.from(qs).orderBy(qs.progress().removedAt.desc())
                .where(
                        qs.progress().progressStatus.eq(State.REMOVED)
                        .and(qs.progress().refundValue.gt(0)))
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
    public List<Structure> getRemoved(Player owner) {
        Session session = HibernateUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);
        QStructure qs = QStructure.structure;
        List<Structure> structures = query.from(qs).orderBy(qs.progress().removedAt.desc())
                .where(
                        qs.progress().progressStatus.eq(State.REMOVED)
                        .and(qs.owner.eq(owner.getName()))
                        .and(qs.progress().refundValue.gt(0)))
                .list(qs);
        session.close();
        return structures;
    }

}
