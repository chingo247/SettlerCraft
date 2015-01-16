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
//import com.chingo247.settlercraft.structureapi.structure.plan.AValidator;
//import com.chingo247.settlercraft.util.document.Elements;
//import com.chingo247.settlercraft.util.document.Nodes;
//import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
//import com.sk89q.worldguard.protection.flags.DefaultFlag;
//import com.sk89q.worldguard.protection.flags.Flag;
//import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
//import java.util.Iterator;
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
//public class StructureRegionFlagValidator extends AValidator {
//
//    public StructureRegionFlagValidator() {
//        super(Nodes.WORLDGUARD_FLAGS_NODE);
//    }
//
//    @Override
//    public void validate(Element en) throws StructureDataException {
//        List<Node> nodes = en.selectNodes(Elements.REGIONFLAG);
//        Iterator<Node> it = nodes.iterator();
//
//        while (it.hasNext()) {
//            Node e = it.next();
//
//            if (e.selectSingleNode("Name") == null) {
//                throw new StructureDataException("Missing 'name' node");
//            }
//            if (e.selectSingleNode("Value") == null) {
//                throw new StructureDataException("Missing 'value' node");
//            }
//
//            Flag f = DefaultFlag.fuzzyMatchFlag(e.selectSingleNode("Name").getText());
//            if (f == null) {
//                throw new StructureDataException("Flag '" + e.selectSingleNode("Name").getText() + "' not recognized");
//            }
//
//            try {
//                Object v = f.parseInput(WorldGuardPlugin.inst(), Bukkit.getConsoleSender(), e.selectSingleNode("Value").getText());
//
//            } catch (InvalidFlagFormat ex) {
//                Logger.getLogger(StructurePlan.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//    }
//
//}
