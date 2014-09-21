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
package com.sc.module.structureapi.structure.construction;

import com.sc.module.structureapi.persistence.ConstructionSiteService;
import com.sc.module.structureapi.structure.Structure;
import com.sc.module.structureapi.structure.Structure.State;
import com.sc.module.structureapi.structure.StructureHologramManager;
import com.sc.module.structureapi.structure.construction.asyncworldedit.SCJobEntry;
import java.util.Date;
import java.util.UUID;
import org.primesoft.asyncworldedit.blockPlacer.IJobEntryListener;
import org.primesoft.asyncworldedit.blockPlacer.entries.JobEntry;
import org.primesoft.asyncworldedit.blockPlacer.entries.JobEntry.JobStatus;

/**
 * The default action to for
 *
 * @author Chingo
 */
public class ConstructionDemolisionCallback extends ConstructionCallback {

    ConstructionDemolisionCallback(UUID issuer, final Structure structure) {
        super(issuer, structure);
    }

    @Override
    public void onJobAdded(final SCJobEntry entry) {
        // Set JobId
        final ConstructionManager cm = ConstructionManager.getInstance();
        cm.getEntry(structure.getId()).setJobId(entry.getJobId());

        // Set state changeListener
        entry.addStateChangedListener(new IJobEntryListener() {

            @Override
            public void jobStateChanged(JobEntry bpje) {
                ConstructionSiteService siteService = new ConstructionSiteService();
                if (bpje.getStatus() == JobStatus.PlacingBlocks) {
                    siteService.setState(structure, State.DEMOLISHING);
                    StructureHologramManager.getInstance().updateHolo(structure);
                } else if (bpje.getStatus() == JobStatus.Done) {
                    // Set state to complete & Create timestamp of completion
                    siteService.setState(structure, State.REMOVED);
                    structure.getLogEntry().setRemovedAt(new Date());
                    siteService.save(structure);
                    StructureHologramManager.getInstance().updateHolo(structure);

                    // Reset JobID
                    cm.getEntry(structure.getId()).setJobId(-1);
                }
            }
        });
    }



}
