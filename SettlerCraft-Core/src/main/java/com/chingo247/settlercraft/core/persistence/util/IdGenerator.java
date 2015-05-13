/*
 * Copyright (C) 2015 Chingo
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
package com.chingo247.settlercraft.core.persistence.util;

import com.chingo247.settlercraft.core.persistence.hibernate.HibernateUtil;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author Chingo
 */
public class IdGenerator {

    private final String generatorName;

    public IdGenerator(String generatorName) {
        this.generatorName = generatorName;
    }

    public synchronized Long nextId() {
        Session session = null;
        Transaction tx = null;
        Long id = null;

        try {
            session = HibernateUtil.getSession();
            tx = session.beginTransaction();
            IdGeneratorEntity generator = (IdGeneratorEntity) session.get(IdGeneratorEntity.class, generatorName);
            id = generator.incrementAndGet();
            session.save(generator);
            tx.commit();

        } catch (HibernateException exception) {
            try {
                if (tx != null) {
                    tx.rollback();
                }
            } catch (HibernateException rbe) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Couldn't rollback transaction", rbe);
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }

        return id;
    }

    synchronized void createIfNotExist() {

        Session session = null;
        Transaction tx = null;

        try {
            session = HibernateUtil.getSession();
            tx = session.beginTransaction();
            HibernateQuery query = new HibernateQuery(session);
            QIdGeneratorEntity qig = QIdGeneratorEntity.idGeneratorEntity;
            boolean exists = query.from(qig).where(qig.name.eq(generatorName)).exists();
            if (!exists) {
                IdGeneratorEntity generator = new IdGeneratorEntity(generatorName);
                session.save(generator);
            }
            tx.commit();

        } catch (HibernateException exception) {
            try {
                if (tx != null) {
                    tx.rollback();
                }
            } catch (HibernateException rbe) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Couldn't rollback transaction", rbe);
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }

        
    }

}
