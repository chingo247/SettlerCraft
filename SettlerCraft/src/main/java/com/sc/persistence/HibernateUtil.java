/*
 * Copyright (C) 2014 Chingo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.sc.persistence;

import com.sc.construction.async.ConstructionProcess;
import com.sc.construction.structure.Structure;
import com.sc.construction.plan.StructurePlan;
import com.sc.construction.plan.StructureSchematic;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

/**
 * Hibernate Utility class with a convenient method to get Session Factory object.
 *
 * @author Chingo
 */
public class HibernateUtil {

    private static final SessionFactory sessionFactory;
    
    static {
        try {
            // Create the SessionFactory from standard (hibernate.cfg.xml) 
            // config file.
            AnnotationConfiguration configuration = new AnnotationConfiguration();
            configuration.addAnnotatedClass(Structure.class);
            configuration.addAnnotatedClass(StructurePlan.class);
            configuration.addAnnotatedClass(ConstructionProcess.class);
            configuration.addAnnotatedClass(StructureSchematic.class);
            sessionFactory = configuration.configure().buildSessionFactory();
        } catch (Throwable ex) {
            // Log the exception. 
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    public static Session getSession() {
        return getSessionFactory().openSession();
    }
    
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
