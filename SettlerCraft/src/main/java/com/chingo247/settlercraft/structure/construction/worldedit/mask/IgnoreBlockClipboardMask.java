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
package com.chingo247.settlercraft.structure.construction.worldedit.mask;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.mask.Mask2D;
import javax.annotation.Nullable;

/**
 *
 * @author Chingo
 */
public class IgnoreBlockClipboardMask implements Mask {
    
    private final CuboidClipboard clipboard;
    private int material;

    /**
     * Constructor.
     * @param clipboard
     * @param material 
     */
    public IgnoreBlockClipboardMask(CuboidClipboard clipboard, int material) {
        this.clipboard = clipboard;
        this.material = material;
    }
    
    

    @Override
    public boolean test(Vector vector) {
        return clipboard.getBlock(vector).getId() != material;
    }

    @Nullable
    @Override
    public Mask2D toMask2D() {
        return null;
    }
    
    
    
}
