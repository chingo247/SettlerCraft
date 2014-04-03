/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.persistence;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author Chingo
 */
public class SettlerCraftService extends AbstractService {

    /**
     * Saves an @Entity annotated class into the MySQL Database with the same identifier
     * The entity would be persisted if the entity was non-existent
     * @param <T> 
     * @param t The persistent object to save
     * @return The persistent object
     */
    public <T extends Object> T save(T t) {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSession();
            tx = session.beginTransaction();

            session.merge(t);
            session.flush();
            tx.commit();
        } catch (HibernateException e) {
            try {
                tx.rollback();
            } catch (RuntimeException rbe) {
                Logger.getLogger(SettlerCraftService.class.getName()).log(Level.SEVERE,"Couldn’t roll back transaction", rbe);
            }
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return t;
    }
}
