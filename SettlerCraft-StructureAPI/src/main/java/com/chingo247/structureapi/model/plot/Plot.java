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
package com.chingo247.structureapi.model.plot;

import com.chingo247.settlercraft.core.model.BaseSettler;
import com.chingo247.settlercraft.core.model.DefaultNodeCollection;
import com.chingo247.settlercraft.core.model.NodeCollection;
import com.chingo247.structureapi.model.Relations;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.util.UUID;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Node;

/**
 *
 * @author Chingo
 */
public class Plot implements IPlot {

    public static final String MIN_X_PROPERTY = "minX", MIN_Y_PROPERTY = "minY", MIN_Z_PROPERTY = "minZ", MAX_X_PROPERTY = "maxX", MAX_Y_PROPERTY = "maxY", MAX_Z_PROPERTY = "maxZ";
    

    private final Node underlyingNode;

    
    public Plot(Node node) {
        this.underlyingNode = node;
    }

    public void setMinX(int x) {
        underlyingNode.setProperty(MIN_X_PROPERTY, x);
    }

    public void setMinY(int y) {
        underlyingNode.setProperty(MIN_Y_PROPERTY, y);
    }

    public void setMinZ(int z) {
        underlyingNode.setProperty(MIN_Z_PROPERTY, z);
    }

    public void setMaxX(int x) {
        underlyingNode.setProperty(MAX_X_PROPERTY, x);
    }

    public void setMaxY(int y) {
        underlyingNode.setProperty(MAX_Y_PROPERTY, y);
    }

    public void setMaxZ(int z) {
        underlyingNode.setProperty(MAX_Z_PROPERTY, z);
    }
    
    public int getMinX() {
        return (int) underlyingNode.getProperty(MIN_X_PROPERTY);
    }

    public int getMinY() {
        return (int) underlyingNode.getProperty(MIN_Y_PROPERTY);
    }

    public int getMinZ() {
        return (int) underlyingNode.getProperty(MIN_Z_PROPERTY);
    }

    public int getMaxX() {
        return (int) underlyingNode.getProperty(MAX_X_PROPERTY);
    }

    public int getMaxY() {
        return (int) underlyingNode.getProperty(MAX_Y_PROPERTY);
    }

    public int getMaxZ() {
        return (int) underlyingNode.getProperty(MAX_Z_PROPERTY);
    }

    @Override
    public Vector getMin() {
        return new BlockVector(getMinX(), getMinY(), getMinZ());
    }

    @Override
    public Vector getMax() {
        return new BlockVector(getMaxX(), getMaxY(), getMaxZ());
    }
    
    
    
    @Override
    public CuboidRegion getCuboidRegion() {
        int minX = (int) underlyingNode.getProperty(MIN_X_PROPERTY);
        int minY = (int) underlyingNode.getProperty(MIN_Y_PROPERTY);
        int minZ = (int) underlyingNode.getProperty(MIN_Z_PROPERTY);
        int maxX = (int) underlyingNode.getProperty(MAX_X_PROPERTY);
        int maxY = (int) underlyingNode.getProperty(MAX_Y_PROPERTY);
        int maxZ = (int) underlyingNode.getProperty(MAX_Z_PROPERTY);
        return new CuboidRegion(new Vector(minX, minY, minZ), new Vector(maxX, maxY, maxZ));
    }

}
