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
package com.chingo247.structureapi.construction.backup;

import com.sk89q.worldedit.blocks.BaseBlock;

/**
 * Chunks are divided into section. Each section has a dimension of 16x16x16. 
 * This class represents one of those sections.
 * @author Chingo
 */
public interface ISectionSnapshot {
    
    /**
     * The Y position of this section normally varying between 0 and 15 
     * @return The y position
     */
    int getY();
    
    /**
     * Get the block at the given position. 
     * @param x The x value, must be between 0 and 15
     * @param y
     * @param z
     * @return 
     */
    BaseBlock getBlockAt(int x, int y, int z);
    
}
