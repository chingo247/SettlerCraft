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

package com.chingo247.structureapi.main.persistence.hibernate;

import com.chingo247.structureapi.main.PlayerMembership;
import com.chingo247.structureapi.main.PlayerOwnership;
import com.chingo247.structureapi.main.SchematicData;
import com.chingo247.structureapi.main.Structure;
import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.cfg.AnnotationConfiguration;

/**
 * Hibernate Utility class with a convenient method to get Session Factory object.
 *
 * @author Chingo
 */
public class HibernateUtil {

    private static final SessionFactory sessionFactory;
    
    
    static {
        File configFile = new File("plugins/SettlerCraft/config.yml");
        final FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        
        String password = String.valueOf(config.get("hsql.password"));
        String username = String.valueOf(config.get("hsql.username"));
        String connectionURL = "jdbc:hsqldb:hsql://localhost";
        String port = String.valueOf(config.get("hsql.port"));
        String database = "SettlerCraft";
        String opts = "hsqldb.write_delay=false;hsqldb.nio_max_size=512";
        
        String connection = connectionURL + ":" + port + "/" + database;
        
        try {
            // Create the SessionFactory from standard (hibernate.cfg.xml) 
            // config file.
            AnnotationConfiguration configuration = new AnnotationConfiguration();
            configuration = (AnnotationConfiguration) configuration.configure("com/chingo247/structureapi/resources/hibernate.cfg.xml");
            configuration.addAnnotatedClass(Structure.class);
            configuration.addAnnotatedClass(PlayerOwnership.class);
            configuration.addAnnotatedClass(PlayerMembership.class);
            configuration.addAnnotatedClass(SchematicData.class);
            sessionFactory = configuration.buildSessionFactory();
        } catch (Throwable ex) {
            // Log the exception. 
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    public static Session getSession() {
        return getSessionFactory().openSession();
    }
    
    public static StatelessSession getStatelessSession() {
        return getSessionFactory().openStatelessSession();
    }
    
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
    public static void shutdown() {
        sessionFactory.close();
    }
}
