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
package com.sc.api.structure.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.hsqldb.Server;

/**
 *
 * @author Chingo
 */
public class HSQLServer {

    private final String HOST = "localhost";
    private final int PORT = 9002;
    private final String DATABASE = "StructureAPI";
    private final String PATH = "plugins//SettlerCraft//SCStructureAPI//Database//data//scstructuredb";
    private static HSQLServer instance;
    private final Server server;

    private HSQLServer() {
        this.server = new Server();
        server.setSilent(true);
        server.setAddress(HOST);
        server.setPort(PORT);
        server.setDatabaseName(0, DATABASE);
        server.setDatabasePath(0, PATH);
//        server.setLogWriter(null);
    }

    public static HSQLServer getInstance() {
        if (instance == null) {
            instance = new HSQLServer();
        }
        return instance;
    }

    public boolean isRunning() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost:9002/StructureAPI", "SA", "");
            return true;
        } catch (SQLException ex) {
            System.out.println("NOT RUNNING!");
            return false;
        }
    }

    public void start() {
        server.start();
    }

    public void stop() {
        System.out.println("Stopping HSQL server");
        server.stop();
    }

}
