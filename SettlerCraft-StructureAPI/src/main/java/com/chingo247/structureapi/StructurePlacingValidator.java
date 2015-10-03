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

import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.structureapi.exception.ConstructionZoneRestrictionException;
import com.chingo247.structureapi.exception.RestrictionException;
import com.chingo247.structureapi.exception.StructureRestrictionException;
import com.chingo247.structureapi.exception.WorldRestrictionException;
import com.chingo247.structureapi.model.settler.ISettlerRepository;
import com.chingo247.structureapi.model.settler.Settler;
import com.chingo247.structureapi.model.settler.SettlerRepositiory;
import com.chingo247.structureapi.model.structure.ConstructionStatus;
import com.chingo247.structureapi.model.structure.IStructureRepository;
import com.chingo247.structureapi.model.structure.StructureNode;
import com.chingo247.structureapi.model.structure.StructureRepository;
import com.chingo247.structureapi.model.zone.AccessType;
import com.chingo247.structureapi.model.zone.ConstructionZoneRepository;
import com.chingo247.structureapi.model.zone.IConstructionZone;
import com.chingo247.structureapi.model.zone.IConstructionZoneRepository;
import com.chingo247.structureapi.util.RegionUtil;
import com.chingo247.structureapi.world.IWorldConfig;
import com.chingo247.xplatform.core.APlatform;
import com.chingo247.xplatform.core.ILocation;
import com.chingo247.xplatform.core.IWorld;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import java.util.Collection;
import java.util.Iterator;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

/**
 *
 * @author Chingo
 */
public class StructurePlacingValidator implements IPlacingValidator {

    private final GraphDatabaseService graph;
    private final IStructureRepository structureRepository;
    private final IConstructionZoneRepository constructionZoneRepository;
    private final ISettlerRepository settlerRepository;
    private final IStructureAPI structureAPI;
    private final APlatform platform;

    public StructurePlacingValidator(GraphDatabaseService graph, IStructureAPI structureAPI, APlatform platform) {
        this.graph = graph;
        this.structureAPI = structureAPI;
        this.structureRepository = new StructureRepository(graph);
        this.settlerRepository = new SettlerRepositiory(graph);
        this.constructionZoneRepository = new ConstructionZoneRepository(graph);
        this.platform = platform;
    }

    @Override
    public void checkWorldRestrictions(World world, CuboidRegion region) throws WorldRestrictionException {

        // Below the world?s
        if (region.getMinimumPoint().getBlockY() <= 1) {
            throw new WorldRestrictionException("Structure must be placed at a minimum height of 1");
        }

        // Exceeds world height limit?
        if (region.getMaximumPoint().getBlockY() > world.getMaxY()) {
            throw new WorldRestrictionException("Structure will reach above the world's max height (" + world.getMaxY() + ")");
        }

        // Check for overlap on the world's 'SPAWN'
        IWorld w = SettlerCraft.getInstance().getPlatform().getServer().getWorld(world.getName());
        ILocation l = w.getSpawn();
        Vector spawnPos = new Vector(l.getBlockX(), l.getBlockY(), l.getBlockZ());
        if (region.contains(spawnPos)) {
            throw new WorldRestrictionException("Structure overlaps the world's spawn...");
        }
    }

    @Override
    public void checkStructureRestrictions(World world, CuboidRegion region, Player player) throws StructureRestrictionException {
        structureAPI.checkRestrictions(null, world, region);
    }

    @Override
    public void checkStructureRestrictions(World world, CuboidRegion region) throws StructureRestrictionException {
        structureAPI.checkRestrictions(null, world, region);
    }

//    @Override
//    public void checkStructureOverlapRestrictions(World world, CuboidRegion region, Player player) throws StructureRestrictionException {
//        IWorld w = SettlerCraft.getInstance().getPlatform().getServer().getWorld(world.getName());
//        
//        boolean allowsSubstructures = structureAPI.getConfig().allowsSubstructures();
//        
//        if(allowsSubstructures) {
//            Collection<StructureNode> structures = structureRepository.findStructuresWithin(w.getUUID(), region, -1);
//            Settler settler = player != null ? settlerRepository.findByUUID(player.getUniqueId()) : null;
//            for (StructureNode structureNode : structures) {
//                if (!RegionUtil.isDimensionWithin(structureNode.getCuboidRegion(), region)) { // overlaps but doesn't fit within
//                    throw new StructureRestrictionException("Structure overlaps structure #" + structureNode.getId() + " " + structureNode.getName());
//                } else if (settler != null && structureNode.getOwnerDomain().isOwner(settler.getUniqueIndentifier())) { // fits within but doesnt own
//                    throw new StructureRestrictionException("Can't create substructure, structure will overlap a structure you don't own");
//                } else if(structureNode.getStatus() != ConstructionStatus.COMPLETED && structureNode.getStatus() != ConstructionStatus.STOPPED) { // fits within and owns, but structure is in progress
//                    throw new StructureRestrictionException("Can't place within a structure that is in progress");
//                }
//            } 
//        } else {
//            Iterator<StructureNode> iterator = structureRepository.findStructuresWithin(w.getUUID(), region, 1).iterator();
//            if(iterator.hasNext()) {
//                StructureNode structureNode = iterator.next();
//                throw new StructureRestrictionException("Structure overlaps structure #" + structureNode.getId() + " " + structureNode.getName());
//            }
//        }
//    }
//
//    @Override
//    public void checkStructureOverlapRestrictions(World world, CuboidRegion region) throws StructureRestrictionException {
//        checkStructureOverlapRestrictions(world, region, null);
//    }

