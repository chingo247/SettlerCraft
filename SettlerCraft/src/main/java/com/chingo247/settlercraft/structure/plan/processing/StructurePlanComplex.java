
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
package com.chingo247.settlercraft.structure.plan.processing;

import com.chingo247.settlercraft.structure.placement.Placement;
import com.chingo247.settlercraft.exception.PlanException;
import com.chingo247.settlercraft.structure.plan.StructurePlan;
import com.chingo247.settlercraft.persistence.entities.world.CuboidDimension;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.dom4j.Document;

/**
 *
 * @author Chingo
 */
public final class StructurePlanComplex implements StructurePlan {
    
    private double price;
    private String name;
    private String category;
    private String description;
    
    protected transient UUID uuid;
    
    protected StructurePlanComplex parent;
    private final File structurePlan;
    private Document document;
    
    private final Set<StructurePlanComplex> internalPlans;  // Substructure - plan
    private final Set<StructurePlanComplex> externalPlans;
    private final Set<Placement> external; // Substructure - placeable
    private final Set<Placement> internal;
    
    protected Placement placement;

    StructurePlanComplex(File structurePlan, StructurePlanComplex parent, Placement placement) {
        this.uuid = UUID.randomUUID();
        this.structurePlan = structurePlan;
        this.parent = parent;
        this.internalPlans = new HashSet<>();
        this.externalPlans = new HashSet<>();
        this.internal = new HashSet<>();
        this.external = new HashSet<>();
        this.placement = placement;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
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
    @Override
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

    @Override
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public void setDescription(String description) {
       this.description = description; 
    }

    @Override
    public String getDescription() {
        return description;
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
    
    public boolean removePlacement(Placement placement) {
        if(!internal.remove(placement)) {
            return external.remove(placement);
        }
        return true;
    }
    
    public void addPlacement(Placement placement) {
        if(CuboidDimension.isDimensionWithin(this.placement, placement)) {
            internal.add(placement);
        } else {
            external.add(placement);
        }
    }
    
    public void addStructurePlan(StructurePlanComplex plan) {
        if(matchesAnyAncestors(plan.getFile().getAbsolutePath())) throw new PlanException("Plans may not be equal to any of its ancestors.");
        if(CuboidDimension.isDimensionWithin(placement, plan.placement)) {
            internalPlans.add(plan);
        } else {
            externalPlans.add(plan);
        }
        
    }
    
    public void removeStructurePlan(StructurePlanComplex plan) {
        
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

    @Override
    public Placement getPlacement() {
        return placement;
    }

    public StructurePlanComplex getParent() {
        return parent;
    }

    public Collection<Placement> getExternal() {
        return external;
    }

    public Collection<Placement> getInternal() {
        return internal;
    }
    

    @Override
    public List<Placement> getSubPlacements() {
        List<Placement> placements = new ArrayList<>(external);
        placements.addAll(internal);
        return placements;
    }

    @Override
    public List<StructurePlan> getSubStructurePlans() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getRelativePath() {
        String path = getFile().getAbsolutePath();
        String base = ".//";
        String relative = new File(base).toURI().relativize(new File(path).toURI()).getPath();
        return relative;
    }

    

  

    

    
   
    
    
}
