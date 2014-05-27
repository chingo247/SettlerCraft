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

import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.sc.api.structure.entity.Structure;
import com.sc.api.structure.entity.progress.ConstructionEntry;
import com.sc.api.structure.entity.progress.ConstructionTask;
import com.sc.api.structure.entity.progress.ConstructionTask.State;
import com.sc.api.structure.entity.progress.QConstructionEntry;
import com.sc.api.structure.entity.progress.QConstructionTask;
import com.sc.api.structure.persistence.HibernateUtil;
import java.util.List;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author Chingo
 */
public class ConstructionService extends AbstractService {

    private final Logger logger = Logger.getLogger("ConstructionService");

    /**
     * Creates an entry for speficied issuer, the issuer may be a player or something else (group / npc) as long as it has a valid representative or owner
     * or a bankaccount (if vault)
     * @param issuer The issuer
     * @return The created entry
     */
    public ConstructionEntry createEntry(String issuer) {
        ConstructionEntry entry = new ConstructionEntry(issuer);
        return save(entry);
    }

    /**
     * Saves a task
     * @param constructionTask The constructionTask
     * @return The save instance
     */
    public ConstructionTask save(ConstructionTask constructionTask) {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSession();
            tx = session.beginTransaction();
            constructionTask = (ConstructionTask) session.merge(constructionTask);
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
        return constructionTask;
    }

    /**
     * Saves a constructionEntry
     * @param constructionEntry The constructionEntry
     * @return The saved instance
     */
    public ConstructionEntry save(ConstructionEntry constructionEntry) {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSession();
            tx = session.beginTransaction();
            constructionEntry = (ConstructionEntry) session.merge(constructionEntry);
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
        return constructionEntry;
    }

    /**
     * Determines wheter this structure already has a task
     * @param structure The structure
     * @return true if this structure already has a task
     */
    public boolean hasConstructionTask(Structure structure) {
        Session session = HibernateUtil.getSession();
        QConstructionTask qt = QConstructionTask.constructionTask;
        JPQLQuery query = new HibernateQuery(session);
        boolean exists = query.from(qt).where(qt.structure().id.eq(structure.getId())).exists();
        session.close();
        return exists;
    }

    /**
     * Updates the the status of a task and saves the task
     * @param task The task
     * @param newStatus The newstatus of the task
     * @return the updated/saved instance of the task
     */
    public ConstructionTask updateStatus(ConstructionTask task, State newStatus) {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSession();
            tx = session.beginTransaction();
            task.setState(newStatus);
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

    public ConstructionEntry getEntry(String player) {
        Session session = HibernateUtil.getSession();
        QConstructionEntry qce = QConstructionEntry.constructionEntry;
        ConstructionEntry cachedEntry = (ConstructionEntry) session.get(ConstructionEntry.class, player);
        if (cachedEntry != null) {
            logger.info("Used a cached entry!");
            return cachedEntry;
        }
        JPQLQuery query = new HibernateQuery(session);
        ConstructionEntry ce = query.from(qce).where(qce.entryName.eq(player)).uniqueResult(qce);
        session.close();
        return ce;
    }

    /**
     * Checks whether the entry exists or not
     * @param entryName The name of the entry
     * @return True if entry exists, othwerwise false
     */
    public boolean hasEntry(String entryName) {
        return getEntry(entryName) != null;
    }

    /**
     * Gets the entries
     * @return  List of all entries
     */
    public List<ConstructionEntry> getEntries() {
        Session session = HibernateUtil.getSession();
        QConstructionEntry qce = QConstructionEntry.constructionEntry;
        JPQLQuery query = new HibernateQuery(session);
        List<ConstructionEntry> entries = query.from(qce).list(qce);
        session.close();
        return entries;
    }


    public ConstructionEntry removeConstructionTask(String issuer, ConstructionTask constructionTask) {
        Session session = null;
        Transaction tx = null;
        ConstructionEntry entry = null;
        try {
            session = HibernateUtil.getSession();
            tx = session.beginTransaction();
            entry = getEntry(issuer);
            entry.remove(constructionTask);
            entry = (ConstructionEntry) session.merge(entry);

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
        return entry;
    }
}
