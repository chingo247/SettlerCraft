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

import java.io.IOException;
import java.net.Socket;
import org.hsqldb.HsqlException;
import org.hsqldb.Server;

/**
 *
 * @author Chingo
 */
public class HSQLServer {

    private final String HOST = "localhost";
    private final int PORT = 9001;
    private final String DATABASE = "SettlerCraft";
    private final String PATH = "plugins//SettlerCraft//Database//data//SettlerCraftDB";
    private static HSQLServer instance;
    private final Server server;

    private HSQLServer() {
        this.server = new Server();
        server.setSilent(true);
        server.setAddress(HOST);
        server.setPort(PORT);
        server.setDatabaseName(0, DATABASE);
        server.setDatabasePath(0, PATH);
        server.setLogWriter(null);
        
    }

    public static HSQLServer getInstance() {
        if (instance == null) {
            instance = new HSQLServer();
        }
        return instance;
    }

    public boolean isRunning() {
        
        try(Socket socket = new Socket(HOST, PORT)) {
            System.out.println("HSQL Server already running");        
            return true;
        } catch(IOException | HsqlException ex) {
            System.out.println("HSQL Server not running, startng it on port " + PORT);        
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
