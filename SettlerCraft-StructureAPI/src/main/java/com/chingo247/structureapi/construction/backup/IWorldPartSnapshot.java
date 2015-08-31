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

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.Vector2D;
import com.sk89q.worldedit.blocks.BaseBlock;
import java.util.Set;

/**
 * Basicly a Sample or snapshot of a part of the world
 * @author Chingo
 */
public interface IWorldPartSnapshot { 
    
    /**
     * Gets the min position
     * @return The min position
     */
    Vector2D getMinPosition();
    
    /**
     * Gets the max position
     * @return The max position
     */
    Vector2D getMaxPosition();
    
    /**
     * Gets the block at the actual world position that is read from the backup
     * @param x The x
     * @param y The y
     * @param z The z
     * @return The block
     */
    BaseBlock getWorldBlockAt(int x, int y, int z);
    
    

}
