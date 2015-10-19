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

import com.chingo247.settlercraft.core.event.async.AsyncEventManager;
import com.chingo247.structureapi.event.zone.CreateConstructionZoneEvent;
import com.chingo247.structureapi.model.owner.OwnerType;
import com.chingo247.structureapi.model.plot.PlotRepository;
import com.chingo247.structureapi.model.settler.ISettler;
import com.chingo247.structureapi.model.settler.ISettlerRepository;
import com.chingo247.structureapi.model.settler.SettlerRepositiory;
import com.chingo247.structureapi.model.structure.IStructureRepository;
import com.chingo247.structureapi.model.structure.StructureNode;
import com.chingo247.structureapi.model.structure.StructureRepository;
import com.chingo247.structureapi.model.world.IStructureWorldRepository;
import com.chingo247.structureapi.model.world.StructureWorld;
import com.chingo247.structureapi.model.world.StructureWorldRepository;
import com.chingo247.structureapi.model.zone.ConstructionZone;
import com.chingo247.structureapi.model.zone.ConstructionZoneRepository;
import com.chingo247.structureapi.model.zone.IConstructionZone;
import com.chingo247.structureapi.model.zone.IConstructionZoneRepository;
import com.chingo247.xplatform.core.IPlayer;
import com.google.common.util.concurrent.Monitor;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.util.Collection;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

/**
 * Handles creation and deletion of construction zones. 
 * @author Chingo
 */
public class ConstructionZoneManager extends AbstractPlotManager implements IConstructionZoneManager {
    
    private final GraphDatabaseService graph;
    private final Monitor monitor;
    private final IStructureRepository structureRepository;
    private final IStructureWorldRepository worldRepository;
    private final ISettlerRepository settlerRepository;
    private final IConstructionZoneRepository constructionZoneRepository;

    ConstructionZoneManager(ConstructionWorld constructionWorld, GraphDatabaseService graph) {
        super(constructionWorld);
        this.graph = graph;
        this.monitor = constructionWorld.getMonitor();
        this.structureRepository = new StructureRepository(graph);
        this.constructionZoneRepository = new ConstructionZoneRepository(graph);
        this.worldRepository = new StructureWorldRepository(graph);
        this.settlerRepository = new SettlerRepositiory(graph);
    }
    
    
    @Override
    public IConstructionZone createZone(CuboidRegion cuboidRegion, IPlayer owner) throws RestrictionException {
        checkWorldRestrictions(cuboidRegion);
        Transaction tx = null;
        IConstructionZone zone = null;
        monitor.enter();
        try {
            tx = graph.beginTx();
            checkConstructionZonePlacingRestrictions(cuboidRegion);
            zone = constructionZoneRepository.add(cuboidRegion);
            if(owner != null) {
                ISettler settler = settlerRepository.findByUUID(owner.getUniqueId());
                zone.getOwnerDomain().setOwnership(settler, OwnerType.MASTER);
            }
            StructureWorld w = worldRepository.addOrGet(world.getName(), world.getUUID());
            w.addZone(zone);
            AsyncEventManager.getInstance().post(new CreateConstructionZoneEvent(zone));
            tx.success();
        } catch (Exception ex) {
            if(tx != null) tx.failure();
        } finally { 
            if(tx != null) {
                tx.close();
            }
            monitor.leave();
        }
        return zone;
    }
    
    /**
     * Checks whether a construction zone may be placed in the given area
     * <p>
     * - This method does not execute the {@link #checkWorldRestrictions(com.sk89q.worldedit.world.World, com.sk89q.worldedit.regions.CuboidRegion)
     * } method and should be executed seperately before calling this method for
     * complete validation.
     * </p>
     *
     * @param world
     * @param region
     */
    @Override
    public void checkConstructionZonePlacingRestrictions(CuboidRegion region) throws RestrictionException {
        Collection<ConstructionZone> zones = constructionZoneRepository.findWithin(world.getUUID(), region, 1);

        if (!zones.isEmpty()) {
            ConstructionZone zone = zones.iterator().next();
            throw new ConstructionZoneRestrictionException("ConstructionZone will overlap another construction zone \n"
                    + "zone at: " + zone.getCuboidRegion());
        }

        Collection<StructureNode> structures = structureRepository.findRootStructuresWithin(world.getUUID(), region, 1);
        if (!structures.isEmpty()) {
            StructureNode sn = structures.iterator().next();
            throw new ConstructionZoneRestrictionException("ConstructionZone overlaps a structure \n"
                    + "Structure: " + sn.getId() + " - " + sn.getName() + "\n"
                    + "Area: " + sn.getCuboidRegion());
        }

    }
    
    
}
