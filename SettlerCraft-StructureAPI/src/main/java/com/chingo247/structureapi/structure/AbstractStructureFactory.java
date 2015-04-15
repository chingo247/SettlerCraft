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
package com.chingo247.structureapi.structure;

import com.chingo247.structureapi.persistence.repository.IStructure;
import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.structureapi.persistence.repository.StructureNode;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author Chingo
 * @param <T>
 */
public abstract class AbstractStructureFactory<T extends IStructure> {
    
    public T makeWithTransaction(StructureNode node) {
        T structure;
        try (Transaction tx = SettlerCraft.getInstance().getNeo4j().beginTx()) {
            structure = makeStructure(node);
            tx.success();
        }
        return structure;
    }
    
    /**
     * Makes a Structure from the node. A transaction needs to be provided by the caller.
     * if it's desired to only make a structure of one node. Use {@link AbstractStructureFactory#makeWithTransaction(com.chingo247.structureapi.persistence.repository.StructureNode) instead}
     * @param node The structureNode
     * @return The Structure
     */
    public abstract T makeStructure(StructureNode node) ;
    
}
