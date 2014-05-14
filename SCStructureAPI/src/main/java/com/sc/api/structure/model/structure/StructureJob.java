/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
