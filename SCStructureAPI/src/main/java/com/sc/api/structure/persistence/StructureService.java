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
import com.mysema.query.jpa.hibernate.HibernateDeleteClause;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.sc.api.structure.model.structure.QStructure;
import com.sc.api.structure.model.structure.ReservedArea;
import com.sc.api.structure.model.structure.Structure;
import com.sc.api.structure.model.structure.StructureState;
import com.sc.api.structure.persistence.util.HibernateUtil;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author Chingo
 */
public class StructureService extends AbstractService {
    
    public Structure getStructure(Long id) {
        QStructure qstructure = QStructure.structure;
        Session session = HibernateUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);
        Structure structure = query.from(qstructure).where(qstructure.id.eq(id)).uniqueResult(qstructure);
        session.close();
        return structure;
    }

    public List<Structure> getStructures() {
        QStructure structure = QStructure.structure;
        Session session = HibernateUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);
        List<Structure> structures = query.from(structure).list(structure);
        session.close();
        return structures;
    }

    public List<Structure> getStructures(String owner) {
        QStructure structure = QStructure.structure;
        Session session = HibernateUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);
        List<Structure> structures = query.from(structure).where(structure.owner.eq(owner)).list(structure);
        session.close();
        return structures;
    }
    
    public void delete(Structure structure) {
        Session session = HibernateUtil.getSession();
        QStructure qstructure = QStructure.structure;
        new HibernateDeleteClause(session, qstructure).where(qstructure.id.eq(structure.getId())).execute();
    }
    
    public void setStatus(Structure structure, StructureState newStatus) {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSession();
            tx = session.beginTransaction();
            structure.setStatus(newStatus);
            session.merge(structure);
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
    }
    
    public void save(Structure structure) {
                Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSession();
            tx = session.beginTransaction();
            session.merge(structure);
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
    }
    
    /**
     * Determines if given location is on a structure. 
     * @param location The location
     * @return  getStructure() != null
     */
    public boolean isOnStructure(Location location) {
      return getStructure(location) != null;
    } 
    
    public Structure getStructure(Location location) {
     QStructure qStructure = QStructure.structure;
     Session session = HibernateUtil.getSession();
     JPQLQuery query = new HibernateQuery(session);
     
    
      Structure structure = query.from(qStructure)
              .where(qStructure.worldLocation().world.eq(location.getWorld().getName())
                .and(qStructure.dimension().startX.loe(location.getBlockX()))
                .and(qStructure.dimension().endX.goe(location.getBlockX()))
                .and(qStructure.dimension().startZ.loe(location.getBlockZ()))
                .and(qStructure.dimension().endZ.goe(location.getBlockZ()))
                .and(qStructure.dimension().startY.loe(location.getBlockY()))
                .and(qStructure.dimension().endY.goe(location.getBlockY()))
                      
              ).uniqueResult(qStructure);

      session.close();
      return structure;
    }
    
    public boolean overlaps(Structure structure) {
     QStructure qStructure = QStructure.structure;
     Session session = HibernateUtil.getSession();
     JPQLQuery query = new HibernateQuery(session);
     
        ReservedArea ra = structure.getReserved();
        int xMinus = ra.getR_xMinus();
        int zMinus = ra.getR_zMinus();
        int xPlus = ra.getR_xPlus();
        int zPlus = ra.getR_zPlus();
        int up = ra.getR_up();
        int down = ra.getR_down();

      boolean overlaps = query.from(qStructure)
              .where(qStructure.worldLocation().world.eq(structure.getLocation().getWorld().getName())
                      .and(qStructure.dimension().endX.add(qStructure.reserved().r_xPlus).goe(structure.getDimension().getStartX() - xMinus).and(qStructure.dimension().startX.subtract(qStructure.reserved().r_xMinus).loe(structure.getDimension().getEndX() + xPlus)))
                      .and(qStructure.dimension().endY.add(qStructure.reserved().r_up).goe(structure.getDimension().getStartY() - down).and(qStructure.dimension().startY.subtract(qStructure.reserved().r_down).loe(structure.getDimension().getEndY() + up)))
                      .and(qStructure.dimension().endZ.add(qStructure.reserved().r_zPlus).goe(structure.getDimension().getStartZ() - zMinus).and(qStructure.dimension().startZ.subtract(qStructure.reserved().r_zMinus).loe(structure.getDimension().getEndZ() + zPlus)))
             
      ).exists();
      session.close();
      return overlaps;
    }

}
