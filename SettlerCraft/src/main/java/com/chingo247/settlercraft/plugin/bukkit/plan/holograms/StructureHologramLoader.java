package com.chingo247.settlercraft.plugin.bukkit.plan.holograms;

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
//package com.chingo247.settlercraft.bukkit.plan.holograms;
//
//import com.chingo247.settlercraft.structureapi.exception.StructureDataException;
//import com.chingo247.settlercraft.structureapi.structure.plan.ALoader;
//import com.chingo247.settlercraft.util.document.Elements;
//import com.chingo247.settlercraft.util.document.Nodes;
//import java.util.ArrayList;
//import java.util.List;
//import org.dom4j.Element;
//import org.dom4j.Node;
//
///**
// *
// * @author Chingo
// */
//public class StructureHologramLoader extends ALoader<StructureHologram> {
//
//    public StructureHologramLoader() {
//        super(Nodes.HOLOGRAMS_NODE);
//    }
//    
//    @Override
//    public List<StructureHologram> load(Element hologramsElement) throws StructureDataException {
//        if (hologramsElement == null) {
//            throw new AssertionError("Overviews element was null");
//        }
//
//        if (!hologramsElement.getName().equals(Elements.STRUCTURE_HOLOGRAMS)) {
//            throw new AssertionError("Expected '" + Elements.STRUCTURE_HOLOGRAMS + "' element, but got '" + hologramsElement.getName() + "'");
//        }
//
//
//        List<StructureHologram> holograms = new ArrayList<>();
//        List<Node> hologramNodes = hologramsElement.selectNodes(Elements.STRUCTURE_HOLOGRAM);
//        int count = 0;
//
//        for (Node hologramNode : hologramNodes) {
//            
//            Node xNode = hologramNode.selectSingleNode(Elements.X);
//            Node yNode = hologramNode.selectSingleNode(Elements.Y);
//            Node zNode = hologramNode.selectSingleNode(Elements.Z);
//            if (xNode == null) {
//                throw new StructureDataException("Missing 'X' node for 'StructureOverview#" + count + "'");
//            }
//            if (yNode == null) {
//                throw new StructureDataException("Missing 'Y' node for 'StructureOverview#" + count + "'");
//            }
//            if (zNode == null) {
//                throw new StructureDataException("Missing 'Z' node for 'StructureOverview#" + count + "'");
//            }
//
//            try {
//                int x = Integer.parseInt(xNode.getText());
//                int y = Integer.parseInt(yNode.getText());
//                int z = Integer.parseInt(zNode.getText());
//                
//                List<Node> lines = hologramNode.selectNodes("Lines/Line");
//                String[] tArray = new String[lines.size()];
//                for(int i = 0; i <  tArray.length; i++) {
//                    tArray[i] = (lines.get(i)).getText();
//                }
//                holograms.add(new StructureHologram(x, y, z, tArray));
//                
//            } catch (NumberFormatException nfe) {
//                throw new StructureDataException("Values for (x,y,z) are not of type number in 'StructureOverview#" + count + "'");
//            }
//            count++;
//
//        }
//
//        return holograms;
//    }
//
//
//
//
//
//}
