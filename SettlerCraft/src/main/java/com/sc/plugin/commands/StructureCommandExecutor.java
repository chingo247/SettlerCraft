/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.plugin.commands;

import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.sc.api.structure.SCStructureAPI;
import com.sc.api.structure.entity.QStructure;
import com.sc.api.structure.entity.Structure;
import com.sc.api.structure.entity.progress.ConstructionTask;
import com.sc.api.structure.persistence.HibernateUtil;
import com.sc.api.structure.persistence.service.StructureService;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.hibernate.Session;

/**
 *
 * @author Chingo
 */
public class StructureCommandExecutor implements CommandExecutor {

    private static final int MAX_LINES = 10;
    private static final String CMD = "/stt";

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
        if (args.length == 0) {
            cs.sendMessage(ChatColor.RED + "Too few arguments");
            return false;
        }
        String arg = args[0];
        Player player = (Player) cs;
        switch (arg) {
            case "info":
                return displayInfo(player, args);
            case "list":
                return displayStructures(player, args);
            case "demolish":
                return demolish(player, args);
            default:
                player.sendMessage(ChatColor.RED + "No actions known for: " + arg);
                return false;
        }
    }

    private boolean displayStructures(Player player, String[] args) {
        List<Structure> structures;
        if (args.length == 1) {
            structures = listStructures(player);
        } else if (args.length >= 2 && args.length < 4) { // 2 || 3
            String playerName = args[1];
            Player ply = Bukkit.getPlayer(playerName);
            if (ply == null) {
                player.sendMessage(ChatColor.RED + "Unknown player: " + playerName);
                return true;
            } else {
                structures = listStructures(ply);
            }
        } else {
            player.sendMessage(ChatColor.RED + "Too many arguments!");
            return true;
        }
        int amountOfStructures = structures.size();
        if (amountOfStructures == 0) {
            if (args.length >= 2 && args[1].equals(player.getName()) || args.length == 1) {
                player.sendMessage(ChatColor.RED + "You don't own any completed structure...");
                return true;
            } else {
                player.sendMessage(ChatColor.RED + args[1] + " doesn't own any completed structure...");
                return true;
            }
        } else {
            int index;
            Player ply;
            if (args.length == 1) {
                index = 1;
                ply = player;
            } else if (args.length == 2) {
                try {
                    index = Integer.parseInt(args[1]);
                } catch (NumberFormatException nfe) {
                    ply = Bukkit.getPlayer(args[1]);
                    if (ply == null) {
                        player.sendMessage(ChatColor.RED + "Second argument should either be an index or player");
                        return true;
                    }
                    index = 1;
                }
            } else { // args == 3
                ply = Bukkit.getPlayer(args[1]);
                if (ply == null) {
                    player.sendMessage(ChatColor.RED + "Unknown player: " + args[1]);
                    return true;
                }

                try {
                    index = Integer.parseInt(args[2]);
                } catch (NumberFormatException nfe) {
                    ply = Bukkit.getPlayer(args[1]);
                    if (ply == null) {
                        player.sendMessage(ChatColor.RED + "Third argument must be an index");
                        return true;
                    }
                    index = 1;
                }
            }
            String[] message = new String[MAX_LINES];
            int pages = (amountOfStructures / (MAX_LINES - 1)) + 1;
            if (index > pages) {
                player.sendMessage(ChatColor.RED + "Max page is " + pages);
                return true;
            }

            message[0] = "-----------(Page: " + (index) + "/" + ((amountOfStructures / (MAX_LINES - 1)) + 1) + " Structures: "+amountOfStructures+")-----------";
            int line = 1;
            int startIndex = (index - 1) * (MAX_LINES - 1);
            for (int i = startIndex; i < startIndex + (MAX_LINES - 1) && i < structures.size(); i++) {
                Structure structure = structures.get(i);
                String l = "ID: " + ChatColor.GOLD + structure.getId() + " " + ChatColor.BLUE + structure.getPlan().getDisplayName() + ChatColor.RESET + " RegionID: " + ChatColor.GOLD + structure.getStructureRegion();
                message[line] = l;
                line++;
            }
            player.sendMessage(message);
        }
        return true;
    }

    private List<Structure> listStructures(Player player) {
        Session session = HibernateUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);
        QStructure qs = QStructure.structure;
        List<Structure> structures = query.from(qs).where(qs.owner.eq(player.getName()).and(qs.task().constructionState.eq(ConstructionTask.State.COMPLETE))).list(qs);
        session.close();
        return structures;
    }

    private boolean demolish(Player player, String[] args) {
        StructureService ss = new StructureService();
        Structure structure;
        if(args.length < 2) {
            player.sendMessage(new String[]{
                ChatColor.RED + "Too few arguments!",
                ChatColor.RED + CMD + " demolish [id]"
            });
            return true;
        } else if(args.length == 2) {
            Long id;
            try {
                id = Long.parseLong(args[1]);
            } catch(NumberFormatException nfe) {
                player.sendMessage(ChatColor.RED + "No valid ID");
                return true;
            }
            structure = ss.getStructure(id);
            if(structure == null) {
                player.sendMessage(ChatColor.RED + "No structure found with id: " + id);
                return true;
            }
        } else {
            player.sendMessage(new String[]{
                ChatColor.RED + "Too many arguments!",
                ChatColor.RED + CMD + " demolish [id]"
            });
            return true;
        }
        
        if(SCStructureAPI.owns(player, structure)) {
            SCStructureAPI.demolish(player, structure);
            player.sendMessage("Structure " + structure.getId() + " (" + structure.getPlan().getDisplayName() + ") will be demolished soon");
            return true;
        } else {
            player.sendMessage(ChatColor.RED + "You don't have ownership of this structure!");
            return true;
        }
        
    }

    private boolean displayInfo(Player player, String[] args) {
        StructureService service = new StructureService();
        Structure structure = service.getStructure(player.getLocation());
        
        if(args.length == 1) {
            if(structure == null) {
                player.sendMessage(ChatColor.RED + " Currently not within a structure");
            } else {
                player.sendMessage("#" + structure.getId() + " " + structure.getPlan().getDisplayName() + " owner: " + structure.getOwner());
            }
            return true;
        } else if (args.length == 4) {
            int x;
            int y; 
            int z;
            try {
                x = Integer.parseInt(args[1]);
                y = Integer.parseInt(args[2]);
                z = Integer.parseInt(args[3]);
            } catch (NumberFormatException nfe) {
                player.sendMessage(ChatColor.RED + "Invalid coordinates");
                return true;
            }
            
            Location location = new Location(player.getWorld(), x, y, z);
            structure = service.getStructure(location);
            if(structure == null) {
                player.sendMessage(ChatColor.RED + "No structure found at coordinates " + x + " " + y + " " + z);
                return true;
            } else {
                player.sendMessage("ID: " + ChatColor.GOLD + structure.getId() + " " + ChatColor.BLUE + structure.getPlan().getDisplayName() + ChatColor.RESET + " owner: " + ChatColor.AQUA + structure.getOwner());
                return true;
            }
        } else {
            if(args.length < 4) {
                player.sendMessage(ChatColor.RED + "Too few arguments!");
            } else {
                player.sendMessage(ChatColor.RED + "Too many arguments!");
            }
            return true;
            
        }
        
        
        
    }

}
