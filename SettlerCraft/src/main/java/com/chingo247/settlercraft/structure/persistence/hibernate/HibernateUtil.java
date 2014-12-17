/*
 * Copyright (C) 2014 Chingo247
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

package com.chingo247.settlercraft.structure.persistence.hibernate;

import com.chingo247.settlercraft.structure.PlayerMembership;
import com.chingo247.settlercraft.structure.PlayerOwnership;
import com.chingo247.settlercraft.structure.Plot;
import com.chingo247.settlercraft.structure.Structure;
import com.chingo247.settlercraft.structure.plan.schematic.SchematicData;
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
            configuration.addAnnotatedClass(SchematicData.class);
            configuration.addAnnotatedClass(Plot.class);
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
