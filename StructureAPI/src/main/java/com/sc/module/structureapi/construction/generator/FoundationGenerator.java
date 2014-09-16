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
package construction.generator;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.blocks.BaseBlock;

/**
 * Foundation generator can be used to generate a foundation for a specific Structure or
 * CuboidClipboard.
 *
 * @author Chingo
 */
public class FoundationGenerator {

    /**
     * Uses a very simple algorithm to generates a clipboard with a height of 1 block with the
     * length and with of the given clipboard
     *
     * @param clipboard The clipboard to generate the foundation for (remains untouched)
     * @param material The material (for ease use worldedit's
     * {@link com.sk89q.worldedit.blocks.BlockID})
     * @return The foundation as CuboidClipboard
     */
    public static CuboidClipboard generateFoundation(CuboidClipboard clipboard, int material) {

        CuboidClipboard cc = new CuboidClipboard(new BlockVector(clipboard.getWidth(), 1, clipboard.getLength()));

        for (int z = 0; z < cc.getLength(); z++) {
            for (int x = 0; x < cc.getWidth(); x++) {
                cc.setBlock(new BlockVector(x, 0, z), new BaseBlock(material));
            }
        }
        return cc;
    }

}
