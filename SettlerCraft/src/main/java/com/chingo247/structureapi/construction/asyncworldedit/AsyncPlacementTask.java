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
package com.chingo247.structureapi.construction.asyncworldedit;

import com.chingo247.structureapi.placement.Placement;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import org.primesoft.asyncworldedit.PlayerEntry;
import org.primesoft.asyncworldedit.blockPlacer.BlockPlacer;

/**
 *
 * @author Chingo
 */
public abstract class AsyncPlacementTask extends SCBaseTask {
    
    private Placement placement;

    public AsyncPlacementTask(Placement placement, EditSession editSession, PlayerEntry player, String commandName, BlockPlacer blocksPlacer, SCJobEntry job) {
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
