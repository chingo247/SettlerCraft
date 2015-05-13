/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structureapi.platforms.bukkit.services.worldguard;

import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.core.persistence.dao.settler.SettlerNode;
import com.chingo247.settlercraft.structureapi.persistence.dao.IStructureDAO;
import com.chingo247.settlercraft.structureapi.persistence.entities.structure.StructureNode;
import com.chingo247.settlercraft.structureapi.platforms.bukkit.util.WorldGuardUtil;
import com.chingo247.settlercraft.structureapi.structure.ConstructionStatus;
import com.chingo247.settlercraft.structureapi.structure.DefaultStructureFactory;
import com.chingo247.settlercraft.structureapi.structure.Structure;
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
public class WorldGuardHelper {
    
    private GraphDatabaseService graph;
    private IStructureDAO structureDAO;

    public WorldGuardHelper(GraphDatabaseService graph, IStructureDAO structureDAO) {
        this.graph = graph;
        this.structureDAO = structureDAO;
    }
    
    public String createRegionForStructure(Structure structure) {
        World world = Bukkit.getWorld(structure.getWorld());
        CuboidRegion dimension = structure.getCuboidRegion();
        
        
        // Get world guard flags
        // StructurePlan plan = structure.getStructurePlan();
        
        RegionManager mgr = WorldGuardUtil.getRegionManager(world);

        Vector p1 = dimension.getMinimumPoint();
        Vector p2 = dimension.getMaximumPoint();
        String id = "sc_stt_"+world.getName()+"_"+structure.getId();

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
            structureNode.getRawNode().setProperty("worldguard_region", id);
            tx.success();
            
        }
        return id;
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
                int index = 0;
                int total = structures.size();
                for(Structure s : structures) {
                    String region = createRegionForStructure(s);
                    index++;
                    System.out.println("Create worldguard region '" + region + "' for Structure #" + s.getId() + " ("+index+"/"+total+")");
                }
            }
        });
        
    }
    
}
