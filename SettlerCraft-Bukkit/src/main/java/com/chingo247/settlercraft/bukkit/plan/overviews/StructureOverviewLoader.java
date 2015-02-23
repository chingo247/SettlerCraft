package com.chingo247.settlercraft.bukkit.plan.overviews;

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
//package com.chingo247.settlercraft.bukkit.plan.overviews;
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
//public class StructureOverviewLoader extends ALoader<StructureOverview>{
//
//    public StructureOverviewLoader() {
//        super(Nodes.STRUCTURE_OVERVIEWS_NODE);
//    }
//
//    
//    @Override
//    public List<StructureOverview> load(Element overviewsElement) throws StructureDataException  {
//        if (overviewsElement == null) {
//            throw new AssertionError("Overviews element was null");
//        }
//
//        if (!overviewsElement.getName().equals(Elements.STRUCTURE_OVERVIEWS)) {
//            throw new AssertionError("Expected '" + Elements.STRUCTURE_OVERVIEWS + "' element, but got '" + overviewsElement.getName() + "'");
//        }
//        
//        new StructureOverviewValidator().validate(overviewsElement);
//
//        List<StructureOverview> overviews = new ArrayList<>();
//        List<Node> overviewNodes = overviewsElement.selectNodes(Elements.STRUCTURE_OVERVIEW);
//
//        for (Node overviewNode : overviewNodes) {
//            Node xNode = overviewNode.selectSingleNode(Elements.X);
//            Node yNode = overviewNode.selectSingleNode(Elements.Y);
//            Node zNode = overviewNode.selectSingleNode(Elements.Z);
//
//            int x = Integer.parseInt(xNode.getText());
//            int y = Integer.parseInt(yNode.getText());
//            int z = Integer.parseInt(zNode.getText());
//            overviews.add(new StructureOverview(x, y, z));
//        }
//
//        return overviews;
//    }
//
//    
//}
