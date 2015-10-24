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

import com.chingo247.settlercraft.core.model.World;
import com.chingo247.settlercraft.core.model.WorldNode;
import com.chingo247.settlercraft.core.model.interfaces.IWorld;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.neo4j.graphdb.Node;

/**
 *
 * @author Chingo
 */
public class Plot implements IPlot {
    
    private Node underlyingNode;
    private Vector min, max;
    private IWorld world;
    private String plotType;

    public Plot(PlotNode plotNode) {
        this.underlyingNode = plotNode.getNode();
        WorldNode wn = plotNode.getWorld();
        this.world = new World(wn);
        this.min = plotNode.getMin();
        this.max = plotNode.getMax();
        this.plotType = plotNode.getPlotType();
    }

    public Plot(Node node) {
        this(new PlotNode(node));
    }
    
    @Override
    public String getPlotType() {
        return plotType;
    }
    
    @Override
    public Node getNode() {
        return underlyingNode;
    }

    @Override
    public Vector getMin() {
        return min;
    }

    @Override
    public Vector getMax() {
        return max;
    }

    @Override
    public CuboidRegion getCuboidRegion() {
        return new CuboidRegion(min, max);
    }

    @Override
    public IWorld getWorld() {
        return world;
    }
    
    
    
    
    
}
