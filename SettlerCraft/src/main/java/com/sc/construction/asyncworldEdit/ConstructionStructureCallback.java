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

import com.sc.construction.structure.Structure;
import com.sc.construction.structure.StructureConstructionManager;
import com.sc.construction.structure.StructureManager;
import com.sc.event.structure.StructureCompleteEvent;
import com.sc.event.structure.StructureConstructionEvent;
import com.sc.event.structure.StructureDemolisionEvent;
import com.sc.event.structure.StructureRemovedEvent;
import com.sc.persistence.StructureService;
import com.sc.plugin.ConfigProvider;
import com.sk89q.worldedit.EditSession;
import java.sql.Timestamp;
import java.util.Date;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.primesoft.asyncworldedit.blockPlacer.BlockPlacerJobEntry;
import org.primesoft.asyncworldedit.blockPlacer.IJobEntryListener;

/**
 * The default action to for
 *
 * @author Chingo
 */
public class ConstructionStructureCallback implements JobCallback {

    private final Player tasker;
    private final Long structureId;
    private final EditSession session;
    private int jobId;
    

    public ConstructionStructureCallback(Player issuer, Structure structure, EditSession session) {
        this.tasker = issuer;
        this.structureId = structure.getId();
        this.session = session;
    }

    @Override
    public void onJobAdded(final BlockPlacerJobEntry entry) {
        final StructureService ss = new StructureService();
        final Structure structure = ss.getStructure(structureId);
        final StructureConstructionManager scm = StructureConstructionManager.getInstance();
        final StructureManager sm = StructureManager.getInstance();
        ConstructionProcess progress = structure.getProgress();
        progress.setJobId(entry.getJobId());
        progress = ss.save(progress);
        this.jobId = entry.getJobId();
        
        // JobID added to ConstructionManager
        scm.putProcess(tasker, jobId, progress);
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
//                    progress.setHasPlacedBlocks(true);
                    progress = ss.save(progress);
                    scm.putProcess(tasker, jobId, progress);
                } else if (bpje.getStatus() == BlockPlacerJobEntry.JobStatus.Done) {
                    ConstructionProcess progress = structure.getProgress();
                    progress.setJobId(-1);
                    if(!progress.isDemolishing()) {
                        if(progress.getStatus() != ConstructionProcess.State.COMPLETE) {
                            Bukkit.getPluginManager().callEvent(new StructureCompleteEvent(structure));
                            progress.setProgressStatus(ConstructionProcess.State.COMPLETE);
                            progress.setCompletedAt(new Timestamp(new Date().getTime()));
                            progress = ss.save(progress);
                            scm.putProcess(tasker, jobId, progress);
                            scm.removeProcess(tasker, jobId);
                        }
                    } else {
                        if(progress.getStatus() != ConstructionProcess.State.REMOVED) {
                             progress.setProgressStatus(ConstructionProcess.State.REMOVED);
                             progress.setRemovedAt(new Timestamp(new Date().getTime()));
                             structure.setRefundValue(structure.getRefundValue() * ConfigProvider.getInstance().getRefundPercentage());
                             if(structure.getRefundValue() > 0) {
                                sm.refund(structure);
                             }
                             structure.setRefundValue(0d);
                             Bukkit.getPluginManager().callEvent(new StructureRemovedEvent(structure));
                             scm.removeHolo(structureId);
                             sm.removeRegion(structure);
                             scm.removeProcess(tasker, jobId);
                             ss.save(structure);
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
        final StructureConstructionManager scm = StructureConstructionManager.getInstance();
        ConstructionProcess progress = structure.getProgress();
        progress.setProgressStatus(ConstructionProcess.State.STOPPED);
        progress = ss.save(progress);
        scm.putProcess(tasker, entry.getJobId(), progress);
    }

}
