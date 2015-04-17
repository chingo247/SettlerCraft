
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
package com.chingo247.settlercraft.core.persistence.hsql;


import java.io.File;
import java.io.IOException;
import java.net.Socket;
import org.apache.log4j.Logger;

import org.hsqldb.HsqlException;
import org.hsqldb.Server;

/**
 *
 * @author Chingo
 */

public class HSQLServer {

    private static final Logger LOG = Logger.getLogger(HSQLServer.class);
    
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
                    LOG.error(ex.getMessage());
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