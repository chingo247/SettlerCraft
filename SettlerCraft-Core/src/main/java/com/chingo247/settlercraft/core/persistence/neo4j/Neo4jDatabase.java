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

/**
 *
 * @author Chingo
 */
public class Neo4jDatabase {
    
    private GraphDatabaseService graph;

    public Neo4jDatabase(File directory, String databaseName) {
        Preconditions.checkNotNull(directory);
        Preconditions.checkNotNull(databaseName);
        
        this.graph = new GraphDatabaseFactory().newEmbeddedDatabase(new File(directory, databaseName).getAbsolutePath());
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
