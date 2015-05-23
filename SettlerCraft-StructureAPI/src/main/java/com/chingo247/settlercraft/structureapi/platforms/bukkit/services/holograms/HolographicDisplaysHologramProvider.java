/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structureapi.platforms.bukkit.services.holograms;

import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.structureapi.persistence.entities.features.hologram.StructureHologram;
import com.chingo247.settlercraft.structureapi.persistence.entities.features.hologram.StructureHologramDAO;
import com.chingo247.settlercraft.structureapi.persistence.entities.features.hologram.StructureHologramFactory;
import com.chingo247.settlercraft.structureapi.persistence.entities.features.hologram.StructureHologramNode;
import com.chingo247.settlercraft.structureapi.persistence.entities.structure.StructureNode;
import com.chingo247.settlercraft.structureapi.platforms.services.holograms.Hologram;
import com.chingo247.settlercraft.structureapi.platforms.services.holograms.HologramsProvider;
import com.chingo247.settlercraft.structureapi.structure.DefaultStructureFactory;
import com.chingo247.settlercraft.structureapi.structure.Structure;
import com.chingo247.xplatform.core.APlatform;
import com.chingo247.xplatform.core.IPlugin;
import com.chingo247.xplatform.core.IScheduler;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.world.World;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.util.com.google.common.collect.Lists;
import net.minecraft.util.com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author Chingo
 */
public class HolographicDisplaysHologramProvider implements HologramsProvider {

    private final GraphDatabaseService graph;
    private final StructureHologramDAO structureHologramDAO;
    private final IScheduler scheduler;
    private final IPlugin plugin;

    public HolographicDisplaysHologramProvider(GraphDatabaseService graph, APlatform platform, IPlugin plugin) {
        this.graph = graph;
        this.structureHologramDAO = new StructureHologramDAO(graph);
        this.scheduler = platform.getServer().getScheduler(plugin);
        this.plugin = plugin;
    }

    @Override
    public Hologram createHologram(String plugin, World world, Vector position) {
        Plugin p = Bukkit.getPluginManager().getPlugin(plugin);
        if (p == null) {
            throw new RuntimeException("Can't find plugin '" + plugin + "'");
        }

        System.out.println("Placing hologram at " + position);

        org.bukkit.World w = Bukkit.getWorld(world.getName());
        com.gmail.filoghost.holographicdisplays.api.Hologram holo = HologramsAPI.createHologram(p, new Location(w, position.getBlockX(), position.getBlockY(), position.getBlockZ()));
        return new HolographicDisplaysHologram(holo);
    }

    @Override
    public void initialize() {
        StructureHologramManager.getInstance().setHologramProvider(this);
        invalidate();
        setupUnchecked();
        initHolos();
    }

    private void initHolos() {
        final Queue<StructureHologram> holograms = new LinkedList<>();
        try (Transaction tx = graph.beginTx()) {
            List<StructureHologramNode> hologramNodes = structureHologramDAO.findAll();

            StructureHologramFactory hologramFactory = new StructureHologramFactory();

            for (StructureHologramNode shn : hologramNodes) {
                if (shn.getStructure() != null) {
                    holograms.add(hologramFactory.makeStructureHologram(shn));
                } else {
                    System.out.println("Structure was null!a");
                }
            }

            tx.success();
        }

        int count = 0;
        while (holograms.peek() != null) {
            if (count % 100 == 0) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(HolographicDisplaysHologramProvider.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            final StructureHologram hologram = holograms.poll();
            final Structure structure = hologram.getStructure();
            final Vector position = structure.translateRelativeLocation(new Vector(hologram.getRelativeX(), hologram.getRelativeY(), hologram.getRelativeZ()));
            final World w = SettlerCraft.getInstance().getWorld(hologram.getStructure().getWorld());

            scheduler.runSync(new Runnable() {

                @Override
                public void run() {
                    Hologram hologram = createHologram(plugin.getName(), w, position, structure);
                    
                }
            });
            count++;
        }

    }

    private void setupUnchecked() {

        // Find unchecked structures without holos
        List<Structure> structures = Lists.newArrayList();
        try (Transaction tx = graph.beginTx()) {

            Map<String, Object> params = Maps.newHashMap();
            params.put("checkedHolograms", true);

            String query = "MATCH (s:" + StructureNode.LABEL.name() + ") "
                    + "WHERE NOT s." + StructureNode.CHECKED_HOLOGRAM_PROPERTY + " = {checkedHolograms} "
                    + "RETURN s";

            Result r = graph.execute(query, params);

            while (r.hasNext()) {
                Map<String, Object> map = r.next();
                for (Object o : map.values()) {
                    Node sn = (Node) o;
                    StructureNode structureNode = new StructureNode(sn);
                    structures.add(DefaultStructureFactory.getInstance().makeStructure(structureNode));
                }
            }
            tx.success();
        }

        try (Transaction tx = graph.beginTx()) {

            for (Structure structure : structures) {
                World w = SettlerCraft.getInstance().getWorld(structure.getWorld());
                Vector position = structure.translateRelativeLocation(Vector.ZERO.add(0, 2, 0));
                createWithoutTransactionHologram(plugin.getName(), w, position, structure);
            }
            tx.success();
        }

    }

    @Override
    public Hologram createHologram(String plugin, World world, Vector position, Structure structure) {
        Hologram h;
        try (Transaction tx = graph.beginTx()) {
            h = createWithoutTransactionHologram(plugin, world, position, structure);
            tx.success();
        }
        StructureHologramManager.getInstance().registerStructureHologram(structure, h);
        
        return h;
    }

    private Hologram createWithoutTransactionHologram(String plugin, World world, Vector position, Structure structure) {
        structureHologramDAO.addHologram(structure, structure.getRelativePosition(position));
        return createHologram(plugin, world, position);
    }

    private void invalidate() {
        System.out.println("Invalidating structure-holograms");
        try (Transaction tx = graph.beginTx()) {
            int count = 0;
            String query = "MATCH (h:Hologram)<-[r:" + StructureHologramNode.RELATION_HAS_HOLOGRAM + "]-(:" + StructureNode.LABEL.name() + ") "
                    + "WHERE r is null "
                    + "RETURN h";

            Result r = graph.execute(query);
            while (r.hasNext()) {
                Map<String, Object> map = r.next();
                for (Object o : map.values()) {
                    Node n = (Node) o;
                    for (Relationship rel : n.getRelationships()) {
                        rel.delete();
                    }
                    n.delete();
                    count++;
                }
            }

            tx.success();
            System.out.println("Invalidated a total of " + count + " holograms");
        }
    }

    private class HolographicDisplaysHologram implements Hologram {

        private final com.gmail.filoghost.holographicdisplays.api.Hologram holo;

        public HolographicDisplaysHologram(com.gmail.filoghost.holographicdisplays.api.Hologram holo) {
            this.holo = holo;
        }

        @Override
        public void insertLine(int i, String s) {
            holo.insertTextLine(i, s);
        }

        @Override
        public void addLine(String s) {
            holo.appendTextLine(s);
        }

        @Override
        public void removeLine(int i) {
            holo.removeLine(i);
        }

        @Override
        public Vector getPosition() {
            Location location = holo.getLocation();
            return new Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        }

        @Override
        public World getWorld() {
            return SettlerCraft.getInstance().getWorld(holo.getWorld().getName());
        }

        @Override
        public void delete() {
            holo.delete();
        }

    }

}
