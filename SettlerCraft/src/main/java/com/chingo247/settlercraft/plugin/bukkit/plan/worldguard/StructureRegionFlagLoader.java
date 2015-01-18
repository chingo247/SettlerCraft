package com.chingo247.settlercraft.plugin.bukkit.plan.worldguard;

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
//package com.chingo247.settlercraft.bukkit.plan.worldguard;
//
//import com.chingo247.settlercraft.structureapi.exception.StructureDataException;
//import com.chingo247.settlercraft.structureapi.plan.StructurePlan;
//import com.chingo247.settlercraft.structureapi.structure.plan.ALoader;
//import com.chingo247.settlercraft.util.document.Elements;
//import com.chingo247.settlercraft.util.document.Nodes;
//import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
//import com.sk89q.worldguard.protection.flags.DefaultFlag;
//import com.sk89q.worldguard.protection.flags.Flag;
//import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import org.bukkit.Bukkit;
//import org.dom4j.Element;
//import org.dom4j.Node;
//
///**
// *
// * @author Chingo
// */
//public class StructureRegionFlagLoader extends ALoader<StructureRegionFlag> {
//
//    public StructureRegionFlagLoader() {
//        super(Nodes.WORLDGUARD_FLAGS_NODE);
//    }
//
//    @Override
//    public List<StructureRegionFlag> load(Element regionFlagsElement) throws StructureDataException {
//        if(!regionFlagsElement.getName().equals(Elements.REGIONFLAGS)) {
//            throw new AssertionError("Expected element '"+Elements.REGIONFLAGS+"', but got '"+regionFlagsElement.getName()+"'");
//        }
//        
//        new StructureRegionFlagValidator().validate(regionFlagsElement);
//        List<Element> elements = regionFlagsElement.selectNodes(Elements.REGIONFLAG);
//        
//        if(elements.isEmpty()) return new ArrayList<>(0); // return an empty list
//        
//        List<StructureRegionFlag> flags = new ArrayList<>(elements.size());
//        
//        
//        Iterator<Node> it = regionFlagsElement.selectNodes(Elements.REGIONFLAG).iterator();
//        while(it.hasNext()) {
//            Node n = it.next();
//            
//            
//            Flag f = DefaultFlag.fuzzyMatchFlag(n.selectSingleNode(Elements.NAME).getText());
//            if (f == null) {
//                throw new StructureDataException("Flag '" + n.selectSingleNode(Elements.NAME).getText() + "' not recognized");
//            }
//
//            try {
//                Object v = f.parseInput(WorldGuardPlugin.inst(), Bukkit.getConsoleSender(), n.selectSingleNode(Elements.VALUE).getText());
//                StructureRegionFlag regionFlag = new StructureRegionFlag(f, v);
//                flags.add(regionFlag);
//
//            } catch (InvalidFlagFormat ex) {
//                Logger.getLogger(StructurePlan.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//        return flags;
//    }
//    
//}
