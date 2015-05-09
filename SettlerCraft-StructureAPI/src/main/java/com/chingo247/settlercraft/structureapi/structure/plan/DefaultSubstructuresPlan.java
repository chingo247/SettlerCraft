package com.chingo247.settlercraft.structureapi.structure.plan;


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


import com.chingo247.settlercraft.structureapi.structure.plan.exception.PlanException;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.Placement;
import com.chingo247.settlercraft.structureapi.structure.plan.util.RegionUtil;
import com.google.common.collect.Sets;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Chingo
 */
public final class DefaultSubstructuresPlan extends AbstractStructurePlan implements SubStructuresPlan {
    
    private final DefaultSubstructuresPlan parent;
    private final Placement placement;
    private final Set<StructurePlan> internalPlans;  // Substructure - plan
    private final Set<StructurePlan> externalPlans;
    private final Set<Placement> external; // Substructure - placeable
    private final Set<Placement> internal;

    protected DefaultSubstructuresPlan(String id, File structurePlanFile, DefaultSubstructuresPlan parent, Placement placement) {
        super(id, structurePlanFile);
        this.parent = parent;
        this.placement = placement;
        this.internal = Sets.newHashSet();
        this.external = Sets.newHashSet();
        this.internalPlans = Sets.newHashSet();
        this.externalPlans = Sets.newHashSet();
    }

    
    @Override
    public boolean isTopLevel() {
        if(parent == null) {
            return true;
        // StructurePlan is referenced in a parent, but outside
        } else if(!RegionUtil.isDimensionWithin(parent.placement.getCuboidRegion(), placement.getCuboidRegion())) {
            // If the parent isTopLevel then this child is also toplevel, because it was outside it's parent
            return parent.isTopLevel();
        } else {
            return false;
        }
    }
    
    @Override
    public boolean removePlacement(Placement placement) {
        if(!internal.remove(placement)) {
            return external.remove(placement);
        }
        return true;
    }
    
    @Override
    public void addPlacement(Placement placement) {
        if(RegionUtil.isDimensionWithin(this.placement.getCuboidRegion(), placement.getCuboidRegion())) {
            internal.add(placement);
        } else {
            external.add(placement);
        }
    }
    
    @Override
    public void addStructurePlan(StructurePlan plan) {
        if(matchesAnyAncestors(plan.getFile())) throw new PlanException("Plans may not be equal to any of its ancestors.");
        if(RegionUtil.isDimensionWithin(placement.getCuboidRegion(), plan.getPlacement().getCuboidRegion())) {
            internalPlans.add(plan);
        } else {
            externalPlans.add(plan);
        }
        
    }
    
    @Override
    public boolean removeStructurePlan(StructurePlan plan) {
        if(!internalPlans.remove(plan)) {
            return externalPlans.remove(plan);
        }
        return true;
    }

    @Override
    public Placement getPlacement() {
        return placement;
    }

    @Override
    public DefaultSubstructuresPlan getParent() {
        return parent;
    }

    @Override
    public List<Placement> getExternalPlacments() {
        return new ArrayList<>(external);
    }

    @Override
    public List<Placement> getInternalPlacements() {
        return new ArrayList<>(internal);
    }

    @Override
    public List<Placement> getSubPlacements() {
        List<Placement> placements = new ArrayList<>(external);
        placements.addAll(internal);
        return placements;
    }

    @Override
    public List<StructurePlan> getSubStructurePlans() {
        List<StructurePlan> pls =  new ArrayList<>(internalPlans);
        pls.addAll(externalPlans);
        return pls;
    }
    
    /**
     * Will check if the corresponding plan matches any Ancestors (recursively)
     * @param plan The StructurePlan to check
     * @return True if it matches any ancestors
     */
    boolean matchesAnyAncestors(File file) {
        if(hash(file).equals(getPathHash())) {
            return true;
        } else if(parent != null) {
            return parent.matchesAnyAncestors(file);
        } else {
            return false;
        }
    }

}
