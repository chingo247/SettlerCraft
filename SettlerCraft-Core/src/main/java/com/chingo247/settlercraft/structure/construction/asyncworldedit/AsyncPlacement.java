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
package com.chingo247.settlercraft.structure.construction.asyncworldedit;

import com.chingo247.settlercraft.world.Direction;
import com.chingo247.settlercraft.structure.construction.options.Options;
import com.chingo247.settlercraft.structure.placement.AbstractPlacement;
import com.chingo247.settlercraft.structure.placement.Placement;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;
import org.primesoft.asyncworldedit.AsyncWorldEditMain;
import org.primesoft.asyncworldedit.PlayerEntry;
import org.primesoft.asyncworldedit.blockPlacer.BlockPlacer;
import org.primesoft.asyncworldedit.utils.WaitFor;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSession;
import org.primesoft.asyncworldedit.worldedit.CancelabeEditSession;

/**
 *
 * @author Chingo
 */
public class AsyncPlacement extends AbstractPlacement {

    private final AsyncWorldEditMain awe;
    private final BlockPlacer placer;
    private final BukkitScheduler scheduler;
    private final Placement placement;
    private final PlayerEntry playerEntry;

    /**
     * Constructor.
     *
     * @param playerEntry The PlayerEntry
     * @param placement The placement
     */
    public AsyncPlacement(PlayerEntry playerEntry, Placement placement) {
        this.playerEntry = playerEntry;
        this.awe = AsyncWorldEditMain.getInstance();
        this.placer = awe.getBlockPlacer();
        this.placement = placement;
        this.scheduler = Bukkit.getScheduler();
    }

    @Override
    public void place(EditSession editSession, final Vector pos, final Options options) {

        final int jobId = getJobId();
        final EditSession session;
        final SCJobEntry job;
        final WaitFor wait;

        if (editSession instanceof AsyncEditSession) {
            AsyncEditSession aSession = (AsyncEditSession) editSession;
            wait = aSession.getWait();
            session = new CancelabeEditSession(aSession, aSession.getMask(), jobId);
            job = new SCJobEntry(playerEntry, (CancelabeEditSession) session, jobId, "place", -1L);
        } else {
            session = editSession;
            wait = null;
            job = new SCJobEntry(playerEntry, jobId, "place", -1L);
        }

        scheduler.runTaskAsynchronously(awe, new AsyncPlacementTask(placement, session, playerEntry, null, placer, job) {

            @Override
            public void task(Placement placement) throws MaxChangedBlocksException {
                if (wait != null) {
                    wait.checkAndWait(null);
                }
                placement.place(session, pos, options);
            }
        });

    }

    @Override
    public Vector getPosition() {
        return placement.getPosition();
    }

    @Override
    public void rotate(Direction direction) {
        placement.rotate(direction);
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
    private int getJobId() {
        return placer.getJobId(playerEntry);
    }

    @Override
    public Vector getMinPosition() {
        return placement.getMinPosition();
    }

    @Override
    public Vector getMaxPosition() {
        return placement.getMaxPosition();
    }
    
}
