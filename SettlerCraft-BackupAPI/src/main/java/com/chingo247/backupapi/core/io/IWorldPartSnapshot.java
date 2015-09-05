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
package com.chingo247.backupapi.core.io;

import com.sk89q.worldedit.Vector2D;
import com.sk89q.worldedit.blocks.BaseBlock;

/**
 * As the name of this class tells, this is a snapshot of a part of the world. Therefore
 * this snapshot may not contain all chunks and also all chunks may not contain all sections.
 * However, real world coordinates can be used.
 * 
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
     * Gets the block at the actual world position that is read from the backup. <br/>
     * <b>NOTE</b>: This method will not return null if the section doesn't exist. Instead it will return a block of air.
     * Reason: Minecraft does not save empty sections of chunks to save space, therefore when the section was null an empty block will be returned
     * @param x The x
     * @param y The y
     * @param z The z
     * @return The block
     */
    BaseBlock getWorldBlockAt(int x, int y, int z);
    
    

}
