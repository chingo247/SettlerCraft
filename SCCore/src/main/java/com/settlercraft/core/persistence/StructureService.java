package com.settlercraft.core.persistence;

import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.settlercraft.core.model.entity.structure.QStructure;
import com.settlercraft.core.model.entity.structure.ReservedArea;
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
     
        ReservedArea ra = structure.getReserved();
        int xMinus = ra.getR_xMinus();
        int zMinus = ra.getR_zMinus();
        int xPlus = ra.getR_xPlus();
        int zPlus = ra.getR_zPlus();
        int up = ra.getR_up();
        int down = ra.getR_down();
      
      boolean overlaps = query.from(qStructure)
              .where(qStructure.worldLocation().world.eq(structure.getLocation().getWorld().getName())
                      // Y between?
                      .and(qStructure.dimension().startY.subtract(qStructure.reserved().r_down).between(structure.getDimension().getStartY() - down, structure.getDimension().getEndY() + up)
                      .or(qStructure.dimension().endY.add(qStructure.reserved().r_up).between(structure.getDimension().getStartY() - down, structure.getDimension().getEndY() + up)))
                      // X between?
                      .and((qStructure.dimension().startX.subtract(qStructure.reserved().r_xMinus).between(structure.getDimension().getStartX() - xMinus, structure.getDimension().getEndX() + xPlus)
                      .or(qStructure.dimension().endX.add(xPlus).between(structure.getDimension().getEndX() + xPlus, structure.getDimension().getStartX() + xMinus)))
                      .or(qStructure.dimension().startX.subtract(qStructure.reserved().r_xMinus).between(structure.getDimension().getEndX() + xPlus, structure.getDimension().getStartX() - xMinus)
                      .or(qStructure.dimension().endX.add(qStructure.reserved().r_xPlus).between(structure.getDimension().getStartX() - xMinus, structure.getDimension().getEndX() + xPlus)))
                      // Z between? 
                      .and((qStructure.dimension().startZ.subtract(qStructure.reserved().r_zMinus).between(structure.getDimension().getStartZ() - zMinus, structure.getDimension().getEndZ() + zPlus)
                      .or(qStructure.dimension().endZ.add(qStructure.reserved().r_zPlus).between(structure.getDimension().getEndZ() + zPlus, structure.getDimension().getStartZ() - zMinus)))
                      .or(qStructure.dimension().startZ.subtract(qStructure.reserved().r_zMinus).between(structure.getDimension().getEndZ() + zPlus, structure.getDimension().getStartZ() - zMinus)
                      .or(qStructure.dimension().endZ.add(qStructure.reserved().r_zPlus).between(structure.getDimension().getStartZ() - zMinus, structure.getDimension().getEndZ() + zPlus))))
                      )
      ).exists();
      session.close();
      return overlaps;
    }



}
