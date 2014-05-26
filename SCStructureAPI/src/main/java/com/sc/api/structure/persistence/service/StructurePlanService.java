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
package com.sc.api.structure.persistence.service;

import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.hibernate.HibernateDeleteClause;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.sc.api.structure.entity.plan.QStructurePlan;
import com.sc.api.structure.entity.plan.StructurePlan;
import com.sc.api.structure.persistence.MemDBUtil;
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
            session.merge(plan);
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
            for (StructurePlan plan : plans) {
                session.merge(plan);
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

    public void clear() {
        System.out.println("Clearing plans");
        Session session = MemDBUtil.getSession();
        QStructurePlan qplan = QStructurePlan.structurePlan;
        new HibernateDeleteClause(session, qplan).execute();
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
