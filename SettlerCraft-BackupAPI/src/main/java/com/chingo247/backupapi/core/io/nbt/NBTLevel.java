/*
 * Copyright (C) 2015 Chingo
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
package com.chingo247.backupapi.core.io.nbt;

import com.sk89q.jnbt.CompoundTag;
import com.sk89q.jnbt.IntTag;
import com.sk89q.jnbt.ListTag;
import com.sk89q.jnbt.LongTag;
import com.sk89q.jnbt.Tag;
import com.sk89q.worldedit.BlockVector;
import java.util.HashMap;
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
