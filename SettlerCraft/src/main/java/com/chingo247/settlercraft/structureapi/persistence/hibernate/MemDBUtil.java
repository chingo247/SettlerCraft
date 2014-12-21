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
package com.chingo247.settlercraft.structureapi.persistence.hibernate;

import com.chingo247.settlercraft.structureapi.plan.schematic.SchematicData;
import com.chingo247.settlercraft.structureapi.structure.PlayerMembership;
import com.chingo247.settlercraft.structureapi.structure.PlayerOwnership;
import com.chingo247.settlercraft.structureapi.structure.Structure;
import com.chingo247.settlercraft.structureapi.structure.complex.Plot;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.cfg.AnnotationConfiguration;

/**
 *
 * @author Chingo
 */
public class MemDBUtil {

    private static final SessionFactory sessionFactory;

    static {
        try {
            AnnotationConfiguration configuration = new AnnotationConfiguration();
            configuration.addAnnotatedClass(Plot.class);
            configuration = (AnnotationConfiguration) configuration.configure("com/chingo247/settlercraft/resources/memdb.cfg.xml");
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
