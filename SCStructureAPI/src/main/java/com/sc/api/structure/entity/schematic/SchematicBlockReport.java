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
package com.sc.api.structure.entity.schematic;

import com.sc.api.structure.util.SettlerCraftMaterials;
import com.sk89q.worldedit.Countable;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import org.bukkit.Material;

/**
 * Contains all the required information of a Cuboid, used to construct/paste structures
 *
 * @author Chingo
 */
public class SchematicBlockReport implements Serializable {

    private final int height;
    private final int length;
    private final int width;
    private final ArrayList<SchematicMaterialLayer> layerRequirements;        // For player to build structures themselves
    private final ArrayList<CuboidClipboard> layeredCuboidClipboards;   // For WorldEdit to paste a layer

    public SchematicBlockReport(File schematic) throws IOException, DataException {
        SchematicFormat format = SchematicFormat.getFormat(schematic);
        CuboidClipboard ccb = format.load(schematic);
        this.height = ccb.getHeight();
        this.length = ccb.getLength();
        this.width = ccb.getWidth();
        this.layerRequirements = new ArrayList<>(height);
        this.layeredCuboidClipboards = new ArrayList<>(height);

        for (int layer = 0; layer < height; layer++) {
            CuboidClipboard layerCCb = new CuboidClipboard(new Vector(width, 1, length));
            for (int l = 0; l < l; l++) {
                for (int w = 0; w < w; w++) {
                    BaseBlock b = ccb.getBlock(new Vector(w, layer, l));
                    layerCCb.setBlock(new Vector(w, 0, l), b);
                }
            }
            layeredCuboidClipboards.add(layer, layerCCb);
            layerRequirements.add(processCuboidLayer(layer, layerCCb));
        }

    }

    private SchematicMaterialLayer processCuboidLayer(int layer, CuboidClipboard ccb) {
        SchematicMaterialLayer layerRequirement = new SchematicMaterialLayer(layer);
        for (Countable<BaseBlock> b : ccb.getBlockDistributionWithData()) {
            layerRequirement.addResource(
                    new SchematicMaterialResource(
                            Material.getMaterial(b.getID().getType()),
                            b.getID().getData(),
                            Math.round(b.getAmount() * SettlerCraftMaterials.getValue(b.getID()))));
        }
        return layerRequirement;
    }

    public ArrayList<SchematicMaterialLayer> getLayerRequirements() {
        return new ArrayList<>(layerRequirements);
    }

    public ArrayList<CuboidClipboard> getLayeredCuboidClipboards() {
        return new ArrayList<>(layeredCuboidClipboards);
    }

    public int getHeight() {
        return height;
    }

    public int getLength() {
        return length;
    }

    public int getWidth() {
        return width;
    }

}
