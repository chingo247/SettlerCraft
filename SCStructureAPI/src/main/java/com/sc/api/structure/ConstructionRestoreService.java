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
import com.mysema.query.jpa.hibernate.HibernateDeleteClause;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.sc.api.structure.construction.async.SCAsyncCuboidClipboard;
import com.sc.api.structure.construction.async.SCDefaultCallbackAction;
import com.sc.api.structure.construction.progress.ConstructionEntry;
import com.sc.api.structure.construction.progress.ConstructionState;
import com.sc.api.structure.construction.progress.ConstructionStrategyType;
import com.sc.api.structure.construction.progress.ConstructionTask;
import com.sc.api.structure.construction.progress.QConstructionTask;
import com.sc.api.structure.entity.Structure;
import com.sc.api.structure.persistence.HibernateUtil;
import com.sc.api.structure.persistence.service.AbstractService;
import com.sc.api.structure.persistence.service.ConstructionService;
import com.sc.api.structure.util.WorldUtil;
import com.sc.api.structure.util.plugins.SCAsyncWorldEditUtil;
import com.sc.api.structure.util.plugins.SCWorldGuardUtil;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Location;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.sk89q.worldguard.protection.managers.RegionManager;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSession;

/**
 *
 * @author Chingo
 */
public class ConstructionRestoreService extends AbstractService {

    private final ExecutorService executor;

    /**
     * The index of the structure id on a sign
     *
     * @deprecated assumes sign hack / workaround is used
     */
    @Deprecated
    private static final int STRUCTURE_ID = 0;
    /**
     * The index of the structure name on a sign
     *
     * @deprecated assumes sign hack / workaround is used
     */
    @Deprecated
    private static final int STRUCTURE_NAME = 1;
    /**
     * The index of the structure's task it's state on a sign
     *
     * @deprecated assumes sign hack / workaround is used
     */
    @Deprecated
    private static final int STRUCTURE_PROGRESS = 2;

    private static ConstructionRestoreService instance;

