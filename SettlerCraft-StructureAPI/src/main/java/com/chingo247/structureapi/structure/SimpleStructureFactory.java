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

import com.chingo247.settlercraft.core.persistence.repository.world.WorldNode;
import com.chingo247.settlercraft.core.regions.CuboidDimension;
import com.chingo247.structureapi.persistence.repository.StructureNode;
import com.chingo247.structureapi.world.Direction;
import java.util.Date;

/**
 *
 * @author Chingo
 */
public class SimpleStructureFactory extends AbstractStructureFactory<SimpleStructure>{
    
    private static SimpleStructureFactory instance;
    
    private SimpleStructureFactory() {}
    
    public static SimpleStructureFactory instance(){
        if(instance == null) {
            instance = new SimpleStructureFactory();
        }
        return instance;
    }

    @Override
    public SimpleStructure makeStructure(StructureNode node) {
        Long id = node.getId();
        String name = node.getName();
        Direction direction = node.getDirection();
        CuboidDimension dimension  = node.getDimension();
        ConstructionStatus constructionStatus  = node.getConstructionStatus();
        State state = node.getState();
        Date createdAt = node.getCreatedAt();
        Date deletedAt  = node.getDeletedAt();
        WorldNode worldNode = node.getWorld();
        return new SimpleStructure(id, name, dimension, worldNode.getName(), worldNode.getUUID(), direction, constructionStatus, state, createdAt, deletedAt);
    }

}
