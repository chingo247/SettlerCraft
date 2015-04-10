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
import java.util.List;

/**
 *
 * @author Chingo
 */
public interface SubStructuredPlan extends StructurePlan {
    
    /**
     * Removes a Placement from this StructurePlan
     * @param placement The Placement to remove
     * @return True if Placement was successfully removed
     */
    public boolean removePlacement(Placement placement);
    
    /**
     * Adds a placement to this StructurePlan
     * @param placement The placement to add
     */
    public void addPlacement(Placement placement);
    
    /**
     * Adds a StructurePlan to this StructurePlan
     * @param plan The StructurePlan to add
     */
    public void addStructurePlan(StructurePlan plan);
    
    /**
     * Removes a StructurePlan from this StructurePlan
     * @param plan The StructurePlan to remove
     * @return True if StructurePlan was successfully removed
     */
    public boolean removeStructurePlan(StructurePlan plan);
    
    
    /**
     * Gets all external placements. A placement is external when it's outside or doesn't reside within the 'Main Placement' of the StructurePlan
     * Which can be called by retrieved by calling {@link StructurePlan#getPlacement()}
     * @return The placements that are internal
     */
    public List<? extends Placement> getExternalPlacments();

    /**
     * Gets all internal placements. A placement is internal when it completely resides within the 'Main Placement' of the StructurePlan
     * Which can be called by retrieved by calling {@link StructurePlan#getPlacement()}
     * @return The placements that are internal
     */
    public List<? extends Placement> getInternalPlacements();
    
    /**
     * Gets all the SubstructurePlans of this StructurePlan.
     * @return The SubstructurePlans
     */
    public List<? extends StructurePlan> getSubStructurePlans();
    
    /**
     * Gets all the SubPlacement of this StructurePlan
     * @return The SubPlacements
     */
    public List<? extends Placement> getSubPlacements();
    
   /**
     * Checks if this StructurePlan is Top-Level, where TopLevel means the StructurePlan has no parent or the StructurePlan's
     * parent is TopLevel but this StructurePlan is Outside his parent. 
     * @return True if StructurePlan is Top-Level
     */
    public boolean isTopLevel();
    
    /**
     * 
     * @param <T>
     * @return 
     */
    public <T extends SubStructuredPlan> T getParent();
    
}
