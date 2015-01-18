/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercrafttownapi.player.selection;

import com.chingo247.settlercrafttownapi.plugin.SettlerCraftTown;
import com.chingo247.settlercrafttownapi.world.Vector2D;
import com.gmail.filoghost.holograms.api.Hologram;
import com.gmail.filoghost.holograms.api.HolographicDisplaysAPI;
import com.sk89q.worldedit.Vector;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 *
 * @author Chingo
 */
public class HologramSelectionManager extends AbstractSelectionManager {
    
    private Map<UUID, ISelection> selections = Collections.synchronizedMap(new HashMap<UUID, ISelection>());

    @Override
    public void select(Player player, World world, Vector vector, ChatColor color) {
        ISelection selection = selections.get(player.getUniqueId());
        if(selection != null) {
            selection.clear();
        } 
        selection = new Selection(new Location(world, vector.getBlockX(), vector.getBlockY(), vector.getBlockZ()), color);
        selections.put(player.getUniqueId(), selection);
    }

    @Override
    public void selectField(Player player, World world, Vector2D pos1, Vector2D pos2, int height, ChatColor color) {
        ISelection selection = selections.get(player.getUniqueId());
        if(selection != null) {
            selection.clear();
        } 
        selection = new FieldSelection(new Location(world, pos1.getX(), height, pos1.getZ()), new Location(world, pos2.getX(), height, pos2.getZ()), color);
        selections.put(player.getUniqueId(), selection);
    }

    @Override
    public void selectRegion(Player player, World world, Vector pos1, Vector pos2, ChatColor color) {
        ISelection selection = selections.get(player.getUniqueId());
        if(selection != null) {
            selection.clear();
        } 
        selection = new FieldSelection(
                new Location(world, pos1.getBlockX(), pos1.getBlockY(), pos1.getBlockZ()), 
                new Location(world, pos2.getBlockX(), pos2.getBlockY(), pos2.getBlockZ()), color);
        selections.put(player.getUniqueId(), selection);
    }

    @Override
    public void clear(UUID uuid) {
        ISelection selection = selections.get(uuid);
        if(selection != null) {
            selection.clear();
        } 
    }
    
    private class Selection implements ISelection {
        
        private final Hologram hologram;
        
        public Selection(Location location) {
            this(location, ChatColor.BLUE);
        }

        public Selection(Location location, ChatColor color) {
            this.hologram = HolographicDisplaysAPI.createHologram(SettlerCraftTown.getInstance(), location, color + "[x]");
        }

        @Override
        public void clear() {
            hologram.delete();
        }
        
    }
    
    private class FieldSelection implements ISelection {
        
        private Hologram point1;
        private Hologram point2;

        public FieldSelection(Location point1, Location point2) {
            this(point1, point2, ChatColor.BLUE);
        }
        
        public FieldSelection(Location point1, Location point2, ChatColor color) {
            this.point1 = HolographicDisplaysAPI.createHologram(SettlerCraftTown.getInstance(), point1, color + "[x]");
            this.point2 = HolographicDisplaysAPI.createHologram(SettlerCraftTown.getInstance(), point2, color + "[x]");
        }

        @Override
        public void clear() {
            point1.delete();
            point2.delete();
        }
        
    }
    
}
