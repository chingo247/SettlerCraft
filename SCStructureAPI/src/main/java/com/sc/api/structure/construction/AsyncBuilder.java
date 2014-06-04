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
package com.sc.api.structure.construction;

import com.sc.api.structure.construction.async.SCAsyncCuboidClipboard;
import com.sc.api.structure.construction.async.SCJobCallback;
import com.sc.api.structure.construction.progress.ConstructionStrategyType;
import com.sc.api.structure.entity.world.SimpleCardinal;
import com.sc.api.structure.util.plugins.SCAsyncWorldEditUtil;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Location;
import com.sk89q.worldedit.MaxChangedBlocksException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSession;

/**
 *
 * @author Chingo
 */
public class AsyncBuilder {

    public static void clear(AsyncEditSession asyncEditSession, Location target, SimpleCardinal cardinal, CuboidClipboard clipboard) throws MaxChangedBlocksException {
        SyncBuilder.clear(asyncEditSession, target, cardinal, clipboard);
    }

    public static void clear(AsyncEditSession asyncEditSession, Location pos1, Location pos2) throws MaxChangedBlocksException {
        SyncBuilder.clear(asyncEditSession, pos1, pos2);
    }

    public static void place(AsyncEditSession editSession, CuboidClipboard cuboidClipboard, Location target, SimpleCardinal cardinal, String jobName) throws MaxChangedBlocksException {
        Location t = ConstructionManager.align(cuboidClipboard, target, cardinal);
        cuboidClipboard.place(editSession, t.getPosition(), true);
    }

    public static void place(Player player, CuboidClipboard cuboidClipboard, Location target, SimpleCardinal cardinal, String jobName) {
        try {
            place(SCAsyncWorldEditUtil.createAsyncEditSession(player, -1), cuboidClipboard, target, cardinal, jobName);
        } catch (MaxChangedBlocksException ex) {
            Logger.getLogger(AsyncBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void placeLayer(Player player, CuboidClipboard clipboard, int layer, Location location, SimpleCardinal direction) throws MaxChangedBlocksException {
        SyncBuilder.place(SCAsyncWorldEditUtil.createAsyncEditSession(player, -1), clipboard, location, direction);
    }

    public static void placeLayer(AsyncEditSession asyncEditSession, CuboidClipboard clipboard, int layer, Location location, SimpleCardinal direction) throws MaxChangedBlocksException {
        SyncBuilder.placeLayer(asyncEditSession, clipboard, layer, location, direction);
    }

    public static void placeLayered(AsyncEditSession asyncEditSession, CuboidClipboard whole, Location location, SimpleCardinal cardinal, String jobName, SCJobCallback callback) throws MaxChangedBlocksException {
        Location t = ConstructionManager.align(whole, location, cardinal);
        SmartClipBoard smartClipboard = new SmartClipBoard(whole, ConstructionStrategyType.LAYERED, false);
        SCAsyncCuboidClipboard asyncCuboidClipboard = new SCAsyncCuboidClipboard(asyncEditSession.getPlayer(), smartClipboard);
        asyncCuboidClipboard.place(asyncEditSession, t.getPosition(), false, callback);
    }

    
    


    
}
