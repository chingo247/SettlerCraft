package com.chingo247.structureapi.platforms.bukkit.plan.overviews;

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
//package com.chingo247.settlercraft.bukkit.plan.overviews;
//
//import com.chingo247.settlercraft.structureapi.plan.document.IStructurePlanElement;
//import com.chingo247.settlercraft.util.document.Elements;
//import org.dom4j.Element;
//import org.dom4j.tree.BaseElement;
//
///**
// *
// * @author Chingo
// */
//public class StructureOverview implements IStructurePlanElement{
//    
//    private int x;
//    private int y;
//    private int z;
//
//    public StructureOverview(int x, int y, int z) {
//        this.x = x;
//        this.y = y;
//        this.z = z;
//    }
//
//    public int getX() {
//        return x;
//    }
//
//    public int getY() {
//        return y;
//    }
//
//    public int getZ() {
//        return z;
//    }
//
//    @Override
//    public Element asElement() {
//        Element root = new BaseElement(Elements.STRUCTURE_OVERVIEW);
//        // Set X
//        Element xElement = new BaseElement(Elements.X);
//        xElement.setText(String.valueOf(x));
//        // Set Y
//        Element yElement = new BaseElement(Elements.Y);
//        yElement.setText(String.valueOf(y));
//        // Set Z
//        Element zElement = new BaseElement(Elements.Z);
//        zElement.setText(String.valueOf(z));
//        // Add nodes
//        root.add(xElement);
//        root.add(yElement);
//        root.add(zElement);
//        return root;
//    }
//
// 
//    
//    
//}
