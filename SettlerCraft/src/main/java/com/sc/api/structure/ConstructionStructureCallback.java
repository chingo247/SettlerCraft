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
package com.sc.api.structure;

import com.sc.event.structure.StructureCompleteEvent;
import com.sc.event.structure.StructureConstructionEvent;
import com.sc.event.structure.StructureDemolisionEvent;
import com.sc.event.structure.StructureRemovedEvent;
import com.sc.persistence.service.StructureService;
import com.sc.plugin.SettlerCraft;
import com.sk89q.worldedit.EditSession;
import java.sql.Timestamp;
import java.util.Date;
import org.bukkit.Bukkit;
import org.primesoft.asyncworldedit.blockPlacer.BlockPlacerJobEntry;
import org.primesoft.asyncworldedit.blockPlacer.IJobEntryListener;

/**
 * The default action to for
 *
 * @author Chingo
 */
public class ConstructionStructureCallback implements JobCallback {

    private final String issuer;
    private final Long structureId;
    private final EditSession session;
    private final int count;
    private int jobId;
    

    public ConstructionStructureCallback(String issuer, Structure structure, EditSession session) {
        this.issuer = issuer;
        this.structureId = structure.getId();
        this.session = session;
        this.count = StructurePlanManager.getInstance().getSchematic(structure.getPlan().getChecksum()).getBlocks();
    }

    @Override
    public void onJobAdded(final BlockPlacerJobEntry entry) {
        final StructureService ss = new StructureService();
        final Structure structure = ss.getStructure(structureId);
        final StructureManager structureManager = StructureManager.getInstance();
        ConstructionProcess progress = structure.getProgress();
        progress.setJobId(entry.getJobId());
        progress = ss.save(progress);
        this.jobId = entry.getJobId();
        structureManager.putProgress(jobId, progress);
        entry.addStateChangedListener(new IJobEntryListener() {

            @Override
            public void jobStateChanged(BlockPlacerJobEntry bpje) {
                if (bpje.getStatus() == BlockPlacerJobEntry.JobStatus.PlacingBlocks) {
                    ConstructionProcess progress = structure.getProgress();
                    if (!progress.isDemolishing()) {
                        if(progress.getStatus() != ConstructionProcess.State.BUILDING) {
                        Bukkit.getPluginManager().callEvent(new StructureConstructionEvent(structure));
                        progress.setProgressStatus(ConstructionProcess.State.BUILDING);
                        }
                        
                    } else {
                        if(progress.getStatus() != ConstructionProcess.State.DEMOLISHING) {
                        progress.setProgressStatus(ConstructionProcess.State.DEMOLISHING);
                        Bukkit.getPluginManager().callEvent(new StructureDemolisionEvent(structure));
                        }
                    }
                    progress.setHasPlacedBlocks(true);
                    progress = ss.save(progress);
                    structureManager.putProgress(jobId, progress);
                } else if (bpje.getStatus() == BlockPlacerJobEntry.JobStatus.Done) {
                    ConstructionProcess progress = structure.getProgress();
                    progress.setJobId(-1);
                    if(!progress.isDemolishing()) {
                        if(progress.getStatus() != ConstructionProcess.State.COMPLETE) {
                            Bukkit.getPluginManager().callEvent(new StructureCompleteEvent(structure));
                            progress.setProgressStatus(ConstructionProcess.State.COMPLETE);
                            progress.setCompletedAt(new Timestamp(new Date().getTime()));
                            progress = ss.save(progress);
                            structureManager.removeProgress(jobId, progress);
                        }
                    } else {
                        if(progress.getStatus() != ConstructionProcess.State.REMOVED) {
                             progress.setProgressStatus(ConstructionProcess.State.REMOVED);
                             structure.setRefundValue(structure.getRefundValue() * SettlerCraft.getSettlerCraft().getRefundPercentage());
                             if(structure.getRefundValue() > 0) {
                                structureManager.refund(structure);
                             }
                             structure.setRefundValue(0d);
                             ss.save(structure);
                             Bukkit.getPluginManager().callEvent(new StructureRemovedEvent(structure));
                             structureManager.removeHolo(structureId);
                             structureManager.removeRegion(structure);
                             structureManager.removeProgress(jobId, progress);
                        }
                       
                        
                        
                       
                    }
                   
                }
            }
        });
    }

    @Override
    public void onJobCanceled(BlockPlacerJobEntry entry) {
        final StructureService ss = new StructureService();
        final Structure structure = ss.getStructure(structureId);
        final StructureManager structureManager = StructureManager.getInstance();
        ConstructionProcess progress = structure.getProgress();
        progress.setProgressStatus(ConstructionProcess.State.STOPPED);
        progress = ss.save(progress);
        structureManager.putProgress(entry.getJobId(), progress);
    }

}
