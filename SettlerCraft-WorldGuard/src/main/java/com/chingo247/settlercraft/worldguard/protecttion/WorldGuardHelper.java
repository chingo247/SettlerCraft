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
package com.chingo247.settlercraft.worldguard.protecttion;

import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.core.event.EventManager;
import com.chingo247.settlercraft.core.persistence.dao.settler.SettlerNode;
import com.chingo247.settlercraft.structureapi.persistence.dao.IStructureDAO;
import com.chingo247.settlercraft.structureapi.persistence.entities.structure.StructureNode;
import com.chingo247.settlercraft.structureapi.persistence.entities.structure.StructureOwnerType;
import com.chingo247.settlercraft.worldguard.restriction.WorldGuardRestriction;
import com.chingo247.settlercraft.structureapi.structure.ConstructionStatus;
import com.chingo247.settlercraft.structureapi.structure.DefaultStructureFactory;
import com.chingo247.settlercraft.structureapi.structure.IStructureAPI;
import com.chingo247.settlercraft.structureapi.structure.Structure;
import com.chingo247.settlercraft.structureapi.structure.StructureAPI;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldConfiguration;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.bukkit.permission.RegionPermissionModel;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author Chingo
 */
public class WorldGuardHelper implements IStructureProtector {
    
    private static final String PREFIX = "SC_REG_";
    public static String WORLD_GUARD_REGION_PROPERTY = "WGRegion";
    private GraphDatabaseService graph;
    private IStructureDAO structureDAO;
    private IStructureAPI structureAPI;

    public WorldGuardHelper(GraphDatabaseService graph, IStructureDAO structureDAO, IStructureAPI structureAPI) {
        this.graph = graph;
        this.structureDAO = structureDAO;
        this.structureAPI = structureAPI;
    }
    
    /**
     * Protects a Structure with WorldGuard. Note this will have no effect if 
     * The config does not have the property 'structure.protected' to 'true'
     * @param structure The structure to protect
     */
    @Override
    public synchronized void protect(Structure structure) {
        if(!structureAPI.getConfig().protectesStructures()) {
            return;
        }
        
        World world = Bukkit.getWorld(structure.getWorld());
        CuboidRegion dimension = structure.getCuboidRegion();
        
        
        // Get world guard flags
        // StructurePlan plan = structure.getStructurePlan();
        
        RegionManager mgr = getRegionManager(world);

        Vector p1 = dimension.getMinimumPoint();
        Vector p2 = dimension.getMaximumPoint();
        String id = getRegionId(structure);

        if (regionExists(world, id)) {
            mgr.removeRegion(id);
        }
        
        ProtectedCuboidRegion region = new ProtectedCuboidRegion(
                id, 
                new BlockVector(p1.getBlockX(), p1.getBlockY(), p1.getBlockZ()), 
                new BlockVector(p2.getBlockX(), p2.getBlockY(), p2.getBlockZ())
        );
        
        try(Transaction tx = graph.beginTx()) {
            StructureNode structureNode = structureDAO.find(structure.getId());
            
            Node rawNode = structureNode.getRawNode();
                for (Relationship rel : rawNode.getRelationships(DynamicRelationshipType.withName("OwnedBy"))) {
                    Node n = rel.getOtherNode(rawNode);
                    if(!n.hasLabel(SettlerNode.LABEL)) {
                        continue;
                    }
                    
                    SettlerNode ownerNode = new SettlerNode(n);
                    Integer typeId = (Integer) rel.getProperty("Type");
                    StructureOwnerType type = StructureOwnerType.match(typeId);
                    if(type == StructureOwnerType.MEMBER) {
                        region.getMembers().addPlayer(ownerNode.getUUID());
                    } else {
                       region.getOwners().addPlayer(ownerNode.getUUID());
                    }
                    
                }
            
            mgr.addRegion(region);
            try {
                mgr.save();
            } catch (StorageException ex) {
                Logger.getLogger(WorldGuardStructureListener.class.getName()).log(Level.SEVERE, null, ex);
                tx.failure();
            }
            
            structureNode.getRawNode().setProperty(WORLD_GUARD_REGION_PROPERTY, id);
            tx.success();
            
        }
    }
    
