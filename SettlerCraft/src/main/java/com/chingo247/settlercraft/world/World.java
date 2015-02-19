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
package com.chingo247.settlercraft.world;

import com.chingo247.settlercraft.WorldConfig;
import com.chingo247.settlercraft.structure.Structure;
import com.chingo247.settlercraft.structure.plan.StructurePlan;
import com.chingo247.settlercraft.structure.plan.placement.Placement;
import com.sk89q.worldedit.Vector;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Chingo
 */
public interface World {
    
    /**
     * Gets the world UUID
     * @return The worldUUID
     */
    public UUID getUniqueId();
    
    /**
     * Gets the name of the world
     * @return The name of the world
     */
    public String getName();
    
    /**
     * Creates a structure
     * @param plan The StructurePlan
     * @param position The position where the structure is gonna be placed
     * @param direction The direction of the structure
     */
    public void createStructure(StructurePlan plan, Vector position, Direction direction);
    
    /**
     * Create a structure
     * @param placement The placement
     * @param postion The position
     * @param direction The direction
     */
    public void createStructure(Placement placement, Vector postion, Direction direction);
    
    /**
     * Gets all the structures in this world
     * @return The structures in this world
     */
    public List<Structure> getStructures();
    
    /**
     * Gets the structure with the corresponding id
     * @param id The structure
     * @return The structure or null of structure was not found.
     */
    public Structure getStructure(long id);
    
    /**
     * Gets the world's config
     * @return The config of this world
     */
    public WorldConfig getConfig();
    

}
