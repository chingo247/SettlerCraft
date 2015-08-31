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
package com.chingo247.structureapi.construction.task;

import com.chingo247.structureapi.construction.ConstructionManager;
import com.chingo247.structureapi.construction.ConstructionEntry;
import com.chingo247.settlercraft.core.event.async.AsyncEventManager;
import com.chingo247.structureapi.construction.event.StructureTaskCancelledEvent;
import com.chingo247.structureapi.construction.event.StructureTaskCompleteEvent;
import com.chingo247.structureapi.exception.ConstructionException;
import com.google.common.base.Preconditions;
import java.util.UUID;

/**
 *
 * @author Chingo
 */
public abstract class StructureTask {

    private ConstructionEntry constructionEntry;
    private UUID uuid;
    private boolean cancelled = false;
    private boolean failed = false;
    private boolean finished = false;
    private String action;

    public StructureTask(String action, ConstructionEntry constructionEntry) {
        Preconditions.checkNotNull(constructionEntry, "ConstructionEntry may not be null");
        Preconditions.checkNotNull(action, "Action may not null");
        Preconditions.checkArgument(!"".equals(action), "Action may not be empty");
        this.constructionEntry = constructionEntry;
        this.uuid = UUID.randomUUID();
        this.action = action;
    }

    public ConstructionEntry getConstructionEntry() {
        return constructionEntry;
    }

    public String getAction() {
        return action;
    }

    public UUID getUUID() {
        return uuid;
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void setFailed(boolean failed) {
        this.failed = false;
    }

    public boolean hasFailed() {
        return failed;
    }

    public void start() {
        _start();
    }

    protected abstract void _start();

    /**
     * Cancels this task. This method is called by the
     * {@link StructureTask#cancel()} and should never be called from any other
     * class or method. This methods solely serves to offer an implementation of
     * the cancel method.
     */
    protected abstract void _cancel();

    public synchronized void cancel() throws ConstructionException {
        if (!cancelled) {
            setCancelled(true);
            _cancel();
            ConstructionManager.getInstance().remove(constructionEntry);
            finish();
        } 
    }

    /**
     * *
     * Indicate that this task has finished
     */
    public synchronized void finish() {
        
        if (!finished) {
            finished = true;
            if (isCancelled()) {
                AsyncEventManager.getInstance().post(new StructureTaskCancelledEvent(this));
            } else {
                AsyncEventManager.getInstance().post(new StructureTaskCompleteEvent(this));
            }

            if(!isCancelled() && !failed) {
                constructionEntry.proceed();
            }
        }
    }

}
