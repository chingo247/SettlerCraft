/*
 * Copyright (C) 2015 Chingo
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


package com.chingo247.settlercraft.structureapi.structure.plan;

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
