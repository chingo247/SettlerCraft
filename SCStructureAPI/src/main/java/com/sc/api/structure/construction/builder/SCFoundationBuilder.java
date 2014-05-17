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

package com.sc.api.structure.construction.builder;

import com.google.common.base.Preconditions;
import com.sc.api.structure.construction.builder.strategies.FoundationStrategy;
import com.sc.api.structure.model.structure.Structure;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Location;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Material;

/**
 *
 * @author Chingo
 */
public class SCFoundationBuilder {

    /**
     * A foundation will be created beneath the structure. A foundation is
     * doesnt have any functionality its just there to give the player some
     * feedback. And also clears the construction site from any blocks
     *
     * @param session The editsession
     * @param structure The structure
     * @param strategy The strategy
     * @param material The material to be used
     * @param autoflush Wheter or not the session needs to be flushed
     */
    public static void placeFoundation(EditSession session, Structure structure, FoundationStrategy strategy, Material material, boolean autoflush) {
        switch (strategy) {
            case DEFAULT:
                placeDefault(session, structure, material, autoflush);
                break;
//            case FANCY:
//                placeFancyFoundation(structure, material);
//                break;
            default:
                throw new UnsupportedOperationException("No action known for: " + strategy);
        }
    }

    /**
     * Placed a default foundation, default foundation will cover the entire area of a structure (only one layer)
     * @param session The editsession
     * @param structure The structure
     * @param material The material to use for the foundation
     * @param autoflush Wheter or not the session needss to be flushed
     */
    public static void placeDefault(EditSession session, Structure structure, Material material, boolean autoflush) {
        Location pos1 = structure.getDimension().getStart();
        Location pos2 = new Location(pos1.getWorld(), new BlockVector(structure.getDimension().getEndX(), 1, structure.getDimension().getEndZ()));

        try {
            CuboidRegion region = new CuboidRegion(pos1.getWorld(), pos1.getPosition(), pos2.getPosition());
            session.makeCuboidFaces(region, new BaseBlock(material.getId()));
            if (autoflush) {
                session.flushQueue();
            }
        }
        catch (MaxChangedBlocksException ex) {
            Logger.getLogger(SCFoundationBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Generates an enclosure for a clipboard, enclosures only mark the edge of a clipboard
     * @param session The session to be used
     * @param clipboard The clipboard
     * @param material The material to use for the enclosure
     * @param height The height
     * @param startY The startY
     * @return The generated enclosure as ClipBoard
     */
    public static CuboidClipboard generateEnclosure(EditSession session, CuboidClipboard clipboard, int material, int height, int startY) {
        Preconditions.checkArgument(height > 0);
        Preconditions.checkArgument(startY < height);
       
        CuboidClipboard cc = new CuboidClipboard(new BlockVector(clipboard.getWidth(), height + startY, clipboard.getLength()));

        for (int z = 0; z < cc.getLength(); z++) {
            for (int x = 0; x < cc.getWidth(); x++) {
                for (int y = startY; y < cc.getHeight(); y++) {
                    if(z % (clipboard.getLength() / 5) == 0 && (z == 0 || z == cc.getLength() - 1 || x == 0 || x == cc.getWidth() - 1)) {
                        cc.setBlock(new BlockVector(x, y, z), new BaseBlock(material));
                    }
                }
            }
        }
        return cc;
    }



}
