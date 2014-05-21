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
package com.sc.api.structure;

import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.sc.api.structure.construction.builder.async.SCAsyncCuboidBuilder;
import com.sc.api.structure.construction.builder.strategies.SCDefaultCallbackAction;
import com.sc.api.structure.construction.progress.ConstructionState;
import com.sc.api.structure.construction.progress.ConstructionTask;
import com.sc.api.structure.construction.progress.QConstructionTask;
import com.sc.api.structure.construction.progress.StructureBuilder;
import com.sc.api.structure.model.Structure;
import com.sc.api.structure.persistence.AbstractService;
import com.sc.api.structure.persistence.util.HibernateUtil;
import com.sc.api.structure.util.plugins.AsyncWorldEditUtil;
import com.sc.api.structure.util.plugins.WorldGuardUtil;
import com.sk89q.worldedit.MaxChangedBlocksException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSession;

/**
 *
 * @author Chingo
 */
class SCConstructionRestoreService extends AbstractService {

    private static Logger logger = Logger.getLogger(SCStructureAPI.class);

    public static void restoreProgress() {
        HashMap<String, Timestamp> worlds = getWorlds();
        for (Entry<String, Timestamp> e : worlds.entrySet()) {
            deleteCreatedBefore(e.getKey(), e.getValue());
            List<ConstructionTask> highPriority = getCompletedBefore(e.getKey(), e.getValue());
            System.out.println("High Priority Tasks: " + highPriority.size());
            restoreConstructionTasks(e.getKey(), e.getValue(), highPriority);
        }
        // Set Completed before
    }

    private static List<ConstructionTask> getCompletedBefore(String w, Timestamp t) {
        Session session = HibernateUtil.getSession();
        QConstructionTask qct = QConstructionTask.constructionTask;
        JPQLQuery query = new HibernateQuery(session);
        List<ConstructionTask> tasks = query.from(qct).orderBy(qct.completeAt.desc()).where(qct.completeAt.after(t).and(qct.structure().worldLocation().world.eq(w))).list(qct);
        session.close();
        return tasks;
    }

    private static void deleteCreatedBefore(String w, Timestamp t) {
        Session session = null;
        Transaction tx = null;
        QConstructionTask qt = QConstructionTask.constructionTask;

        try {
            session = HibernateUtil.getSession();
            tx = session.beginTransaction();
            JPQLQuery query = new HibernateQuery(session);
            List<ConstructionTask> tasks = query.from(qt).where(qt.createdAt.after(t).and(qt.structure().worldLocation().world.eq(w))).list(qt);
            if (!tasks.isEmpty()) {
                logger.warn("SCStructureAPI: removing structures placed after last save for world " + w);
                World world = Bukkit.getWorld(w);
                for (ConstructionTask task : tasks) {
                    String region = task.getStructure().getStructureRegion();

                    if (world != null) {
                        WorldGuardUtil.getGlobalRegionManager(world).removeRegion(region);
                    }
                    logger.info("StructureRegion removed: " + region);
                    session.delete(task);
//                    session.flush();
                    logger.info("Task: " + task.getId() + " removed");
                }

            }
            tx.commit();
        }
        catch (HibernateException e) {
            try {
                tx.rollback();
            }
            catch (HibernateException rbe) {
                java.util.logging.Logger.getLogger(AbstractService.class.getName()).log(Level.SEVERE, "Couldn’t roll back transaction", rbe);
            }
            throw e;
        }
        finally {
            if (session != null) {
                session.close();
            }
        }
    }

    private static HashMap<String, Timestamp> getWorlds() {
        HashMap<String, Timestamp> worlds = new HashMap<>();
        for (World w : Bukkit.getWorlds()) {
            worlds.put(w.getName(), new Timestamp(w.getWorldFolder().lastModified()));
        }
        return worlds;
    }

    private static void restoreConstructionTasks(String w, Timestamp t, List<ConstructionTask> highPriority) {
        Session session = HibernateUtil.getSession();
        QConstructionTask qct = QConstructionTask.constructionTask;
        JPQLQuery query = new HibernateQuery(session);
        List<ConstructionTask> tasks = query.from(qct).orderBy(qct.createdAt.desc()).where(qct.structure().worldLocation().world.eq(w).and(qct.state.ne(ConstructionState.FINISHED))).list(qct);
        System.out.println("Tasks: " + tasks.size());
        session.close();
        for (ConstructionTask ct : highPriority) {
            System.out.println("HighPriority Task: " + ct.getStructure().getStructureRegion());
            startTask(ct);
        }
        
        
        for (ConstructionTask ct : tasks) {
            System.out.println("Task: " + ct.getStructure().getStructureRegion());
            startTask(ct);
        }
    }

    private static void startTask(ConstructionTask task) {
        place(task.getConstructionEntry().getEntryName(), task);
    }

    private static void place(String placer,ConstructionTask task) {
        final Structure structure = task.getStructure();
        final AsyncEditSession asyncSession = AsyncWorldEditUtil.createAsyncEditSession(placer, structure.getLocation().getWorld(), -1); // -1 = infinite

        System.out.println("Added " + structure.getStructureRegion() + " to Queue");
        //TODO Place enclosure
        SCDefaultCallbackAction dca = new SCDefaultCallbackAction(placer, structure, task, asyncSession);

        try {
            SCAsyncCuboidBuilder.placeLayered(
                    asyncSession,
                    structure.getPlan().getSchematic(),
                    structure.getLocation(),
                    structure.getDirection(),
                    structure.getPlan().getDisplayName(),
                    dca
            );
        }
        catch (MaxChangedBlocksException ex) {
            java.util.logging.Logger.getLogger(StructureBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
