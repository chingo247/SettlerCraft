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
package com.sc.module.databasetests.hsqldb;

import com.chingo247.settlercraft.plugin.SettlerCraft;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.hsqldb.HsqlException;
import org.hsqldb.server.Server;

/**
 *
 * @author Chingo
 */
/**
 *
 * @author Chingo
 */
public class HSQLServer {

    private static final String HOST = "localhost";
    private static final int PORT = 9001;
    private static final String MAIN_DATABASE = "SettlerCraft";
//    private static final String BLOCKS_DATABASE = "SettlerCraftBlocks";
    private final String PATH = "plugins//SettlerCraft//Database//";
    private final String MAIN_PATH = PATH + MAIN_DATABASE;
//    private final String BLOCKS_PATH = PATH + "Blocks//" + BLOCKS_DATABASE;
    private static HSQLServer instance;
    private final Server server;

    private HSQLServer() {
        this.server = new Server();
        server.setSilent(true);
        server.setAddress(HOST);
        server.setPort(PORT);
        
        // Main Database
        server.setDatabaseName(0, MAIN_DATABASE);
        server.setDatabasePath(0, MAIN_PATH);
//        // Blocks Database
//        server.setDatabaseName(1, BLOCKS_DATABASE);
//        server.setDatabasePath(1, BLOCKS_PATH);
    
        server.setLogWriter(null);
        server.setErrWriter(null);
    }

    public static HSQLServer getInstance() {
        if (instance == null) {
            instance = new HSQLServer();
        }
        return instance;
    }

    public boolean isRunning() {
        Socket socket = null;
        try {
            socket = new Socket(HOST, PORT);
            Bukkit.getConsoleSender().sendMessage(SettlerCraft.MSG_PREFIX + "HSQL Server already running");        
            return true;
        } catch(IOException | HsqlException ex) {
            Bukkit.getConsoleSender().sendMessage(SettlerCraft.MSG_PREFIX + "HSQL Server not running, startng it on port " + PORT);        
            return false;
        } finally {
            if(socket != null) {
                try {
                    socket.close();
                } catch (IOException ex) {
                    Logger.getLogger(HSQLServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void start() {
        server.start();
        
    }

    public void stop() {
        Bukkit.getConsoleSender().sendMessage(SettlerCraft.MSG_PREFIX + "Stopping HSQL server");
        server.stop();
    }

}