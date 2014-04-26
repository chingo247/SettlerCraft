/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.core.util.Database;

import com.google.common.collect.Sets;
import java.util.Set;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;

/**
 * A database that fully exists in the system memmory. All data will be lost when the server shutsdown.
 * However when u have a lot of data that needs to be queried and may differ at every server start, this offers a fair approach to get it done.
 * @author Chingo
 */
public class ShopDBUtil {
    private static SessionFactory factory;
    private static Set<Class> annotatedClasses = Sets.newHashSet();
    private static AnnotationConfiguration config = new AnnotationConfiguration();

    public static Session getSession() {
        if (factory == null) {
            initializeConfiguration(config);
            factory = config.configure("memdb.cfg.xml").buildSessionFactory();
        }
        return factory.openSession();
    }
    
    

    private static Configuration initializeConfiguration(final AnnotationConfiguration configuration) {
        for (Class clazz : annotatedClasses) {
            configuration.addAnnotatedClass(clazz);
        }
        return configuration.configure();
    }

    public static void addAnnotatedClass(Class clazz) {
        annotatedClasses.add(clazz);
        factory = config.buildSessionFactory();
        System.out.println("[SettlerCraft]: registered " + clazz);
    }
    
    public static void addAnnotatedClasses(Class... clazzes) {
        for(Class clazz : clazzes) {
            System.out.println("[SettlerCraft]: registered " + clazz);
            annotatedClasses.add(clazz);
        }
        initializeConfiguration(config);
        factory = config.configure("memdb.cfg.xml").buildSessionFactory();
    }

    public static void shutdown() {
        factory.close();
    }
}
