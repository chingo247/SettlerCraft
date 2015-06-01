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
package com.chingo247.settlercraft.structureapi.structure.construction.asyncworldedit;

import com.chingo247.settlercraft.structureapi.structure.plan.placement.Placement;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import org.primesoft.asyncworldedit.api.blockPlacer.IBlockPlacer;
import org.primesoft.asyncworldedit.playerManager.PlayerEntry;

/**
 *
 * @author Chingo
 */
public abstract class AsyncPlacementTask extends SCBaseTask {
    
    private Placement placement;

    public AsyncPlacementTask(Placement placement, EditSession editSession, PlayerEntry player, String commandName, IBlockPlacer blocksPlacer, SCJobEntry job) {
        super(editSession, player, commandName, blocksPlacer, job);
        this.placement = placement;
    }

    
    @Override
    protected Object doRun() throws MaxChangedBlocksException {
        task(placement);
        return null;
    }

    @Override
    protected void doPostRun(Object o) {
    }
    
    /**
     * Task to run
     *
     * @param placement The placement
     * @throws com.sk89q.worldedit.MaxChangedBlocksException
     */
    public abstract void task(Placement placement)
            throws MaxChangedBlocksException;
    
}
