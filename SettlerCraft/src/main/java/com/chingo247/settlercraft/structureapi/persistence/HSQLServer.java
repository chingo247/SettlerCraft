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
package com.chingo247.settlercraft.structureapi.persistence;

import com.chingo247.settlercraft.bukkit.SettlerCraftPlugin;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.hsqldb.HsqlException;
import org.hsqldb.server.Server;

/**
 *
 * @author Chingo
 */

public class HSQLServer {

    private final String HOST = "localhost";
    private final int PORT;
    private final String MAIN_DATABASE = "SettlerCraft";
    private final String PATH = "plugins//SettlerCraft//Database//";
    private final String MAIN_PATH = PATH + MAIN_DATABASE;
    private static HSQLServer instance;
    private final Server server;
    

    private HSQLServer() {
//        File configFile = new File(SettlerCraftPlugin.getInstance().getDataFolder(), "config.yml");
//        final FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        PORT = 9001;

        server = new Server();
        server.setSilent(true);
        server.setAddress(HOST);
        server.setPort(PORT);
        
        server.setDatabaseName(0, MAIN_DATABASE);
        server.setDatabasePath(0, MAIN_PATH);
    
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
            print("HSQL Server already running");        
            return true;
        } catch(IOException | HsqlException ex) {
            print("HSQL Server not running, startng it on port " + PORT);        
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
        print("Stopping HSQL server");
        server.stop();
    }
    
    private void print(String messsage) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[SettlerCraft]: " + ChatColor.RESET + messsage);
    }
}