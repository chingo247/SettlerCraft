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
import com.mysema.query.jpa.hibernate.HibernateUpdateClause;
import com.sc.api.structure.construction.progress.ConstructionEntry;
import com.sc.api.structure.construction.progress.ConstructionState;
import com.sc.api.structure.construction.progress.ConstructionTask;
import com.sc.api.structure.construction.progress.QConstructionEntry;
import com.sc.api.structure.construction.progress.QConstructionTask;
import com.sc.api.structure.model.Structure;
import com.sc.api.structure.persistence.HibernateUtil;
import com.sc.api.structure.util.WorldUtil;
import java.sql.Timestamp;
import java.util.Date;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.bukkit.block.Sign;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author Chingo
 */
public class ConstructionService extends AbstractService {

    private final Logger logger = Logger.getLogger("ConstructionService");

    public ConstructionEntry createEntry(String issuer) {
        ConstructionEntry entry = new ConstructionEntry(issuer);
        return save(entry);
    }

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

    public boolean hasConstructionTask(Structure structure) {
        Session session = HibernateUtil.getSession();
        QConstructionTask qt = QConstructionTask.constructionTask;
        JPQLQuery query = new HibernateQuery(session);
        boolean exists = query.from(qt).where(qt.structure().id.eq(structure.getId())).exists();
        session.close();
        return exists;
    }

    public void updateStatus(ConstructionTask task, ConstructionState newStatus) {
        Session session = HibernateUtil.getSession();
        QConstructionTask qct = QConstructionTask.constructionTask;
        if (newStatus == ConstructionState.FINISHED) {
            Timestamp completedAt = new Timestamp(new Date().getTime());
            new HibernateUpdateClause(session, qct).where(qct.id.eq(task.getId())).set(qct.state, newStatus).set(qct.completeAt, completedAt).execute();
        } else {
            new HibernateUpdateClause(session, qct).where(qct.id.eq(task.getId())).set(qct.state, newStatus).execute();
        }

        Sign sign = WorldUtil.getSign(task.getSignLocation());
        if (sign != null) {
            sign.setLine(2, newStatus.name());
            sign.update(true);
        }
    }

    public ConstructionEntry getEntry(String player) {
        Session session = HibernateUtil.getSession();
        QConstructionEntry qce = QConstructionEntry.constructionEntry;
        ConstructionEntry cachedEntry = (ConstructionEntry) session.get(ConstructionEntry.class, player);
        if (cachedEntry != null) {
            logger.info("Used a cached entry! >:D");
            return cachedEntry;
        }
        JPQLQuery query = new HibernateQuery(session);
        ConstructionEntry ce = query.from(qce).where(qce.player.eq(player)).uniqueResult(qce);
        session.close();
        return ce;
    }

    public boolean hasEntry(String entryName) {
        return getEntry(entryName) != null;
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
