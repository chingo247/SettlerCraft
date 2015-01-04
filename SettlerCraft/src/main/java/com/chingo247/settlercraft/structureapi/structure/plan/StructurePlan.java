
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
package com.chingo247.settlercraft.structureapi.structure.plan;

import com.chingo247.settlercraft.structureapi.exception.PlanException;
import com.chingo247.settlercraft.structureapi.structure.regions.CuboidDimension;
import com.sk89q.worldedit.Vector;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import org.dom4j.Document;

/**
 *
 * @author Chingo
 */
public class StructurePlan {
    
    private StructurePlan parent;
    private File structurePlan;
    private Document document;
    
    private List<StructurePlan> plans;  // Substructure - plan
    private List<Placeable> placeables; // Substructure - placeable
    
    private Placeable placeable;
    private long xxHashPlan;

    StructurePlan(StructurePlan parent, File structurePlan, Placeable placeable) {
        this.parent = parent;
        this.structurePlan = structurePlan;
    }
    
    /**
     * Recursive function that checks if this StructurePlan is Top-Level, where TopLevel means the StructurePlan has no parent or the StructurePlan's
     * parent is TopLevel but the StructurePlan is Outside his parent. 
     * @return True if StructurePlan is Top-Level
     */
    public boolean isTopLevel() {
        if(!hasParent()) {
            return true;
        // StructurePlan is referenced in a parent, but outside
        } else if(!CuboidDimension.isDimensionWithin(parent.placeable, placeable)) {
            // If the parent isTopLevel then this child is also toplevel, because it was outside it's parent
            return parent.isTopLevel();
        } else {
            return false;
        }
    }
    
    public boolean hasParent() {
        return parent != null;
    }
    
    void addPlaceable(Placeable placeable) {
        this.placeables.add(placeable);
    }
    
    
    
    void addStructurePlan(StructurePlan plan) {
        if(matchesAnyAncestors(plan)) throw new PlanException("Plans may not be equal to any of its ancestors. Otherwise a StackOverflow will occur");
        this.plans.add(plan);
    }
    
    File getFile() {
        return structurePlan;
    }
    
    /**
     * Will check if the corresponding plan matches any Ancestors (recursively)
     * @param plan The StructurePlan to check
     * @return True if it matches any ancestors
     */
    private boolean matchesAnyAncestors(StructurePlan plan) {
        String path = plan.getFile().getAbsolutePath();
        if(path.equals(getFile().getAbsolutePath())) {
            return true;
        } else if(parent != null) {
            return parent.matchesAnyAncestors(plan);
        } else {
            return false;
        }
    }

    public Placeable getPlaceable() {
        return placeable;
    }

    public StructurePlan getParent() {
        return parent;
    }

    public List<Placeable> getSubStructurePlaceables() {
        return placeables;
    }

    public List<StructurePlan> getSubStructurePlans() {
        return plans;
    }

    
   
    
    
}
