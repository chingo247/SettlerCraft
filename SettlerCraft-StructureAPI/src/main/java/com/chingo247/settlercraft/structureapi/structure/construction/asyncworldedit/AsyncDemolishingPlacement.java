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

import com.chingo247.settlercraft.structureapi.structure.Structure;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.Placement;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.options.DemolishingOptions;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.DemolishingPlacement;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import org.primesoft.asyncworldedit.PlayerEntry;
import org.primesoft.asyncworldedit.utils.WaitFor;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSession;
import org.primesoft.asyncworldedit.worldedit.CancelabeEditSession;

/**
 *
 * @author Chingo
 */
public class AsyncDemolishingPlacement extends AbstractAsyncPlacement<DemolishingOptions, DemolishingPlacement> {

    private final AsyncPlacementCallback callback;
    private final long structureId;
    
    /**
     * Constructor.
     *
     * @param playerEntry The PlayerEntry
     * @param placement The placement
     * @param callback
     * @param structure The id of the structure (or null/-1 if no structure)
     */
    public AsyncDemolishingPlacement(PlayerEntry playerEntry, DemolishingPlacement placement, AsyncPlacementCallback callback, Structure structure) {
        super(playerEntry, placement);
        this.callback = callback;
        this.structureId = structure == null ? -1 : structure.getId();
    }
    
    public AsyncDemolishingPlacement(PlayerEntry playerEntry, DemolishingPlacement placement) {
        this(playerEntry, placement, null, null);
    }

    @Override
    public void place(EditSession editSession, final Vector pos, final DemolishingOptions options) {

        final int jobId = getJobId();
        final EditSession session;
        final SCJobEntry job;
        final WaitFor wait;

        if (editSession instanceof AsyncEditSession) {
            AsyncEditSession aSession = (AsyncEditSession) editSession;
            wait = aSession.getWait();
            session = new CancelabeEditSession(aSession, aSession.getMask(), jobId);
            job = new SCJobEntry(playerEntry, (CancelabeEditSession) session, jobId, "place", structureId);
        } else {
            session = editSession;
            wait = null;
            job = new SCJobEntry(playerEntry, jobId, "place", structureId);
        }
        
        if(callback != null) {
            callback.onJobAdded(jobId);
        }

        scheduler.runAsync(new AsyncPlacementTask(placement, session, playerEntry, null, placer, job) {

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
    public int getWidth() {
        return placement.getWidth();
    }

    @Override
    public int getHeight() {
        return placement.getHeight();
    }

    @Override
    public int getLength() {
        return placement.getLength();
    }

    @Override
    public String getTypeName() {
        return placement.getTypeName();
    }

    @Override
    public Vector getSize() {
        return placement.getSize();
    }

}
