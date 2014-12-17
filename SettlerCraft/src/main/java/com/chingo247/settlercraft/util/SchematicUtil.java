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

package com.chingo247.settlercraft.util;

import com.chingo247.settlercraft.structureapi.world.Dimension;
import com.chingo247.settlercraft.structureapi.world.Direction;
import static com.chingo247.settlercraft.structureapi.world.Direction.EAST;
import static com.chingo247.settlercraft.structureapi.world.Direction.NORTH;
import static com.chingo247.settlercraft.structureapi.world.Direction.SOUTH;
import static com.chingo247.settlercraft.structureapi.world.Direction.WEST;
import com.chingo247.settlercraft.structureapi.plan.schematic.SchematicData;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.util.Countable;
import org.bukkit.Material;
import org.bukkit.material.Attachable;
import org.bukkit.material.Crops;
import org.bukkit.material.Directional;
import org.bukkit.material.SimpleAttachableMaterialData;

/**
 *
 * @author Chingo
 */
public class SchematicUtil {

    private SchematicUtil() {
    }

    public static Dimension calculateDimension(SchematicData schematic, Vector pos, Direction direction) {
        Vector end = getPoint2Right(pos, direction, new BlockVector(
                schematic.getWidth(),
                schematic.getHeight(),
                schematic.getLength())
        );
        Dimension dimension = new Dimension(pos, end);
        return dimension;
    }
    
    private static Vector getPoint2Right(Vector point1, Direction direction, Vector size) {
        switch (direction) {
            case EAST:
                return point1.add(size.subtract(1, 1, 1));
            case SOUTH:
                return point1.add(-(size.getBlockZ() - 1), size.getBlockY() - 1, (size.getBlockX() - 1));
            case WEST:
//                clipboard.rotate2D(180);
                return point1.add(-(size.getBlockX() - 1), size.getBlockY() - 1, -(size.getBlockZ() - 1));
            case NORTH:
                return point1.add((size.getBlockZ() - 1), size.getBlockY() - 1, -(size.getBlockX() - 1));
//                clipboard.rotate2D(270);

            default:
                throw new AssertionError("unreachable");
        }
    }



    public static int getHighestYAt(CuboidClipboard clipboard, int x, int z) {
        int height = clipboard.getHeight();

        int y = height - 1;
        while (y > 0) {
            if (clipboard.getBlock(new Vector(x, y, z)).isAir()) {
                y--;
            } else {
                break;
            }
        }
        return y;
    }

    public static int getBlocks(CuboidClipboard clipboard, int x, int z) {
        int height = clipboard.getHeight();

        int count = 0;
        for (int y = height - 1; y > 0; y--) {
            BaseBlock b = clipboard.getBlock(new BlockVector(x, y, z));
            if (b == null || b.isAir()) {
                continue;
            }
            count++;
        }

        return count;
    }

    public static int[][] getHeightMap(CuboidClipboard clipboard) {
        int[][] hMap = new int[clipboard.getWidth()][clipboard.getLength()];
        for (int x = 0; x < clipboard.getWidth(); x++) {
            for (int z = 0; z < clipboard.getLength(); z++) {
                hMap[x][z] = getHighestYAt(clipboard, x, z);
            }
        }
        return hMap;
    }

    public static int count(CuboidClipboard c, boolean noAir) {
        int count = 0;
        for (Countable<BaseBlock> b : c.getBlockDistributionWithData()) {
            if (b.getID().isAir() && noAir) {
                continue;
            }
            count += b.getAmount();
        }
        return count;
    }

    public static CuboidClipboard getLayer(CuboidClipboard whole, int layer) {
        CuboidClipboard layerClip = new CuboidClipboard(new BlockVector(whole.getWidth(), 1, whole.getLength()));
        for (int x = 0; x < whole.getWidth(); x++) {
            for (int z = 0; z < whole.getLength(); z++) {
                layerClip.setBlock(new BlockVector(x, 0, z), whole.getBlock(new BlockVector(x, layer, z)));
            }
        }
        return layerClip;
    }

    /**
     * Creates a string from a value e.g. value > 1E3 = value/1E3 + "K" e.g. value > 1E6 = value/1E6
     * + "M"
     *
     * @param value
     * @return
     */
    public static String valueString(double value) {
        if (value < 1000) {
            return String.valueOf(value);
        } else if (value < 1E6) {
            return String.valueOf(Math.round(value / 1E3)) + "K";
        } else {
            return String.valueOf(Math.round(value / 1E6)) + "M";
        }
    }

    public static boolean isAttachable(Material material, byte data) {
        return (material.getData().cast(material.getNewData(data)) instanceof Attachable);
    }

    public static boolean isDirectional(Material material, byte data) {
        return (material.getData().cast(material.getNewData(data)) instanceof Directional);
    }

    public static boolean isSimpleAttachable(Material material, byte data) {
        return (material.getData().cast(material.getNewData(data)) instanceof SimpleAttachableMaterialData);
    }

    public static boolean isCrops(Material material, byte data) {
        return (material.getData().cast(material.getNewData(data)) instanceof Crops);
    }

    public static void align(CuboidClipboard clipboard, Direction direction) {
        switch (direction) {
            case EAST:
                break;
            case SOUTH:
                clipboard.rotate2D(90);
                break;
            case WEST:
                clipboard.rotate2D(180);
                break;
            case NORTH:
                clipboard.rotate2D(270);
                break;
            default:
                throw new AssertionError("Unreachable");
        }
    }
}
