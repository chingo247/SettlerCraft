/*
 * Copyright (C) 2014 Chingo247
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
package com.chingo247.settlercraft.structure.construction;

import com.chingo247.settlercraft.structure.entities.structure.Structure;
import com.chingo247.settlercraft.structure.entities.structure.Structure;
import java.util.UUID;

/**
 *
 * @author Chingo
 */
public class ConstructionEntry {

//    private final Map<Long, ConstructionTask> tasks; // ConstructionSiteID - ConstructionTask
    private UUID player;
    private UUID fenceUUID; // Used to place a fence
    private final Structure structure;
    private int jobId = -1;
    private boolean demolishing = false;
    private int buildmode = 0;
    private int demolishmode = 0;
    private boolean canceled = false;

    ConstructionEntry(Structure structure) {
        this.structure = structure;
    }

    public Structure getStructure() {
        return structure;
    }
    
    public void setPlayer(UUID player) {
        this.player = player;
    }

    public UUID getFenceUUID() {
        return fenceUUID;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public boolean isCanceled() {
        return canceled;
    }
    
    public void setFence(UUID uuid) {
        this.fenceUUID = uuid;
    }

    public UUID getPlayer() {
        return player;
    }

    public void setDemolishing(boolean demolishing) {
        this.demolishing = demolishing;
    }

    public boolean isDemolishing() {
        return demolishing;
    }
    
    public void setBuildmode(int buildmode) {
        this.buildmode = buildmode;
    }

    public void setDemolishmode(int demolishmode) {
        this.demolishmode = demolishmode;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public int getBuildmode() {
        return buildmode;
    }

    public int getDemolishmode() {
        return demolishmode;
    }

//    public ConstructionSite getStructure() {
//        return constructionSite;
//    }

    public int getJobId() {
        return jobId;
    }

}
