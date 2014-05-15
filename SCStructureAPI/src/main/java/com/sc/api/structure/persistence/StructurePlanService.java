/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.api.structure.persistence;

import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.sc.api.structure.model.structure.plan.QStructurePlan;
import com.sc.api.structure.model.structure.plan.StructurePlan;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author Chingo
 */
public class StructurePlanService {
    
    
    public void save(StructurePlan plan) {
        Session session = null;
        Transaction tx = null;
        try {
            session = MemDBUtil.getSession();
            tx = session.beginTransaction();
                session.persist(plan);
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
    
    public void save(List<StructurePlan> plans) {
        Session session = null;
        Transaction tx = null;
        try {
            session = MemDBUtil.getSession();
            tx = session.beginTransaction();
            for(StructurePlan plan : plans) {
                session.persist(plan);
            }
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
    

    
    public StructurePlan getPlan(String planId) {
        QStructurePlan structurePlan = QStructurePlan.structurePlan;
        Session session = MemDBUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);
        StructurePlan plan = query.from(structurePlan).where(structurePlan.displayName.eq(planId)).uniqueResult(structurePlan);
        session.close();
        return plan;
    }
    
    public List<StructurePlan> getPlans() {
        QStructurePlan structurePlan = QStructurePlan.structurePlan;
        Session session = MemDBUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);
        List<StructurePlan> plan = query.from(structurePlan).list(structurePlan);
        session.close();
        return plan;
    }
    
    public List<StructurePlan> getPlans(List<StructurePlan> newPlans) {
        QStructurePlan structurePlan = QStructurePlan.structurePlan;
        Session session = MemDBUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);
        List<StructurePlan> plan = query.from(structurePlan).where(structurePlan.notIn(newPlans)).list(structurePlan);
        session.close();
        return plan;
    }
    
    
    

}
