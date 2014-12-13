package com.chingo247.settlercraft.bukkit.commands;

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
import com.chingo247.settlercraft.bukkit.BukkitStructureAPI;
import com.chingo247.settlercraft.structure.persistence.hibernate.StructureDAO;
import com.chingo247.settlercraft.structure.Structure;
import com.chingo247.settlercraft.structure.construction.BuildOptions;
import com.chingo247.settlercraft.structure.construction.DemolitionOptions;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Chingo
 */
public class ConstructionCommandExecutor implements CommandExecutor {

    private static final String CMD = "/cst";
    private final ChatColor CCC = ChatColor.DARK_PURPLE;
    private final BukkitStructureAPI structureAPI;
    private final StructureDAO structureDAO = new StructureDAO();

    public ConstructionCommandExecutor(BukkitStructureAPI structureAPI) {
        this.structureAPI = structureAPI;
    }
    
    


    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String label, String[] args) {
        if (!(cs instanceof Player)) {
            cs.sendMessage("You are not a player!"); // Command is issued from server console
            return true;
        }

        if (args.length == 0) {
            cs.sendMessage(ChatColor.RED + "Too few arguments");
            cs.sendMessage(new String[]{
                CCC + CMD + " cancel [id]",
                CCC + CMD + " build [id]",
                CCC + CMD + " demolish [id]"});
            return true;
        }
        
        
        String arg = args[0];
        Player player = (Player) cs;
        switch (arg) {
            case "cancel":
                return cancelTask(player, args);
            case "build":
                return build(player, args);
            case "demolish":
                return demolish(player, args);
            case "rollback":
                return rollback(player, args);
            default:
                player.sendMessage(ChatColor.RED + "No actions known for: " + arg);
                cs.sendMessage(new String[]{
                    CCC + CMD + " cancel [id]",
                    CCC + CMD + " build [id]",
                    CCC + CMD + " demolish [id]"
                });
                return true;
        }

    }

    private boolean cancelTask(Player player, String[] args) {
        if (args.length > 2) {
            player.sendMessage(ChatColor.RED + "Too many arguments");
            player.sendMessage(new String[]{
                "Usage: ",
                CCC + CMD + " cancel [id]"
            });
            return true;
        } else if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Too few arguments");
            player.sendMessage(new String[]{
                "Usage: ",
                CCC + CMD + " cancel [id]"
            });
            return true;
        }
        Long id;
        try {
            id = Long.parseLong(args[1]);
        } catch (NumberFormatException nfe) {
            player.sendMessage(ChatColor.RED + "No valid id");
            return true;
        }

        
        Structure structure = structureDAO.find(id);

        if (structure == null) {
            player.sendMessage(ChatColor.RED + "Unable to find structure #" + ChatColor.GOLD + id);
            return true;
        }

        if (structureAPI.stop(player, structure)) {
            player.sendMessage("#" + ChatColor.GOLD + id + " " + ChatColor.BLUE + structure.getName()  + ChatColor.RESET + " has been canceled");
        } else {
            player.sendMessage(ChatColor.RED + "Failed to cancel #" + ChatColor.GOLD + id + " " + ChatColor.BLUE + structure.getName());
        }

        return true;
    }

    private boolean build(Player player, String[] args) {
        if (args.length > 2) {
            player.sendMessage(ChatColor.RED + "Too many arguments");
            player.sendMessage(new String[]{
                "Usage: ",
                CCC + CMD + " build [id]"
            });
            return true;
        } else if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Too few arguments");
            player.sendMessage(new String[]{
                "Usage: ",
                CCC + CMD + " build [id]"
            });
            return true;
        }
        Long id;
        try {
            id = Long.parseLong(args[1]);
        } catch (NumberFormatException nfe) {
            player.sendMessage(ChatColor.RED + "No valid id");
            return true;
        }

        Structure structure = structureDAO.find(id);

        if (structure == null) {
            player.sendMessage(ChatColor.RED + "Unable to find structure # " + ChatColor.GOLD + id);
            return true;
        }
        if (structureAPI.build(player, structure, new BuildOptions(false), false)) {
            player.sendMessage(ChatColor.RESET + "#" + ChatColor.GOLD + id + " " + ChatColor.BLUE + structure.getName() + ChatColor.RESET + " will be build");
        }

        return true;
    }

    private boolean demolish(Player player, String[] args) {
        if (args.length > 2) {
            player.sendMessage(ChatColor.RED + "Too many arguments");
            player.sendMessage(new String[]{
                "Usage: ",
                CCC + CMD + " demolish [id]"
            });
            return true;
        } else if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Too few arguments");
            player.sendMessage(new String[]{
                "Usage: ",
                CCC + CMD + " demolish [id]"
            });
            return true;
        }
        Long id;
        try {
            id = Long.parseLong(args[1]);
        } catch (NumberFormatException nfe) {
            player.sendMessage(ChatColor.RED + "No valid id");
            return true;
        }
        
        Structure structure = structureDAO.find(id);
        
        if (structure == null) {
            player.sendMessage(ChatColor.RED + "Unable to find structure #" + id);
            return true;
        }
        if (structureAPI.demolish(player, structure, new DemolitionOptions(), false)) {
            player.sendMessage(ChatColor.RESET + "#" + ChatColor.GOLD + id + ChatColor.BLUE + structure.getName() + " will be demolished");
        }
        return true;
    }

//    private boolean rollback(Player player, String[] args) {
//       if (args.length > 2) {
//            player.sendMessage(ChatColor.RED + "Too many arguments");
//            player.sendMessage(new String[]{
//                "Usage: ",
//                CCC + CMD + " rollback [id]"
//            });
//            return true;
//        } else if (args.length < 2) {
//            player.sendMessage(ChatColor.RED + "Too few arguments");
//            player.sendMessage(new String[]{
//                "Usage: ",
//                CCC + CMD + " rollback [id]"
//            });
//            return true;
//        }
//        Long id;
//        try {
//            id = Long.parseLong(args[1]);
//        } catch (NumberFormatException nfe) {
//            player.sendMessage(ChatColor.RED + "No valid id");
//            return true;
//        }
//
//        StructureService ss = new StructureService();
//        Structure structure = ss.getStructure(id);
//
//        if (structure == null) {
//            player.sendMessage(ChatColor.RED + "Unable to find structure # " + ChatColor.GOLD + id);
//            return true;
//        }
//        structureAPI.rollback(structure);
//        
//
//        return true;
//    }

    private boolean rollback(Player player, String[] args) {
        long structureID;
        try {
            structureID = Long.parseLong(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid id...");
            return true;
        }
        
        Structure structure = structureDAO.find(structureID);
        
        structureAPI.rollback(player, structure, structure.getLog().getCreatedAt());
        
        return true;
        
        
    }
    
}
