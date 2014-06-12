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
package com.sc.persistence;

import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.sc.construction.plan.QStructureSchematic;
import com.sc.construction.plan.StructureSchematic;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author Chingo
 */
public class SchematicService extends AbstractService {

    public StructureSchematic save(StructureSchematic schematic) {
        
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSession();
            tx = session.beginTransaction();
            schematic = (StructureSchematic) session.merge(schematic);
            tx.commit();
        } catch (HibernateException e) {
            try {
                tx.rollback();
            } catch (HibernateException rbe) {
                Logger.getLogger(AbstractService.class.getName()).log(Level.SEVERE, "Couldn’t roll back transaction", rbe);
            }
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return schematic;
    }
    
    public boolean exists(StructureSchematic schematic) {
        Session session = HibernateUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);
        QStructureSchematic qss = QStructureSchematic.structureSchematic;
        boolean exists = query.from(qss).where(qss.checkSum.eq(schematic.getCheckSum())).exists();
        session.close();
        return exists;
    }
    
    public boolean exists(Long checksum) {
        Session session = HibernateUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);
        QStructureSchematic qss = QStructureSchematic.structureSchematic;
        boolean exists = query.from(qss).where(qss.checkSum.eq(checksum)).exists();
        session.close();
        return exists;
    }
    
    public StructureSchematic getSchematic(Long checksum) {
        Session session = HibernateUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);
        QStructureSchematic qss = QStructureSchematic.structureSchematic;
        StructureSchematic schematic = query.from(qss).where(qss.checkSum.eq(checksum)).uniqueResult(qss);
        session.close();
        return schematic;
    }

}