    private String getRegionId(Structure structure) {
        return PREFIX+structure.getId();
    }
    
    
    private void processStructuresWithoutRegion() {
        final List<Structure> structures = Lists.newArrayList();
        try(Transaction tx = graph.beginTx()) {
            
            String query = "MATCH(s:" + StructureNode.LABEL.name() + ") "
                    + "WHERE s."+WORLD_GUARD_REGION_PROPERTY+" IS NULL "
                    + "AND NOT s." + StructureNode.CONSTRUCTION_STATUS_PROPERTY + " = " + ConstructionStatus.REMOVED.getStatusId() + " "
                    + "RETURN s";
            
            Result r = graph.execute(query);
            while(r.hasNext()) {
                Map<String,Object> map = r.next();
                for(Object o : map.values()) { // all structures
                    Node n = (Node) o;
                    StructureNode structureNode = new StructureNode(n);
                    Structure structure = DefaultStructureFactory.getInstance().makeStructure(structureNode);
                    structures.add(structure);
                }
            }
            tx.success();
        }
        
        if(!structures.isEmpty()) {
            System.out.println("[SettlerCraft]: Processing " + structures.size() + " structures without a worldguard region");
        }
        
        SettlerCraft.getInstance().getExecutor().submit(new Runnable() {

            @Override
            public void run() {
                for(Structure s : structures) {
                    protect(s);
                    System.out.println("[SettlerCraft]: Protected structure #" + s.getId() + " with 'WorldGuard'");
                }
            }
        });
        
    }

    @Override
    public String getName() {
        return "WorldGuard";
    }

