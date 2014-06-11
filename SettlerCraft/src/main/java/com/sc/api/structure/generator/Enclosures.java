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
package com.sc.api.structure.generator;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.blocks.BaseBlock;

/**
 * Enclosures should only cover the border of a structure. The function of an enclosure is to mark
 * construction areas, so that players can see that a the ground they are standing on is claimed or
 * in progress. It kinda visualize the WorldGuard region border of a structure as a structure has
 * it's own region.
 *
 * For now it will only be able to visualize a structure or cuboid. In the future it may also be
 * able to visualize worldguard borders
 *
 * @author Chingo
 */
public class Enclosures {

    /**
     * Generates an enclosure for a clipboard, enclosures only mark the edge of a clipboard
     *
     * @param clipboard The clipboard
     * @param material The material to use for the enclosure
     * @return The generated enclosure as ClipBoard
     */
    public static CuboidClipboard standard(CuboidClipboard clipboard, int material) {

        CuboidClipboard cc = new CuboidClipboard(new BlockVector(clipboard.getWidth(), 2 , clipboard.getLength()));

        for (int z = 0; z < cc.getLength(); z+= (cc.getLength() - 1)) {
            for (int x = 0; x < cc.getWidth(); x++) {
                for (int y = 1; y < 2; y++) {
                    cc.setBlock(new BlockVector(x, y, z), new BaseBlock(material));
                }
            }
        }
        
        for(int z = 1; z < cc.getLength() - 1; z++) {
            for(int x = 0; x < cc.getWidth(); x += (cc.getWidth()-1)) {
               for (int y = 1; y < 2; y++) {
                    cc.setBlock(new BlockVector(x, y, z), new BaseBlock(material));
                } 
            }
        }
        
        return cc;
    }

}
