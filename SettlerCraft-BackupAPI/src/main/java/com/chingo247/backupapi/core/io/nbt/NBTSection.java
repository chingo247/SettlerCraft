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

import com.sk89q.jnbt.ByteTag;
import com.sk89q.jnbt.Tag;
import com.sk89q.worldedit.Vector2D;
import java.util.Map;

/**
 *
 * @author Chingo
 */
public class NBTSection {

    private int y; // cached

    private Tag sectionTag;
    
    private NBTLevel levelNBT;

    public NBTSection(NBTLevel nbtl, Tag sectionTag) {
        this.y = Integer.MIN_VALUE;
        this.sectionTag = sectionTag;
        this.levelNBT = nbtl;
    }
    
    public Map<String, Tag> getValueMap() {
        return (Map) sectionTag.getValue();
    }

    public Tag getSectionTag() {
        return sectionTag;
    }
    
    public int getY() throws TagNotFoundException {
        if(y == Integer.MIN_VALUE) {
            Map<String, Tag> section = (Map) sectionTag.getValue();

            Tag yTag = section.get("Y");
            if (yTag == null) {
                throw new TagNotFoundException("Couldn't find tag 'Y'");
            }
            y = ((ByteTag) yTag).getValue();
        }
        return y;
    }

   

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + this.y;
        Vector2D pos = levelNBT.getNBTChunk().getPos();
        hash = 67 * hash + pos.getBlockX();
        hash = 67 * hash + pos.getBlockZ();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NBTSection other = (NBTSection) obj;
        
        if (this.y != other.y) {
            return false;
        }
        Vector2D pos = levelNBT.getNBTChunk().getPos();
        Vector2D opos = other.levelNBT.getNBTChunk().getPos();
        
        return pos.equals(opos);
    }

  
    
    
    
    

}
