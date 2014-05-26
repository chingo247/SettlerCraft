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
package com.sc.api.structure;

import com.sc.api.structure.entity.world.SimpleCardinal;
import com.sc.api.structure.util.CuboidUtil;
import com.sc.api.structure.util.WorldUtil;
import com.sc.api.structure.util.plugins.SCWorldEditUtil;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Location;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        Location pos2 = WorldUtil.calculateEndLocation(target, cardinal, cuboidClipboard);
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

    /**
     * Aligns target clipboard to speficied direction, assuming that the initial
     * state is pointed to EAST (entrance to the west)
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
    


    /**
     * Aligns clipboard to direction and pastes it on target location
     *
     * @param editSession The editSession
     * @param clipboard The clipboard
     * @param target The target location
     * @param cardinal The cardinal
     */
    public static void place(EditSession editSession, CuboidClipboard clipboard, Location target, SimpleCardinal cardinal) throws MaxChangedBlocksException {
        Location t = align(clipboard, target, cardinal);
        clipboard.paste(editSession, t.getPosition(), true);
    }

    /**
     * Aligns the clipboard to given cardinal and creates a session for infinite
     * blocks and places a CuboidClipBoard instantly aligned to direction
     *
     * @param cuboidClipboard The cuboidclipboard
     * @param target The target location
     * @param cardinal The direction
     */
    public static void place(CuboidClipboard cuboidClipboard, Location target, SimpleCardinal cardinal) {
        Location t = align(cuboidClipboard, target, cardinal);
        try {
            SyncBuilder.place(SCWorldEditUtil.getEditSession(t.getWorld(), -1), cuboidClipboard, t, cardinal);
        } catch (MaxChangedBlocksException ex) {
            Logger.getLogger(SyncBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Aligns the clipboard to given cardinal and creates a session for infinite
     * blocks and places a specified layer of a cuboid at target location,
     *
     * @param whole The whole cuboidClipBoard
     * @param layer The layer, must be between 0 and height
     * @param location The target location
     * @param cardinal The cardinal
     */
    public static void placeLayer(CuboidClipboard whole, int layer, Location location, SimpleCardinal cardinal) {
        try {
            placeLayer(SCWorldEditUtil.getEditSession(location.getWorld(), -1), whole, layer, location, cardinal);
        } catch (MaxChangedBlocksException ex) {
            Logger.getLogger(SyncBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Aligns the Clipboard to given cardinal and uses the specified session to
     * place a layer of a cuboid at target location
     *
     * @param editSession
     * @param whole The whole cuboidClipBoard
     * @param layer The layer, must be between 0 and height
     * @param target The target location
     * @param cardinal The cardinal
     * @throws com.sk89q.worldedit.MaxChangedBlocksException
     */
    public static void placeLayer(EditSession editSession, CuboidClipboard whole, int layer, Location target, SimpleCardinal cardinal) throws MaxChangedBlocksException {
        CuboidClipboard layerClip = CuboidUtil.getLayer(whole, layer);
        Location t = align(layerClip, target, cardinal);
        SyncBuilder.place(editSession, layerClip, t, cardinal);
    }

    /**
     * Aligns the clipboard to given cardinal and places a cuboidClipBoard in
     * layers. A runnable is used to fire the placement of each layer at a
     * certain interval
     *
     * @param editSession The editsession
     * @param whole The complete clipboard
     * @param target The target location
     * @param cardinal The cardinal
     * @param interval The interval at which layers will be placed
     */
    public static void placeLayered(EditSession editSession, CuboidClipboard whole, Location target, SimpleCardinal cardinal, int interval) {
        Location t = align(whole, target, cardinal);
        placeLayered(editSession, whole, CuboidUtil.getLayers(whole), t, interval, 0);
    }

    private static void placeLayered(final EditSession editSession, final CuboidClipboard whole, final List<CuboidClipboard> all, final Location location, final int delayBetweenLayers, final int index) {
        try {
            all.get(index).paste(editSession, location.getPosition().add(new BlockVector(0, 1, 0)), true);

        } catch (MaxChangedBlocksException ex) {
            Logger.getLogger(SyncBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
        final int next = index + 1;
        if (next < all.size()) {
            placeLayered(editSession, whole, all, location, delayBetweenLayers, next);
        }
    }

}
