/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.module.structureapi.util;

import com.sc.module.structureapi.structure.schematic.Schematic;
import com.sc.module.structureapi.world.Cardinal;
import com.sc.module.structureapi.world.Dimension;
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

    public static Dimension calculateDimension(Schematic schematic, Vector pos, Cardinal cardinal) {
        Vector end = WorldUtil.getPoint2Right(pos, cardinal, new BlockVector(
                schematic.getWidth(),
                schematic.getHeight(),
                schematic.getLength())
        );
        Dimension dimension = new Dimension(pos, end);
        return dimension;
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
            System.out.println(b.getID() + ":" + b.getAmount());
            count += b.getAmount();
        }
        System.out.println("Total: " + count);
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

    public static void align(CuboidClipboard clipboard, Cardinal cardinal) {
        switch (cardinal) {
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