    @Override
    public void removeProtection(Structure structure) {
        World world = Bukkit.getWorld(structure.getWorld());
        RegionManager mgr = getRegionManager(world);
        String region = getRegionId(structure);
        if(mgr.hasRegion(region)) {
            mgr.removeRegion(region);
            try {
                mgr.save();
            } catch (StorageException ex) {
                Logger.getLogger(WorldGuardHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public boolean hasProtection(Structure structure) {
        World world = Bukkit.getWorld(structure.getWorld());
        RegionManager mgr = getRegionManager(world);
        String region = getRegionId(structure);
        return mgr.hasRegion(region);
    }

    @Override
    public void initialize() {
        EventManager.getInstance().getEventBus().register(new WorldGuardStructureListener(this, graph));
        StructureAPI.getInstance().addRestriction(new WorldGuardRestriction());
        processStructuresWithoutRegion();
        processInvalidStructures();
    }
    
    public void addMember(UUID player, Structure structure) {
        World world = Bukkit.getWorld(structure.getWorld());
        RegionManager mgr = getRegionManager(world);
        String regionId = getRegionId(structure);
        ProtectedRegion region = mgr.getRegion(regionId);
        if(region != null) {
            if(!region.getMembers().contains(player)) {
                region.getMembers().addPlayer(player);
                try {
                    mgr.save();
                } catch (StorageException ex) {
                    Logger.getLogger(WorldGuardHelper.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public void addOwner(UUID player, Structure structure) {
        World world = Bukkit.getWorld(structure.getWorld());
        RegionManager mgr = getRegionManager(world);
        String regionId = getRegionId(structure);
        ProtectedRegion region = mgr.getRegion(regionId);
        if(region != null) {
            if(!region.getOwners().contains(player)) {
                region.getOwners().addPlayer(player);
                try {
                    mgr.save();
                } catch (StorageException ex) {
                    Logger.getLogger(WorldGuardHelper.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public void removeOwner(UUID player, Structure structure) {
        World world = Bukkit.getWorld(structure.getWorld());
        RegionManager mgr = getRegionManager(world);
        String regionId = getRegionId(structure);
        ProtectedRegion region = mgr.getRegion(regionId);
        if(region != null) {
            region.getOwners().removePlayer(player);
            try {
                mgr.save();
            } catch (StorageException ex) {
                Logger.getLogger(WorldGuardHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void removeMember(UUID player, Structure structure) {
        World world = Bukkit.getWorld(structure.getWorld());
        RegionManager mgr = getRegionManager(world);
        String regionId = getRegionId(structure);
        ProtectedRegion region = mgr.getRegion(regionId);
        if(region != null) {
            region.getMembers().removePlayer(player);
            try {
                mgr.save();
            } catch (StorageException ex) {
                Logger.getLogger(WorldGuardHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static WorldGuardPlugin getWorldGuard() {
        return WorldGuardPlugin.inst();
    }

    public static boolean overlaps(World world, BlockVector pos1, BlockVector pos2) {
        return getWorldGuard().getRegionManager(world)
                .getApplicableRegions(new ProtectedCuboidRegion(null, pos1, pos2)).size() > 0;
    }

    public static boolean hasRegion(World world, String id) {
        return getRegionManager(world).hasRegion(id);
    }

    public static WorldConfiguration getWorldConfiguration(World world) {
        return getWorldGuard().getGlobalStateManager().get(world);
    }

    public static LocalPlayer getLocalPlayer(Player player) {
        return getWorldGuard().wrapPlayer(player);
    }

    public static boolean hasReachedMaxRegionCount(World world, Player player) {
        int maxRegionCount = getWorldConfiguration(world).getMaxRegionCount(player);
        return maxRegionCount >= 0
                && getRegionManager(world).getRegionCountOfPlayer(getLocalPlayer(player)) >= maxRegionCount;
    }

    public static RegionManager getRegionManager(World world) {
        return WGBukkit.getRegionManager(world);
    }

    public static RegionPermissionModel getRegionPermissionModel(Player player) {
        return new RegionPermissionModel(getWorldGuard(), player);
    }
    
     /**
     * Checks wheter the region exists.
     * @param world The world
     * @param id The region's id
     * @return True if regions exists, otherwise false.
     */
    public static boolean regionExists(World world, String id) {
        return getRegionManager(world).hasRegion(id);
    }
    
    public static boolean mayClaim(Player player) {
        RegionPermissionModel permissionModel = getRegionPermissionModel(player);
        return permissionModel.mayClaim();
    }
    
    /**
     * Checks whether the player can claim a region within a world
     * @param player The player
     * @param world The world
     * @return True if player can claim, false if region count was exceeded or when there is no region manager for the specified world
     */
    public static boolean canClaim(Player player, World world) {
        WorldConfiguration wcfg = getWorldGuard().getGlobalStateManager().get(player.getWorld());
        RegionPermissionModel permissionModel = getRegionPermissionModel(player);
        RegionManager mgr = getWorldGuard().getRegionManager(world);
        if(mgr == null) {
            return false;
        }

        // Check whether the player has created too many regions
        if (!permissionModel.mayClaimRegionsUnbounded()) {
            int maxRegionCount = wcfg.getMaxRegionCount(player);
            if (maxRegionCount >= 0
                    && mgr.getRegionCountOfPlayer(getLocalPlayer(player)) >= maxRegionCount) {

                return false;
            }
        }
        return true;
    }

    private void processInvalidStructures() {
        
        String query = "MATCH(s:"+StructureNode.LABEL.name() + ") "
                     + "WHERE s."+WORLD_GUARD_REGION_PROPERTY + " IS NOT NULL "
                     + "AND s."+StructureNode.CONSTRUCTION_STATUS_PROPERTY + " = " + ConstructionStatus.REMOVED.getStatusId() + " "
                     + "RETURN s";
        
        try(Transaction tx = graph.beginTx()) {
            Result r = graph.execute(query);
            
            while(r.hasNext()) {
                for(Object o : r.next().values()) {
                    Node n = (Node) o;
                    Structure structure = DefaultStructureFactory.getInstance().makeStructure(new StructureNode(n));
                    removeProtection(structure);
                    n.removeProperty(WORLD_GUARD_REGION_PROPERTY);
                    System.out.println("[SettlerCraft-WorldGuard]: Removed protection from structure #"+structure.getId()+" because it was removed");
                }
            }
            
            
            tx.success();
        }
        
    }
    
}
