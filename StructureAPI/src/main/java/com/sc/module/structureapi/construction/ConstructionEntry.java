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
package com.sc.module.structureapi.construction;

import com.sc.module.structureapi.structure.ConstructionSite;
import com.sk89q.worldedit.CuboidClipboard;
import java.util.UUID;

/**
 *
 * @author Chingo
 */
public class ConstructionEntry {

//    private final Map<Long, ConstructionTask> tasks; // ConstructionSiteID - ConstructionTask
    private UUID player;
    private final CuboidClipboard cuboidClipboard;
    private final ConstructionSite constructionSite;
    private int jobId = -1;
    private boolean demolishing = false;
    private int buildmode = 0;
    private int demolishmode = 0;

    ConstructionEntry(ConstructionSite constructionSite, CuboidClipboard cuboidClipboard) {
        this.cuboidClipboard = cuboidClipboard;
        this.constructionSite = constructionSite;
    }

    public ConstructionSite getConstructionSite() {
        return constructionSite;
    }
    
    void setPlayer(UUID player) {
        this.player = player;
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

//    public ConstructionSite getConstructionSite() {
//        return constructionSite;
//    }

    public int getJobId() {
        return jobId;
    }

    public CuboidClipboard getCuboidClipboard() {
        return cuboidClipboard;
    }

}
