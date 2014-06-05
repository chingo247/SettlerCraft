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
package com.sc.api.structure.construction.async;

import com.sc.api.structure.construction.ConstructionProgress;
import com.sc.api.structure.construction.Structure;
import com.sc.api.structure.construction.StructureManager;
import com.sc.api.structure.event.structure.StructureCompleteEvent;
import com.sc.api.structure.persistence.service.StructureService;
import com.sk89q.worldedit.EditSession;
import org.bukkit.Bukkit;
import org.primesoft.asyncworldedit.blockPlacer.BlockPlacerJobEntry;
import org.primesoft.asyncworldedit.blockPlacer.IJobEntryListener;

/**
 * The default action to for
 *
 * @author Chingo
 */
public class ConstructionCallback implements SCJobCallback {

    private final String issuer;
    private final Long structureId;
    private final EditSession session;

    public ConstructionCallback(String issuer, Structure structure, EditSession session) {
        this.issuer = issuer;
        this.structureId = structure.getId();
        this.session = session;
    }

    @Override
    public void onJobAdded(final BlockPlacerJobEntry entry) {
        final StructureService ss = new StructureService();
        final Structure structure = ss.getStructure(structureId);
        final StructureManager structureManager = StructureManager.getInstance();
        ConstructionProgress progress = structure.getProgress();
        progress.setJobId(entry.getJobId());
        structureManager.putProgress(entry.getJobId(), progress);
        entry.addStateChangedListener(new IJobEntryListener() {

            @Override
            public void jobStateChanged(BlockPlacerJobEntry bpje) {
                if (bpje.getStatus() == BlockPlacerJobEntry.JobStatus.PlacingBlocks) {
                    ConstructionProgress progress = structure.getProgress();
                    if (!progress.isDemolishing()) {
                        progress.setProgressStatus(ConstructionProgress.State.BUILDING);
                    } else {
                        progress.setProgressStatus(ConstructionProgress.State.DEMOLISHING);
                    }
                    progress.setHasPlacedBlocks(true);
                    progress = ss.save(progress);
                    structureManager.putProgress(entry.getJobId(), progress);
                } else if (bpje.getStatus() == BlockPlacerJobEntry.JobStatus.Done) {
                    Bukkit.getPluginManager().callEvent(new StructureCompleteEvent(structure));
                    ConstructionProgress progress = structure.getProgress();
                    progress.setJobId(-1);
                    progress.setProgressStatus(ConstructionProgress.State.COMPLETE);
                    progress = ss.save(progress);
                    structureManager.putProgress(entry.getJobId(), progress);
                }
            }
        });
    }

    @Override
    public void onJobCanceled(BlockPlacerJobEntry entry) {
        final StructureService ss = new StructureService();
        final Structure structure = ss.getStructure(structureId);
        final StructureManager structureManager = StructureManager.getInstance();
        ConstructionProgress progress = structure.getProgress();
        progress.setProgressStatus(ConstructionProgress.State.STOPPED);
        progress = ss.save(progress);
        structureManager.putProgress(entry.getJobId(), progress);
    }

}
