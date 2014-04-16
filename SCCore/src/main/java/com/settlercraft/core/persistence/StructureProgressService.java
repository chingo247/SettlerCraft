/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.core.persistence;

import com.settlercraft.core.model.entity.structure.Structure;
import com.settlercraft.core.model.entity.structure.StructureProgress;
import com.settlercraft.core.model.plan.requirement.material.MaterialResource;
import com.settlercraft.core.util.HibernateUtil;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author Chingo
 */
public class StructureProgressService extends AbstractService<StructureProgress> {

    public final int resourceTransaction(final StructureProgress progress, final MaterialResource resource, final int maxAmount) {
        Session session = null;
        Transaction tx = null;
        int removed = 0;
        try {
            session = HibernateUtil.getSession();
            tx = session.beginTransaction();
            removed = Math.min(resource.getAmount(), maxAmount);
            resource.setAmount(resource.getAmount() - removed);
            if (resource.getAmount() == 0) {
                progress.getResources().remove(resource);
            }
            session.merge(progress);
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
        return removed;
    }
    
    public boolean nextLayer(final Structure structure) {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSession();
            tx = session.beginTransaction();
            StructureProgress progress = structure.getProgress();
            if(!progress.getResources().isEmpty() || progress.getLayer() == progress.getMaxHeight() - 1) {
                return false;
            } else {
                progress.setLayer(progress.getLayer() + 1);
                progress.getResources().addAll(structure.getPlan().getRequirement().getMaterialRequirement().getLayer(progress.getLayer()).getResources());
                session.merge(progress);
                tx.commit();
                return true;
            }
            
           
            
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

}
