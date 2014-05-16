/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.api.structure.persistence.util;

import java.io.File;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

/**
 * A database that fully exists in the system memmory. All data will be lost when the server shutsdown.
 * @author Chingo
 */
public class MemDBUtil {
    private static SessionFactory factory;
    private static final AnnotationConfiguration config = new AnnotationConfiguration();
    
    static {
        File file = new File("plugins/SCStructureAPI/DataBase/memdb.cfg.xml");
        factory = config.configure(file).buildSessionFactory();
    }


    public static Session getSession() {
        return factory.openSession();
    }
    
    

    public static void addAnnotatedClass(Class clazz) {
        config.addAnnotatedClass(clazz);
        factory = config.buildSessionFactory();
    }
    
    public static void addAnnotatedClasses(Class... clazzes) {
        for(Class clazz : clazzes) {
            config.addAnnotatedClass(clazz);
        }
        factory = config.configure().buildSessionFactory();
    }

    public static void shutdown() {
        factory.close();
    }
}
