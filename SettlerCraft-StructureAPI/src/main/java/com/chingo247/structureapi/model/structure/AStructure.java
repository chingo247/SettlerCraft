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
package com.chingo247.structureapi.model.structure;

import com.chingo247.structureapi.model.interfaces.IStructure;
import com.chingo247.structureapi.structure.StructureAPI;
import com.chingo247.structureapi.structure.plan.IStructurePlan;
import com.chingo247.structureapi.structure.plan.StructurePlanReader;
import com.chingo247.structureapi.util.WorldUtil;
import com.sk89q.worldedit.Vector;
import java.io.File;
import org.neo4j.graphdb.Node;

/**
 *
 * @author Chingo
 */
public abstract class AStructure implements IStructure {

    private final Node underlyingNode;
    
    public AStructure(Node node) {
        this.underlyingNode = node;
    }

    @Override
    public Node getNode() {
        return underlyingNode;
    }
    
    
    /**
     * Will add the offset to the structure's origin, which is always the front
     * left corner of a structure.
     *
     * @param offset The offset
     * @return the location
     */
    public Vector translateRelativeLocation(Vector offset) {
        Vector p = WorldUtil.translateLocation(getOrigin(), getDirection(), offset.getX(), offset.getY(), offset.getZ());
        return new Vector(p.getBlockX(), p.getBlockY(), p.getBlockZ());
    }

    /**
     * Gets the relative position
     * @param worldPosition The worldposition
     * @return The relative position
     */
    public Vector getRelativePosition(Vector worldPosition) {
        switch (getDirection()) {
            case NORTH:
                return new Vector(
                        worldPosition.getBlockX() - this.getOrigin().getX(),
                        worldPosition.getBlockY() - this.getOrigin().getY(),
                        this.getOrigin().getZ() - worldPosition.getBlockZ()
                );
            case SOUTH:
                return new Vector(
                        this.getOrigin().getX() - worldPosition.getBlockX(),
                        worldPosition.getBlockY() - this.getOrigin().getY(),
                        worldPosition.getBlockZ() - this.getOrigin().getZ()
                );
            case EAST:
                return new Vector(
                        worldPosition.getBlockZ() - this.getOrigin().getZ(),
                        worldPosition.getBlockY() - this.getOrigin().getY(),
                        worldPosition.getBlockX() - this.getOrigin().getX()
                );
            case WEST:
                return new Vector(
                        this.getOrigin().getZ() - worldPosition.getBlockZ(),
                        worldPosition.getBlockY() - this.getOrigin().getY(),
                        this.getOrigin().getX() - worldPosition.getBlockX()
                );
            default:
                throw new AssertionError("Unreachable");
        }
    }

    /**
     * Returns the directory for this structure
     *
     * @return The directory
     */
    public final File getStructureDirectory() {
        File worldStructureFolder = StructureAPI.getInstance().getStructuresDirectory(getWorld().getName());
        return new File(worldStructureFolder, String.valueOf(getId()));
    }

    @Override
    public IStructurePlan getStructurePlan() {
        File planFile = new File(getStructureDirectory(), "structureplan.xml");

        StructurePlanReader reader = new StructurePlanReader();
        IStructurePlan plan = reader.readFile(planFile);

        return plan;
    }
    
}
