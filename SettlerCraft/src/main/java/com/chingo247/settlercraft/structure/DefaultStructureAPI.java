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
package com.chingo247.settlercraft.structure;

import com.chingo247.settlercraft.SettlerCraftContext;
import com.chingo247.settlercraft.structure.exception.StructureException;
import com.chingo247.settlercraft.structure.persistence.entities.StructureEntity;
import com.chingo247.settlercraft.structure.persistence.entities.StructureState;
import com.chingo247.settlercraft.structure.persistence.entities.StructureType;
import com.chingo247.settlercraft.structure.persistence.service.StructureDAO;
import com.chingo247.settlercraft.structure.plan.StructurePlan;
import com.chingo247.settlercraft.structure.plan.StructureAPIPlanManager;
import com.chingo247.settlercraft.structure.plan.placement.Placement;
import com.chingo247.settlercraft.structure.plan.processing.StructurePlanComplex;
import com.chingo247.settlercraft.structure.regions.CuboidDimensional;
import com.chingo247.settlercraft.structure.restriction.StructureHeightRestriction;
import com.chingo247.settlercraft.structure.restriction.StructureOverlapRestriction;
import com.chingo247.settlercraft.structure.restriction.StructureRestriction;
import com.chingo247.settlercraft.structure.restriction.StructureWorldRestriction;
import com.chingo247.settlercraft.structure.selection.ISelectionManager;
import com.chingo247.settlercraft.world.Direction;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.world.World;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Chingo
 */
public class DefaultStructureAPI implements StructureAPI {

    private StructureDAO structureDAO;
    private Set<StructureRestriction> restrictions;
    private StructureStorage structureStorage;
    private ExecutorService cachedPool;
    private SettlerCraftContext context;
    private static DefaultStructureAPI instance;
    private boolean isLoading = false;

    private DefaultStructureAPI() {
        this.structureStorage = new StructureStorage();
        this.context = SettlerCraftContext.getContext();
        this.cachedPool = Executors.newCachedThreadPool();
        this.structureDAO = new StructureDAO();
        this.restrictions = new HashSet<>();
        this.restrictions.add(new StructureWorldRestriction());
        this.restrictions.add(new StructureHeightRestriction());
        this.restrictions.add(new StructureOverlapRestriction());
    }

    public static DefaultStructureAPI getInstance() {
        if (instance == null) {
            instance = new DefaultStructureAPI();
        }
        return instance;
    }

    public boolean isLoading() {
        return isLoading;
    }
    
    

    public synchronized void reload() {
        if (!isLoading) {
            isLoading = true;
            try {
                StructureAPIPlanManager.getInstance().loadPlans();
            } finally {
                isLoading = false;
            }
        }
    }

    private void applyRestrictions(World world, CuboidDimensional dimensional) throws StructureException {
        for (StructureRestriction restriction : restrictions) {
            restriction.allow(world, dimensional, StructureType.SCHEMATIC);
        }
    }

    @Override
    public Structure getStructure(long id) {
        StructureComplex complex = structureStorage.find(id);
        if (complex == null) {
            StructureEntity entity = structureDAO.find(id);
            if (entity == null) {
                return null;
            }
            complex = new StructureComplex(cachedPool, entity, this);
            structureStorage.store(complex);
        }
        return complex;
    }

    @Override
    public List<Structure> getSubstructures(long id) {
        return structureStorage.getSubStructures(id);
    }

    StructureStorage getStorage() {
        return structureStorage;
    }

    private void createRecursive(StructurePlan plan, World world, Vector position, Direction d, StructureComplexTree holder) {
        for (Placement p : plan.getSubPlacements()) {
            StructureEntity pEntity = new StructureEntity(world, p.getCuboidDimension(), StructureType.getType(p));
            StructureComplex pComplex = new StructureComplex(cachedPool, pEntity, this);
            holder.add(new StructureComplexTree(pComplex));
        }
        for (StructurePlan p : plan.getSubStructurePlans()) {
            StructureEntity pEntity = new StructureEntity(world, p.getPlacement().getCuboidDimension(), StructureType.getType(p.getPlacement()));
            StructureComplex pComplex = new StructureComplex(cachedPool, pEntity, this);
            StructureComplexTree pComplexTree = new StructureComplexTree(pComplex);
            createRecursive(p, world, position, d, pComplexTree);
            holder.add(pComplexTree);
        }
    }

    @Override
    public Structure create(StructurePlan plan, World world, Vector position, Direction direction) {
        StructureEntity entity = new StructureEntity(world, plan.getPlacement().getCuboidDimension(), StructureType.getType(plan.getPlacement()));
        StructureComplex complex = new StructureComplex(cachedPool, entity, this);
        StructureComplexTree tree = new StructureComplexTree(complex);

        createRecursive(plan, world, position, direction, tree);

        //TODO Apply restrictions...
        List<StructureEntity> entities = tree.listEntities();

        // Generate ids for each structureEntity
        entities = structureDAO.bulkUpsert(entities);

        //TODO Create folders! //{TYPE}//{ID}
        // Set relations
        setSubStructureRelations(tree);

        // Set state to created
        for (StructureEntity se : entities) {
            se.setState(StructureState.CREATED);
        }
        structureDAO.bulkUpsert(entities);

        return complex;
    }

    private void setSubStructureRelations(StructureComplexTree tree) {
        List<StructureEntity> trees = new ArrayList<>();
        setSubStructureRelationsRecursive(tree, trees);
    }

    private void setSubStructureRelationsRecursive(StructureComplexTree tree, List<StructureEntity> stes) {
        StructureEntity parent = tree.getSelf().getEntity();
        for (StructureComplexTree subTree : tree.getTrees()) {
            subTree.getSelf().getEntity().setParent(parent.getId());
            setSubStructureRelationsRecursive(subTree, stes);
        }
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
        return StructureAPIPlanManager.getInstance().getPlan(id);
    }

    @Override
    public List<StructurePlan> getStructurePlans() {
        return StructureAPIPlanManager.getInstance().getPlans();
    }

    protected class StructureComplexTree {

        private final StructureComplex self;
        private List<StructureComplexTree> tree;

        public StructureComplexTree(StructureComplex self) {
            this.self = self;
            this.tree = new ArrayList<>();
        }

        public StructureComplex getSelf() {
            return self;
        }

        public List<StructureComplexTree> getTrees() {
            return tree;
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
         *
         * @param holder
         */
        private void list(List<StructureComplex> holder) {
            holder.add(self);
            for (StructureComplexTree sct : tree) {
                sct.list(holder);
            }
        }

        public List<StructureEntity> listEntities() {
            List<StructureEntity> holder = new ArrayList<>();
            listEntities(holder);
            return holder;
        }

        private void listEntities(List<StructureEntity> holder) {
            holder.add(self.getEntity());
            for (StructureComplexTree sct : tree) {
                sct.listEntities(holder);
            }
        }

    }

}
