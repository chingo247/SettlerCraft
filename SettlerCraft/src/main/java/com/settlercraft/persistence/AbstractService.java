/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.persistence;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.EntityPath;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;


/**
 *
 * @author Chingo
 */
public abstract class AbstractService {

    private EntityManager entityManager;

    private final SessionFactory sessionFactory;

    protected AbstractService() {
        Configuration configuration = getConfiguration().configure();
        StandardServiceRegistryBuilder  serviceRegistryBuilder = new StandardServiceRegistryBuilder ()
                .applySettings(configuration.getProperties());
        
        sessionFactory = configuration.buildSessionFactory(serviceRegistryBuilder.build());
    }

    private Configuration getConfiguration() {
        Configuration config = new Configuration();
        config.configure("hibernate.cfg.xml");
        // Detect all classes that are annotated as @Entity
        ClassPathScanningCandidateComponentProvider scanner
                = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Entity.class));

        // only register classes within "com.fooPackage" package
        for (BeanDefinition bd : scanner.findCandidateComponents("com.settlercraft.model")) {
            String name = bd.getBeanClassName();
            System.out.println(name);
            try {
                config.addAnnotatedClass(Class.forName(name));
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage());
            }
        }

        return config;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    protected JPAQuery from(EntityPath<?>... paths) {
        return new JPAQuery(entityManager).from(paths);
    }

    protected void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

}
