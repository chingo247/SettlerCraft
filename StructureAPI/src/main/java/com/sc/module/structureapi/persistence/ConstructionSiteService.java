/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.module.structureapi.persistence;

import com.sc.module.structureapi.event.structure.StructureConstructionEvent;
import com.sc.module.structureapi.structure.ConstructionSite;
import com.sc.module.structureapi.structure.ConstructionSite.State;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author Chingo
 */
public class ConstructionSiteService {

    public ConstructionSite save(ConstructionSite site) {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSession();
            tx = session.beginTransaction();
            site = (ConstructionSite) session.merge(site);
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
        return site;
    }

    public ConstructionSite setState(final ConstructionSite site, State newState) {
        if (site.getState() != newState) {
            Bukkit.getPluginManager().callEvent(new StructureConstructionEvent(site.getStructure()));
            site.setState(newState);
            return save(site);
        }
        return site;
    }
    
}
