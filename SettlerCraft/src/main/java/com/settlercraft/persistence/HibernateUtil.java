/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.persistence;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Entity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

/**
 *
 * @author Chingo
 */
public class HibernateUtil {
    
    private static SessionFactory factory;
    
    public static Session getSession() {
        if(factory == null) {
            Configuration config = HibernateUtil.getInitializedConfiguration();
            factory = config.buildSessionFactory();
        }
        return factory.openSession();
    }
    
    public static Configuration getInitializedConfiguration() {
        AnnotationConfiguration config = new AnnotationConfiguration();
        for(Class clazz : getAnnotatedClasses()) {
            config.addAnnotatedClass(clazz);
        }
        config.configure();
        return config;
    }
    
    private static List<Class> getAnnotatedClasses() {
        List<Class> clazzes = new LinkedList();
        ClassPathScanningCandidateComponentProvider scanner
                = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Entity.class));

        // only register classes within "com.fooPackage" package
        for (BeanDefinition bd : scanner.findCandidateComponents("com.settlercraft.model")) {
            String name = bd.getBeanClassName();
            System.out.println(name);
            try {
                clazzes.add(Class.forName(name));
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, ex.getMessage());
            }
        }
        return clazzes;
    }
    
}
