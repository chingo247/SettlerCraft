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
import org.hibernate.Session;

/**
 *
 * @author Chingo
 */
public class IdManager {
    
    private final String generatorName;

    public IdManager(String generatorName) {
        this.generatorName = generatorName;
    }
    
    public synchronized long nextId() {
        Session session = HibernateUtil.getSession();
        HibernateQuery query = new HibernateQuery(session);
        QIdGenerator qig = QIdGenerator.idGenerator;
        IdGenerator generator = query.from(qig).where(qig.name.eq(generatorName)).uniqueResult(qig);
        long id = generator.incrementAndGet();
        session.save(generator);
        return id;
    }
    
    synchronized void createIdGenerator() {
        Session session = HibernateUtil.getSession();
        HibernateQuery query = new HibernateQuery(session);
        QIdGenerator qig = QIdGenerator.idGenerator;
        boolean exists = query.from(qig).where(qig.name.eq(generatorName)).exists();
        if(!exists) {
            IdGenerator generator = new IdGenerator(generatorName);
            session.persist(generator);
        }
        session.close();
    }
    
}
