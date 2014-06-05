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
package com.sc.api.structure.construction.async;

import com.sc.api.structure.construction.SyncBuilder;
import com.sc.api.structure.construction.progress.ConstructionStrategyType;
import com.sc.api.structure.entity.world.SimpleCardinal;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Location;
import com.sk89q.worldedit.MaxChangedBlocksException;
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

    /**
     * Aligns target clipboard to speficied direction, assuming that the initial state is pointed to
     * EAST (entrance to the west)
     *
     * @param clipboard
     * @param location
     * @param direction
     * @return The new target location
     */
    public static Location align(final CuboidClipboard clipboard, Location location, SimpleCardinal direction) {
        switch (direction) {
            case EAST:
                return location;
            case SOUTH:
                clipboard.rotate2D(90);
                return location.add(new BlockVector(-(clipboard.getWidth() - 1), 0, 0));
            case WEST:
                clipboard.rotate2D(180);
                return location.add(new BlockVector(-(clipboard.getWidth() - 1), 0, -(clipboard.getLength() - 1)));
            case NORTH:
                clipboard.rotate2D(270);
                return location.add(new BlockVector(0, 0, -(clipboard.getLength() - 1)));
            default:
                throw new AssertionError("unreachable");
        }
    }

    public static void place(AsyncEditSession asyncEditSession, CuboidClipboard whole, Location location, SimpleCardinal cardinal) throws MaxChangedBlocksException {
        align(whole, location, cardinal);
        SmartClipBoard smartClipboard = new SmartClipBoard(whole, ConstructionStrategyType.LAYERED, false);
        SCAsyncCuboidClipboard asyncCuboidClipboard = new SCAsyncCuboidClipboard(asyncEditSession.getPlayer(), smartClipboard);
        asyncCuboidClipboard.place(asyncEditSession, location.getPosition(), false);
    }

}
