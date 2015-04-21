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

import com.chingo247.settlercraft.core.persistence.dao.world.WorldNode;
import com.chingo247.structureapi.persistence.dao.structure.StructureFactory;
import com.chingo247.structureapi.persistence.dao.structure.StructureNode;
import com.chingo247.settlercraft.core.Direction;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.util.Date;

/**
 *
 * @author Chingo
 */
public class DefaultStructureFactory implements StructureFactory<Structure>{
    
    private static StructureFactory instance;
    
    private DefaultStructureFactory() {}
    
    public static StructureFactory instance(){
        if(instance == null) {
            instance = new DefaultStructureFactory();
        }
        return instance;
    }

    @Override
    public Structure makeStructure(StructureNode structureNode) {
        Long id = structureNode.getId();
        String name = structureNode.getName();
        Direction direction = structureNode.getDirection();
        CuboidRegion dimension  = structureNode.getCuboidRegion();
        ConstructionStatus constructionStatus  = structureNode.getConstructionStatus();
        Date createdAt = structureNode.getCreatedAt();
        Date deletedAt  = structureNode.getDeletedAt();
        Date completedAt = structureNode.getCompletedAt();
        WorldNode world = structureNode.getWorld();
        Double value = structureNode.getValue();
        return new DefaultStructure(id, name, dimension, world.getName(), world.getUUID(), direction, constructionStatus, createdAt, deletedAt, completedAt, value);
    }

}
