/*
 * Copyright (C) 2014 Chingo
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
package com.sc.api.structure.util.plugins;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.Location;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.regions.CuboidRegionSelector;
import com.sk89q.worldedit.schematic.SchematicFormat;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * This class contains worldedit methods that will be frequently used, feel free
 * to copy, modify, use it for your own purposes
 *
 * @author Chingo
 */
public class WorldEditUtil {
    
    public static Location getLocation(org.bukkit.Location location) {
        return new Location(getLocalWorld(location.getWorld().getName()),new BlockVector(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
    }

    public static CuboidClipboard load(File SchematicFile) throws FileNotFoundException, DataException, IOException {
        SchematicFormat format = SchematicFormat.getFormat(SchematicFile);
        return format.load(SchematicFile);
    }

    public static WorldEditPlugin getWorldEditPlugin() {
        return (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
    }
    
    public static EditSession createEditSession(Player player, int maxblocks) {
        return getWorldEditPlugin().createEditSession(player);
    }
    
    public static EditSession getEditSession(LocalWorld world, int maxblocks) {
        return WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, maxblocks);
    }

    public static LocalSession getLocalSession(Player player) {
        return getWorldEditPlugin().getSession(player);
    }

    public static LocalPlayer getLocalPlayer(Player player) {
        return getWorldEditPlugin().wrapPlayer(player);
    }

    public static LocalWorld getLocalWorld(Player player) {
        return getLocalPlayer(player).getWorld();
    }
    
    public static LocalWorld getLocalWorld(String world) {
        List<LocalWorld> worlds = getWorldEditPlugin().getWorldEdit().getServer().getWorlds();
        for(LocalWorld lw : worlds) {
            if(lw.getName().equals(world)) {
                return lw;
            }
        }
        return null;
    }

    public static void selectClipboardArea(Player player, Location pos1, Location pos2) {
            LocalPlayer localPlayer = getLocalPlayer(player);
            LocalWorld world = localPlayer.getWorld();
            LocalSession session = getLocalSession(player);
            
//            Vector pos = getBlockWorldVector(location);
//            Vector pos2 = pos.add(clipboard.getSize().subtract(1,1,1));
            
            session.setRegionSelector(world, new CuboidRegionSelector(world, pos1.getPosition(), pos2.getPosition()));
            session.getRegionSelector(world).learnChanges();
            session.getRegionSelector(world).explainRegionAdjust(localPlayer, session);
            
            
    }

}
