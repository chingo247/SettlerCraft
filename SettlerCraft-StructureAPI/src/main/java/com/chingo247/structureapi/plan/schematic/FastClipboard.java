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
package com.chingo247.structureapi.plan.schematic;

import com.chingo247.settlercraft.core.Direction;
import com.sk89q.jnbt.ByteArrayTag;
import com.sk89q.jnbt.CompoundTag;
import com.sk89q.jnbt.IntTag;
import com.sk89q.jnbt.ListTag;
import com.sk89q.jnbt.NBTInputStream;
import com.sk89q.jnbt.ShortTag;
import com.sk89q.jnbt.Tag;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.data.DataException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author Chingo
 */
public class FastClipboard {
    
    private Direction direction;
    private Vector offset;
    private int xMod = 1;
    private int zMod = 1;
    private final int width;
    private final int height;
    private final int length;
    private final short[] blockIds;
    private final byte[] data;
    private final Map<BlockVector, Map<String, Tag>> tileEntities;
    
    private FastClipboard(int width, int height, int length, short[] blockIds, byte[] data, Map<BlockVector, Map<String, Tag>> tileEntities) {
        this.offset = Vector.ZERO;
        this.tileEntities = tileEntities;
        this.width = width;
        this.height = height;
        this.length = length;
        this.blockIds = blockIds;
        this.data = data;
        direction = Direction.EAST;
    }
    
    public BaseBlock getBlock(int x, int y, int z) {
        Vector pos = offset.add(x*xMod, y, z*zMod);
        int index = pos.getBlockY() * width * length + pos.getBlockZ() * width + pos.getBlockX();
        BaseBlock b = new BaseBlock(blockIds[index], data[index]);
        
        Map<String, Tag> tileMap = tileEntities.get(new BlockVector(pos));
        if(tileMap != null) {
            b.setNbtData(new CompoundTag(tileMap));
        }
        return b;
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
    
    public Direction getDirection() {
        return direction;
    }
    
    public void setDirection(Direction direction) {
        switch(direction) {
            case EAST: 
                offset = Vector.ZERO;
                xMod = 1;
                zMod = 1;
                break;
            case WEST:
                offset = new Vector(width,0,length);
                xMod = -1;
                zMod = -1;
                break;
            case NORTH:
                offset = new Vector(0, 0, length);
                xMod = 1;
                zMod = -1;
                break;
            case SOUTH:
                offset = new Vector(width, 0, 0);
                xMod = -1;
                zMod = 1;
                break;
            default:break;
        }
        this.direction = direction;
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
            throw new RuntimeException("Schematic file is missing a \"" + key + "\" tag");
        }
        Tag tag = items.get(key);
        if (!expected.isInstance(tag)) {
            throw new RuntimeException(
                    key + " tag is not of tag type " + expected.getName());
        }
        return expected.cast(tag);
    }
    
    public static FastClipboard read(File schematicFile) throws IOException {
        System.out.println("Opening file");
        long start = System.currentTimeMillis();
        NBTInputStream nbtStream = new NBTInputStream(
                new GZIPInputStream(new FileInputStream(schematicFile)));

        Tag rootTag = nbtStream.readTag();

        System.out.println(rootTag.getName());
        if (!rootTag.getName().equalsIgnoreCase("Schematic")) {
            throw new RuntimeException("Tag 'Schematic' does not exist or is not first");
        }
        nbtStream.close();

        // Check
        Map<String, Tag> schematic = (Map) rootTag.getValue();
        if (!schematic.containsKey("Blocks")) {
            throw new RuntimeException("Schematic file is missing a \"Blocks\" tag");
        }
        
        

        // Get information
        short width = getChildTag(schematic, "Width", ShortTag.class).getValue();
        short length = getChildTag(schematic, "Length", ShortTag.class).getValue();
        short height = getChildTag(schematic, "Height", ShortTag.class).getValue();
        byte[] ids = getChildTag(schematic, "Blocks", ByteArrayTag.class).getValue();
        byte[] data = getChildTag(schematic, "Data", ByteArrayTag.class).getValue();
        byte[] addId = new byte[0];
        

        // We support 4096 block IDs using the same method as vanilla Minecraft, where
        // the highest 4 bits are stored in a separate byte array.
        if (schematic.containsKey("AddBlocks")) {
            addId = getChildTag(schematic, "AddBlocks", ByteArrayTag.class).getValue();
        }
        
        
        
        // read ids
        short[] blockids = new short[ids.length]; // Have to later combine IDs
        // Combine the AddBlocks data with the first 8-bit block ID
        for (int index = 0; index < ids.length; index++) {
            if ((index >> 1) >= addId.length) { // No corresponding AddBlocks index
                blockids[index] = (short) (ids[index] & 0xFF);
            } else {
                if ((index & 1) == 0) {
                    blockids[index] = (short) (((addId[index >> 1] & 0x0F) << 8) + (ids[index] & 0xFF));
                } else {
                    blockids[index] = (short) (((addId[index >> 1] & 0xF0) << 4) + (ids[index] & 0xFF));
                }
            }
        }
        
        // read data
        byte[] blockData = new byte[data.length];
        for (int index = 0; index < data.length; index++) {
            if ((index >> 1) >= addId.length) { // No corresponding AddBlocks index
                blockData[index] =  (data[index]);
            } else {
                if ((index & 1) == 0) {
                    blockData[index] = (byte) (((addId[index >> 1] & 0x0F) << 8) + (blockData[index]));
                } else {
                    blockData[index] = (byte) (((addId[index >> 1] & 0xF0) << 4) + (blockData[index]));
                }
            }
        }
        
        // Need to pull out tile entities
        List<Tag> tileEntities = getChildTag(schematic, "TileEntities", ListTag.class)
                .getValue();
        Map<BlockVector, Map<String, Tag>> tileEntitiesMap =
                new HashMap<>();

        for (Tag tag : tileEntities) {
            if (!(tag instanceof CompoundTag)) continue;
            CompoundTag t = (CompoundTag) tag;

            int x = 0;
            int y = 0;
            int z = 0;

            Map<String, Tag> values = new HashMap<>();

            for (Map.Entry<String, Tag> entry : t.getValue().entrySet()) {
                if (entry.getKey().equals("x")) {
                    if (entry.getValue() instanceof IntTag) {
                        x = ((IntTag) entry.getValue()).getValue();
                    }
                } else if (entry.getKey().equals("y")) {
                    if (entry.getValue() instanceof IntTag) {
                        y = ((IntTag) entry.getValue()).getValue();
                    }
                } else if (entry.getKey().equals("z")) {
                    if (entry.getValue() instanceof IntTag) {
                        z = ((IntTag) entry.getValue()).getValue();
                    }
                }

                values.put(entry.getKey(), entry.getValue());
            }

            BlockVector vec = new BlockVector(x, y, z);
            tileEntitiesMap.put(vec, values);
        }
        System.out.println("Done in " + (System.currentTimeMillis() - start) + " ms");
        return new FastClipboard(width, height, length, blockids, blockData, tileEntitiesMap);
    }
    
    public static void main(String[] args) {
        try {
            FastClipboard.read(new File("F:\\GAMES\\MineCraftServers\\Bukkit 1.7.10-SettlerCraft-RC4\\plugins\\SettlerCraft-StructureAPI\\plans\\Shiganshina.schematic"));
        } catch (IOException ex) {
            Logger.getLogger(FastClipboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
