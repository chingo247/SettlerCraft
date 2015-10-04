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
package com.chingo247.structureapi;

import com.chingo247.structureapi.model.plot.Plot;
import com.chingo247.structureapi.model.plot.PlotRepository;
import com.chingo247.structureapi.model.zone.ConstructionZone;
import com.chingo247.structureapi.platform.permission.PermissionManager;
import com.google.common.util.concurrent.Monitor;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import java.util.Iterator;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author Chingo
 */
public class ConstructionZoneCreator {
    
    private final GraphDatabaseService graph;
    private final ConstructionWorld constructionWorld;
    private final Monitor monitor;
    private final PlotRepository plotRepository;

    ConstructionZoneCreator(ConstructionWorld constructionWorld, GraphDatabaseService graph) {
        this.graph = graph;
        this.constructionWorld = constructionWorld;
        this.monitor = constructionWorld.getMonitor();
        this.plotRepository = new PlotRepository(graph);
    }
    
    public boolean mayCreateZone(Player player) {
        return PermissionManager.getInstance().isAllowed(player, PermissionManager.Perms.SETTLER_CONSTRUCTION_ZONE_PLACE);
    }
    
    public void createZone(CuboidRegion region) {
        createZone(region, null);
    }
    
    public ConstructionZone createZone(CuboidRegion cuboidRegion, Player owner) {
        Transaction tx = null;
        ConstructionZone zone = null;
        try {
            tx = graph.beginTx();
            monitor.enter();
            
            Iterator<Plot> it = plotRepository.findWithin(constructionWorld, cuboidRegion, 1).iterator();
            Plot p = it.next();
            if(p != null) {
                Node n = p.getNode();
                
            }
            
            
            tx.success();
        } finally {
            if(tx != null) {
                tx.close();
            }
            monitor.leave();
        }
        return zone;
    }
    
    
}
