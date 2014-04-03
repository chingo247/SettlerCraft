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
    
    


}
