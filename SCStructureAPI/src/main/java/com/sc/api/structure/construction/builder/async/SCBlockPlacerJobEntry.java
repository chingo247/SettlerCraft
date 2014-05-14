/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.api.structure.construction.builder.async;

import org.primesoft.asyncworldedit.blockPlacer.BlockPlacer;
import org.primesoft.asyncworldedit.blockPlacer.BlockPlacerJobEntry;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSession;
import org.primesoft.asyncworldedit.worldedit.CancelabeEditSession;

/**
 *
 * @author Chingo
 */
public class SCBlockPlacerJobEntry extends BlockPlacerJobEntry {
    private String player;

    public SCBlockPlacerJobEntry(AsyncEditSession editSession, CancelabeEditSession cEditSession, int jobId, String name) {
        super(editSession, cEditSession, jobId, name);
        this.player = editSession.getPlayer();
    }

    public SCBlockPlacerJobEntry(String player, CancelabeEditSession cEditSession, int jobId, String name) {
        super(player, cEditSession, jobId, name);
        this.player = player;
    }

    public SCBlockPlacerJobEntry(String player, int jobId, String name) {
        super(player, jobId, name);
        this.player = player;
    }

    @Override
    public void Process(BlockPlacer bp) {
        switch (getStatus()) {
            case Done:
                bp.removeJob(player, this);
                return;
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
    }
    
    
   
}
