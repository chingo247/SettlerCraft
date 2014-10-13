package com.chingo247.settlercraft.bukkit.commands;

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
import com.chingo247.settlercraft.persistence.StructureService;
import com.chingo247.settlercraft.plugin.SettlerCraft;
import com.chingo247.settlercraft.structure.StructureAPI;
import com.chingo247.settlercraft.structure.entities.structure.Structure;
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

    private final SettlerCraft PLUGIN;
    private static final String CMD = "/cst";
    private final ChatColor CCC = ChatColor.DARK_PURPLE;

    public ConstructionCommandExecutor(SettlerCraft settlerCraft) {
        this.PLUGIN = settlerCraft;
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
                CCC + CMD + " list [index]",
                CCC + CMD + " halt [id]",
                CCC + CMD + " postpone [id]",
                CCC + CMD + " continue [id]",
                CCC + CMD + " demolish [id]",
                CCC + CMD + " build [id]",});
            return true;
        }
        String arg = args[0];
        Player player = (Player) cs;
        switch (arg) {
            case "cancel":
                return cancelTask(player, args);
            case "delay":
                return delayTask(player, args);
            case "build":
                return build(player, args);
            case "demolish":
                return demolish(player, args);
            default:
                player.sendMessage(ChatColor.RED + "No actions known for: " + arg);
                cs.sendMessage(new String[]{
                    CCC + CMD + " cancel [id]",
                    CCC + CMD + " delay [id]",
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

        StructureService ss = new StructureService();
        Structure structure = ss.getStructure(id);

        if (structure == null) {
            player.sendMessage(ChatColor.RED + "Unable to find structure #" + ChatColor.GOLD + id);
            return true;
        }

        if (StructureAPI.stop(player, structure)) {
            player.sendMessage("#" + ChatColor.GOLD + id + " " + ChatColor.BLUE + structure.getName() + " has been canceled");
        } else {
            player.sendMessage(ChatColor.RED + "Failed to cancel #" + ChatColor.GOLD + id + " " + ChatColor.BLUE + structure.getName());
        }

        return true;
    }

    private boolean delayTask(Player player, String[] args) {
        if (args.length > 2) {
            player.sendMessage(ChatColor.RED + "Too many arguments");
            player.sendMessage(new String[]{
                "Usage: ",
                CCC + CMD + " delay [id]"
            });
            return true;
        } else if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Too few arguments");
            player.sendMessage(new String[]{
                "Usage: ",
                CCC + CMD + " delay [id]"
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

        StructureService ss = new StructureService();
        Structure structure = ss.getStructure(id);

        if (structure == null) {
            player.sendMessage(ChatColor.RED + "Unable to find structure #" + ChatColor.GOLD + id);
            return true;
        }

//        if (StructureAPI.delay(PLUGIN, player, structure)) {
//            player.sendMessage("#" + ChatColor.GOLD + id + ChatColor.BLUE + structure.getName() + ChatColor.RESET + " has been placed at the back of the queue");
//        }
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

        StructureService ss = new StructureService();
        Structure structure = ss.getStructure(id);

        if (structure == null) {
            player.sendMessage(ChatColor.RED + "Unable to find structure # " + ChatColor.GOLD + id);
            return true;
        }
        if (StructureAPI.build(player, structure)) {
            player.sendMessage(ChatColor.RESET + "#" + ChatColor.GOLD + id + " " + ChatColor.BLUE + structure.getName() + " will be build");
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
        
        StructureService ss = new StructureService();
        Structure structure = ss.getStructure(id);
        
        if (structure == null) {
            player.sendMessage(ChatColor.RED + "Unable to find structure #" + id);
            return true;
        }
        if (StructureAPI.demolish(player, structure)) {
            player.sendMessage(ChatColor.RESET + "#" + ChatColor.GOLD + id + ChatColor.BLUE + structure.getName() + " will be demolished");
        }
        return true;
    }
    
//    /**
//     * Displays a list of all tasks that haven't been marked as removed
//     *
//     * @param player The player
//     * @param args The arguments
//     * @return
//     */
//    private boolean listTasks(Player player, String[] args) {
//        final List<ConstructionTask> tasks = SCM.getEntry(player.getUniqueId()).getTasks();
//        if (tasks == null) {
//            player.sendMessage(ChatColor.RED + "No structures in progress...");
//            return true;
//        }
//
//        int amountOfTasks = tasks.size();
//
//        int index;
//        if (args.length == 1) {
//            index = 1;
//        } else if (args.length == 2) {
//            try {
//                index = Integer.parseInt(args[1]);
//                if (index < 1) {
//                    player.sendMessage(ChatColor.RED + "Page index has to be greater than 1");
//                    return true;
//                }
//
//            } catch (NumberFormatException nfe) {
//                player.sendMessage(ChatColor.RED + "Invalid index");
//                return true;
//            }
//        } else {
//            player.sendMessage(ChatColor.RED + "Too many arguments");
//            player.sendMessage(new String[]{
//                "Usage:",
//                CCC + CMD + " list",
//                CCC + CMD + " list [index]"
//            });
//            return true;
//        }
//
//        String[] message = new String[MAX_LINES];
//        int pages = (amountOfTasks / (MAX_LINES - 1)) + 1;
//        if (index > pages || index <= 0) {
//            player.sendMessage(ChatColor.RED + "Page " + index + " out of " + pages + "...");
//            return true;
//        }
//
//        message[0] = "---------Page(" + (index) + "/" + ((amountOfTasks / (MAX_LINES - 1)) + 1) + " Total: " + amountOfTasks + ")---------";
//        int lineIndex = 1;
//        int startIndex = (index - 1) * (MAX_LINES - 1);
//        for (int i = startIndex; i < startIndex + (MAX_LINES - 1) && i < tasks.size(); i++) {
//            ConstructionTask task = tasks.get(i);
//            String line = "#" + ChatColor.GOLD + task.getConstructionSite().getId()
//                    + " " + ChatColor.BLUE + task.getConstructionSite().getStructure().getPlan().getDisplayName();
//                    
//            State state = task.getConstructionSite().getState();
//
//            switch (state) {
//                case DEMOLISHING:
//                case BUILDING:
//                    line += ChatColor.YELLOW;
//                    break;
//                case COMPLETE:
//                    line += ChatColor.GREEN;
//                    break;
//                case STOPPED:
//                    line += ChatColor.RED;
//                    break;
//                default:
//                    line += ChatColor.WHITE;
//                    break;
//            }
//            line += " " + state.name();
//            message[lineIndex] = line;
//            lineIndex++;
//        }
//        player.sendMessage(message);
//        return true;
//    }


}