    @Override
    public void checkStructurePlacingRestrictions(World world, CuboidRegion affectArea, Vector placingPoint, Player player) throws RestrictionException {
        boolean allowsSubstructures = structureAPI.getConfig().allowsSubstructures();
        ConstructionWorld w = StructureAPI.getInstance().getConstructionWorld(world);
        IWorldConfig config = w.getConfig();
        
        if(config.restrictsStructures()) {
            throw new WorldRestrictionException("This world does not allow any structures");
        }
        
        checkConstructionZoneRestrictions(w, affectArea, player);
        
        
        if(allowsSubstructures) {
            StructureNode possibleParent = structureRepository.findStructureOnPosition(w.getUUID(), placingPoint);
            Vector min = affectArea.getMinimumPoint();
            Vector max = affectArea.getMaximumPoint();
            StructureNode overlappingStructure;
            if (possibleParent != null) {
                Node n = possibleParent.getNode();
                StructureNode sn = new StructureNode(n);
                Iterator<StructureNode> subIt = sn.getSubStructuresWithin(new CuboidRegion(min, max)).iterator();
                overlappingStructure = subIt.hasNext() ? subIt.next() : null;
            } else {
                IWorld iw = platform.getServer().getWorld(world.getName());
                Iterator<StructureNode> subIt = structureRepository.findStructuresWithin(iw.getUUID(), new CuboidRegion(min, max), 1).iterator();
                overlappingStructure = subIt.hasNext() ? subIt.next() : null;
            }

            if (overlappingStructure != null && !overlappingStructure.getOwnerDomain().isOwner(player.getUniqueId())) {
                CuboidRegion overlappingArea = overlappingStructure.getCuboidRegion();
                throw new StructureRestrictionException("Can't place structure, structure would overlap structure: \n#" + overlappingStructure.getId() + " - " + overlappingStructure.getName() + "\n"
                        + "Located at min: " + overlappingArea.getMinimumPoint() + ", max: " + overlappingArea.getMaximumPoint());
            }
            
            if(overlappingStructure != null && (overlappingStructure.getStatus() != ConstructionStatus.COMPLETED && overlappingStructure.getStatus() != ConstructionStatus.STOPPED)) {
                throw new StructureRestrictionException("Can't place within a structure that is in progress");
            }
            
            
        } else {
            checkStructureRestrictions(world, affectArea);
        }

    }

    @Override
    public void checkStructurePlacingRestrictions(World world, CuboidRegion affectArea, Vector placingPoint) throws RestrictionException {
        checkStructurePlacingRestrictions(world, affectArea, placingPoint, null);
    }

    private void checkConstructionZoneRestrictions(ConstructionWorld world, CuboidRegion affectArea, Player player) throws ConstructionZoneRestrictionException {
        Collection<IConstructionZone> zones = constructionZoneRepository.findWithin(world.getUUID(), affectArea, 2);
        
        // May not overlap multiple zones
        if(zones.size() == 2) {
            throw new ConstructionZoneRestrictionException("Structure overlaps multiple construction zones");
        }
        
        // Check if restricted to zones
        if(zones.isEmpty() && world.getConfig().isZonesOnly()) {
            throw new ConstructionZoneRestrictionException("Structures may only be placed within construction zones");
        }
        
        // If zones != empty, check acces
        if(!zones.isEmpty()) {
            IConstructionZone zone = zones.iterator().next();
            CuboidRegion zoneRegion = zone.getCuboidRegion();
            
            if(zone.getAccessType() == AccessType.RESTRICTED) {
                throw new ConstructionZoneRestrictionException("Placing structures is restricted within this construction zone: "
                        + "\n" + "(" + zoneRegion.getMinimumPoint() + ", " + zoneRegion.getMaximumPoint());
            }
            
            if(zone.getAccessType() == AccessType.PRIVATE && !zone.getOwnerDomain().isOwner(player.getUniqueId())) {
                throw new ConstructionZoneRestrictionException("You are not a member of this zone!"
                         + "\n" + "(" + zoneRegion.getMinimumPoint() + ", " + zoneRegion.getMaximumPoint());
            }
            
            if(!RegionUtil.isDimensionWithin(zoneRegion, affectArea)) {
                throw new ConstructionZoneRestrictionException("Structure is not within construction zone"
                         + "\n" + "(" + zoneRegion.getMinimumPoint() + ", " + zoneRegion.getMaximumPoint());
            }
            
        }
        
    }
    
}
