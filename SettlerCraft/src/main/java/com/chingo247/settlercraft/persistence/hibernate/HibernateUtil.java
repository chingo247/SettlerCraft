
/*
 * The MIT License
 *
 * Copyright 2015 Chingo.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.chingo247.settlercraft.persistence.hibernate;

import com.chingo247.settlercraft.persistence.legacy.PlayerMembership;
import com.chingo247.settlercraft.persistence.legacy.PlayerOwnership;
import com.chingo247.settlercraft.persistence.legacy.Structure;
import com.chingo247.settlercraft.persistence.entities.SchematicEntity;
import com.chingo247.settlercraft.persistence.entities.structure.StructureEntity;
import com.chingo247.settlercraft.persistence.entities.structure.StructurePlayerEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.cfg.AnnotationConfiguration;

/**
 * Hibernate Utility class with a method to get SessionFactory.
 *
 * @author Chingo
 */
public class HibernateUtil {

    private static final SessionFactory sessionFactory;
    
    
    static {
        try {
            AnnotationConfiguration configuration = new AnnotationConfiguration();
            configuration.addAnnotatedClass(Structure.class);
            configuration.addAnnotatedClass(PlayerOwnership.class);
            configuration.addAnnotatedClass(PlayerMembership.class);
            configuration.addAnnotatedClass(StructurePlayerEntity.class);
            configuration.addAnnotatedClass(SchematicEntity.class);
            configuration.addAnnotatedClass(StructureEntity.class);
            configuration = (AnnotationConfiguration) configuration.configure("com/chingo247/settlercraft/resources/hibernate.cfg.xml");
            sessionFactory = configuration.buildSessionFactory();
        } catch (Throwable ex) {
            // Log the exception. 
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    public static Session getSession() {
        return sessionFactory.openSession();
    }
    
    public static StatelessSession getStatelessSession() {
        return sessionFactory.openStatelessSession();
    }

    
    public static void shutdown() {
        sessionFactory.close();
    }
}
