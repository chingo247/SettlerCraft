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
public abstract class SubStructureType {

    private String name;
    private SettlerCraftPlan plan;
    private SubStructureType parent;
    private int level;
    private List<SubStructureType> structures;

    private boolean hasParent() {
        return parent != null;
    }
    
    public abstract Dimension getDimension();

    private boolean isFullyWithinParent(Dimension dimension) {
        return parent.getDimension().getMinX() <= dimension.getMinX()
                && parent.getDimension().getMinY() <= dimension.getMinY()
                && parent.getDimension().getMinZ() <= dimension.getMinZ()
                && parent.getDimension().getMaxZ() <= dimension.getMaxZ()
                && parent.getDimension().getMaxY() <= dimension.getMaxY()
                && parent.getDimension().getMaxX() <= dimension.getMaxX();
    }

    private boolean isFullyOutsideParent(Dimension dimension) {
        // NO OVERLAP AT ALL WITH PARENT
        return !(parent.getDimension().getMaxX() > dimension.getMinX() && parent.getDimension().getMinX() < dimension.getMaxX()
                && parent.getDimension().getMaxY() > dimension.getMinY() && parent.getDimension().getMinY() < dimension.getMaxY()
                && parent.getDimension().getMaxZ() > dimension.getMinZ() && parent.getDimension().getMinZ() < dimension.getMaxZ());
    }

}
