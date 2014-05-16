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

package com.sc.api.structure.model.structure;

import com.sk89q.worldedit.Countable;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BlockID;

/**
 *
 * @author Chingo
 */
public class StructureJob {
    private int id;
    private final String world;
    private int x;
    private int y;
    private int z;
    private int jobSize;

    protected StructureJob() {
        this.world = null;
    }

    public StructureJob(int jobId, Structure structure) {
        int count = 0;
        for(Countable<BaseBlock> b : structure.getPlan().getSchematic().getBlockDistributionWithData()) {
            if(b.getID().getId() != BlockID.AIR) {
                count+= b.getAmount();
            }
        }
        this.jobSize = count;
        Vector pos = structure.getLocation().getPosition();
        this.world = structure.getLocation().getWorld().getName();
        this.x = pos.getBlockX();
        this.y = pos.getBlockY();
        this.z = pos.getBlockZ();
        this.id = jobId;
    }

    public int getId() {
        return id;
    }

    public String getWorld() {
        return world;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int getJobSize() {
        return jobSize;
    }
    
}
