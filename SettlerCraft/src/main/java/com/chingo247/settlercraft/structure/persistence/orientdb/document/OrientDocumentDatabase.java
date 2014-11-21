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
package com.chingo247.settlercraft.structure.persistence.orientdb.document;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import org.apache.log4j.Logger;

/**
 *
 * @author Chingo
 */
public class OrientDocumentDatabase {
    
    private final Logger LOG = Logger.getLogger(OrientDocumentDatabase.class);
    private final ODatabaseDocumentTx database;
    protected final String name;
    protected final String path;
    private final String username = "admin";
    private final String password = "admin";
    protected final OrientMode mode;
    
    

    public OrientDocumentDatabase(String name, OrientMode mode, String path) {
        LOG.debug("Initializing OrientDB in " + mode + " Mode");
        LOG.debug("Database name: " + name);
        LOG.debug("Database path: " + path + "" + name);
        this.mode = mode;
        this.path = path;
        this.name = name;
        
       
        
        database = new ODatabaseDocumentTx(mode.mode + ":" + path + name);
        
        
        
        if(!database.exists()) {
            database.create();
        }
    }
    
    
    
    public OrientDocumentDatabase open() {
        database.open(username, password);
        return this;
    }
    
    public void close() {
        database.close();
    }
    
    public boolean isClosed() {
        return database.isClosed();
    }
    
    
    
    public OrientCollection getCollection(String collection) {
        return new OrientCollection(new OrientDocumentDatabase(name, mode, path), collection);
    }

    public ODatabaseDocumentTx getDatabase() {
        return database;
    }
    
}
