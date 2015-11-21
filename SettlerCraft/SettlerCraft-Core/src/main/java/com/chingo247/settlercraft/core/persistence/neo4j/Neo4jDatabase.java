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
package com.chingo247.settlercraft.core.persistence.neo4j;

import com.google.common.base.Preconditions;
import java.io.File;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;

/**
 *
 * @author Chingo
 */
public class Neo4jDatabase {
    
    private GraphDatabaseService graph;
    

    public Neo4jDatabase(File directory, String databaseName, int pageCacheMemory) {
        Preconditions.checkArgument(pageCacheMemory >= 512, "Min pageCache is 512");
        Preconditions.checkNotNull(directory);
        Preconditions.checkNotNull(databaseName);
        this.graph = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder(new File(directory, databaseName))
                .setConfig(GraphDatabaseSettings.keep_logical_logs, "300M size")
                .setConfig(GraphDatabaseSettings.execution_guard_enabled, "true")
                .setConfig(GraphDatabaseSettings.allow_store_upgrade, "false")
                .setConfig(GraphDatabaseSettings.pagecache_memory, pageCacheMemory +"m")
                .setConfig(GraphDatabaseSettings.log_queries, "true")
                .newGraphDatabase();
        
        
       
        
        
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

            @Override
            public void run() {
                graph.shutdown();
            }
        }));
        
        
        
        
        
    }
    

    public GraphDatabaseService getGraph() {
        return graph;
    }

    public void shutdown() {
        graph.shutdown();
    }
    
    

    
    
}
