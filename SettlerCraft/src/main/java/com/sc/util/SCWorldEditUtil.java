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
package com.sc.util;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.schematic.SchematicFormat;
import com.sk89q.worldedit.world.World;
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
public class SCWorldEditUtil {

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

    public static World getWorld(Player player) {
        return getLocalPlayer(player).getWorld();
    }
    
    public static World getWorld(String world) {
        List<? extends World> worlds = WorldEdit.getInstance().getServer().getWorlds();
        for(int i = 0; i < worlds.size(); i++) {
            World w = worlds.get(i);
            if(w.getName().equals(world)) {
                return w;
            }
        }
        return null;
    }

    public static void select(Player player, Vector pos1, Vector pos2) {
        Actor localPlayer = getLocalPlayer(player);
        World world = localPlayer.getWorld();
        LocalSession session = getLocalSession(player);

//            Vector pos = getBlockWorldVector(location);
//            Vector pos2 = pos.add(clipboard.getSize().subtract(1,1,1));
        session.setRegionSelector(world, new com.sk89q.worldedit.regions.selector.CuboidRegionSelector(world, pos1, pos2));
        session.getRegionSelector(world).learnChanges();
        session.getRegionSelector(world).explainRegionAdjust(localPlayer, session);

    }

}
