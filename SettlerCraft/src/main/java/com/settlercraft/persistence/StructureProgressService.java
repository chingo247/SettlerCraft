/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.persistence;

import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.settlercraft.model.entity.structure.QStructureProgress;
import com.settlercraft.model.entity.structure.StructureProgress;
import com.settlercraft.util.HibernateUtil;
import org.hibernate.Session;

/**
 *
 * @author Chingo
 */
public class StructureProgressService extends AbstractService<StructureProgress> {

    public StructureProgress getProgress(Long id) {
        QStructureProgress qsp = QStructureProgress.structureProgress;
        Session session = HibernateUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);
        StructureProgress progress = query.from(qsp).where(qsp.id.eq(id)).uniqueResult(qsp);
        session.close();
        return progress;
    }

}
