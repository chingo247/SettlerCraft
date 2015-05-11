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
import com.google.common.collect.Sets;
import java.io.File;
import java.util.Collection;
import java.util.Set;

/**
 *
 * @author Chingo
 */
public final class DefaultSubstructuresPlan extends AbstractStructurePlan implements SubStructuresPlan {

    private final DefaultSubstructuresPlan parent;
    private final Placement mainPlacement;
    private final Set<StructurePlan> plans;
    private final Set<Placement> placements; // Substructure - placeable

    protected DefaultSubstructuresPlan(String id, File structurePlanFile, DefaultSubstructuresPlan parent, Placement placement) {
        super(id, structurePlanFile);
        this.parent = parent;
        this.mainPlacement = placement;
        this.placements = Sets.newHashSet();
        this.plans = Sets.newHashSet();
    }

    @Override
    public boolean removePlacement(Placement placement) {
        return placements.remove(placement);
    }

    @Override
    public void addPlacement(Placement placement) {
        placements.add(placement);
    }

    @Override
    public void addStructurePlan(StructurePlan plan) {
        if (matchesParentRecursively(plan.getFile())) {
            throw new PlanException("Plans may not be equal to any of its ancestors.");
        }
        plans.add(plan);
    }

    @Override
    public boolean removeStructurePlan(StructurePlan plan) {
        return plans.remove(plan);
    }

    @Override
    public Placement getPlacement() {
        return mainPlacement;
    }

    @Override
    public DefaultSubstructuresPlan getParent() {
        return parent;
    }

    @Override
    public Collection<Placement> getSubPlacements() {
        return placements;
    }

    @Override
    public Collection<StructurePlan> getSubStructurePlans() {
        return plans;
    }

    /**
     * Will check if the corresponding plan matches any Ancestors (recursively)
     *
     * @param plan The StructurePlan to check
     * @return True if it matches any ancestors
     */
    boolean matchesParentRecursively(File file) {
        if (hash(file).equals(getPathHash())) {
            return true;
        } else if (parent != null) {
            return parent.matchesParentRecursively(file);
        } else {
            return false;
        }
    }

}
