
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
package com.chingo247.structureapi.plan;

import com.chingo247.structureapi.plan.placement.Placement;
import com.chingo247.structureapi.exception.PlanException;
import com.chingo247.structureapi.regions.CuboidDimension;
import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import org.dom4j.Document;

/**
 *
 * @author Chingo
 */
public final class StructurePlan  {
    
    private double price;
    private String name;
    
    protected transient UUID uuid;
    
    protected StructurePlan parent;
    private final File structurePlan;
    private Document document;
    
    private final Set<StructurePlan> plans;  // Substructure - plan
    private final Set<Placement> placements; // Substructure - placeable
    
    protected Placement placement;

    StructurePlan(File structurePlan, StructurePlan parent, Placement placement) {
        this.uuid = UUID.randomUUID();
        this.structurePlan = structurePlan;
        this.parent = parent;
        this.plans = new HashSet<>();
        this.placements = new HashSet<>();
        this.placement = placement;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public UUID getUuid() {
        return uuid;
    }

    /**
     * Gets the file of this StructurePlan
     * @return The file
     */
    public File getFile() {
        return structurePlan;
    }

    /**
     * Sets the name
     * @param name The name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the price
     * @param price The price to set
     */
    public void setPrice(double price) {
        this.price = price;
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
        } else if(!CuboidDimension.isDimensionWithin(parent.placement, placement)) {
            // If the parent isTopLevel then this child is also toplevel, because it was outside it's parent
            return parent.isTopLevel();
        } else {
            return false;
        }
    }
    
    public boolean hasParent() {
        return parent != null;
    }
    
    public void removePlacement(Placement placement) {
        Iterator<Placement> it = placements.iterator();
        while(it.hasNext()) {
            if(it.next().getId().equals(placement.getId())) {
                it.remove();
                break;
            }
        }
    }
    
    public void addPlacement(Placement placement) {
        placements.add(placement);
    }
    
    public void addStructurePlan(StructurePlan plan) {
        if(matchesAnyAncestors(plan.getFile().getAbsolutePath())) throw new PlanException("Plans may not be equal to any of its ancestors.");
        plans.add(plan);
    }
    
    public void removeStructurePlan(StructurePlan plan) {
        
    }
    
    /**
     * Will check if the corresponding plan matches any Ancestors (recursively)
     * @param plan The StructurePlan to check
     * @return True if it matches any ancestors
     */
    boolean matchesAnyAncestors(String path) {
        
        if(path.equals(getFile().getAbsolutePath())) {
            return true;
        } else if(parent != null) {
            return parent.matchesAnyAncestors(path);
        } else {
            return false;
        }
    }

    public Placement getPlacement() {
        return placement;
    }

    public StructurePlan getParent() {
        return parent;
    }

    public Collection<Placement> getSubStructurePlacements() {
        return placements;
    }

    public Collection<StructurePlan> getSubStructurePlans() {
        return plans;
    }

    

  

    

    
   
    
    
}
