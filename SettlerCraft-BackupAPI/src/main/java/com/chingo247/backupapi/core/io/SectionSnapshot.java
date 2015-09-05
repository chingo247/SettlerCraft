/*
 * The MIT License
 *
 * Copyright 2015 Chingo.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.chingo247.backupapi.core.io;

import com.chingo247.backupapi.core.io.region.RegionFileFormat;
import com.sk89q.jnbt.ByteArrayTag;
import com.sk89q.jnbt.CompoundTag;
import com.sk89q.jnbt.Tag;
import com.sk89q.worldedit.blocks.BaseBlock;
import java.util.Map;

/**
 *
 * @author Chingo
 */
public class SectionSnapshot implements ISectionSnapshot {

    private int y;
    private Map<String, Tag> sectionMap;
    private byte[] ids;
    private byte[] data;
    private byte[] addId;
    private byte[] blockLight;
    private byte[] Skylight;
    private ChunkSnapshot cs;

    SectionSnapshot(ChunkSnapshot cs, Tag sectionTag, int y) {
        this.sectionMap = (Map) sectionTag.getValue();
        this.y = y;

        this.addId = sectionMap.containsKey("Add")
                ? getChildTag(sectionMap, "Add", ByteArrayTag.class).getValue() : new byte[0];
        this.ids = getChildTag(sectionMap, "Blocks", ByteArrayTag.class).getValue();
        this.data = getChildTag(sectionMap, "Data", ByteArrayTag.class).getValue();
        this.blockLight = getChildTag(sectionMap, "BlockLight", ByteArrayTag.class).getValue();
        this.Skylight = getChildTag(sectionMap, "SkyLight", ByteArrayTag.class).getValue();
        this.cs = cs;
    }

    @Override
    public int getY() {
        return y;
    }
    
    private CompoundTag getTileEntityData(int x, int y, int z) {
        return cs.getTileEntityData(x, this.y + y, z);
    }

    @Override
    public BaseBlock getBlockAt(int x, int y, int z) {
        int id = getBlockId(x, y, z);
        
        int data = getData(x, y, z);
        CompoundTag tag = getTileEntityData(x, y, z);
        
        if(id < 0 || data < 0) {
            System.out.println("id: " + id + " data: " + data);
            return null;
        }
        
        return new BaseBlock(id, data, tag);
    }
    
    private int getBlockId(int x, int y, int z) {
        int index = getArrayIndex(x, y, z);
        if((index >> 1) >= addId.length) {
            return getBlockIdA(x, y, z) & 0xFF;
        }
        return (getBlockIdA(x, y, z) & 0xFF) + (getBlockIdB(x, y, z) << 8);
    }
    
    private byte getBlockIdA(int x, int y, int z) {
        int index = getArrayIndex(x, y, z);
        return ids[index];
    }
    
    private int getBlockIdB(int x, int y, int z) {
        
        
        
        return getNibble4(addId, getArrayIndex(x, y, z));
    }

    private int getData(int x, int y, int z) {
        return getNibble4(data, getArrayIndex(x, y, z));
    }

    private int getNibble4(byte[] arr, int index) {
        return index % 2 == 0 ? arr[index / 2] & 0x0F : (arr[index / 2] >> 4) & 0x0F;
    }

    private int getArrayIndex(int x, int y, int z) {
        return y * RegionFileFormat.CHUNK_SIZE * RegionFileFormat.CHUNK_SIZE + z * RegionFileFormat.CHUNK_SIZE + x;
    }

    /**
     * Get child tag of a NBT structure.
     *
     * @param items The parent tag map
     * @param key The name of the tag to get
     * @param expected The expected type of the tag
     * @return child tag casted to the expected type
     * @throws DataException if the tag does not exist or the tag is not of the
     * expected type
     */
    private static <T extends Tag> T getChildTag(Map<String, Tag> items, String key,
            Class<T> expected) {

        if (!items.containsKey(key)) {
            throw new RuntimeException("Section is missing a \"" + key + "\" tag");
        }
        Tag tag = items.get(key);
        if (!expected.isInstance(tag)) {
            throw new RuntimeException(
                    key + " tag is not of tag type " + expected.getName());
        }
        return expected.cast(tag);
    }

}
