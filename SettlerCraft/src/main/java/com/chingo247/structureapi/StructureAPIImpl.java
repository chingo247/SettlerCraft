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
 *s
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.chingo247.structureapi;

import com.chingo247.structureapi.restriction.StructureRestriction;
import com.chingo247.settlercraft.world.Direction;
import com.chingo247.structureapi.entities.StructureEntity;
import com.chingo247.structureapi.entities.StructureType;
import com.chingo247.structureapi.exception.StructureException;
import com.chingo247.structureapi.persistence.StructureDAO;
import com.chingo247.structureapi.plan.StructurePlan;
import com.chingo247.structureapi.placement.Placement;
import com.chingo247.structureapi.plan.StructurePlanManager;
import com.chingo247.structureapi.regions.CuboidDimensional;
import com.chingo247.structureapi.restriction.StructureHeightRestriction;
import com.chingo247.structureapi.restriction.StructureOverlapRestriction;
import com.chingo247.structureapi.restriction.StructureWorldRestriction;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.world.World;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Chingo
 */
public class StructureAPIImpl implements StructureAPI {
    
    private StructureDAO structureDAO;
    private List<StructureRestriction> restrictions;
    private StructureStorage structureStorage;
    private ExecutorService cachedPool;

    public StructureAPIImpl() {
        this.structureStorage = new StructureStorage();
        this.cachedPool = Executors.newCachedThreadPool();
        this.structureDAO = new StructureDAO();
        this.restrictions = new ArrayList<>();
        this.restrictions.add(new StructureHeightRestriction());
        this.restrictions.add(new StructureWorldRestriction());
        this.restrictions.add(new StructureOverlapRestriction());
    }
    
    private void applyRestrictions(World world, CuboidDimensional dimensional) throws StructureException {
        for(StructureRestriction restriction : restrictions) {
            restriction.allow(world, dimensional, StructureType.SCHEMATIC);
        }
    }

    @Override
    public Structure getStructure(long id) {
        StructureComplex complex = structureStorage.find(id);
        if(complex == null) {
            StructureEntity entity = structureDAO.find(id);
            if(entity == null) {
                return null;
            } 
            complex = new StructureComplex(cachedPool, entity, this) {};
            structureStorage.store(complex);
        }
        return complex;
    }
    
    StructureStorage getStorage() {
        return structureStorage;
    }
    
     private void createRecursive(StructurePlan plan, World world, Vector position, Direction d, StructureComplexTree holder) {
        for(Placement p : plan.getSubStructurePlacements()) {
            StructureEntity pEntity = new StructureEntity(world, p.getCuboidDimension(), StructureType.getType(p));
            StructureComplex pComplex = new StructureComplex(cachedPool, pEntity, this, StructurePlanManager.createPlan(p));
            holder.add(new StructureComplexTree(pComplex));
        }
        for(StructurePlan p : plan.getSubStructurePlans()) {
            StructureEntity pEntity = new StructureEntity(world, p.getPlacement().getCuboidDimension(), StructureType.getType(p.getPlacement()));
            StructureComplex pComplex = new StructureComplex(cachedPool, pEntity, this, plan);
            StructureComplexTree pComplexTree = new StructureComplexTree(pComplex);
            createRecursive(p, world, position, d, pComplexTree);
            holder.add(pComplexTree);
        }
    }

    @Override
    public Structure create(StructurePlan plan, World world, Vector position, Direction direction) {
        // Apply restrictions...
        
        StructureEntity entity = new StructureEntity(world, plan.getPlacement().getCuboidDimension(), StructureType.getType(plan.getPlacement()));
        StructureComplex complex = new StructureComplex(cachedPool, entity, this, plan);
        StructureComplexTree tree = new StructureComplexTree(complex);
        
        
        return null;
    }
    
    @Override
    public Structure create(Placement placement, World world, Vector position, Direction direction) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public boolean overlaps(CuboidDimensional cuboid) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addRestriction(StructureRestriction restriction) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public StructurePlan getStructurePlan(String id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    private class StructureComplexTree {
    
    private final StructureComplex self;
    private List<StructureComplexTree> tree;

    public StructureComplexTree(StructureComplex self) {
        this.self = self;
        this.tree = new ArrayList<>();
    }
    
    public void add(StructureComplexTree complex) {
        this.tree.add(complex);
    }
    
    public List<StructureComplex> list() {
        List<StructureComplex> structures = new ArrayList<>();
        list(structures);
        return structures;
    }
    
    /**
     * Recursive call
     * @param holder 
     */
    private void list(List<StructureComplex> holder) {
        holder.add(self);
        for(StructureComplexTree sct : tree) {
            sct.list(holder);
        }
    }
    
    
    
}
   
    
    
}
