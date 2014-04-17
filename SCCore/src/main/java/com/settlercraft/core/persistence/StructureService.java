package com.settlercraft.core.persistence;

import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.settlercraft.core.model.entity.structure.QStructure;
import com.settlercraft.core.model.entity.structure.Structure;
import com.settlercraft.core.model.entity.structure.Structure.STATE;
import com.settlercraft.core.util.HibernateUtil;
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
public class StructureService extends AbstractService<Structure> {

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
    
    public void setStatus(Structure structure, STATE newStatus) {
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
      
      boolean overlaps = query.from(qStructure)
              .where(qStructure.worldLocation().world.eq(structure.getLocation().getWorld().getName())
                      // Y between?
                      .and(qStructure.dimension().startY.between(structure.getDimension().getStartY(), structure.getDimension().getEndY())
                      .or(qStructure.dimension().endY.between(structure.getDimension().getStartY(), structure.getDimension().getEndY())))
                      // X between?
                      .and((qStructure.dimension().startX.between(structure.getDimension().getStartX(), structure.getDimension().getEndX())
                      .or(qStructure.dimension().endX.between(structure.getDimension().getEndX(), structure.getDimension().getStartX())))
                      .or(qStructure.dimension().startX.between(structure.getDimension().getEndX(), structure.getDimension().getStartX())
                      .or(qStructure.dimension().endX.between(structure.getDimension().getStartX(), structure.getDimension().getEndX())))
                      // Z between? 
                      .and((qStructure.dimension().startZ.between(structure.getDimension().getStartZ(), structure.getDimension().getEndZ())
                      .or(qStructure.dimension().endZ.between(structure.getDimension().getEndZ(), structure.getDimension().getStartZ())))
                      .or(qStructure.dimension().startZ.between(structure.getDimension().getEndZ(), structure.getDimension().getStartZ())
                      .or(qStructure.dimension().endZ.between(structure.getDimension().getStartZ(), structure.getDimension().getEndZ()))))
                      )
      ).exists();
      session.close();
      return overlaps;
    }



}
