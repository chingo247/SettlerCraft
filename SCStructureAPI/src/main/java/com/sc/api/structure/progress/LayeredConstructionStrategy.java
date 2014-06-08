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
package com.sc.api.structure.progress;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BlockType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Chingo
 */
public class LayeredConstructionStrategy extends ConstructionStrategy {

    @Override
    public List<Vector> getList(CuboidClipboard cliboard, boolean noAir) {
         List<Vector> placeFirst = new ArrayList<>();

        for (int y = 0; y < cliboard.getHeight(); y++) {
            List<Vector> placeLater = new ArrayList<>();
            List<Vector> placeFinal = new ArrayList<>();
            List<Vector> water = new ArrayList<>();
            List<Vector> lava = new ArrayList<>();
            for (int x = 0; x < cliboard.getWidth(); x++) {
                for (int z = 0; z < cliboard.getLength(); z++) {
                    final BlockVector v = new BlockVector(x, y, z);
                    BaseBlock b = cliboard.getBlock(v);

                    if (b == null || (noAir && b.isAir())) {
                        continue;
                    }

                    if (isWater(b)) {
                        water.add(v);
                    } else if (isLava(b)) {
                        lava.add(v);
                    } else if (BlockType.shouldPlaceLast(b.getId())) {
                        placeLater.add(v);
                    } else if (BlockType.shouldPlaceFinal(b.getId())) {
                        placeFinal.add(v);
                    } else {
                        placeFirst.add(v);
                    }
                }
            }
            placeFirst.addAll(lava);
            placeFirst.addAll(water);
            placeFirst.addAll(placeLater);
            placeFirst.addAll(placeFinal);
        }

        return placeFirst;
    }

    
}
