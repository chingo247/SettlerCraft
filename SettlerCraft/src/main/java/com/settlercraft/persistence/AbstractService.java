/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.persistence;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.EntityPath;
import javax.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

/**
 *
 * @author Chingo
 */
public abstract class AbstractService {
    
    private EntityManager entityManager;
    
    protected JPAQuery from(EntityPath<?>... paths) {
        return new JPAQuery(entityManager).from(paths);
    }
    
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    public Session getSession() {
        SessionFactory sessionFactory = new AnnotationConfiguration()
                .configure().buildSessionFactory();
        return sessionFactory.openSession();
    }
    
    public void save(Object o) {
        Session session = getSession();
        session.save(o);
        session.close();
    }
    
    
    public void delete(Object o) {
        Session session = getSession();
        session.delete(o);
        session.close();
    }

    
    

}
