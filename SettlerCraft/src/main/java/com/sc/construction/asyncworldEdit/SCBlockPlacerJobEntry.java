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
package com.sc.construction.asyncworldEdit;

import java.util.UUID;
import org.primesoft.asyncworldedit.blockPlacer.BlockPlacer;
import org.primesoft.asyncworldedit.blockPlacer.BlockPlacerJobEntry;
import org.primesoft.asyncworldedit.worldedit.CancelabeEditSession;

/**
 *
 * @author Chingo
 */
public class SCBlockPlacerJobEntry extends BlockPlacerJobEntry {

    private UUID player;

//    public SCBlockPlacerJobEntry(AsyncEditSession editSession, CancelabeEditSession cEditSession, int jobId, String name) {
//        super(editSession, cEditSession, jobId, name);
//        this.player = editSession.getPlayer();
//    }

    public SCBlockPlacerJobEntry(UUID player, CancelabeEditSession cEditSession, int jobId, String name) {
        super(player, cEditSession, jobId, name);
        this.player = player;
    }

    public SCBlockPlacerJobEntry(UUID player, int jobId, String name) {
        super(player, jobId, name);
        this.player = player;
    }

    @Override
    public long Process(BlockPlacer bp) {
        long start = System.currentTimeMillis();
        switch (getStatus()) {
            case Done:
                bp.removeJob(player, this);
                return System.currentTimeMillis() - start;
            case PlacingBlocks:
                setStatus(BlockPlacerJobEntry.JobStatus.Done);
                bp.removeJob(player, this);
                break;
            case Initializing:
            case Preparing:
            case Waiting:
                setStatus(BlockPlacerJobEntry.JobStatus.PlacingBlocks);
                break;
        }
        return System.currentTimeMillis() - start;
    }

}
