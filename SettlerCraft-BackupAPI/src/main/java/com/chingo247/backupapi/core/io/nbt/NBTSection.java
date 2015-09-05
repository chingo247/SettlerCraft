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
