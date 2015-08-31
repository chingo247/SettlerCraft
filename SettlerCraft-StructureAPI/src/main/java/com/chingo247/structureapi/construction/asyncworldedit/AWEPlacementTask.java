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
package com.chingo247.structureapi.construction.asyncworldedit;

import com.chingo247.settlercraft.core.event.async.AsyncEventManager;
import com.chingo247.structureapi.construction.ConstructionEntry;
import com.chingo247.structureapi.construction.StructureTask;
import com.chingo247.structureapi.construction.event.StructureTaskCancelledEvent;
import com.chingo247.structureapi.construction.event.StructureTaskStartEvent;
import com.chingo247.structureapi.event.async.StructureJobAddedEvent;
import com.chingo247.structureapi.structure.plan.placement.Placement;
import com.chingo247.structureapi.structure.plan.placement.options.Options;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import java.util.UUID;
import org.primesoft.asyncworldedit.AsyncWorldEditMain;
import org.primesoft.asyncworldedit.blockPlacer.entries.JobEntry;
import org.primesoft.asyncworldedit.playerManager.PlayerEntry;

/**
 * AWE Placement task, places a placement by using AsyncWorldEdit. Note this
 * task needs to be executed outside of the main thread!
 *
 * @author Chingo
 * @param <T> The Options Type
 */
public class AWEPlacementTask<T extends Options> extends StructureTask {

    private UUID uuid;
    private Placement placement;
    private boolean canceled = false;
    private PlayerEntry playerEntry;
    private Vector position;
    private EditSession editSession;
    private T options;
    private int jobId;

    private boolean checked;

    /**
     * Constructor.
     *
     * @param connstructionEntry The constructionEntry
     * @param placement The placement
     * @param playerEntry The playerEntry
     * @param editSession The editsession
     * @param position The position
     * @param options The options to use when placing
     */
    public AWEPlacementTask(String action, ConstructionEntry connstructionEntry, Placement placement, PlayerEntry playerEntry, EditSession editSession, Vector position, T options) {
        super(action, connstructionEntry);
        this.uuid = UUID.randomUUID();
        this.playerEntry = playerEntry;
        this.position = position;
        this.placement = placement;
        this.options = options;
        this.jobId = -1;
        this.editSession = editSession;
    }

    void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    void setJobId(int id) {
        this.jobId = id;
    }

    public int getJobId() {
        return jobId;
    }

    public boolean isCanceled() {
        return canceled;
    }

    boolean isChecked() {
        return checked;
    }

    void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    protected void _start() {
        System.out.println("Starting place task!");
        final StructureTask t = this;
        AWEPlacement p = new AWEPlacement(playerEntry, placement, uuid, new IAWECallback() {

            @Override
            public void onJobAdded(AWEJobEntry job) {
                System.out.println("On job added!");
                setJobId(job.getJobId());
                AWEJobManager.getInstance().register(AWEPlacementTask.this);
                AsyncEventManager.getInstance().post(new StructureJobAddedEvent(getConstructionEntry().getStructure().getId(), jobId, playerEntry.getUUID(), canceled));
            }

            @Override
            public void onCancelled() {
                _cancel();
            }

            @Override
            public void onStarted() {
                AsyncEventManager.getInstance().post(new StructureTaskStartEvent(t));
            }
        }
        );

        System.out.println("[AWEPlacementTask]: Performing " + getAction());

        p.place(editSession, position, options);
    }

    @Override
    protected void _cancel() {
        canceled = true;
        JobEntry e = AsyncWorldEditMain.getInstance().getBlockPlacer().getJob(playerEntry, jobId);
        if(e.getStatus() != JobEntry.JobStatus.Canceled) {
           AsyncWorldEditMain.getInstance().getBlockPlacer().cancelJob(playerEntry, jobId);
        } 
        AsyncEventManager.getInstance().post(new StructureTaskCancelledEvent(this));
    }

}
