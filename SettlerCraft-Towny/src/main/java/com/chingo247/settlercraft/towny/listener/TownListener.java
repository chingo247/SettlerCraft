/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.towny.listener;

import com.chingo247.settlercraft.structureapi.model.interfaces.IStructureWorld;
import com.chingo247.settlercraft.structureapi.model.owner.StructureOwnerNode;
import com.chingo247.settlercraft.structureapi.model.structure.StructureNode;
import com.chingo247.settlercraft.structureapi.model.structure.StructureStatus;
import com.chingo247.settlercraft.structureapi.model.world.StructureWorldRepository;
import com.chingo247.settlercraft.towny.plugin.SettlerCraftTowny;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.event.TownUnclaimEvent;
import com.palmergames.bukkit.towny.object.WorldCoord;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.Vector2D;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.util.List;
import java.util.concurrent.ExecutorService;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author Chingo
 */
public class TownListener implements Listener {

    private final GraphDatabaseService graph;
    private final ExecutorService executor;
    private final StructureWorldRepository worldRepository;

    public TownListener(GraphDatabaseService graph, ExecutorService executor) {
        this.graph = graph;
        this.executor = executor;
        this.worldRepository = new StructureWorldRepository(graph);
    }

  
    @EventHandler
    public void onTownDelete(TownUnclaimEvent unclaimEvent) {
        removeStructuresFromCell(unclaimEvent.getWorldCoord() , ", because plot has been unclaimed!");
    }

    private void removeStructuresFromCell(WorldCoord t, final String reason) {
        int blockSize = TownySettings.getTownBlockSize();
        Vector2D vector = SettlerCraftTowny.translate(t);

        Vector min = new Vector(vector.getBlockX(), 0, vector.getBlockZ());
        Vector max = min.add(blockSize, 256, blockSize);

        final CuboidRegion region = new CuboidRegion(min, max);
        final World w = Bukkit.getWorld(t.getWorldName());

        executor.execute(new Runnable() {

            @Override
            public void run() {
                try (Transaction tx = graph.beginTx()) {
                    IStructureWorld sw = worldRepository.findByUUID(w.getUID());
                    List<StructureNode> sns = sw.getStructuresWithin(region, -1);


                    for (StructureNode sn : sns) {
                        if (sn.getStatus() != StructureStatus.REMOVED) {
                            sn.setStatus(StructureStatus.REMOVED);
                            for (StructureOwnerNode owner : sn.getOwners()) {
                                Player player = Bukkit.getPlayer(owner.getUUID());
                                player.sendMessage("Structure " + sn.getName() + " has been removed, " + reason);
                            }
                        }
                    }

                    tx.success();
                }
            }
        });
    }

}
