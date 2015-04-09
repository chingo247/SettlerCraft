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

import com.arangodb.ArangoDriver;
import com.arangodb.ArangoException;
import com.arangodb.entity.EdgeDefinitionEntity;
import com.arangodb.entity.GraphEntity;
import java.util.List;

/**
 *
 * @author Chingo
 */
public class ArangoGraphHelper {

    private final ArangoDriver driver;
    
    ArangoGraphHelper(ArangoDriver driver) {
        this.driver = driver;
    }
    
    public boolean hasGraph(String graph) {
        try {
            return driver.getGraphList().contains(graph);
        } catch (ArangoException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public boolean deleteGraph(String graph) throws ArangoException {
        if(hasGraph(graph)) {
            driver.deleteGraph(graph, true);
            return true;
        }
        return false;
    }
    
    public GraphEntity createGraph(String graph) throws ArangoException {
        return driver.createGraph(graph, Boolean.TRUE);
    }
    
    public GraphEntity createGraphIfNotExist(String graph) throws ArangoException {
        if(!hasGraph(graph)) {
            return createGraph(graph);
        } 
        return null;
    }
    
    public boolean hasEdgeDefinition(String graph, String edgeCollection) throws ArangoException {
        if(hasGraph(graph)) {
            GraphEntity graphEntity = driver.getGraph(graph);
            return graphEntity.getEdgeDefinitionsEntity().getEdgeDefinition(edgeCollection) != null;
        }
        return false;
    }
    
    public void modifyAllEdgeDefinitions(String graph, List<EdgeDefinitionEntity> entities) throws ArangoException {
        GraphEntity graphEntity = driver.getGraph(graph);
        graphEntity.getEdgeDefinitionsEntity().setEdgeDefinitions(entities);
    }
    
    public void modifyEdgeDefinition(String graph, EdgeDefinitionEntity ede) throws ArangoException {
        if(hasEdgeDefinition(graph, ede.getCollection())) {
            driver.graphReplaceEdgeDefinition(graph, ede.getCollection(), ede);
        } else {
            driver.graphCreateEdgeDefinition(graph, ede);
        }
    } 
    
    
}
