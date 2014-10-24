/*
 * Copyright (C) 2014 Chingo247
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

package com.chingo247.settlercraft.structure.construction.worldedit;

import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.world.World;
import java.util.List;
import org.bukkit.entity.Player;
import org.primesoft.asyncworldedit.AsyncWorldEditMain;

/**
 *
 * @author Chingo
 */
public class WorldEditUtil {

    private WorldEditUtil() {
    }

    public static World getWorld(String world) {
        List<? extends World> worlds = WorldEdit.getInstance().getServer().getWorlds();
        for (World w : worlds) {
            if (w.getName().equals(world)) {

                return w;
            }
        }
        return null;
    }

    public static LocalPlayer getLocalPlayer(Player player) {
        return getWorldEditPlugin().wrapPlayer(player);
    }

    public static WorldEditPlugin getWorldEditPlugin() {
        return AsyncWorldEditMain.getWorldEdit(AsyncWorldEditMain.getInstance());
    }

}
