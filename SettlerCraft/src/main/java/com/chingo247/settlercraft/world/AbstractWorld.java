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

import com.chingo247.settlercraft.SettlerCraft;
import com.chingo247.settlercraft.model.world.Direction;
import com.chingo247.settlercraft.structure.Structure;
import com.chingo247.settlercraft.structure.StructureAPI;
import com.chingo247.settlercraft.structure.StructureManager;
import com.chingo247.settlercraft.structure.placement.Placement;
import com.chingo247.settlercraft.structure.plan.StructurePlan;
import com.google.common.base.Preconditions;
import com.sk89q.worldedit.Vector;
import java.io.File;
import java.util.UUID;

/**
 *
 * @author Chingo
 */
public class AbstractWorld implements World {
    
    private final SettlerCraft settlerCraft;
    private final StructureAPI structureAPI;
    private final StructureManager structureManager;
    
    private final String world;
    private final UUID uuid;
    private final File worldFile;
    
    protected AbstractWorld(SettlerCraft settlerCraft, String world, UUID worldUUID, File worldFile) {
        Preconditions.checkNotNull(settlerCraft);
        Preconditions.checkNotNull(world);
        Preconditions.checkNotNull(worldUUID);
        Preconditions.checkNotNull(worldFile);
        
        this.world = world;
        this.uuid = worldUUID;
        this.worldFile = worldFile;
        this.settlerCraft = settlerCraft;
        this.structureAPI = settlerCraft.getStructureAPI();
        this.structureManager = structureAPI.getStructureManager(world);
    }

    @Override
    public String getName() {
        return world;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public Structure createStructure(Placement placement, Vector position, Direction direction) {
        return structureManager.createStructure(placement, position, direction);
    }

    @Override
    public Structure createStructure(StructurePlan plan, Vector position, Direction direction) {
        return structureManager.createStructure(plan, position, direction);
    }

    @Override
    public Structure getStructure(long id) {
        return structureManager.getStructure(id);
    }
    
}
