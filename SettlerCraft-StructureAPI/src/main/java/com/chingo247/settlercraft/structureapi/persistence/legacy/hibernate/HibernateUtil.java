package com.chingo247.settlercraft.structureapi.persistence.legacy.hibernate;


/*
 * Copyright (C) 2015 Chingo
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


import com.chingo247.settlercraft.structureapi.persistence.legacy.PlayerOwnership;
import com.chingo247.settlercraft.structureapi.persistence.legacy.PlayerMembership;
import com.chingo247.settlercraft.structureapi.persistence.legacy.PlayerMembershipId;
import com.chingo247.settlercraft.structureapi.persistence.legacy.PlayerOwnershipId;
import com.chingo247.settlercraft.structureapi.persistence.legacy.Structure;
import com.chingo247.settlercraft.structureapi.persistence.legacy.StructureLog;
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
            configuration.setProperty("hibernate.bytecode.use_reflection_optimizer", "false");
            configuration.setProperty("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");
            configuration.setProperty("hibernate.connection.url", "jdbc:hsqldb:hsql://localhost:9005/SettlerCraft");
            configuration.setProperty("hibernate.cache.provider_class", "org.hibernate.cache.EhCacheProvider");
            configuration.setProperty("hibernate.cache.use_query_cache", "true");
            configuration.setProperty("hibernate.connection.username", "SA");
            configuration.setProperty("hibernate.connection.password", "");
            configuration.setProperty("hibernate.connection.pool_size", "20");
            configuration.setProperty("hibernate.jdbc.batch_size", "5000");
            configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
            configuration.setProperty("hibernate.show_sql", "false");
            configuration.setProperty("hibernate.hbm2ddl.auto", "update");
//            configuration = (AnnotationConfiguration) configuration.configure();
            configuration.addAnnotatedClass(Structure.class);
            configuration.addAnnotatedClass(PlayerMembership.class);
            configuration.addAnnotatedClass(PlayerMembershipId.class);
            configuration.addAnnotatedClass(PlayerOwnership.class);
            configuration.addAnnotatedClass(PlayerOwnershipId.class);
            configuration.addAnnotatedClass(StructureLog.class);
            
            
            
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

    
    public static void close() {
        sessionFactory.close();
    }
}
