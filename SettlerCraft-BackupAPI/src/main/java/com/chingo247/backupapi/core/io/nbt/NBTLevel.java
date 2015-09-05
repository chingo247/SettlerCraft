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
package com.chingo247.backupapi.core.io.nbt;

import com.sk89q.jnbt.ListTag;
import com.sk89q.jnbt.LongTag;
import com.sk89q.jnbt.Tag;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A wrapper class for NBTLevel tags. All properties of the Tag are read lazily.
 * @author Chingo
 */
public class NBTLevel extends NBTData {
    
    private Tag levelTag;
    private NBTChunk chunk;
    private Map<String, Tag> levelMap;

    public NBTLevel(NBTChunk nBTChunk, Tag levelTag) {
        this.levelTag = levelTag;
        this.chunk = nBTChunk;
        this.levelMap = (Map) levelTag.getValue();
    }
    
//    public CompoundTag getTileEntityData(int x, int y, int z) { 
//        Map<String,Tag> compoundData = tileEntitiesMap.get(new BlockVector(x, y, z));
//        return compoundData != null ? new CompoundTag(compoundData) : null;
//    }
    
    public ListTag getTileEntityData() {
        if(levelMap.containsKey("TileEntities")) {
            return getChildTag(levelMap, "TileEntities", ListTag.class);
        }
        return null;
    }
    
    public ListTag getEntityData() {
        if(levelMap.containsKey("Entities")) {
            return getChildTag(levelMap, "Entities", ListTag.class);
        }
        return null;
    }
    
    public Long getLastUpdate() {
        Map<String, Tag> vMap = getValueMap();
        Tag t = vMap.get("LastUpdate");
        if(t != null) {
            return ((LongTag) t).getValue();
        }
        return null;
    }
    
    public Set<NBTSection> getSections() throws TagNotFoundException {
        return getSections(-1, -1);
    }
    
    public Set<NBTSection> getSections(int min, int max) throws TagNotFoundException {
        Set<NBTSection> sections = new HashSet<>();
        Map<String, Tag> vMap = getValueMap();
        
        Tag sectionsTag = vMap.get("Sections");
        if (sectionsTag == null) {
            throw new TagNotFoundException("Couldn't find tag 'Sections'");
        }
        
        List<Tag> sectionTagList = ((ListTag) sectionsTag).getValue();
        for (Tag sectionTag : sectionTagList) {
            
            NBTSection nbts = new NBTSection(this, sectionTag);
            
            int y = nbts.getY();
            if(y >= min && y <= max) {
                sections.add(nbts);
            } 
        }
        return sections;
    }
    
    public Map<String, Tag> getValueMap() {
        return (Map) levelTag.getValue();
    }

    public NBTChunk getNBTChunk() {
        return chunk;
    }
    
    

    
    
    
}
