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

package com.sc.api.structure.construction.builder.async;

import com.sc.api.structure.construction.builder.SCCuboidBuilder;
import com.sc.api.structure.construction.builder.strategies.PlaceLayeredStrategy;
import com.sc.api.structure.model.world.SimpleCardinal;
import com.sc.api.structure.util.plugins.AsyncWorldEditUtil;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Location;
import com.sk89q.worldedit.MaxChangedBlocksException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSession;

/**
 * Performs all CuboidBuilder operations Async
 * @author Chingo
 */
public class SCAsyncCuboidBuilder {

    
    public static void clear(AsyncEditSession asyncEditSession, Location target, SimpleCardinal cardinal, CuboidClipboard clipboard) throws MaxChangedBlocksException {
        SCCuboidBuilder.clear(asyncEditSession, target, cardinal, clipboard);
    }

    public static void clear(AsyncEditSession asyncEditSession, Location pos1, Location pos2) throws MaxChangedBlocksException {
        SCCuboidBuilder.clear(asyncEditSession, pos1, pos2);
    }

    public static void place(AsyncEditSession editSession, CuboidClipboard cuboidClipboard, Location target, SimpleCardinal cardinal, String jobName) throws MaxChangedBlocksException {
        SCCuboidBuilder.align(cuboidClipboard, target, cardinal);
        cuboidClipboard.place(editSession, target.getPosition(), true);
    }

    public static void place(Player player, CuboidClipboard cuboidClipboard, Location target, SimpleCardinal cardinal, String jobName) {
        try {
            place(AsyncWorldEditUtil.createAsyncEditSession(player, -1), cuboidClipboard, target, cardinal, jobName);
        }
        catch (MaxChangedBlocksException ex) {
            Logger.getLogger(SCAsyncCuboidBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void placeLayer(Player player, CuboidClipboard clipboard, int layer, Location location, SimpleCardinal direction) throws MaxChangedBlocksException {
        SCCuboidBuilder.place(AsyncWorldEditUtil.createAsyncEditSession(player, -1), clipboard, location, direction);
    }

    public static void placeLayer(AsyncEditSession asyncEditSession, CuboidClipboard clipboard, int layer, Location location, SimpleCardinal direction) throws MaxChangedBlocksException {
        SCCuboidBuilder.placeLayer(asyncEditSession, clipboard, layer, location, direction);
    }

    public static void placeLayered(AsyncEditSession asyncEditSession, CuboidClipboard whole, Location location, SimpleCardinal cardinal, String jobName, SCJobCallback callback) throws MaxChangedBlocksException {
        Location target = SCCuboidBuilder.align(whole, location, cardinal);
        SCSmartClipboard smartClipboard = new SCSmartClipboard(whole, new PlaceLayeredStrategy());
        SCAsyncCuboidClipboard asyncCuboidClipboard = new SCAsyncCuboidClipboard(asyncEditSession.getPlayer(), smartClipboard);
        asyncCuboidClipboard.place(asyncEditSession, target.getPosition(), true, callback);
    }
}
