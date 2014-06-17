/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.construction.structure;

import com.google.common.base.Preconditions;
import com.sc.construction.asyncworldEdit.ConstructionEntry;
import com.sc.construction.asyncworldEdit.ConstructionProcess;
import com.sc.construction.exception.StructureException;
import com.sc.persistence.AbstractService;
import com.sc.persistence.HibernateUtil;
import com.sc.persistence.StructureService;
import com.sc.plugin.SettlerCraft;
import com.sc.util.SCAsyncWorldEditUtil;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.primesoft.asyncworldedit.blockPlacer.BlockPlacer;

/**
 *
 * @author Chingo
 */
public class StructureConstructionManager {

    private static final int ENCLOSURE_BUFFER_SIZE = 100;
    private final Map<UUID, ConstructionEntry> playerEntries;
    private final Map<Long, StructureTask> structureTasks;
    private final Plugin plugin = SettlerCraft.getSettlerCraft();
    private static StructureConstructionManager instance;

    private StructureConstructionManager() {
        this.playerEntries = Collections.synchronizedMap(new HashMap<UUID, ConstructionEntry>());
        this.structureTasks = Collections.synchronizedMap(new HashMap<Long, StructureTask>());
    }

    /**
     * Gets the instance.
     *
     * @return The constructionManager instance
     */
    public static StructureConstructionManager getInstance() {
        if (instance == null) {
            instance = new StructureConstructionManager();
        }
        return instance;
    }

    /**
     * Lists all processes of a player.
     *
     * @param owner The player
     * @return List of processes of the owner or null if the owner doesn't exist
     */
    public List<ConstructionProcess> listProgress(final Player owner) {
        if (playerEntries.get(owner.getUniqueId()) == null) {
            return null;
        } else {
            return playerEntries.get(owner.getUniqueId()).list();
        }
    }

    /**
     * Removes a process from the corresponding entry.
     *
     * @param owner The owner
     * @param jobId The jobId
     * @return True if process exist and was removed
     */
    public boolean removeProcess(final UUID owner, final Integer jobId) {
        if (playerEntries.get(owner) != null) {
            return playerEntries.get(owner).remove(jobId) != null;
        }
        return false;
    }

    /**
     * Removes a process from the corresponding entry.
     *
     * @param owner The owner
     * @param jobId The jobId
     * @return True if process exist and was removed
     */
    public boolean removeProcess(final Player owner, final Integer jobId) {
        return removeProcess(owner.getUniqueId(), jobId);
    }

    /**
     * Puts a process in the corresponding entry.
     *
     * @param player The player
     * @param jobId The jobid
     * @param process The process
     */
    public void putProcess(final Player player, final Integer jobId, final ConstructionProcess process) {
        if (playerEntries.get(player.getUniqueId()) == null) {
            playerEntries.put(player.getUniqueId(), new ConstructionEntry(player));
        }
        playerEntries.get(player.getUniqueId()).put(jobId, process);
    }

    /**
     * Gets a process from the corresponding entry.
     *
     * @param owner The owner
     * @param jobId The jobid
     * @return The constructionProcess
     */
    public ConstructionProcess getProcess(final UUID owner, final Integer jobId) {
        if (playerEntries.get(owner) != null) {
            return null;
        } else {
            return playerEntries.get(owner).get(jobId);
        }
    }

    /**
     * Gets a process from the corresponding entry.
     *
     * @param owner The owner
     * @param jobId The jobid
     * @return The constructionProcess
     */
    public ConstructionProcess getProgress(final Player owner, final Integer jobId) {
        return getProcess(owner.getUniqueId(), jobId);
    }

    /**
     * Continues construction of a structure.
     *
     * @param player The player
     * @param process The process to continue
     * @param force will ignore the task current state, therefore even if the task was marked
     * completed it will try to continue the task
     * @throws com.sc.construction.exception.StructureException if structure was removed
     */
    public void continueProcess(Player player, ConstructionProcess process, boolean force) throws StructureException {
        final Structure structure = process.getStructure();
        ConstructionProcess.State status = process.getStatus();
        if ((status != ConstructionProcess.State.STOPPED) && !force) {
            return;
        }

        if (status == ConstructionProcess.State.REMOVED) {
            throw new StructureException("Tried to continue a removed structure");
        }
        if (force) {
            stopProcess(player, process, force);
        }
        
        // Stop the task if any
        StructureTask task = structureTasks.get(structure.getId());
        if(task != null) {
            task.cancel();
        }
        
        StructureService ss = new StructureService();
        process.setJobId(-1);
//        process.setHasPlacedEnclosure(false);
        ss.save(process);

        task = new StructureTask(player, structure, process.isDemolishing());
        task.runTaskAsynchronously(plugin);
        structureTasks.put(structure.getId(), task);
    }

