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

import com.chingo247.settlercraft.structureapi.structure.Structure;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.Placement;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.options.DemolishingOptions;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.DemolishingPlacement;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import org.primesoft.asyncworldedit.playerManager.PlayerEntry;
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
