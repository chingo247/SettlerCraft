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
package com.chingo247.settlercraft.structureapi.structure.construction.asyncworldedit;

import com.chingo247.settlercraft.structureapi.structure.plan.placement.Placement;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.options.PlacementOptions;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;
import org.primesoft.asyncworldedit.AsyncWorldEditMain;
import org.primesoft.asyncworldedit.PlayerEntry;
import org.primesoft.asyncworldedit.blockPlacer.BlockPlacer;

/**
 *
 * @author Chingo
 * @param <T> Options
 * @param <P> The placement
 */
public abstract class AbstractAsyncPlacement<T extends PlacementOptions, P extends Placement> implements Placement<T> {
    
    protected final AsyncWorldEditMain awe;
    protected final BlockPlacer placer;
    protected final BukkitScheduler scheduler;
    protected final Placement placement;
    protected final PlayerEntry playerEntry;

    /**
     * Constructor.
     *
     * @param playerEntry The PlayerEntry
     * @param placement The placement
     */
    public AbstractAsyncPlacement(PlayerEntry playerEntry, P placement) {
        this.playerEntry = playerEntry;
        this.awe = AsyncWorldEditMain.getInstance();
        this.placer = awe.getBlockPlacer();
        this.placement = placement;
        this.scheduler = Bukkit.getScheduler();
    }

   

    @Override
    public Vector getPosition() {
        return placement.getPosition();
    }

    

    @Override
    public void move(Vector offset) {
        placement.move(offset);
    }

    /**
     * Get next job id for current player
     *
     * @return Job id
     */
    protected int getJobId() {
        return placer.getJobId(playerEntry);
    }

    @Override
    public CuboidRegion getCuboidRegion() {
        return placement.getCuboidRegion();
    }

    
}
