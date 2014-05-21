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
package com.sc.api.structure.persistence;

import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.sc.api.structure.construction.progress.ConstructionEntry;
import com.sc.api.structure.construction.progress.ConstructionTask;
import com.sc.api.structure.construction.progress.QConstructionEntry;
import com.sc.api.structure.construction.progress.QConstructionTask;
import com.sc.api.structure.model.Structure;
import com.sc.api.structure.persistence.util.HibernateUtil;
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
    
    public ConstructionEntry createEntry(String issuer) {
        ConstructionEntry entry = new ConstructionEntry(issuer);
        return save(entry);
    }
    


    public ConstructionTask save(ConstructionTask constructionTask) {
        Session session = HibernateUtil.getSession();
        ConstructionTask cp = (ConstructionTask) session.merge(constructionTask);
        session.close();
        return cp;
    }

    public ConstructionEntry save(ConstructionEntry constructionEntry) {
        Session session = HibernateUtil.getSession();
        ConstructionEntry ce = (ConstructionEntry) session.merge(constructionEntry);
        session.close();
        return ce;
    }
    
    public boolean hasConstructionTask(Structure structure) {
        return getConstructionTask(structure) != null;
    }
    
    public ConstructionTask getConstructionTask(Structure structure) {
        return getConstructionTask(structure.getId());
    }
    
    public ConstructionTask getConstructionTask(Long id) {
        Session session = HibernateUtil.getSession();
        QConstructionTask qt = QConstructionTask.constructionTask;
        JPQLQuery query = new HibernateQuery(session);
        ConstructionTask cachedTask = (ConstructionTask) session.get(ConstructionTask.class, id);
        if(cachedTask != null) {
            logger.info("Used a cached entry! >:D");
            return cachedTask;
        }
        ConstructionTask ce = query.from(qt).where(qt.id.eq(id)).uniqueResult(qt);
        session.close();
        return ce;
    }
    
    public ConstructionEntry getEntry(String entryName) {
        Session session = HibernateUtil.getSession();
        QConstructionEntry qce = QConstructionEntry.constructionEntry;
        ConstructionEntry cachedEntry = (ConstructionEntry) session.get(ConstructionEntry.class, entryName);
        if(cachedEntry != null) {
            logger.info("Used a cached entry! >:D");
            return cachedEntry;
        }
        JPQLQuery query = new HibernateQuery(session);
        ConstructionEntry ce = query.from(qce).where(qce.entryName.eq(entryName)).uniqueResult(qce);
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
