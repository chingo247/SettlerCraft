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
package com.chingo247.structureapi.construction.asyncworldedit;

import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.structureapi.plan.placement.Placement;
import com.chingo247.structureapi.construction.options.Options;
import com.chingo247.xplatform.core.IScheduler;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.primesoft.asyncworldedit.AsyncWorldEditMain;
import org.primesoft.asyncworldedit.api.blockPlacer.IBlockPlacer;
import org.primesoft.asyncworldedit.playerManager.PlayerEntry;

/**
 *
 * @author Chingo
 * @param <T> Options
 * @param <P> The placement
 */
public abstract class AbstractAsyncPlacement<T extends Options, P extends Placement> implements Placement<T> {
    
    protected final AsyncWorldEditMain awe;
    protected final IBlockPlacer placer;
    protected final IScheduler scheduler;
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
        this.scheduler = SettlerCraft.getInstance().getPlatform().getServer().getScheduler(SettlerCraft.getInstance().getPlugin());
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
