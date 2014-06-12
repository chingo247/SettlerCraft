package com.sc.commands;

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
import com.sc.construction.async.ConstructionProcess;
import com.sc.construction.async.ConstructionProcess.State;
import com.sc.construction.exception.StructureException;
import com.sc.construction.structure.Structure;
import com.sc.construction.structure.StructureConstructionManager;
import com.sc.construction.structure.StructureManager;
import com.sc.persistence.StructureService;
import com.sc.plugin.SettlerCraft;
import java.util.List;
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

    private final SettlerCraft settlerCraft;
    private final StructureConstructionManager scm = StructureConstructionManager.getInstance();
    private final StructureManager sm = StructureManager.getInstance();
    private static final int MAX_LINES = 10;
    private static final String CMD = "/cst";

    public ConstructionCommandExecutor(SettlerCraft settlerCraft) {
        this.settlerCraft = settlerCraft;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String label, String[] args) {
        if (!(cs instanceof Player)) {
            cs.sendMessage("You are not a player!"); // Command is issued from server console
            return true;
        }

        if (args.length == 0) {
            cs.sendMessage(ChatColor.RED + "Too few arguments");
            return true;
        }
        String arg = args[0];
        Player player = (Player) cs;
        switch (arg) {
            case "list":
                return displayInfo(player, args);
            case "cancel":
                return cancelTask(player, args);
            case "halt":
                return stopTask(player, args);
            case "postpone":
                return moveTask(player, args);
            case "continue":
                return continueTask(player, args);
            case "demolish":
                return demolish(player, args);
            case "build":
                return build(player, args);
            default:
                player.sendMessage(ChatColor.RED + "No actions known for: " + arg);
                return false;
        }

    }

    /**
     * Displays a list of all tasks that haven't been marked as removed
     *
     * @param player The player
     * @param args The arguments
     * @return
     */
    private boolean displayInfo(Player player, String[] args) {
        final List<ConstructionProcess> list = scm.listProgress(player);
        if (list == null) {
            player.sendMessage("No structures in progress...");
            return true;
        }

        int amountOfTasks = list.size();
        if (amountOfTasks == 0) {
            player.sendMessage("No structures in progress...");
        } else {
            int index;
            if (args.length == 1) {
                index = 1;
            } else if (args.length == 2) {
                try {
                    index = Integer.parseInt(args[1]);
                    if (index < 1) {
                        player.sendMessage(ChatColor.RED + "Page index has to be greater than 1");
                        return true;
                    }

                } catch (NumberFormatException nfe) {
                    player.sendMessage(ChatColor.RED + "Invalid page index");
                    return true;
                }
            } else {
                player.sendMessage(ChatColor.RED + "Too many arguments");
                return true;
            }

            String[] message = new String[MAX_LINES];
            int pages = (amountOfTasks / (MAX_LINES - 1)) + 1;
            if (index > pages) {
                player.sendMessage(ChatColor.RED + "Max page is " + pages);
                return true;
            }

            message[0] = "---------Page(" + (index) + "/" + ((amountOfTasks / (MAX_LINES - 1)) + 1) + " Total: " + amountOfTasks + ")---------";
            int line = 1;
            int startIndex = (index - 1) * (MAX_LINES - 1);
            for (int i = startIndex; i < startIndex + (MAX_LINES - 1) && i < list.size(); i++) {
                ConstructionProcess task = list.get(i);
                String l = "#" + ChatColor.GOLD + task.getId() + " " + ChatColor.BLUE + " " + task.getStructure().getPlan().getDisplayName() + " ";
                State state = task.getStatus();

                switch (state) {
                    case DEMOLISHING:
                    case BUILDING:
                        l += ChatColor.YELLOW;
                        break;
                    case COMPLETE:
                        l += ChatColor.GREEN;
                        break;
                    case STOPPED:
                        l += ChatColor.RED;
                        break;
                    default:
                        l += ChatColor.WHITE;
                        break;
                }
                l += state.name();
                message[line] = l;
                line++;
            }
            player.sendMessage(message);
        }
        return true;
    }

    private boolean cancelTask(Player player, String[] args) {
        if (args.length > 2) {
            player.sendMessage(ChatColor.RED + "Too many arguments");
            return true;
        } else if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Too few arguments");
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
        } else if (!sm.owns(player, structure)) {
            player.sendMessage(ChatColor.RED + "You don't own this structure");
            return true;
        } else if (structure.getProgress().getStatus() == State.REMOVED) {
            player.sendMessage(ChatColor.RED + "#" + ChatColor.GOLD + structure.getId() + ChatColor.BLUE + structure.getPlan().getDisplayName() + ChatColor.RED + " was removed, unable to cancel task...");
            return true;
        }
        ConstructionProcess progress = structure.getProgress();

        scm.stopProcess(player, progress, true);
        //FIXME EVER REACHED?
        if (!progress.hasPlacedBlocks()) {
            player.sendMessage("Construction for " + ChatColor.GOLD + id + ChatColor.BLUE + structure.getPlan().getDisplayName() + ChatColor.RESET + " was canceled");
            sm.refund(structure);
            structure.setRefundValue(0d);
            ss.save(structure);
            progress.setProgressStatus(State.REMOVED);
        } 

        return true;
    }

    private boolean stopTask(Player player, String[] args) {
        if (args.length > 2) {
            player.sendMessage(ChatColor.RED + "Too many arguments");
            return false;
        } else if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Too few arguments");
            return false;
        }
        Long id;
        try {
            id = Long.parseLong(args[1]);
        } catch (NumberFormatException nfe) {
            player.sendMessage(ChatColor.RED + "No valid id");
            return false;
        }

        StructureService ss = new StructureService();
        Structure structure = ss.getStructure(id);

        if (structure == null) {
            player.sendMessage(ChatColor.RED + "Unable to find task # " + ChatColor.GOLD + id);
            return false;
        } else if (!sm.owns(player, structure)) {
            player.sendMessage(ChatColor.RED + "You are not authorized to manage task #" + ChatColor.GOLD + id);
        } else if (structure.getProgress().getStatus() == State.REMOVED) {
            player.sendMessage(ChatColor.RED + "#" + ChatColor.GOLD + structure.getId() + ChatColor.BLUE + structure.getPlan().getDisplayName() + ChatColor.RED + " was removed, can't stop task...");
            return true;
        }
        ConstructionProcess progress = structure.getProgress();
        if (progress.getStatus() == State.COMPLETE) {
            player.sendMessage(ChatColor.RED + "#" + ChatColor.GOLD + structure.getId() + ChatColor.BLUE + structure.getPlan().getDisplayName() + ChatColor.RED + " already complete...");
            return true;
        }

        if(scm.stopProcess(player, progress, true)) {
            player.sendMessage("Stopping #" + ChatColor.GOLD + id + " " + ChatColor.BLUE + structure.getPlan().getDisplayName());
        } else {
            player.sendMessage("Failed to stop #" + ChatColor.GOLD + id + " " + ChatColor.BLUE + structure.getPlan().getDisplayName());
        }

        return true;
    }

    private boolean moveTask(Player player, String[] args) {
        if (args.length > 2) {
            player.sendMessage(ChatColor.RED + "Too many arguments");
            return false;
        } else if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Too few arguments");
            return false;
        }
        Long id;
        try {
            id = Long.parseLong(args[1]);
        } catch (NumberFormatException nfe) {
            player.sendMessage(ChatColor.RED + "No valid id");
            return false;
        }

        StructureService ss = new StructureService();
        Structure structure = ss.getStructure(id);

        if (structure == null) {
            player.sendMessage(ChatColor.RED + "Unable to find task # " + ChatColor.GOLD + id);
            return false;
        } else if (!sm.owns(player, structure)) {
            player.sendMessage(ChatColor.RED + "You are not authorized to manage task #" + ChatColor.GOLD + id);
        } else if (structure.getProgress().getStatus() == State.REMOVED) {
            player.sendMessage(ChatColor.RED + "#" + ChatColor.GOLD + structure.getId() + ChatColor.BLUE + structure.getPlan().getDisplayName() + ChatColor.RED + " was removed, can't stop task...");
            return true;
        }
        ConstructionProcess progress = structure.getProgress();

        try {
            scm.delayProcess(player, progress);
            player.sendMessage("#" + ChatColor.GOLD + id + ChatColor.BLUE + structure.getPlan().getDisplayName() + ChatColor.RESET + " has been placed at the back of the queue");
        } catch (StructureException ex) {
            player.sendMessage(ChatColor.RED + ex.getMessage());
        }

        return true;
    }

    private boolean continueTask(Player player, String[] args) {
        if (args.length > 2) {
            player.sendMessage(ChatColor.RED + "Too many arguments");
            return true;
        } else if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Too few arguments");
            return true;
        }
        Long id;
        try {
            id = Long.parseLong(args[1]);
        } catch (NumberFormatException nfe) {
            player.sendMessage(ChatColor.RED + "No valid id");
            return false;
        }

        StructureService ss = new StructureService();
        Structure structure = ss.getStructure(id);

        if (structure == null) {
            player.sendMessage(ChatColor.RED + "Unable to find task # " + ChatColor.GOLD + id);
            return false;
        } else if (!sm.owns(player, structure)) {
            player.sendMessage(ChatColor.RED + "You are not authorized to manage task #" + ChatColor.GOLD + id);
        } else if (structure.getProgress().getStatus() == State.REMOVED) {
            player.sendMessage(ChatColor.RED + "#" + ChatColor.GOLD + structure.getId() + ChatColor.BLUE + structure.getPlan().getDisplayName() + ChatColor.RED + " was removed, can't stop task...");
            return true;
        }
        ConstructionProcess progress = structure.getProgress();
        try {
            scm.stopProcess(player, progress, true); //TODO remove this and perform add the force as feature
            scm.continueProcess(player, progress, true);
            player.sendMessage(ChatColor.RESET + "Construction for Task #" + ChatColor.GOLD + id + ChatColor.BLUE + structure.getPlan().getDisplayName() + ChatColor.RESET + " has been continued");
        } catch (StructureException ex) {
            player.sendMessage(ChatColor.RED + ex.getMessage());
        }

        return true;
    }

    private boolean demolish(Player player, String[] args) {
        StructureService ss = new StructureService();
        Structure structure;
        if (args.length < 2) {
            player.sendMessage(new String[]{
                ChatColor.RED + "Too few arguments!",
                ChatColor.RED + CMD + " demolish [id]"
            });
            return true;
        } else if (args.length == 2) {
            Long id;
            try {
                id = Long.parseLong(args[1]);
            } catch (NumberFormatException nfe) {
                player.sendMessage(ChatColor.RED + "No valid ID");
                return true;
            }
            structure = ss.getStructure(id);
            if (structure == null) {
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
        ConstructionProcess progress = structure.getProgress();
        if (progress.getStatus() == State.DEMOLISHING) {
            player.sendMessage(ChatColor.RED + "#" + ChatColor.GOLD + structure.getId() + " " + ChatColor.BLUE + structure.getPlan().getDisplayName() + ChatColor.RED + " already demolishing");
            return true;
        }
        
        if (structure.getProgress().getStatus() == State.REMOVED) {
            player.sendMessage(ChatColor.RED + "#" + ChatColor.GOLD + structure.getId() + ChatColor.BLUE + structure.getPlan().getDisplayName() + ChatColor.RED + " was removed, can't stop task...");
            return true;
        }

        if (sm.owns(player, structure)) {
            scm.demolish(player, structure);
            player.sendMessage("#" + ChatColor.GOLD + structure.getId() + " " + ChatColor.BLUE + structure.getPlan().getDisplayName() + ChatColor.RESET + " will be demolished");
            return true;
        } else {
            player.sendMessage(ChatColor.RED + "You don't have ownership of this structure!");
            return true;
        }

    }

    private boolean build(Player player, String[] args) {
        StructureService ss = new StructureService();
        Structure structure;
        if (args.length < 2) {
            player.sendMessage(new String[]{
                ChatColor.RED + "Too few arguments!",
                ChatColor.RED + CMD + " demolish [id]"
            });
            return true;
        } else if (args.length == 2) {
            Long id;
            try {
                id = Long.parseLong(args[1]);
            } catch (NumberFormatException nfe) {
                player.sendMessage(ChatColor.RED + "No valid ID");
                return true;
            }
            structure = ss.getStructure(id);
            if (structure == null) {
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
        ConstructionProcess progress = structure.getProgress();
        if (progress.getStatus() == State.BUILDING) {
            player.sendMessage(ChatColor.RED + "#" + ChatColor.GOLD + structure.getId() + " " + ChatColor.BLUE + structure.getPlan().getDisplayName() + ChatColor.RED + " already building");
            return true;
        } else if (structure.getProgress().getStatus() == State.REMOVED) {
            player.sendMessage(ChatColor.RED + "#" + ChatColor.GOLD + structure.getId() + ChatColor.BLUE + structure.getPlan().getDisplayName() + ChatColor.RED + " was removed, can't stop task...");
            return true;
        }

        if (sm.owns(player, structure)) {
            scm.build(player, structure);
            player.sendMessage("#" + ChatColor.GOLD + structure.getId() + " " + ChatColor.BLUE + structure.getPlan().getDisplayName() + ChatColor.RESET + " will continue construction");
            return true;
        } else {
            player.sendMessage(ChatColor.RED + "You don't have ownership of this structure!");
            return true;
        }

    }

}
