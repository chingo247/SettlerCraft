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
package com.sc.api.structure.construction;

import com.sc.api.structure.construction.async.ConstructionCallback;
import com.sc.api.structure.construction.async.DemolisionCallback;
import com.sc.api.structure.construction.async.SCAsyncCuboidClipboard;
import com.sc.api.structure.construction.async.SCJobCallback;
import com.sc.api.structure.construction.progress.ConstructionStrategyType;
import com.sc.api.structure.construction.progress.ConstructionTaskException;
import com.sc.api.structure.entity.Structure;
import com.sc.api.structure.entity.progress.ConstructionEntry;
import com.sc.api.structure.entity.progress.ConstructionTask;
import com.sc.api.structure.persistence.HibernateUtil;
import com.sc.api.structure.persistence.service.AbstractService;
import com.sc.api.structure.util.plugins.SCAsyncWorldEditUtil;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.primesoft.asyncworldedit.blockPlacer.BlockPlacer;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSession;

/**
 *
 * @author Chingo
 */
public class ConstructionTaskManager {

    private static final int INFINITE = -1;

    /**
     * Continues the task
     *
     * @param task The task
     * @param force will ignore the task current state, therefore even if the task was marked
     * completed it will try to continue the task
     * @throws com.sc.api.structure.construction.progress.ConstructionTaskException if task was
     * removed
     */
    public void continueTask(ConstructionTask task, boolean force) throws ConstructionTaskException {
        if ((task.getState() == ConstructionTask.State.PROGRESSING
                || task.getState() == ConstructionTask.State.QUEUED
                || task.getState() == ConstructionTask.State.COMPLETE) && !force) {
            return;
        }

        if (task.getState() == ConstructionTask.State.REMOVED) {
            throw new ConstructionTaskException("Tried to continue a removed task");
        }

        String placer = task.getPlacer();
        Structure structure = task.getStructure();
        LocalWorld world = structure.getLocation().getWorld();
        AsyncEditSession session = SCAsyncWorldEditUtil.createAsyncEditSession(placer, world, INFINITE);
//        ConstructionCallback dca = 

        List<Vector> vertices;
        SCJobCallback jc;
        CuboidClipboard schematic;
        if (!task.isDemolishing()) {
            schematic = structure.getPlan().getSchematic();
            jc = new ConstructionCallback(placer, structure, task, session);
            vertices = ConstructionStrategyType.LAYERED.getList(schematic, false);
        } else {
            schematic = structure.getAreaBefore();
            vertices = ConstructionStrategyType.LAYERED.getList(schematic, false);
            Collections.reverse(vertices);
            jc = new DemolisionCallback(structure.getOwner(), structure, task, session);
        }
        ConstructionManager.align(schematic, structure.getLocation(), structure.getCardinal());
        final SmartClipBoard smartClipBoard = new SmartClipBoard(schematic, vertices);
        final SCAsyncCuboidClipboard asyncCuboidClipboard = new SCAsyncCuboidClipboard(session.getPlayer(), smartClipBoard);
        try {
            asyncCuboidClipboard.place(session, structure.getDimension().getMin().getPosition(), false, jc);
        } catch (MaxChangedBlocksException ex) {
            java.util.logging.Logger.getLogger(SyncBuilder.class.getName()).log(Level.SEVERE, null, ex); // Won't happen
        }
    }

    /**
     * Stops the task, the task will be removed from AsyncWorldEdit's blockplacer queue, but will
     * still remain in the database
     *
     * @param task The task to stop
     * @param force
     * @return The construction task
     */
    public ConstructionTask stopTask(ConstructionTask task, boolean force) throws ConstructionTaskException {
        if (task.getState() == ConstructionTask.State.STOPPED && !force) {
            return task;
        }

        if (task.getState() == ConstructionTask.State.REMOVED) {
            throw new ConstructionTaskException("Tried to stop a removed task");
        }

        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSession();
            tx = session.beginTransaction();
            BlockPlacer placer = SCAsyncWorldEditUtil.getBlockPlacer();
            int jobId = task.getJobId();
            ConstructionEntry constructionEntry = task.getConstructionEntry();

            if (jobId != -1) {
                placer.cancelJob(constructionEntry.getEntryName(), jobId);
            } else {
                // Task already stopped
                return task;
            }
            task.setJobId(-1);
            task.setState(ConstructionTask.State.STOPPED);
            task = (ConstructionTask) session.merge(task);
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
        return task;
    }

    /**
     * Stops the task and continue's it. As result the task will be added at the back of the queue
     * by AsyncWorldEdit
     *
     * @param task The task
     * @param force
     * @throws com.sc.api.structure.construction.progress.ConstructionTaskException
     */
    public void moveTask(ConstructionTask task, boolean force) throws ConstructionTaskException {
        if (task.getConstructionEntry().getTasks().size() > 1) { // otherwise the task is probably me...
            stopTask(task, force);
            continueTask(task, force);
        }
    }

//    public ConstructionTask cancelTask(ConstructionTask task, boolean force) throws ConstructionTaskException {
//        if (task.getState() == ConstructionTask.State.CANCELED && !force) {
//            return task;
//        }
//
//        if (task.getState() == ConstructionTask.State.REMOVED) {
//            throw new ConstructionTaskException("Tried to cancel a removed task");
//        }
//
//        Session session = null;
//        Transaction tx = null;
//        try {
//            session = HibernateUtil.getSession();
//            tx = session.beginTransaction();
//            BlockPlacer placer = SCAsyncWorldEditUtil.getBlockPlacer();
//            int jobId = task.getJobId();
//            ConstructionEntry constructionEntry = task.getConstructionEntry();
//
//            if (jobId != -1) {
//                placer.cancelJob(constructionEntry.getEntryName(), jobId);
//                task.setJobId(-1);
//                tx.commit();
//                if (task.hasPlacedBlocks()) {
//                    SCStructureAPI.undo(task.getStructure());
//                } else {
//                    SCStructureAPI.remove(task.getStructure());
//                }
//            }
//
//        } catch (HibernateException e) {
//            try {
//                tx.rollback();
//            } catch (HibernateException rbe) {
//                java.util.logging.Logger.getLogger(AbstractService.class.getName()).log(Level.SEVERE, "Couldn’t roll back transaction", rbe);
//            }
//            throw e;
//        } finally {
//            if (session != null) {
//                session.close();
//            }
//        }
//        return task;
//    }

}
