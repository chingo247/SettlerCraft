/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.model.plan.schematic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jnbt.ByteArrayTag;
import org.jnbt.CompoundTag;
import org.jnbt.IntTag;
import org.jnbt.ListTag;
import org.jnbt.NBTInputStream;
import org.jnbt.Tag;

/**
 *
 * @author Chingo
 */
public class SchematicReader {
    private Tag getChildTag(Map<String, Tag> items, String key, Class<? extends Tag> expected) {
        Tag tag = items.get(key);
        return tag;
    }

    private List<SchematicBlockData> readBlocksMaterials(final int height, final int width, final int length, final byte[] materialData, byte[] data) {
        int rLayer = 0;
        final List<SchematicBlockData> blks = new ArrayList<>(height * length * width);
        for (int layer = 0; layer < height * length * width; layer += width * length) {
            for (int z = 0; z < length * width; z += width) {
                for (int x = 0; x < width; x++) {
                    SchematicBlockData block = new SchematicBlockData(x, z, rLayer, materialData[z+x+layer], data[z+x+layer]);
                    blks.add(block);
                    
                }
            }
            rLayer++;
        }
        return blks;
    }
    
    

    public SchematicObject readFile(File schematicFile) {
        SchematicObject obj = null;
        try {
            try (FileInputStream fis = new FileInputStream(schematicFile); NBTInputStream nbt = new NBTInputStream(fis)) {
                CompoundTag backuptag = (CompoundTag) nbt.readTag();
                Map<String, Tag> tagCollection = backuptag.getValue();

                int width = ((Short) getChildTag(tagCollection, "Width", IntTag.class).getValue()).intValue();
                int height = ((Short) getChildTag(tagCollection, "Height", IntTag.class).getValue()).intValue();
                int length = ((Short) getChildTag(tagCollection, "Length", IntTag.class).getValue()).intValue();

                byte[] blocks = (byte[]) getChildTag(tagCollection, "Blocks", ByteArrayTag.class).getValue();
                byte[] data = (byte[]) getChildTag(tagCollection, "Data", ByteArrayTag.class).getValue();
                List<SchematicBlockData> blks = new ArrayList<>(readBlocksMaterials(height, width, length, blocks, data));
                
                List entities = (List) getChildTag(tagCollection, "Entities", ListTag.class).getValue();
                List tileentities = (List) getChildTag(tagCollection, "TileEntities", ListTag.class).getValue();

                obj = new SchematicObject(width, height, length, blks, entities, tileentities);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return obj;
    }
}
