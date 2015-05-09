///*
// * The MIT License
// *
// * Copyright 2015 Chingo.
// *
// * Permission is hereby granted, free of charge, to any person obtaining a copy
// * of this software and associated documentation files (the "Software"), to deal
// * in the Software without restriction, including without limitation the rights
// * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the Software is
// * furnished to do so, subject to the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in
// * all copies or substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// * THE SOFTWARE.
// */
//package com.chingo247.structureapi.structure;
//
//import com.chingo247.settlercraft.core.Direction;
//import com.chingo247.settlercraft.core.persistence.dao.settler.ISettlerDAO;
//import com.chingo247.settlercraft.core.persistence.dao.settler.SettlerDAO;
//import com.chingo247.settlercraft.core.persistence.dao.world.IWorldDAO;
//import com.chingo247.settlercraft.core.persistence.dao.world.WorldNode;
//import com.chingo247.structureapi.exception.StructureException;
//import com.chingo247.structureapi.persistence.dao.IStructureDAO;
//import com.chingo247.structureapi.persistence.entities.structure.StructureNode;
//import com.chingo247.structureapi.persistence.entities.structure.StructureWorldNode;
//import com.chingo247.structureapi.structure.plan.SubStructuresPlan;
//import com.chingo247.structureapi.structure.plan.placement.Placement;
//import com.chingo247.structureapi.util.PlacementUtil;
//import com.sk89q.worldedit.Vector;
//import com.sk89q.worldedit.entity.Player;
//import com.sk89q.worldedit.regions.CuboidRegion;
//import com.sk89q.worldedit.world.World;
//import java.io.File;
//import java.io.IOException;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import org.neo4j.graphdb.GraphDatabaseService;
//import org.neo4j.graphdb.Transaction;
//
///**
// *
// * @author Chingo
// */
//class SubstructureHandler extends AbstractStructureHandler<SubStructuresPlan> {
//
//    private final ISettlerDAO settlerDAO;
//    private final IStructureDAO structureDAO;
//
//    public SubstructureHandler(GraphDatabaseService graph, IWorldDAO worldDAO, IStructureDAO structureDAO, SettlerDAO settlerDAO, StructureAPI structureAPI) {
//        super(graph, worldDAO, structureAPI);
//        this.structureDAO = structureDAO;
//        this.settlerDAO = settlerDAO;
//    }
//
//    @Override
//    public Structure handleStructure(SubStructuresPlan structurePlan, World world, Vector vector, Direction direction, Player owner) throws StructureException {
//        Structure mainStructure = null;
//        WorldNode worldNode = registerWorld(world);
//        try (Transaction tx = getGraph().beginTx()) {
//            mainStructure = placeRecursively(tx, new StructureWorldNode(worldNode), structurePlan, world, vector, direction, owner, 1);
//            tx.success();
//        }
//
//        return mainStructure;
//    }
//
//    private Structure placeRecursively(Transaction tx, StructureWorldNode worldNode, SubStructuresPlan plan, World world, Vector position, Direction direction, Player owner, int indexId) throws StructureException {
//        Structure structure = null;
//        Placement mainPlacement = plan.getPlacement();
//
//        Vector min = position.add(mainPlacement.getPosition());
//        Vector max = PlacementUtil.getPoint2Right(min, direction, mainPlacement.getCuboidRegion().getMaximumPoint());
//        CuboidRegion structureRegion = new CuboidRegion(min, max);
//
////        // Check overlap? here?? external only!
////        com.chingo247.settlercraft.core.World scWorld = DefaultWorldFactory.instance().makeWorld(worldNode);
////        if (structureDAO.hasStructuresWithin(scWorld, structureRegion)) {
////            tx.failure();
////            throw new StructureException("Structure overlaps another structure...");
////        }
//        StructureNode structureNode = structureDAO.addStructure(plan.getName(), structureRegion, direction, plan.getPrice());
//        worldNode.addStructure(structureNode);
//
//        try {
//            moveResources(worldNode, structureNode, plan);
//        } catch (IOException ex) {
//            // Rollback
//            File structureDir = getDirectoryForStructure(worldNode, structureNode);
//            structureDir.delete();
//            tx.failure();
//            Logger.getLogger(SubstructureHandler.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        structure = DefaultStructureFactory.instance().makeStructure(structureNode);
//        return structure;
//    }
//
//}
