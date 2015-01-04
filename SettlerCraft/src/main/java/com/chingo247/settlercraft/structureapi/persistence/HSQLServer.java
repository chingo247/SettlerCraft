
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
package com.chingo247.settlercraft.structureapi.persistence;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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