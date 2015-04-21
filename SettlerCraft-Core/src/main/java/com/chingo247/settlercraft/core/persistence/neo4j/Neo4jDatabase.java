/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
        this.graph = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder(new File(directory, databaseName).getAbsolutePath())
                .setConfig(GraphDatabaseSettings.execution_guard_enabled, "true")
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
