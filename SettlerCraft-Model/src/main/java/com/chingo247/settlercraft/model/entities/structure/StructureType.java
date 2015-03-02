
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
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.chingo247.settlercraft.model.entities.structure;

/**
 *
 * @author Chingo
 */
public enum StructureType {
     /**
     * A Structure which has a schematic attached
     */
    SCHEMATIC,
    /**
     * A Structure Lot has no schematic attached. StructureLot are used to reserve an area for one or more structures. 
     */
    STRUCTURELOT,
    /**
     * Structure has no schematic and is completely generated based on info within the StructurePlan
     */
    GENERATED,
    
    OTHER;
    
   
//    public static StructureType getType(Placement placement) {
//        if(placement instanceof SchematicPlacement) {
//            return SCHEMATIC;
//        } else if (placement instanceof StructureLot) {
//            return STRUCTURELOT;
//        } else if (placement instanceof GeneratedPlacement){
//            return GENERATED;
//        } else {
//            return OTHER;
//        }
//    }
    
    
}