    /**
     * Stops the task, the task will be removed from AsyncWorldEdit's blockplacer queue, but will
     * still remain in the database
     *
     * @param tasker The task requester
     * @param process The process to stop
     * @param force whether to check if the progress already has stopped
     * @return true if succesfully stopped
     */
    public boolean stopProcess(Player tasker, ConstructionProcess process, boolean force) {
        Preconditions.checkArgument(process.getStatus() != ConstructionProcess.State.REMOVED);
        ConstructionProcess.State progressStatus = process.getStatus();

        if (progressStatus == ConstructionProcess.State.STOPPED && !force) {
            return false;
        }

        if (progressStatus == ConstructionProcess.State.COMPLETE || process.getJobId() == -1) {
            return false;
        }
        
         // Stop the task if any, processes always have the same id as their structure
        StructureTask task = structureTasks.get(process.getId());
        if(task != null) {
            task.cancel();
        }

        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSession();
            tx = session.beginTransaction();
            BlockPlacer placer = SCAsyncWorldEditUtil.getBlockPlacer();
            int jobId = process.getJobId();

            UUID owner = tasker.getUniqueId();
            placer.cancelJob(owner, jobId);

            process.setProgressStatus(ConstructionProcess.State.STOPPED);
            removeProcess(tasker, jobId);
            process.setJobId(-1);
            session.merge(process);
            tx.commit();
        } catch (HibernateException e) {
            try {
                tx.rollback();
            } catch (HibernateException rbe) {
                java.util.logging.Logger.getLogger(AbstractService.class.getName()).log(Level.SEVERE, "Couldn’t roll back transaction", rbe);
            }
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }
        
        
        return true;
    }

    /**
     * Stops the task and continue's it. As result the task will be added at the back of the queue
     * by AsyncWorldEdit
     *
     * @param tasker The task requester
     * @param process The process
     * @throws com.sc.construction.exception.StructureException if structure was removed
     */
    public void postPoneProcess(Player tasker, ConstructionProcess process) throws StructureException {
        if (process.getJobId() == -1) {
            return;
        }
        stopProcess(tasker, process, true);
        continueProcess(tasker, process, true);
    }

    /**
     * Demolishes a structure
     * structure was placed
     *
     * @param player The demolisher
     * @param structure The target structure
     * @return True if demolision request was succesful
     */
    public boolean demolish(Player player, Structure structure) {
        if (!structure.getOwner().equals(player.getName())) {
            player.sendMessage(ChatColor.RED + "You don't own this structure");
            return false;
        }
        ConstructionProcess progress = structure.getProgress();
        progress.setIsDemolishing(true);
        try {
            continueProcess(player, progress, true);
            return true;
        } catch (StructureException ex) {
            java.util.logging.Logger.getLogger(SettlerCraft.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Builds a structure
     * structure was placed
     *
     * @param player The demolisher
     * @param structure The target structure
     * @return True if build request was succesful
     */
    public boolean build(Player player, Structure structure) {
        if (!structure.getOwner().equals(player.getName())) {
            player.sendMessage(ChatColor.RED + "You don't own this structure");
            return false;
        }
        ConstructionProcess progress = structure.getProgress();
        progress.setIsDemolishing(false);
        try {
            continueProcess(player, progress, true);
            return true;
        } catch (StructureException ex) {
            java.util.logging.Logger.getLogger(SettlerCraft.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public void stopAll() {
        for (ConstructionEntry ce : playerEntries.values()) {
            for (ConstructionProcess process : ce.list()) {
                if (process.getStatus() != ConstructionProcess.State.COMPLETE && process.getStatus() != ConstructionProcess.State.REMOVED) {
                    stopProcess(ce.getOwner(), process, true);
                }
            }
        }
    }

}
