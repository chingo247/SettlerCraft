/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.core.persistence;

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
public class MaterialResourceService extends AbstractService<MaterialResource> {
    
    /**
     * Substracts an amount of the given material resource, also performs an extra check if the amount that 
     * was given isnt bigger than the amount that was needed. Therefore this method returns the actual amount that
     * was removed from this layer.
     * @param resource The resource
     * @param amount The amount to substract from the resource
     * @return The actual amount that was removed
     */
    public int removeValue(MaterialResource resource, int amount) {
        Session session = null;
        Transaction tx = null;
        int removed = 0;
        try {
            session = HibernateUtil.getSession();
            tx = session.beginTransaction();
            removed = Math.min(resource.getValue(), amount); // secure check here
            resource.removeAmount(amount);
            if(resource.getValue() == 0) {
                delete(resource);
            } else {
                merge(resource);
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
    
  
    
}
