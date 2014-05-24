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
import com.sc.api.structure.construction.AsyncBuilder;
import com.sc.api.structure.construction.async.SCDefaultCallbackAction;
import com.sc.api.structure.construction.progress.ConstructionEntry;
import com.sc.api.structure.construction.progress.ConstructionException;
import com.sc.api.structure.construction.progress.ConstructionState;
import com.sc.api.structure.construction.progress.ConstructionTask;
import com.sc.api.structure.construction.progress.QConstructionTask;
import com.sc.api.structure.model.Structure;
import com.sc.api.structure.persistence.service.AbstractService;
import com.sc.api.structure.persistence.HibernateUtil;
import com.sc.api.structure.util.WorldUtil;
import com.sc.api.structure.util.plugins.SCAsyncWorldEditUtil;
import com.sc.api.structure.util.plugins.SCWorldGuardUtil;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSession;

/**
 *
 * @author Chingo
 */
class SCConstructionRestoreService extends AbstractService {

    private static final int STRUCTURE_ID = 0;
    private static final int STRUCTURE_NAME = 1;
    private static final int STRUCTURE_PROGRESS = 2;

    private static Logger logger = Logger.getLogger(SCStructureAPI.class);

    public static void restoreProgress() {
        // PREPARE TRANSACTION
        Set<ConstructionTask> removed = new HashSet<>();
        Set<ConstructionTask> completed = new HashSet<>();
        Set<ConstructionEntry> invalid = new HashSet<>();
        Set<ConstructionTask> tasks = new HashSet<>(getTasks());
        validate(tasks, removed, completed, invalid);
        commitChanges(removed, completed);
    }

    public static void commitChanges(Set<ConstructionTask> removed, Set<ConstructionTask> completed) {
        Session session = null;
        Transaction tx = null;

        try {
            session = HibernateUtil.getSession();
            tx = session.beginTransaction();
            Iterator<ConstructionTask> ri = removed.iterator();
            Iterator<ConstructionTask> ci = completed.iterator();

            while (ri.hasNext()) {
                ConstructionTask ct = ri.next();
                session.merge(ct);
            }

            while (ci.hasNext()) {
                ConstructionTask ct = ci.next();
                session.delete(ct);
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

    public static void validate(Set<ConstructionTask> tasks, final Set<ConstructionTask> removed, Set<ConstructionTask> completed, Set<ConstructionEntry> invalid) {
        Iterator<ConstructionTask> it = tasks.iterator();

        while (it.hasNext()) {
            ConstructionTask t = it.next();
            if (!hasSign(t)) {
                if (t.getState() == ConstructionState.FINISHED) {
                    it.remove();
                    if (t.getStructure() == null) {
                        removed.add(t);
                        continue;
                    }

                    if (validProgress(t)) {
                        System.out.println("completion confirmed task #" + t.getId());
                        completed.add(t);
                    } else {
                        System.out.println("invalid completed task #: " + t.getId());
                        System.out.println("storing entry if not present: " + t.getConstructionEntry().getEntryName());
                        invalid.add(t.getConstructionEntry());
                    }

                } else {
                    it.remove();

                    t.setState(ConstructionState.REMOVED);
                    removed.add(t);
                    System.out.println("task #" + t.getId() + " has been assigned for removal");
                    if (t.getStructure() != null) {
                        SCWorldGuardUtil.getGlobalRegionManager(Bukkit.getWorld(t.getStructure().getLocation().getWorld().getName())).removeRegion(t.getStructure().getStructureRegion());
                        System.out.println("removed  associated structure region: " + t.getStructure().getStructureRegion());
                    }

                }
            } else {
                if (!validProgress(t)) {
                    System.out.println("invalid progress task #: " + t.getId());
                    invalid.add(t.getConstructionEntry());
                }
            }
        }
    }

    private static boolean validProgress(ConstructionTask task) {

        Sign sign = WorldUtil.getSign(task.getSignLocation());
        if (task.getState() == ConstructionState.FINISHED && sign == null) {
            return true;
        }

        if (sign.getLine(STRUCTURE_PROGRESS).equals(task.getState().name())) {
            return true;
        }
        return false;
    }

    private static boolean hasSign(ConstructionTask task) {
        if (task.getStructure() == null) {
            return false;
        }

        Sign sign = WorldUtil.getSign(task.getSignLocation());
        if (sign == null) {
            return false;
        }
        System.out.println("task: " + task.getId());
        System.out.println("structure: " + task.getStructure());
        System.out.println("sId: " + task.getStructure().getId());

        if (sign.getLine(STRUCTURE_ID).isEmpty()
                || !Long.getLong(sign.getLine(STRUCTURE_ID))
                .equals(task.getStructure().getId())) {
            return false;
        }

        return true;
    }

    public static List<ConstructionTask> getTasks() {
        Session session = HibernateUtil.getSession();
        QConstructionTask qct = QConstructionTask.constructionTask;
        JPQLQuery query = new HibernateQuery(session);
        List<ConstructionTask> tasks = query.from(qct).orderBy(qct.id.asc()).where(qct.state.ne(ConstructionState.REMOVED)).list(qct);
        session.close();
        return tasks;
    }

    private static void startTask(ConstructionTask task) {
        place(task.getConstructionEntry().getEntryName(), task);
    }

    private static void place(String placer, ConstructionTask task) {
        final Structure structure = task.getStructure();
        final AsyncEditSession asyncSession = SCAsyncWorldEditUtil.createAsyncEditSession(placer, structure.getLocation().getWorld(), -1); // -1 = infinite

        System.out.println("Added " + structure.getStructureRegion() + " to Queue");
        //TODO Place enclosure
        SCDefaultCallbackAction dca = new SCDefaultCallbackAction(placer, structure, task, asyncSession);

        try {
            AsyncBuilder.placeStructure(placer, structure);
        }
        catch (ConstructionException ex) {
            java.util.logging.Logger.getLogger(SCConstructionRestoreService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
