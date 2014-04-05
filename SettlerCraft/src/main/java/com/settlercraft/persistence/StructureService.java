/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.persistence;

import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.settlercraft.model.entity.structure.QStructure;
import com.settlercraft.model.entity.structure.Structure;
import com.settlercraft.util.HibernateUtil;
import java.util.List;
import org.hibernate.Session;

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
    
    public boolean overlaps(Structure structure) {
      QStructure qStructure = QStructure.structure;
      Session session = HibernateUtil.getSession();
      JPQLQuery query = new HibernateQuery(session);
      
      boolean overlaps = query.from(qStructure)
              .where(qStructure.worldLocation().world.eq(structure.getStructureLocation().getWorld().getName())
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
