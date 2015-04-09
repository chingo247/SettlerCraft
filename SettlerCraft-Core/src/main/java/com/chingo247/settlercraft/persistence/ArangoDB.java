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
package com.chingo247.settlercraft.persistence;

import com.arangodb.ArangoConfigure;
import com.arangodb.ArangoDriver;
import com.arangodb.ArangoException;
import com.arangodb.ErrorNums;
import com.arangodb.entity.CollectionEntity;
import com.arangodb.entity.CollectionKeyOption;
import com.arangodb.entity.CollectionOptions;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A Helper class for ArangoDB
 * @author Chingo
 */
public class ArangoDB {
    
    private final ArangoDriver driver;

    public ArangoDB(int port, String host, String password, String user, String database) throws ArangoException {
       
        
        ArangoConfigure configure = new ArangoConfigure();
        configure.setHost(host);
        configure.setPort(port);
        configure.setPassword(password);
        configure.setUser(user);
        configure.init();
        this.driver = new ArangoDriver(configure);
        createDatabaseIfNotExist(database);
        setDefaultDatabase(database);
    }

    public ArangoDriver getDriver() {
        return driver;
    }
    
    /**
     * Checks if there already is a database with the same name
     * @param database The database
     * @return True if database exists
     */
    public boolean hasDatabase(String database) {
        try {
            List<String> databases = driver.getDatabases().getResult();
            return databases.contains(database);
        } catch (ArangoException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public final boolean dropDatabase(String database) throws ArangoException {
        if(hasDatabase(database)) {
            driver.deleteDatabase(database);
            return true;
        }
        return false;
    }
    
    /**
     * Creates a database
     * @param database The database to create
     * @throws ArangoException 
     */
    public final void createDatabase(String database) throws ArangoException {
        driver.createDatabase(database);
    }
    
    /**
     * Creates a database but only if the database doesn't already exist
     * @param database The name of the database
     * @throws ArangoException 
     */
    public final void createDatabaseIfNotExist(String database) throws ArangoException {
        if(!hasDatabase(database)) {
            createDatabase(database);
        }
    }
    
    /**
     * Creates a collection with traditional incrementing IDs (1, 2, 3, etc)
     * Note that IDs are still Strings as that's the way ArangoDB manages IDs
     * @param collection The collection to create
     * @throws ArangoException 
     */
    public final void createNumericKeyCollection(String collection) throws ArangoException {
        CollectionOptions options = new CollectionOptions();
        options.setKeyOptions(CollectionKeyOption.createIncrementOption(false, 1, 1));
        driver.createCollection(collection, options);
    }
    
    /**
     * Deletes a collection if the collection exists
     * @param collection The collection to delete
     * @return True if the collection was successfully deleted
     * @throws ArangoException 
     */
    public boolean deleteCollection(String collection) throws ArangoException {
        if(hasCollection(collection)) {
            driver.deleteCollection(collection);
            return true;
        }
        return false;
    }
    
    /**
     * Checks if the database has the collection
     * @param collection The name of the collection
     * @return  True if database has the collection
     */
    public final boolean hasCollection(String collection) {
        CollectionEntity entity = null;
        try {
            entity = driver.getCollection(collection);
        } catch (ArangoException ex) {
            if(ex.getErrorNumber() != ErrorNums.ERROR_ARANGO_COLLECTION_NOT_FOUND) {
                Logger.getLogger(ArangoDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return entity != null;
    }
    
    /**
     * Sets the default database for this client
     * @param database The database to set
     */
    public final void setDefaultDatabase(String database) {
        driver.setDefaultDatabase(database);
    }
    
    /**
     * Returns a GraphHelper for this client
     * @return A new GrapHelper instance
     */
    public ArangoGraphHelper getGraphHelper() {
        return new ArangoGraphHelper(driver);
    }
    
    
    
    
    
}
