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
import com.chingo247.settlercraft.exception.StructureException;
import com.chingo247.settlercraft.persistence.HibernateUtil;
import com.chingo247.settlercraft.persistence.StructureService;
import com.chingo247.settlercraft.structure.StructureAPI;
import com.chingo247.settlercraft.structure.construction.restore.RollbackService;
import com.chingo247.settlercraft.structure.entities.structure.PlayerOwnership;
import com.chingo247.settlercraft.structure.entities.structure.QPlayerOwnership;
import com.chingo247.settlercraft.structure.entities.structure.Structure;
import com.chingo247.settlercraft.structure.entities.structure.Structure.State;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.sc.module.menuapi.menus.menu.util.ShopUtil;
import com.sk89q.worldedit.Vector;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
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
    private final ChatColor CCC = ChatColor.DARK_PURPLE;
    private final RollbackService service = new RollbackService();

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
        
        
        if (args.length == 0) {
            cs.sendMessage(ChatColor.RED + "Too few arguments");
            return true;
        }
        String arg = args[0];
       
        switch (arg) {
            case "info":
                return displayInfo(cs, args);
            case "list":
                return displayStructures(cs, args);
            case "pos":
                if(cs instanceof Player) {
                    return getPos((Player)cs, args);
                } else {
                    cs.sendMessage(ChatColor.RED + "You need to be a player!");
                    return true;
                }
            case "rollback": 
                rollback(cs, args);
                return true;
            
            case "owner":
                if(args.length > 4) {
                    cs.sendMessage(ChatColor.RED + "Too many arguments!");
                    return true;
                }
                
                // stt owner [id] add [player]
                if(args.length == 4 && args[2].equalsIgnoreCase("add")) {
                    addOwner(cs, args);
                    return true;
                // stt owner [id] remove [player]
                } else if(args.length == 4 && args[2].equalsIgnoreCase("remove")) {
                    removeOwner(cs, args);
                    return true;
                // stt owner [id] list
                } else if (args.length == 3 && args[2].equalsIgnoreCase("list")) {
                    displayOwners(cs, args);
                    return true;
                } else {
                    String actionLast = "";
                    for (String s : args) {
                        actionLast += s + " ";
                    }
                    cs.sendMessage(ChatColor.RED + "No actions known for: " + actionLast);
                    return true;
                }
                
                
                
                
                
                    
                        
                
//            case "flag":
//                return flag(player, args);
//            case "owner":
//                return owner(player,args);

            default:
                String actionLast = "";
                    for (String s : args) {
                        actionLast += s + " ";
                    }
                    cs.sendMessage(ChatColor.RED + "No actions known for: " + actionLast);
                return true;
        }
    }
    
    
    private void rollback(CommandSender cs, String[] args) {
        if(args.length < 2) {
            cs.sendMessage(ChatColor.RED + "Too few arguments!");
        } else if (args.length > 2) {
            cs.sendMessage(ChatColor.RED + "Too many arguments");
        }
        
        long structureId;
        try {
            structureId = Long.parseLong(args[1]);
        } catch (NumberFormatException nfe) {
            cs.sendMessage("Invalid id, '"+args[1]+"'");
            return;
        }
        StructureService ss = new StructureService();
        Structure structure = ss.getStructure(structureId);
        service.rollback(structure.getLocation().getWorld(), structure.getDimension());
        
    }

    private boolean displayStructures(CommandSender sender, String[] args) {
        List<Structure> structures;
        
        // Who's structures do we want?
        if (args.length < 3) { // =>> ME!
            if(sender instanceof Player) {
                structures = getStructures((Player)sender);
            } else {
                sender.sendMessage(ChatColor.RED + "No player name was provided!");
                return true;
            }
        } else if (args.length == 3) { // 2 || 3
            String playerName = args[2];
            Player ply = Bukkit.getPlayer(playerName);
            if (ply == null) {
                sender.sendMessage(ChatColor.RED + "Player doesn't exist!");
                return true;
            } else {
                structures = getStructures(ply);
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Too many arguments!");
            sender.sendMessage(new String[]{
                "Usage:",
                CCC + CMD + " list " + ChatColor.RESET + " - Display a list of your structure at index 1",
                CCC + CMD + " list [index] " + ChatColor.RESET + " - Display a list of your structures at given index",
                CCC + CMD + " list [index][player] " + ChatColor.RESET + " - Display a list of structures of a player at given index",
            });
            return true;
        }
        
        
        int amountOfStructures = structures.size();
        if (amountOfStructures == 0) {
            // Nothing to display...
            sender.sendMessage(ChatColor.RED + "No structures found...");
            return true;
        } else {
            // Now get the index
            int index;
            Player ply;
            if (args.length == 1) {
                index = 1;
            } else /*if (args.length <= 3) already done above */{ 
                try {
                    index = Integer.parseInt(args[1]);
                } catch (NumberFormatException nfe) {
                  sender.sendMessage(ChatColor.RED + "Invalid index");
                  return true;
                }
            } 
            String[] message = new String[MAX_LINES];
            int pages = (amountOfStructures / (MAX_LINES - 1)) + 1;
             if (index > pages || index <= 0) {
                sender.sendMessage(ChatColor.RED + "Page " + index + " out of " + pages +"...");
                return true;
            } 
            

            message[0] = "-----------(Page: " + (index) + "/" + ((amountOfStructures / (MAX_LINES - 1)) + 1) + ", Structures: " + amountOfStructures + ")-----------";
            int line = 1;
            int startIndex = (index - 1) * (MAX_LINES - 1);
            for (int i = startIndex; i < startIndex + (MAX_LINES - 1) && i < structures.size(); i++) {
                Structure structure = structures.get(i);
                String l = "#" + ChatColor.GOLD + structure.getId() + " " + ChatColor.BLUE + structure.getName()+ ChatColor.RESET
                        + " " + ChatColor.YELLOW + "X: " + ChatColor.RESET + structure.getLocation().getX()
                        + " " + ChatColor.YELLOW + "Y: " + ChatColor.RESET + structure.getLocation().getY()
                        + " " + ChatColor.YELLOW + "Z: " + ChatColor.RESET + structure.getLocation().getZ()
                        + " " + ChatColor.RESET + "Value: " + ChatColor.GOLD + ShopUtil.valueString(structure.getRefundValue());
                message[line] = l;
                line++;
            }
            sender.sendMessage(message);
        }
        return true;
    }

    private List<Structure> getStructures(Player player) {
        Session session = HibernateUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);
        QPlayerOwnership qpo = QPlayerOwnership.playerOwnership;
        List<Structure> structures = query.from(qpo).where(qpo.player.eq(player.getUniqueId()).and(qpo.structure().state.ne(State.REMOVED))).list(qpo.structure());
        session.close();
        return structures;
    }

    private boolean displayInfo(CommandSender sender, String[] args) {
        StructureService service = new StructureService();
        Structure structure;

        if (args.length == 2) {
            Long id;
            try {
                id = Long.parseLong(args[1]);
            } catch (NumberFormatException nfe) {
                sender.sendMessage(ChatColor.RED + "Invalid id");
                return true;
            }
            structure = service.getStructure(id);
            if(structure == null) {
                sender.sendMessage(ChatColor.RED + "No structure found with id #" + id);
                return true;
            }
        } else if (args.length == 1 && (sender instanceof Player)) {
            Player player = (Player) sender;
            structure = service.getStructure(player.getLocation());
            if (structure == null) {
                sender.sendMessage(ChatColor.RED + " Currently not within a structure");
                return true;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Too many arguments!");
            return true;
        }

        String valueString = ShopUtil.valueString(structure.getRefundValue());

        sender.sendMessage("#" + ChatColor.GOLD + structure.getId() + " "
                + ChatColor.BLUE + structure.getName()+ ChatColor.RESET
                + " Value: " + ChatColor.GOLD + valueString + ChatColor.WHITE + " State: " + statusString(structure.getState()));
        return true;
    }
    
    public String statusString(State state) {
        String statusString;
      
        switch (state) {
            case BUILDING:
                statusString = ChatColor.GOLD + "BUILDING " + ChatColor.RESET;
                break;
            case DEMOLISHING:
                statusString = ChatColor.GOLD + "DEMOLISHING " + ChatColor.RESET;
                break;
            case COMPLETE:
                statusString = ChatColor.GREEN + "COMPLETE"  + ChatColor.RESET;
                break;
            case INITIALIZING:
                statusString = ChatColor.DARK_PURPLE + "INITIALIZING " + ChatColor.RESET;
                break;
            case LOADING_SCHEMATIC:
                statusString = ChatColor.DARK_PURPLE + "LOADING SCHEMATIC " + ChatColor.RESET;
                break;
            case PLACING_FENCE:
                statusString = ChatColor.DARK_PURPLE + "PLACING FENCE " + ChatColor.RESET;
                break;
            case QUEUED:
                statusString = ChatColor.DARK_PURPLE + "QUEUED " + ChatColor.RESET ;
                break;
            case REMOVED:
                statusString = ChatColor.RED + "REMOVED " + ChatColor.RESET;
                break;
            case STOPPED:
                statusString = ChatColor.RED + "STOPPED " + ChatColor.RESET ;
                break;
            default:
                statusString = state.name();
        }
        return statusString;
    }

    private boolean getPos(Player player, String[] args) {
        StructureService service = new StructureService();
        Structure structure;
        if (args.length == 1) {
            structure = service.getStructure(player.getLocation());
            if (structure == null) {
                player.sendMessage(new String[]{
                    ChatColor.RED + "Currently not within a structure",
                    ChatColor.RED + "You may also try " + CMD + " pos [id]"
                });
                return true;
            }
        } else if (args.length == 2) {
            Long id;
            try {
                id = Long.parseLong(args[1]);
            } catch (NumberFormatException nfe) {
                player.sendMessage("No valid id");
                return true;
            }
            structure = service.getStructure(id);
            if (structure == null) {
                player.sendMessage(ChatColor.RED + "No structure found with id: " + id);
                return true;
            }
        } else {
            player.sendMessage(ChatColor.RED + "Too many arguments!");
            return true;
        }
        Location loc = player.getLocation();
        Vector pos = structure.getRelativePosition(new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));

        player.sendMessage("#" + ChatColor.GOLD + structure.getId() + " "
                + ChatColor.BLUE + structure.getName()
                + ChatColor.RESET + ": "
                + ChatColor.YELLOW + "x:" + ChatColor.RESET + pos.getBlockX() + " "
                + ChatColor.YELLOW + "y:" + ChatColor.RESET + pos.getBlockY() + " "
                + ChatColor.YELLOW + "z:" + ChatColor.RESET + pos.getBlockZ()
        );

        return true;
    }

    private Structure getStructureFromString(CommandSender sender, String structureId) throws SettlerCraftException {
        Long id;
        try {
            id = Long.parseLong(structureId);
        } catch (NumberFormatException nfe){
            throw new SettlerCraftException("Invalid id");
        }
        Structure structure = new StructureService().getStructure(id);
        if(structure == null) {
            throw new SettlerCraftException("No structure found for id #" + id);
        }
        return structure;
    }
