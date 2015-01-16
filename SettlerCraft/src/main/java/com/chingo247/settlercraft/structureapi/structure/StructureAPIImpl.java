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
//package com.chingo247.settlercraft.structureapi.structure;
//
//import com.chingo247.settlercraft.structureapi.plan.StructurePlan;
//import com.chingo247.settlercraft.structureapi.structure.schematic.Schematic;
//import com.chingo247.settlercraft.structureapi.world.Direction;
//import com.sk89q.worldedit.Vector;
//import com.sk89q.worldedit.world.World;
//import java.io.File;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//
///**
// *
// * @author Chingo
// */
//public class StructureAPIImpl implements StructureAPI {
//    
//    private final Map<Long, Schematic> schematics = Collections.synchronizedMap(new HashMap<Long, Schematic>());
//    
//
//    public StructureAPIImpl() {
//    }
//    
//    
//
//    @Override
//    public Structure create(StructurePlan plan, World world, Vector position, Direction direction) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public void build(UUID uuid, Structure structure) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public void demolish(UUID uuid, Structure structure) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public void stop(Structure structure) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public void addRestriction(StructureRestriction restriction) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public Schematic loadSchematic(File schematic) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public StructurePlan getStructurePlan(String id) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public void loadPlans(File planDirectory) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//    
//}
