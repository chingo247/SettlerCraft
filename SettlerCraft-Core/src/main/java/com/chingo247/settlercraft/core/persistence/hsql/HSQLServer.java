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
package com.chingo247.settlercraft.core.persistence.hsql;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hsqldb.HsqlException;
import org.hsqldb.Server;

/**
 *
 * @author Chingo
 * @deprecated This class may be removed in future releases
 */
@Deprecated
public class HSQLServer {

    private static final Logger LOG = Logger.getLogger(HSQLServer.class.getName());
    
    private final Server server;
    

    public HSQLServer(int port, String host, File directory, String database) {
        server = new Server();
        server.setSilent(true);
        server.setAddress(host);
        server.setPort(port);
        
        server.setDatabaseName(0, database);
        server.setDatabasePath(0, directory.getAbsolutePath());
    
        server.setLogWriter(null);
        server.setErrWriter(null);
    }

    

    public static boolean isRunning(String host, int port) {
        Socket socket = null;
        try {
            socket = new Socket(host, port);
            LOG.info("HSQL Server already running");        
            return true;
        } catch(IOException | HsqlException ex) {
            LOG.info("HSQL Server not running, startng it on port " + port);        
            return false;
        } finally {
            if(socket != null) {
                try {
                    socket.close();
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, ex.getMessage(),ex);
                }
            }
        }
    }

    public void start() {
        server.start();
    }

    public void stop() {
        LOG.info("Stopping HSQL server");
        server.stop();
    }
    
   
}