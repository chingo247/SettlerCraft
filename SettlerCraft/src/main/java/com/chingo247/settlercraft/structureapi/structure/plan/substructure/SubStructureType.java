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
import com.chingo247.settlercraft.structureapi.plan.schematic.SchematicManager;
import com.chingo247.settlercraft.structureapi.structure.plan.SettlerCraftPlan;
import com.chingo247.settlercraft.structureapi.world.Dimension;
import com.chingo247.settlercraft.structureapi.world.Direction;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Vector;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Chingo
 */
public class SubStructureType {

    private String name;
    private SettlerCraftPlan plan;
    private Dimension dimension;
    private SubStructureType parent;
    private int level;
    private List<SubStructureType> structures;

    SubStructureType load(SettlerCraftPlan plan, Direction direction, Vector position) throws IOException {
        File schematic = plan.getSchematic();
        long checksum = FileUtils.checksumCRC32(schematic);
        SchematicManager sm = SchematicManager.getInstance();
        CuboidClipboard s = sm.getClipboard(checksum, direction);
        Dimension dim = new Dimension(Vector.ZERO, new Vector(s.getWidth(), s.getLength(), s.getHeight()));

        if (hasParent()) {
            if (!(isFullyOutsideParent(dim) || isFullyWithinParent(dim))) {
                throw new PlanException("Invalid substructureing at dept: " + level + " in plan " + plan.getName());
            }
            
            
        }

    }
    
    private boolean matchesParent(SettlerCraftPlan plan) {
        if()
    }

    private boolean hasParent() {
        return parent != null;
    }

    private boolean isFullyWithinParent(Dimension dimension) {
        return parent.dimension.getMinX() <= dimension.getMinX()
                && parent.dimension.getMinY() <= dimension.getMinY()
                && parent.dimension.getMinZ() <= dimension.getMinZ()
                && parent.dimension.getMaxZ() <= dimension.getMaxZ()
                && parent.dimension.getMaxY() <= dimension.getMaxY()
                && parent.dimension.getMaxX() <= dimension.getMaxX();
    }

    private boolean isFullyOutsideParent(Dimension dimension) {
        // NO OVERLAP AT ALL WITH PARENT
        return !(parent.dimension.getMaxX() > dimension.getMinX() && parent.dimension.getMinX() < dimension.getMaxX()
                && parent.dimension.getMaxY() > dimension.getMinY() && parent.dimension.getMinY() < dimension.getMaxY()
                && parent.dimension.getMaxZ() > dimension.getMinZ() && parent.dimension.getMinZ() < dimension.getMaxZ());
    }

}
