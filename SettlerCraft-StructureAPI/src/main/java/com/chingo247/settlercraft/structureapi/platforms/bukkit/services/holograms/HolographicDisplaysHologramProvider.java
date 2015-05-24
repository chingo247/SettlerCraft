/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structureapi.platforms.bukkit.services.holograms;

import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.structureapi.persistence.entities.features.hologram.StructureHologramDAO;
import com.chingo247.settlercraft.structureapi.platforms.services.holograms.Hologram;
import com.chingo247.settlercraft.structureapi.platforms.services.holograms.HologramsProvider;
import com.chingo247.xplatform.core.APlatform;
import com.chingo247.xplatform.core.IPlugin;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.world.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 *
 * @author Chingo
 */
public class HolographicDisplaysHologramProvider implements HologramsProvider {

    private final GraphDatabaseService graph;
    private final StructureHologramDAO structureHologramDAO;

    public HolographicDisplaysHologramProvider(GraphDatabaseService graph, APlatform platform, IPlugin plugin) {
        this.graph = graph;
        this.structureHologramDAO = new StructureHologramDAO(graph);
    }

    @Override
    public Hologram createHologram(String plugin, World world, Vector position) {
        Plugin p = Bukkit.getPluginManager().getPlugin(plugin);
        if (p == null) {
            throw new RuntimeException("Can't find plugin '" + plugin + "'");
        }

        org.bukkit.World w = Bukkit.getWorld(world.getName());
        com.gmail.filoghost.holographicdisplays.api.Hologram holo = HologramsAPI.createHologram(p, new Location(w, position.getBlockX(), position.getBlockY(), position.getBlockZ()));
        return new HolographicDisplaysHologram(holo);
    }

    @Override
    public void initialize() {
        StructureHologramManager.getInstance().setHologramProvider(this);
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
