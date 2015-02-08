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

import com.chingo247.settlercraft.world.Direction;
import com.chingo247.structureapi.exception.PlanException;
import com.chingo247.structureapi.placement.GeneratedPlacement;
import com.chingo247.structureapi.placement.Placement;
import com.chingo247.structureapi.regions.CuboidDimension;
import com.sk89q.worldedit.Vector;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 *
 * @author Chingo
 */
public class PlacementListType {

    private final Logger LOG = Logger.getLogger(getClass());
    private final PlacementListType container;
    private final PlacementListType parent;
    private final Placement self;
    private final StructurePlan holder;
    private final ArrayList<PlacementListType> internals;
    private final ArrayList<PlacementListType> externals;
    private final ArrayList<GeneratedPlacement> generated;

    public PlacementListType(StructurePlan plan) {
        this(plan, plan.getPlacement(), null, null);
    }

    public Placement getSelf() {
        return self;
    }

    public ArrayList<PlacementListType> getExternals() {
        return externals;
    }
    
    

    private PlacementListType(StructurePlan plan, Placement placement, PlacementListType container, PlacementListType parent) {
        this.internals = new ArrayList<>();
        this.externals = new ArrayList<>();
        this.generated = new ArrayList<>();
        this.self = placement;
        this.container = container;
        this.parent = parent;
        this.holder = plan;

        // Only execute when u are the owner of the structure plan
        if (plan.getPlacement().getId().equals(self.getId())) {
            for (Placement p : plan.getSubStructurePlacements()) {
                putPlacement(plan, p);
            }

            for (StructurePlan sp : plan.getSubStructurePlans()) {
                putStructurePlan(sp);
            }
        }

    }

    private boolean withinContainer(Placement p) {
        if (container == null) {
            return true;
        }
        return CuboidDimension.isDimensionWithin(container.self, p);
    }

    private void putPlacementExternal(StructurePlan holder, Placement placement) {
        for (PlacementListType plt : externals) {
            // If there is any overlap
            if (CuboidDimension.overlaps(plt.self, placement)) {
                // And the placement is within this PlacementListType
                if (CuboidDimension.isDimensionWithin(plt.self, placement)) {
//                    if(holder.getUuid().equals(this.holder.getUuid())) {
                    plt.putPlacementInternal(holder, placement);
                    return;
//                    } else {
//                        // Throw exception not in the same plan!?
//                    }
                } else {
                    // Illegal overlap?
                    throw new PlanException("Illegal overlap between (" + this.holder.getFile().getName() + ", placement: #" + placement.getId() + ") "
                            + "and (" + holder.getFile().getName() + ", placement: #" + placement.getId() + ")");
                }
            }
        }
        externals.add(new PlacementListType(holder, placement, container, this));

    }

    private void putPlacementInternal(StructurePlan holder, Placement placement) {
        for (PlacementListType plt : internals) {

            // If there is any overlap...
            if (CuboidDimension.overlaps(plt.self, placement)) {

                // And placement is within...
                if (CuboidDimension.isDimensionWithin(plt.self, placement)) {
//                    if (holder.getUuid().equals(this.holder.getUuid())) {
                    plt.putPlacementInternal(holder, placement);
                    return;
//                    } else {
//                        // Throw exception not in the same plan!?
//                    }
                }

            }

        }
        // If the placement was internal... then this PlacementListType is both parent and container
        internals.add(new PlacementListType(holder, placement, this, this));

    }

    private void putPlacement(StructurePlan holder, Placement placement) {

        // Within container?
        if (withinContainer(placement)) {
            
            // Generated stuff has its own rules...
            if (placement instanceof GeneratedPlacement) {
                generated.add((GeneratedPlacement) placement);
                return;
            }

            // Check if placement is outside its parent
            if (!CuboidDimension.overlaps(self, placement)) {
                // Put external!
                putPlacementExternal(holder, placement);

                // otherwise check if placement is FULLY inside parent
            } else if (CuboidDimension.isDimensionWithin(self, placement)) {

            } else {
                // throw exception?
                throw new PlanException("Illegal overlap between (" + this.holder.getFile().getName() + ", placement: MAIN) "
                        + "and (" + holder.getFile().getName() + ", placement: #" + placement.getId() + ")");
            }

        } else {
            // Throw exception?
        }

        // Overlaps parent?
    }

    private void putStructurePlan(StructurePlan plan) {
        Placement main = plan.getPlacement();
        PlacementListType newPlacementListType;

        if (withinContainer(main)) {

            if (!CuboidDimension.overlaps(self, main)) {
                
                // FIT????
                newPlacementListType = new PlacementListType(plan, plan.getPlacement(), container, this);
            } else if (CuboidDimension.isDimensionWithin(self, main)) {
                 // FIT????
                newPlacementListType = new PlacementListType(plan, plan.getPlacement(), this, this);
            } else {
                throw new PlanException("Illegal overlap between (" + this.holder.getFile().getName() + ", placement: MAIN) "
                        + "and (" + plan.getFile().getName() + ", placement: MAIN)");
            }

        } else {
            // outside container!
            throw new PlanException("The main placement of " + plan.getFile().getName() + " should be within " + container.self);
        }
        
        for(PlacementListType plt : container.internals) {
            
        }
        
        
        

    }

//     private class StructurePlanPlacement extends Placement{
//        private final StructurePlan parent;
//        private final Placement placement;
//        private final boolean isStructurePlan;
//
//        public StructurePlanPlacement(StructurePlan parent, Placement placement, boolean isStructurePlan) {
//            this.parent = parent;
//            this.placement = placement;
//            this.isStructurePlan = isStructurePlan;
//        }
//        
//
//        @Override
//        public Vector getRelativePosition() {
//            return placement.getRelativePosition();
//        }
//
//        @Override
//        public void rotate(Direction direction) {
//            placement.rotate(direction);
//        }
//
//        @Override
//        public CuboidDimension getCuboidDimension() {
//            return placement.getCuboidDimension();
//        }
//        
//    }
    
    
}
