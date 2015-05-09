package com.chingo247.settlercraft.structureapi.platforms.bukkit.plan.worldguard;

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
//import com.chingo247.settlercraft.structureapi.plan.document.IStructurePlanElement;
//import com.chingo247.settlercraft.util.document.Elements;
//import com.sk89q.worldguard.protection.flags.Flag;
//import org.dom4j.Element;
//import org.dom4j.tree.BaseElement;
//
///**
// *
// * @author Chingo
// * @param <T>
// */
//public class StructureRegionFlag <T> implements IStructurePlanElement {
//    
//    private final Flag flag;
//    private final T value;
//
//    public StructureRegionFlag(Flag flag, T value) {
//        this.flag = flag;
//        this.value = value;
//    }
//
//    public Flag getFlag() {
//        return flag;
//    }
//
//    public T getValue() {
//        return value;
//    }
//
//    @Override
//    public Element asElement() {
//        Element element = new BaseElement(Elements.REGIONFLAG);
//        
//        Element flagName = new BaseElement(Elements.NAME);
//        flagName.setText(flag.getName());
//        
//        Element flagValue = new BaseElement(Elements.VALUE);
//        flagValue.setText(String.valueOf(value));
//        
//        element.add(flagName);
//        element.add(flagValue);
//        
//        return element;
//    }
//    
//}
