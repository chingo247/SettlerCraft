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
package com.chingo247.structureapi.structure.construction.worldedit.mask;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.mask.Mask2D;
import org.bukkit.block.Block;

/**
 *
 * @author Chingo
 */
public class ReplaceBlockClipboardMask extends ClipboardMask {
    
    private final int currentMaterial;
    private final int currentData;
    private final int newMaterial;
    private final int newData;

    public ReplaceBlockClipboardMask(CuboidClipboard clipboard, int currentMaterial, int newMaterial) {
        this(clipboard, currentMaterial, 0 , newMaterial, 0);
    }
    
    /**
     * Constructor
     * @param clipboard The clipboard
     * @param matchingMaterial The material id which will be replaced or -1 to replace any material
     * @param matchingData The data id which will be replaced or -1 to replace any data
     * @param newMaterial The new material which will replace the current
     * @param newData Th
     */
    public ReplaceBlockClipboardMask(CuboidClipboard clipboard, int matchingMaterial, int matchingData, int newMaterial, int newData) {
        super(clipboard);
        this.currentMaterial = matchingMaterial;
        this.currentData = matchingData;
        this.newMaterial = newMaterial;
        this.newData = newData;
    }

    public int getMaterial() {
        return newMaterial;
    }

    public int getData() {
        return newData;
    }
    
    @Override
    public boolean test(Vector vector) {
        BaseBlock b = getClipboard().getBlock(vector);
        // If -1 it always matches otherswise the check on material it peformed
        boolean material = currentMaterial == -1 ? true : b.getId() == currentMaterial;
        boolean data = currentData == -1 ? true : b.getData() == currentData;
        
        return material && data;
    }

    @Override
    public Mask2D toMask2D() {
        return null;
    }

    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + this.currentMaterial;
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
        final ReplaceBlockClipboardMask other = (ReplaceBlockClipboardMask) obj;
        if (this.currentMaterial != other.currentMaterial) {
            return false;
        }
        return true;
    }
    
    
    
}
