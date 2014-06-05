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
package com.sc.api.structure.persistence.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Chingo
 */
public class RestoreService {

    private final ExecutorService executor;

    private static final int INFINITE = -1;

    public RestoreService() {
        int processors = Runtime.getRuntime().availableProcessors();
        this.executor = Executors.newFixedThreadPool(processors);
    }

//    public void restore() {
//        HashMap<String, Timestamp> worlddata = getWorldData();
//        for (Entry<String, Timestamp> entry : worlddata.entrySet()) {
//            setTasks(entry.getKey(), entry.getValue());
//            removeCreatedBefore(entry.getKey(), entry.getValue());
//        }
//        resetJobIds();
//        
////        TaskService service = new TaskService();
////        List<ConstructionEntry> entries = service.getEntries();
////        for (final ConstructionEntry e : entries) {
////            executor.execute(new Runnable() {
////                @Override
////                public void run() {
////                    processEntry(e);
////                }
////            });
////        }
//    }

//    /**
//     * Gets the world data for all worlds that have/had structure tasks assigned within them
//     *
//     * @return Map of all structure-tasked worlds with worldname as key and timestamp as value
//     */
//    private HashMap<String, Timestamp> getWorldData() {
//        HashMap<String, Timestamp> worldData = new HashMap<>();
//        for (World world : Bukkit.getWorlds()) {
//            if (hasTask(world)) {
//                Timestamp t = new Timestamp(world.getWorldFolder().lastModified());
//                System.out.println("World: " + world.getName() + " Timestamp: " + t);
//                worldData.put(world.getName(), t);
//            }
//        }
//        return worldData;
//    }

//    /**
//     * Gets the level.dat file from a worldfolder
//     *
//     * @param worldDataFolder The worldFolder
//     * @return The level.data file
//     */
//    private File getDatFile(File worldDataFolder) {
//        for (File file : worldDataFolder.listFiles()) {
//            if (file.getName().equals("level.dat")) {
//                return file;
//            }
//        }
//        throw new RuntimeException("World doesnt have a level.dat!");
//    }
//    /**
//     * Checks wheter this world has been tasked with structures
//     *
//     * @param world The world
//     * @return true if this world has tasked the structure-api
//     */
//    private boolean hasTask(World world) {
//        Session session = HibernateUtil.getSession();
//        JPQLQuery query = new HibernateQuery(session);
//        QConstructionTask qtask = QConstructionTask.constructionTask;
//        boolean exists = query.from(qtask).where(qtask.constructionTaskData().worldId.eq(world.getUID())).exists();
//        session.close();
//        return exists;
//    }
//
//    private void resetJobIds() {
//        Session session = HibernateUtil.getSession();
//        QConstructionTask qct = QConstructionTask.constructionTask;
//        new HibernateUpdateClause(session, qct).set(qct.jobId, -1).execute();
//        session.close();
//    }
//
//    private void setTasks(String world, Timestamp timestamp) {
//        Session session = null;
//        Transaction tx = null;
//        try {
//            session = HibernateUtil.getSession();
//            tx = session.beginTransaction();
//            QConstructionTask qct = QConstructionTask.constructionTask;
//            JPQLQuery query = new HibernateQuery(session);
//            List<ConstructionTask> tasks = query.from(qct).where(qct.completeAt.after(timestamp).or((qct.constructionState.ne(ConstructionTask.State.COMPLETE)).and(qct.constructionState.ne(ConstructionTask.State.REMOVED)))).list(qct);
//            Iterator<ConstructionTask> it = tasks.iterator();
//            while (it.hasNext()) {
//                ConstructionTask t = it.next();
//                t.setState(ConstructionTask.State.STOPPED);
//                session.merge(t);
//            }
//            tx.commit();
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
//    }
//
//    private void removeCreatedBefore(String world, Timestamp timestamp) {
//        Session session = null;
//        Transaction tx = null;
//        try {
//            session = HibernateUtil.getSession();
//            tx = session.beginTransaction();
//            QConstructionTask qct = QConstructionTask.constructionTask;
//            JPQLQuery query = new HibernateQuery(session);
//            List<ConstructionTask> tasks = query.from(qct).where(qct.createdAt.after(timestamp)).list(qct);
//            if (!tasks.isEmpty()) {
//                System.out.println("[SCStructureAPI]:" + world + " has " + tasks.size() + " tasks / structure that were placed after the last save");
//            }
//            Iterator<ConstructionTask> it = tasks.iterator();
//            World w = Bukkit.getWorld(world);
//            RegionManager rmgr = SCWorldGuardUtil.getGlobalRegionManager(w);
//            while (it.hasNext()) {
//                ConstructionTask t = it.next();
//                rmgr.removeRegion(t.getData().getRegionId());
//                System.out.println("[SCStructureAPI]: " + t.getData().getRegionId() + " has been removed");
//                t.setState(ConstructionTask.State.REMOVED);
//                session.update(t);
//            }
//
//            try {
//                rmgr.save();
//                tx.commit();
//            } catch (ProtectionDatabaseException ex) {
//                Logger.getLogger(RestoreService.class.getName()).log(Level.SEVERE, null, ex);
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
//    }
//
//   
//
//    /**
//     * Removes all construction tasks in the state REMOVED even if they haven't been refunded
//     */
//    public void clean() {
//        Session session = HibernateUtil.getSession();
//        QConstructionTask qct = QConstructionTask.constructionTask;
//        new HibernateDeleteClause(session, qct).where(qct.constructionState.eq(ConstructionTask.State.REMOVED));
//        session.close();
//    }
//


//    public void processEntry(ConstructionEntry e) {
//        List<ConstructionTask> tasks = getQueuedTasks(e);
//        Iterator<ConstructionTask> taskIterator = tasks.iterator();
//        while (taskIterator.hasNext()) {
//            ConstructionTask t = taskIterator.next();
//            continueTask(t);
//
//        }
//    }

//    private void continueTask(ConstructionTask task) {
//        String placer = task.getPlacer();
//
//        System.out.println("placer: " + placer);
//
//        Structure structure = task.getStructure();
//        LocalWorld world = structure.getLocation().getWorld();
//        AsyncEditSession session = SCAsyncWorldEditUtil.createAsyncEditSession(placer, world, INFINITE);
//
//        System.out.println(session.getPlayer());
//
//        final ConstructionCallback dca = new ConstructionCallback(placer, structure, task, session);
//
//        final CuboidClipboard schematic = structure.getPlan().getSchematic();
//        final Location t = ConstructionManager.align(schematic, structure.getLocation(), structure.getCardinal());
//        final SmartClipBoard smartClipboard = new SmartClipBoard(schematic, ConstructionStrategyType.LAYERED, false);
//        final SCAsyncCuboidClipboard asyncCuboidClipboard = new SCAsyncCuboidClipboard(session.getPlayer(), smartClipboard);
//
//        try {
//            Thread.sleep(5000);
//            asyncCuboidClipboard.place(session, t.getPosition(), false, dca);
//        } catch (MaxChangedBlocksException ex) {
//            Logger.getLogger(SyncBuilder.class.getName()).log(Level.SEVERE, null, ex); // Won't happen
//        } catch (InterruptedException ex) {
//            Logger.getLogger(RestoreService.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

//    private List<ConstructionTask> getQueuedTasks(ConstructionEntry e) {
//        Session session = HibernateUtil.getSession();
//        JPQLQuery query = new HibernateQuery(session);
//        QConstructionTask qct = QConstructionTask.constructionTask;
//        List<ConstructionTask> tasks = query.from(qct)
//                .orderBy(qct.createdAt.asc())
//                .where(
//                        qct.constructionEntry().entryName.eq(e.getEntryName())
//                        .and(qct.constructionState.eq(ConstructionTask.State.QUEUED)))
//                .list(qct);
//        return tasks;
//    }
}
