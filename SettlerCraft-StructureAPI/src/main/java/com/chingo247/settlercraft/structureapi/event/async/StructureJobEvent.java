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
package com.chingo247.settlercraft.structureapi.event.async;

import com.sk89q.worldedit.world.World;

/**
 *
 * @author Chingo
 */
public class StructureJobEvent  {
    
    private final int jobId;
    private final long structure;
    private final World world;

    public StructureJobEvent(World world, long structure, int jobid) {
        this.jobId = jobid;
        this.structure = structure;
        this.world = world;
    }

    public final long getStructure() {
        return structure;
    }

    public World getWorld() {
        return world;
    }
    
    

    /**
     * Gets the JobId (AsyncWorldEdit's jobId
     * @return The jobId
     */
    public final int getJobId() {
        return jobId;
    }
    
}
