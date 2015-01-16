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

import com.chingo247.settlercraft.structureapi.structure.regions.CuboidDimension;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Chingo
 */
public class StructurePlanValidator {

    public void validate(StructurePlan plan) {
        
        
    }
    
    private void validate(StructurePlan plan, PlacementListType placementListType) {
        
    }
    
    private class PlacementListType {
        private PlacementListType parent;
        private Placement self;
        private List<PlacementListType> structurListTypes;

        public PlacementListType() {
            this.structurListTypes = new ArrayList<>();
        }
        
        private PlacementListType(PlacementListType parent, Placement self) {
            this.parent = parent;
            this.structurListTypes = new ArrayList<>();
        }
        
        public void putPlacement(StructurePlan plan) {
            // Im the first!
            if(self == null) {
                self = plan.placement;
                for(Placement p : plan.getSubStructurePlacements()) {
                    structurListTypes.add(new PlacementListType(this, p));
                }
            }
        }
        
        private void overlapsAny(PlacementListType listType, Placement placement) {
            for(PlacementListType plt : listType.structurListTypes) {
                if(overlaps(plt.self, placement)) {
                    // Throw Exception
                }
                
                overlapsAny(plt, placement);
            }
        }
        
        private boolean overlaps(Placement a, Placement b) {
            return CuboidDimension.overlaps(a, b);
        }
        
        
        
        
    }
    
    
    
}
