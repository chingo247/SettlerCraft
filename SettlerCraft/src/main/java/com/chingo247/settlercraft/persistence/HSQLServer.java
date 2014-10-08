/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.persistence;

import com.chingo247.settlercraft.plugin.SettlerCraft;
import java.io.IOException;
import java.net.Socket;
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

    private final String HOST = "localhost";
    private final int PORT = 9001;
    private final String DATABASE = "StructureAPI";
    private final String PATH = "plugins//SettlerCraft//StructureAPI//Database//" + DATABASE;
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
        server.setErrWriter(null);
    }

    public static HSQLServer getInstance() {
        if (instance == null) {
            instance = new HSQLServer();
        }
        return instance;
    }

    public boolean isRunning() {
        
        try(Socket socket = new Socket(HOST, PORT)) {
            Bukkit.getConsoleSender().sendMessage(SettlerCraft.MSG_PREFIX + "HSQL Server already running");        
            return true;
        } catch(IOException | HsqlException ex) {
            Bukkit.getConsoleSender().sendMessage(SettlerCraft.MSG_PREFIX + "HSQL Server not running, startng it on port " + PORT);        
            return false;
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