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
package com.sc.structure.sync;

import com.sc.structure.sync.SyncPlaceTask;
import com.sc.plugin.SettlerCraft;
import com.sc.structure.sync.SyncPlaceTask.PlaceCallback;
import com.sc.structure.entity.world.SimpleCardinal;
import com.sc.util.Ticks;
import com.sc.util.WorldUtil;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Location;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author Chingo
 */
public class SyncBuilder {

    /**
     * Clears an area within the cliboards target area
     *
     * @param editSession The editSession
     * @param cardinal The cardinal
     * @param target The target location
     * @param cuboidClipboard The cuboidClipboard
     * @throws MaxChangedBlocksException
     */
    public static void clear(EditSession editSession, Location target, SimpleCardinal cardinal, CuboidClipboard cuboidClipboard) throws MaxChangedBlocksException {
        Location pos2 = WorldUtil.getPos2(target, cardinal, cuboidClipboard);
        clear(editSession, pos2, pos2);
    }

    /**
     * Clears an area between two points
     *
     * @param editSession The editsession
     * @param pos1 The first location
     * @param pos2 The secondary location
     * @throws MaxChangedBlocksException
     */
    public static void clear(EditSession editSession, Location pos1, Location pos2) throws MaxChangedBlocksException {
        editSession.setBlocks(new CuboidRegion(pos1.getPosition(), pos2.getPosition()), new BaseBlock(0));
        editSession.flushQueue();
    }
    
    public static BukkitTask placeBuffered(final EditSession editSession, 
            final CuboidClipboard whole, int blocksPerSecond, final Location location, PlaceCallback callback) {
         SyncPlaceTask task = new SyncPlaceTask(editSession, whole, location, blocksPerSecond, callback);
         return task.runTaskTimer(SettlerCraft.getSettlerCraft(), Ticks.ONE_SECOND, Ticks.ONE_SECOND);
    }

    public static BukkitTask placeLayered(final EditSession editSession, final CuboidClipboard whole, final Location location, PlaceCallback callback) {
        return placeBuffered(editSession, whole, whole.getLength() * whole.getWidth(), location, callback);
    }



}
