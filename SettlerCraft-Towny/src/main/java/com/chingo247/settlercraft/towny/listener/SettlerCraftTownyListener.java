/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.towny.listener;

import com.chingo247.settlercraft.structureapi.persistence.dao.StructureDAO;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.event.PlotClearEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.neo4j.graphdb.GraphDatabaseService;


/**
 *
 * @author Chingo
 */
public class SettlerCraftTownyListener implements Listener {
    
    private StructureDAO structureDAO;
    private GraphDatabaseService graph;
    private int size;

    public SettlerCraftTownyListener(GraphDatabaseService graph) {
        this.graph = graph;
        this.structureDAO = new StructureDAO(graph);
        this.size = TownySettings.getTownBlockSize();
    }
    
    
    
    @EventHandler
    public void onPlotCleared(PlotClearEvent event) {
//        List<StructureNode> structures;
//        try(Transaction tx = graph.beginTx()) {
//            
//            TownBlock block = event.getTownBlock();
//            TownyWorld world = block.getWorld();
//            
//            World w = SettlerCraft.getInstance().getWorld(world.getName());
//            Vector start = new Vector(block.getX(), 0, block.getZ());
//            Vector end = new Vector(start.getBlockX() + size, 128, start.getBlockZ() + size);
//            CuboidRegion region = new CuboidRegion(start, end);
//            
//            structures = structureDAO.getStructuresWithin(w, region, Integer.MAX_VALUE);
//            
//            tx.success();
//        }
        
        
    }
    
}
