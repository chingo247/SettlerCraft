/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structureapi.platforms.bukkit.services.worldguard;

import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.core.event.EventManager;
import com.chingo247.settlercraft.core.persistence.dao.settler.SettlerNode;
import com.chingo247.settlercraft.structureapi.persistence.dao.IStructureDAO;
import com.chingo247.settlercraft.structureapi.persistence.entities.structure.StructureNode;
import com.chingo247.settlercraft.structureapi.platforms.bukkit.structure.restriction.WorldGuardRestriction;
import com.chingo247.settlercraft.structureapi.platforms.bukkit.util.WorldGuardUtil;
import com.chingo247.settlercraft.structureapi.platforms.services.protection.IStructureProtector;
import com.chingo247.settlercraft.structureapi.structure.ConstructionStatus;
import com.chingo247.settlercraft.structureapi.structure.DefaultStructureFactory;
import com.chingo247.settlercraft.structureapi.structure.Structure;
import com.chingo247.settlercraft.structureapi.structure.StructureAPI;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.util.com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
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
    private StructureAPI structureAPI;

    public WorldGuardHelper(GraphDatabaseService graph, IStructureDAO structureDAO, StructureAPI structureAPI) {
        this.graph = graph;
        this.structureDAO = structureDAO;
        this.structureAPI = structureAPI;
    }
    
    @Override
    public void protect(Structure structure) {
        World world = Bukkit.getWorld(structure.getWorld());
        CuboidRegion dimension = structure.getCuboidRegion();
        
        
        // Get world guard flags
        // StructurePlan plan = structure.getStructurePlan();
        
        RegionManager mgr = WorldGuardUtil.getRegionManager(world);

        Vector p1 = dimension.getMinimumPoint();
        Vector p2 = dimension.getMaximumPoint();
        String id = getRegionId(structure);

        if (WorldGuardUtil.regionExists(world, id)) {
            mgr.removeRegion(id);
        }
        
        ProtectedCuboidRegion region = new ProtectedCuboidRegion(
                id, 
                new BlockVector(p1.getBlockX(), p1.getBlockY(), p1.getBlockZ()), 
                new BlockVector(p2.getBlockX(), p2.getBlockY(), p2.getBlockZ())
        );
        
        try(Transaction tx = graph.beginTx()) {
            SettlerNode settler = structureDAO.getMasterOwnerForStructure(structure.getId());
            
            if(settler != null) {
                LocalPlayer localPlayer = WorldGuardUtil.getWorldGuard().wrapPlayer(Bukkit.getPlayer(settler.getId()));
                region.getOwners().addPlayer(localPlayer);
            }
            mgr.addRegion(region);
            try {
                mgr.save();
            } catch (StorageException ex) {
                Logger.getLogger(WorldGuardStructureListener.class.getName()).log(Level.SEVERE, null, ex);
                tx.failure();
            }
            
            StructureNode structureNode = structureDAO.find(structure.getId());
            structureNode.getRawNode().setProperty(WORLD_GUARD_REGION_PROPERTY, id);
            tx.success();
            
        }
    }
    
    private String getRegionId(Structure structure) {
        return PREFIX+structure.getId();
    }
    
    public void processStructuresWithoutRegion() {
        final List<Structure> structures = Lists.newArrayList();
        try(Transaction tx = graph.beginTx()) {
            
            String query = "MATCH(s:" + StructureNode.LABEL.name() + ") "
                    + "WHERE s.worldguard_region IS NULL "
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
                    System.out.println("Protected structure #" + s.getId() + " with 'WorldGuard'");
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
        RegionManager mgr = WorldGuardUtil.getRegionManager(world);
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
        RegionManager mgr = WorldGuardUtil.getRegionManager(world);
        String region = getRegionId(structure);
        return mgr.hasRegion(region);
    }

    @Override
    public void initialize() {
        EventManager.getInstance().getEventBus().register(new WorldGuardStructureListener(this));
        StructureAPI.getInstance().addRestriction(new WorldGuardRestriction());
        processStructuresWithoutRegion();
        structureAPI.addStructureProtector(this);
    }
    
}
