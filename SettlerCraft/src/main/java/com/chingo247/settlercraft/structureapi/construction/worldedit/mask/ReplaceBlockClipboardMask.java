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
package com.chingo247.settlercraft.structureapi.construction.worldedit.mask;

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