//
    private boolean displayOwners(CommandSender sender, String[] args) {
        
        Comparator<PlayerOwnership> ownerComp = new Comparator<PlayerOwnership>() {

            @Override
            public int compare(PlayerOwnership o1, PlayerOwnership o2) {
                return new Integer(o2.getOwnerType().ordinal()).compareTo(o1.getOwnerType().ordinal());
            }
        };
        Structure structure;
        try {
            structure = getStructureFromString(sender, args[1]);
        } catch (SettlerCraftException ex) {
            sender.sendMessage(ChatColor.RED + ex.getMessage());
            return true;
        }
        
        
        
        TreeSet<PlayerOwnership> owners = new TreeSet<>(ownerComp);
        
        owners.addAll(structure.getOwnerships());
        
        
        
        String ownersString = "";
        if(owners.isEmpty()) {
            ownersString = ChatColor.RED + "(none)";
        } else {
            int count = 0;
            for(PlayerOwnership ply : owners) {
                if(ply.getOwnerType() == PlayerOwnership.Type.FULL) {
                    ownersString += ChatColor.GOLD + ply.getName();
                } else {
                    ownersString += ChatColor.GREEN + ply.getName();
                }
                
                count++;
                if(count < owners.size()) {
                    ownersString += ",";
                }
            }
        }
        
        
        sender.sendMessage(new String[]{
                structure.toString(),
                ChatColor.WHITE + "Owners: " + ownersString
        });
        return true;
    }

    private boolean addOwner(CommandSender sender, String[] args) {
       
        Structure structure;
        try {
            structure = getStructureFromString(sender, args[1]);
        } catch (SettlerCraftException ex) {
            sender.sendMessage(ChatColor.RED + ChatColor.stripColor(ex.getMessage()));
            return true;
        }
        
         // Command is executed by a player, permission?
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(!structure.isOwner(player, PlayerOwnership.Type.FULL)) {
                sender.sendMessage(ChatColor.RED + "You need to have FULL ownership to add/remove other owners");
                return true;
            }
            
        }
        
        Player player = Bukkit.getPlayer(args[3]);
        if(player == null) {
            sender.sendMessage(ChatColor.RED + "Player '"+args[3]+"' doesn't exist!");
            return true;
        }
        try {
            StructureAPI.makeOwner(player, PlayerOwnership.Type.BASIC, structure);
        } catch (StructureException ex) {
            sender.sendMessage(ChatColor.RED + ChatColor.stripColor(ex.getMessage()));
        }
        
        
        
        
        return true;
        
    }

    private boolean removeOwner(CommandSender sender, String[] args) {
       
        
        Structure structure;
        try {
            structure = getStructureFromString(sender, args[1]);
        } catch (SettlerCraftException ex) {
            sender.sendMessage(ChatColor.RED + ex.getMessage());
            return true;
        }
        
         // Command is executed by a player, permission?
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(!structure.isOwner(player, PlayerOwnership.Type.FULL)) {
                sender.sendMessage(ChatColor.RED + "You need to have FULL ownership to add/remove other owners");
                return true;
            }
            
        }
        
        Player player = Bukkit.getPlayer(args[3]);
        if(player == null) {
            sender.sendMessage(ChatColor.RED + "Player '"+args[3]+"' doesn't exist!");
            return true;
        }
        try {
            StructureAPI.removeOwner(player, structure);
        } catch (StructureException ex) {
            sender.sendMessage(ChatColor.RED + ex.getMessage());
        }
        
        return true;
    }
}
