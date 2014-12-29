/*
 * Copyright (C) 2014 Chingo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.chingo247.settlercraft.structureapi.structure.plan.substructure;

import com.chingo247.settlercraft.structureapi.exception.PlanException;
import com.chingo247.settlercraft.structureapi.plan.schematic.Schematic;
import com.chingo247.settlercraft.structureapi.structure.plan.SettlerCraftPlan;
import com.chingo247.settlercraft.structureapi.structure.plan.StructureDocument;
import com.chingo247.settlercraft.structureapi.structure.plan.document.Loader;
import com.google.common.base.Preconditions;
import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 *
 * @author Chingo
 */
public class SubstructureLoader extends Loader<SubStructureListType>{
    
    private static final String ROOT_ELEMENT = "SubStructures";
    private static final String SUB_ELEMENT = "SubStructure";
    
    private File structurePlan;
    /**
     * 
     * @param structurePlan 
     */
    public SubstructureLoader(File structurePlan) {
        Preconditions.checkNotNull(structurePlan);
        Preconditions.checkArgument(structurePlan.exists());
        this.structurePlan = structurePlan;
    }

    @Override
    public SubStructureType load(Element e) {
        if(!e.getName().equals(ROOT_ELEMENT)) throw new PlanException("Expected element '"+ ROOT_ELEMENT +"', but got '"+e.getName()+"'");
        
        Iterator<Element> it = e.elementIterator();
        SubStructureType sslt;
        Set<String> holder = new HashSet<>(); // Avoids stackoverflow by noting which plans are used so there is no infinite substructuring
        holder.add(structurePlan.getName());
        while(it.hasNext()) {
            Element subElement = it.next();
            
            
            
            SubStructureType subStructure = load(subElement, structurePlan, new HashSet<>(holder)); // use copy because it only matters for substructure on the same branch
            
            
            
        }
    }
    
    private Schematic getSchematic(Element e) throws DocumentException {
       Element element = (Element) e.selectSingleNode("File");
       if(element == null) return null;
       
       File f = new File(structurePlan.getParentFile(), element.getTextTrim());
       if(!f.exists()) throw new PlanException("Couldn't resolve substructure file referenced to '" + f.getAbsolutePath()+"'");
       
        StructureDocument d = new StructureDocument(f);
        SettlerCraftPlan plan = new SettlerCraftPlan(d);
       
        
        return null;
       
    }
    
    private boolean hasDimension(Element e) {
        return e.selectSingleNode("Dimension") != null;
    }
    
    private SubStructureType load(Element subElement, File structurePlan, Set<String> holder) {
         if(!subElement.getName().equals(SUB_ELEMENT)) throw new PlanException("Expected element '"+ SUB_ELEMENT +", but got '"+subElement.getName()+"'");
        
         
    }
    
}