    private ConstructionRestoreService() {
        executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public static ConstructionRestoreService getInstance() {
        if (instance == null) {
            instance = new ConstructionRestoreService();

        }
        return instance;
    }

    public void restoreProgress() {
        // PREPARE TRANSACTION
        Set<ConstructionTask> removed = new HashSet<>();
        Set<ConstructionTask> confirmed = new HashSet<>();
        Set<ConstructionTask> tasks = new HashSet<>(getTasks());
        validateRemove();
        validate(tasks, removed, confirmed);
        commitChanges(removed, confirmed);
        processEntries();

    }

    /**
     * Move tasks from remove state to recycle bin
     */
    private void validateRemove() {
        Session session = null;
        Transaction tx = null;
        try {
        session = HibernateUtil.getSession();
        tx = session.beginTransaction();
        QConstructionTask qct = QConstructionTask.constructionTask;
        JPQLQuery query = new HibernateQuery(session);
        List<ConstructionTask> tasks = query.from(qct)
                .where(
                        qct.constructionState.eq(ConstructionState.REMOVED)
                ).list(qct);
      
        Iterator<ConstructionTask> it = tasks.iterator();
        while(it.hasNext()) {
            ConstructionTask t = it.next();
        
            Sign sign = WorldUtil.getSign(t.getSignLocation());
            if(sign == null) {
                t.setState(ConstructionState.IN_RECYCLE_BIN);
                session.merge(t);
            } 
        }
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
        
        
        
        
    }

    public List<ConstructionTask> getTasks() {
        Session session = HibernateUtil.getSession();
        QConstructionTask qct = QConstructionTask.constructionTask;
        JPQLQuery query = new HibernateQuery(session);
        List<ConstructionTask> tasks = query.from(qct).orderBy(qct.id.asc())
                .where(
                        qct.constructionState.ne(ConstructionState.IN_RECYCLE_BIN)
                        .and(qct.constructionState.ne(ConstructionState.COMPLETION_CONFIRMED)
                        )
                ).list(qct);
        session.close();
        return tasks;
    }

    private void commitChanges(Set<ConstructionTask> removed, Set<ConstructionTask> completed) {

        Session session = null;
        Transaction tx = null;

        try {
            session = HibernateUtil.getSession();
            tx = session.beginTransaction();
            Iterator<ConstructionTask> ri = removed.iterator();
            Iterator<ConstructionTask> ci = completed.iterator();

            while (ri.hasNext()) {
                ConstructionTask ct = ri.next();
                ct.setState(ConstructionState.IN_RECYCLE_BIN);
                session.merge(ct);
            }

            while (ci.hasNext()) {
                ConstructionTask ct = ci.next();
                ct.setState(ConstructionState.COMPLETION_CONFIRMED);
                session.merge(ct);
            }

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
    }

    private void validate(Set<ConstructionTask> tasks, final Set<ConstructionTask> removed, Set<ConstructionTask> confirmed) {
        Iterator<ConstructionTask> it = tasks.iterator();
        Set<String> worlds = new HashSet<>();
        while (it.hasNext()) {
            ConstructionTask t = it.next();
            World world = t.getStructureData().getWorld();
            if (world == null || t.getStructure() == null) {
                t.setState(ConstructionState.REMOVED); // World doesnt exist by UUID!
                continue;
            }
            if (!hasSign(t)) {
                if (t.getState() == ConstructionState.COMPLETE) {

                    if (validProgress(t)) {
                        System.out.println("completion confirmed task #" + t.getId());
                        confirmed.add(t);
                    }
                } else if (t.getState() == ConstructionState.IN_RECYCLE_BIN) {
                    RegionManager mgr = SCWorldGuardUtil.getGlobalRegionManager(world);
                    if (mgr.hasRegion(t.getStructureData().getRegionId())) {
                        System.out.println("Removing region: " + t.getStructureData().getRegionId() + " from " + world.getName());
                        mgr.removeRegion(t.getStructureData().getRegionId());
                        worlds.add(world.getName());
                    }
                } else {
                    it.remove();
                    removed.add(t);
                }
            } else {
                if (validProgress(t)) {
                    System.out.println("validated progress task #: " + t.getId());
                } else {
                    System.out.println("invalid progress task #: " + t.getId());
                }
            }
        }
        if (!worlds.isEmpty()) {
            System.out.println("Saving changes to worldguard files");
            for (String world : worlds) {
                RegionManager mgr = SCWorldGuardUtil.getGlobalRegionManager(Bukkit.getWorld(world));
                try {
                    mgr.save();
                } catch (ProtectionDatabaseException ex) {
                    Logger.getLogger(ConstructionRestoreService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private boolean validProgress(ConstructionTask task) {

        Sign sign = WorldUtil.getSign(task.getSignLocation());
        if (task.getState() == ConstructionState.COMPLETE && sign == null) {
            return true;
        }

        if (sign.getLine(STRUCTURE_PROGRESS).trim().equals(task.getState().name())) {
            return true;
        }

        return false;
    }

    private boolean hasSign(ConstructionTask task) {

        Sign sign = WorldUtil.getSign(task.getSignLocation());
        if (sign == null) {
            return false;
        }

        Long structureId = task.getStructure().getId();
        Long signId = Long.parseLong(sign.getLine(STRUCTURE_ID));

        if (!sign.getLine(STRUCTURE_ID).isEmpty()
                && !signId.equals(structureId)) {
            return false;
        }

        return true;
    }

    private void processEntries() {

        ConstructionService service = new ConstructionService();
        List<ConstructionEntry> entries = service.getEntries();
        System.out.println("Processing: " + entries.size() + " entries");
        Iterator<ConstructionEntry> it = entries.iterator();
        while (it.hasNext()) {
            final ConstructionEntry ce = it.next();
            executor.execute(new Runnable() {

                @Override
                public void run() {
                    processEntry(ce);
                }
            });
        }
    }

    private void processEntry(ConstructionEntry ce) {
        System.out.println("Processing Entry: " + ce.getEntryName());
        List<ConstructionTask> tasks = new ArrayList<>(ce.getTasks());
        System.out.println("tasks: " + tasks.size());
        Iterator<ConstructionTask> it = tasks.iterator();
        List<ConstructionTask> unfinished = new ArrayList<>();
        List<ConstructionTask> inprogress = new ArrayList<>();
        List<ConstructionTask> other = new ArrayList<>();

        while (it.hasNext()) {
            ConstructionTask t = it.next();
            System.out.println("task: " + t.getId());
            if (t.getState() == ConstructionState.IN_RECYCLE_BIN) {
                continue;
            }
            // Means it was validated and marked as invalid
            if (t.getState() == ConstructionState.COMPLETE) {
                unfinished.add(t);
            } else if (t.getState() == ConstructionState.IN_PROGRESS) {
                inprogress.add(t);
            } else {
                other.add(t);
            }
        }

        unfinished.addAll(inprogress);
        unfinished.addAll(other);

        System.out.println("starting queue: " + unfinished.size());
        Iterator<ConstructionTask> ext = unfinished.iterator();
        while (ext.hasNext()) {
            continueTask(ce.getEntryName(), ext.next());
        }
    }

    private void continueTask(String placer, ConstructionTask task) {
        System.out.println("continue task #" + task.getId());
        final Structure structure = task.getStructure();
        final AsyncEditSession asyncSession = SCAsyncWorldEditUtil.createAsyncEditSession(placer, structure.getLocation().getWorld(), -1); // -1 = infinite

        System.out.println("Added " + structure.getStructureRegion() + " to Queue");
        //TODO Place enclosure

        SCDefaultCallbackAction dca = new SCDefaultCallbackAction(placer, structure, task, asyncSession);
        CuboidClipboard schematic = structure.getPlan().getSchematic();
        Location t = SyncBuilder.align(schematic, structure.getLocation(), structure.getCardinal());
        Vector signVec = structure.getLocation().getPosition().subtract(t.getPosition()).add(0, 1, 0);
        SmartClipBoard smartClipboard = new SmartClipBoard(schematic, signVec, ConstructionStrategyType.LAYERED, false);
        SCAsyncCuboidClipboard asyncCuboidClipboard = new SCAsyncCuboidClipboard(asyncSession.getPlayer(), smartClipboard);

        try {
            asyncCuboidClipboard.place(asyncSession, t.getPosition(), false, dca);
        } catch (MaxChangedBlocksException ex) {
            Logger.getLogger(SyncBuilder.class.getName()).log(Level.SEVERE, null, ex); // Won't happen
        }
    }

    public void cleanBin() {
        Session session = HibernateUtil.getSession();
        QConstructionTask qct = QConstructionTask.constructionTask;
        new HibernateDeleteClause(session, qct).where(qct.constructionState.eq(ConstructionState.IN_RECYCLE_BIN));
        session.close();
    }

    public List<ConstructionTask> getUnRefunded() {
        Session session = HibernateUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);
        QConstructionTask qct = QConstructionTask.constructionTask;
        List<ConstructionTask> tasks = query.from(qct).where(qct.constructionState.eq(ConstructionState.IN_RECYCLE_BIN).and(qct.sd().refunded.ne(Boolean.TRUE))).list(qct);
        session.close();
        return tasks;
    }

}
