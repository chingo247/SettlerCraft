/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.plugin.commands;

import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.sc.api.structure.construction.ConstructionTaskManager;
import com.sc.api.structure.construction.progress.ConstructionTaskException;
import com.sc.api.structure.entity.progress.ConstructionTask;
import com.sc.api.structure.entity.progress.QConstructionTask;
import com.sc.api.structure.persistence.HibernateUtil;
import com.sc.api.structure.persistence.service.TaskService;
import com.sc.plugin.SettlerCraft;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class ConstructionCommandExecutor implements CommandExecutor {

    private final SettlerCraft settlerCraft;
    private final ConstructionTaskManager taskManager = new ConstructionTaskManager();
    private static final int MAX_LINES = 10;

    public ConstructionCommandExecutor(SettlerCraft settlerCraft) {
        this.settlerCraft = settlerCraft;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String label, String[] args) {
        if (args.length == 0) {
            cs.sendMessage(ChatColor.RED + "Too few arguments");
            return false;
        }
        String arg = args[0];
        Player player = (Player) cs;
        switch (arg) {
            case "info":
                return displayInfo(player, args);
            case "cancel":
                return cancelTask(player, args);
            case "stop":
                return stopTask(player, args);
            case "move":
                return moveTask(player, args);
            case "continue":
                return continueTask(player, args);
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
        final List<ConstructionTask> tasks = getTasks(player.getName());
        int amountOfTasks = tasks.size();
        if (amountOfTasks == 0) {
            player.sendMessage("No tasks...");
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

            message[0] = "-------------------Page(" + (index) + "/" + ((amountOfTasks / (MAX_LINES - 1)) + 1) + ")-------------------";
            int line = 1;
            int startIndex = (index - 1) * (MAX_LINES - 1);
            for (int i = startIndex; i < startIndex + (MAX_LINES - 1) && i < tasks.size(); i++) {
                ConstructionTask task = tasks.get(i);
                String l = "Task " + ChatColor.GOLD + "#" + task.getId() + ChatColor.BLUE + " " + task.getStructure().getPlan().getDisplayName() + " ";
                if (task.getState() == ConstructionTask.State.REMOVED) {
                    l += ChatColor.RED + task.getState().name();
                } else if (task.getState() == ConstructionTask.State.COMPLETE) {
                    l += ChatColor.GREEN + task.getState().name();
                } else if (task.getState() == ConstructionTask.State.PROGRESSING) {
                    l += ChatColor.YELLOW + task.getState().name();
                } else {
                    l += ChatColor.RESET + task.getState().name();
                }
                l += " " + ChatColor.RESET + task.getCreatedAt();
                message[line] = l;
                line++;
            }
            player.sendMessage(message);
        }
        return true;
    }

    /**
     * Gets all the tasks that weren't marked as removed ordered by date
     *
     * @param entryName The entryName
     * @return List of constructionTasks
     */
    private List<ConstructionTask> getTasks(String entryName) {
        Session session = HibernateUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);
        QConstructionTask qct = QConstructionTask.constructionTask;
        List<ConstructionTask> tasks = query.from(qct).orderBy(qct.createdAt.desc()).where(qct.constructionEntry().entryName.eq(entryName).and(qct.constructionState.ne(ConstructionTask.State.REMOVED))).list(qct);
        session.close();
        return tasks;
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

        TaskService service = new TaskService();
        ConstructionTask task = service.getTask(id);

        if (task == null) {
            player.sendMessage(ChatColor.RED + "Unable to find task #: " + id);
            return true;
        } else if (!task.getConstructionEntry().getEntryName().equals(player.getName())) {
            player.sendMessage(ChatColor.RED + "You dont have permission to manage task #" + id);
            return true;
        } else if (task.getState() == ConstructionTask.State.REMOVED) {
            player.sendMessage(ChatColor.RED + "Task #" + task.getId() + " was removed, unable to cancel task...");
            return true;
        }
        
        if (task.getState() == ConstructionTask.State.CANCELED || task.isDemolishing()) {
            try {
                task.setIsDemolishing(false);
                task = service.save(task);
                taskManager.stopTask(task, true);
                taskManager.continueTask(task, true);
                player.sendMessage("Task #" + id + ChatColor.RESET + " was canceled and has now been continued");
            } catch (ConstructionTaskException ex) {
                player.sendMessage(ChatColor.RED + "Task #" + id + " was unable to cancel");
                Logger.getLogger(ConstructionCommandExecutor.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            try {
                
                taskManager.stopTask(task, true);
                if(task.hasPlacedBlocks()) {
                    task.setIsDemolishing(true);
                    task = service.save(task);
                    taskManager.continueTask(task, true);
                    player.sendMessage("Task #" + id + ChatColor.RESET + " has been canceled, and structure will be removed");
                } else {
                    player.sendMessage("Task #" + id + ChatColor.RESET + " has been canceled and removed");
                    //TODO REFUND PLAYERS?
                    task.getData().setRefundable(false);
                    service.updateStatus(task, ConstructionTask.State.REMOVED);
                }
                
               
            } catch (ConstructionTaskException ex) {
                player.sendMessage(ChatColor.RED + "Task #" + id + " was unable to be canceled");
                Logger.getLogger(ConstructionCommandExecutor.class.getName()).log(Level.SEVERE, null, ex);
            }
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

        TaskService service = new TaskService();
        ConstructionTask task = service.getTask(id);

        if (task == null) {
            player.sendMessage(ChatColor.RED + "Unable to find task #: " + id);
            return false;
        } else if (!task.getConstructionEntry().getEntryName().equals(player.getName())) {
            player.sendMessage(ChatColor.RED + "You are not authorized to manage task #" + id);
        } else if (task.getState() == ConstructionTask.State.REMOVED) {
            player.sendMessage(ChatColor.RED + "Task #" + task.getId() + " was removed, can't stop task...");
            return true;
        }

        try {
            taskManager.stopTask(task, true);
            player.sendMessage("Construction for Task #" + id + ChatColor.RESET + " (" + task.getStructure().getPlan().getDisplayName() + ") has stopped");
        } catch (ConstructionTaskException ex) {
            player.sendMessage(ChatColor.RED + "Task #" + id + " was unable to stop");
            Logger.getLogger(ConstructionCommandExecutor.class.getName()).log(Level.SEVERE, null, ex);
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

        TaskService service = new TaskService();
        ConstructionTask task = service.getTask(id);
        if (task == null) {
            player.sendMessage(ChatColor.RED + "Unable to find task #: " + id);
            return false;
        } else if (!task.getConstructionEntry().getEntryName().equals(player.getName())) {
            player.sendMessage(ChatColor.RED + "You are not authorized to manage task #" + id);
        } else if (task.getState() == ConstructionTask.State.REMOVED) {
            player.sendMessage(ChatColor.RED + "Task #" + task.getId() + " was removed, can't move task...");
            return true;
        }

        try {
            taskManager.moveTask(task, true);
            player.sendMessage("Task #" + id + " has been placed at the back of the queue");
        } catch (ConstructionTaskException ex) {
            Logger.getLogger(ConstructionCommandExecutor.class.getName()).log(Level.SEVERE, null, ex);
            player.sendMessage(ChatColor.RED + "Task #" + id + " was unable to move");
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
        Long id = null;
        try {
            id = Long.parseLong(args[1]);
        } catch (NumberFormatException nfe) {
            player.sendMessage(ChatColor.RED + "No valid id");
            return false;
        }

        TaskService service = new TaskService();
        ConstructionTask task = service.getTask(id);

        if (task == null) {
            player.sendMessage(ChatColor.RED + "Unable to find task #: " + id);
            return true;
        } else if (!task.getConstructionEntry().getEntryName().equals(player.getName())) {
            player.sendMessage(ChatColor.RED + "You are not authorized to manage task #" + id);
            return true;
        } else if (task.getState() == ConstructionTask.State.COMPLETE) {
            player.sendMessage(ChatColor.RED + " Task #" + id + " has already been completed...");
            return false;
        } else if (task.getState() == ConstructionTask.State.REMOVED) {
            player.sendMessage(ChatColor.RED + " Unable to continue construction, because Task #" + id + " was removed");
            return false;
        }
        try {
            taskManager.stopTask(task, true);
            taskManager.continueTask(task, true);
            player.sendMessage(ChatColor.RESET + "Construction for Task #" + id + "(" + task.getStructure().getPlan().getDisplayName() + ") has been continued");
        } catch (ConstructionTaskException ex) {
            Logger.getLogger(ConstructionCommandExecutor.class.getName()).log(Level.SEVERE, null, ex);
        }

        return true;
    }

}
