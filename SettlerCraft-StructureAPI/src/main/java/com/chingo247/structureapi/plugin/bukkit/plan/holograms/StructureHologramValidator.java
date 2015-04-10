package com.chingo247.structureapi.plugin.bukkit.plan.holograms;

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
//
//package com.chingo247.settlercraft.bukkit.plan.holograms;
//
//import com.chingo247.settlercraft.structureapi.exception.StructureDataException;
//import com.chingo247.settlercraft.structureapi.structure.plan.AValidator;
//import com.chingo247.settlercraft.util.document.Elements;
//import com.chingo247.settlercraft.util.document.Nodes;
//import java.util.List;
//import org.dom4j.Element;
//import org.dom4j.Node;
//
///**
// *
// * @author Chingo
// */
//public class StructureHologramValidator extends AValidator {
//
//    public StructureHologramValidator() {
//        super(Nodes.HOLOGRAMS_NODE);
//    }
//
//    @Override
//    public void validate(Element e) throws StructureDataException {
//         List<Node> nodes = e.selectNodes(Nodes.HOLOGRAM_NODE);
//
//        if (nodes != null && !nodes.isEmpty()) {
//            int count = 0;
//            for (Node n : nodes) {
//                Node xNode = n.selectSingleNode(Elements.X);
//                Node yNode = n.selectSingleNode(Elements.Y);
//                Node zNode = n.selectSingleNode(Elements.Z);
//
//                if (xNode == null) {
//                    throw new StructureDataException("Missing 'X' node for 'Hologram#" + count + "'");
//                }
//                
//                if (yNode == null) {
//                    throw new StructureDataException("Missing 'Y' node for 'Hologram#" + count + "'");
//                }
//                
//                if (zNode == null) {
//                    throw new StructureDataException("Missing 'Z' node for 'Hologram#" + count + "'");
//                }
//                try {
//                    Integer.parseInt(xNode.getText());
//                } catch (NumberFormatException nfe) {
//                    throw new StructureDataException("Invalid X value should 'Hologram#" + count + "'");
//                }
//
//                try {
//                    Integer.parseInt(yNode.getText());
//                } catch (NumberFormatException nfe) {
//                    throw new StructureDataException("Invalid Y value for 'Hologram#" + count + "'");
//                }
//
//                try {
//                    Integer.parseInt(zNode.getText());
//                } catch (NumberFormatException nfe) {
//                    throw new StructureDataException("Invalid Z value for 'Hologram#" + count + "'");
//                }
//
//                Node linesNode = n.selectSingleNode("Lines");
//                if (linesNode == null) {
//                    throw new StructureDataException("Missing 'Lines' node for 'Hologram#" + count + "'");
//                }
//
//                List<Node> lineNodes = n.selectNodes("Lines/Line");
//                if (lineNodes == null || lineNodes.isEmpty()) {
//                    throw new StructureDataException("Missing 'Line' nodes for 'Hologram#" + count + "'");
//                }
//
//                count++;
//            }
//        }
//    }
//    
//}
