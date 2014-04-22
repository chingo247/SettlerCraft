/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.core.persistence;

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
public class StructureProgressService extends AbstractService {

    public final int resourceTransaction(final MaterialResource resource, final int maxAmount) {
        Session session = null;
        Transaction tx = null;
        int removed = 0;
        try {
            session = HibernateUtil.getSession();
            tx = session.beginTransaction();
            
            removed = Math.min(resource.getAmount(), maxAmount);
            resource.setAmount(resource.getAmount() - removed);
            if (resource.getAmount() == 0) {
                StructureProgress progress = resource.getProgress();
                progress.getResources().remove(resource);
                session.merge(progress);
            } else {
                session.merge(resource);
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
        return removed;
    }
    
    /**
     * Sets the current progress to the next layer, but only if there are no more resources left to do
     * @param progress The structure w
     * @param force If true it wont check if the resource requirements are met
     * @return 
     */
    public boolean nextLayer(final StructureProgress progress, boolean force) {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSession();
            tx = session.beginTransaction();
            if(!progress.getResources().isEmpty() && !force) {
                return false;
            } else {
                if(progress.getLayer() < progress.getMaxHeight() - 1) {
                progress.setLayer(progress.getLayer() + 1);
                progress.setResources(progress.getStructure().getPlan().getRequirement().getMaterialRequirement().getLayer(progress.getLayer()).getResources(progress));
                } 
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
