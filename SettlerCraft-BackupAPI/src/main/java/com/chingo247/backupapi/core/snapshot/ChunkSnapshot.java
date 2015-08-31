/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.backupapi.core.snapshot;

import com.chingo247.structureapi.construction.backup.IChunkSnapshot;
import com.chingo247.structureapi.construction.backup.ISectionSnapshot;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sk89q.jnbt.CompoundTag;
import com.sk89q.jnbt.IntTag;
import com.sk89q.jnbt.ListTag;
import com.sk89q.jnbt.Tag;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Chingo
 */
public class ChunkSnapshot implements IChunkSnapshot {
    
    private Map<String, Tag> chunkMap;
    private int x, z;
    private Map<Integer, ISectionSnapshot> sections;
    private Map<Vector, Map<String, Tag>> tileEntitiesMap;
    
    ChunkSnapshot(Map<String, Tag> chunkMap, int x, int z) {
        this.chunkMap = chunkMap;
        this.x = x;
        this.z = z;
        this.sections = Maps.newHashMap();
        this.tileEntitiesMap = Maps.newHashMap();
        
        List<Tag> tileEntities = getChildTag(chunkMap, "TileEntities", ListTag.class)
                .getValue();
        
        for (Tag tag : tileEntities) {
            if (!(tag instanceof CompoundTag)) {
                continue;
            }
            
            CompoundTag t = (CompoundTag) tag;

            int eX = 0;
            int eY = 0;
            int eZ = 0;

            Map<String, Tag> values = new HashMap<>();
            for (Map.Entry<String, Tag> entry : t.getValue().entrySet()) {
                if (entry.getKey().equals("x")) {
                    if (entry.getValue() instanceof IntTag) {
                        eX = ((IntTag) entry.getValue()).getValue();
                    }
                } else if (entry.getKey().equals("y")) {
                    if (entry.getValue() instanceof IntTag) {
                        eY = ((IntTag) entry.getValue()).getValue();
                    }
                } else if (entry.getKey().equals("z")) {
                    if (entry.getValue() instanceof IntTag) {
                        eZ = ((IntTag) entry.getValue()).getValue();
                    }
                }
                values.put(entry.getKey(), entry.getValue());
            }

            BlockVector vec = new BlockVector(eX, eY, eZ);
            tileEntitiesMap.put(vec, values);
        }
        
    }
    
    public CompoundTag getTileEntityData(int x, int y, int z) { 
        Map<String,Tag> compoundData = tileEntitiesMap.get(new BlockVector(x, y, z));
        return compoundData != null ? new CompoundTag(compoundData) : null;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getZ() {
        return z;
    }

    @Override
    public ISectionSnapshot getSection(int section) {
        ISectionSnapshot sectionSnapshot = sections.get(section);
        if (sectionSnapshot == null) {
            Tag sectionTag = chunkMap.get("Section-[" + section + "]");
            if(sectionTag == null) {
                return null;
            }
            sectionSnapshot = new SectionSnapshot(this, sectionTag, section);
            sections.put(section, sectionSnapshot);
        }
        return sectionSnapshot;
    }
    
    
    

    @Override
    public boolean hasSection(int y) {
        return getSection(y) != null;
    }

    @Override
    public Collection<ISectionSnapshot> getSections() {
        List<ISectionSnapshot> sections = Lists.newArrayList();
        for (Map.Entry<String, Tag> entrySet : chunkMap.entrySet()) {
            String key = entrySet.getKey();
            if(key.contains("Section-[")) {
                 Tag value = entrySet.getValue();   
                 sections.add(new SectionSnapshot(this, value, x));
            }
        }
        return sections;
    }

    @Override
    public BaseBlock getBlockAt(int x, int y, int z) {
        int section = y >> 4;
        ISectionSnapshot ss = getSection(section);
        return ss == null ? null : ss.getBlockAt(x, y - (section * 16), z);
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
    protected static <T extends Tag> T getChildTag(Map<String, Tag> items, String key,
            Class<T> expected) {

        if (!items.containsKey(key)) {
            throw new RuntimeException("Chunk is missing a \"" + key + "\" tag");
        }
        Tag tag = items.get(key);
        if (!expected.isInstance(tag)) {
            throw new RuntimeException(
                    key + " tag is not of tag type " + expected.getName());
        }
        return expected.cast(tag);
    }
    
}
