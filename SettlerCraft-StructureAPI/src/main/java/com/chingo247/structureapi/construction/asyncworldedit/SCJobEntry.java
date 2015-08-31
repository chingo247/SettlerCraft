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
 
/*
 * AsyncWorldEdit a performance improvement plugin for Minecraft WorldEdit plugin.
 * Copyright (c) 2014, SBPrime <https://github.com/SBPrime/>
 * Copyright (c) AsyncWorldEdit contributors
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted free of charge provided that the following 
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution,
 * 3. Redistributions of source code, with or without modification, in any form 
 *    other then free of charge is not allowed,
 * 4. Redistributions in binary form in any form other then free of charge is 
 *    not allowed.
 * 5. Any derived work based on or containing parts of this software must reproduce 
 *    the above copyright notice, this list of conditions and the following 
 *    disclaimer in the documentation and/or other materials provided with the 
 *    derived work.
 * 6. The original author of the software is allowed to change the license 
 *    terms or the entire license of the software as he sees fit.
 * 7. The original author of the software is allowed to sublicense the software 
 *    or its parts using any license terms he sees fit.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.chingo247.structureapi.construction.asyncworldedit;

import com.chingo247.settlercraft.core.event.async.AsyncEventManager;
import com.chingo247.structureapi.event.async.StructureJobStartedEvent;
import java.util.ArrayList;
import java.util.List;
import org.primesoft.asyncworldedit.api.blockPlacer.IBlockPlacer;
import org.primesoft.asyncworldedit.api.blockPlacer.IJobEntryListener;
import org.primesoft.asyncworldedit.blockPlacer.entries.JobEntry;
import org.primesoft.asyncworldedit.playerManager.PlayerEntry;
import org.primesoft.asyncworldedit.worldedit.CancelabeEditSession;

/**
 * JobEntry NON-TALKATIVE version
 * @author Chingo
 */
public class SCJobEntry extends JobEntry {

    /**
     * Job name
     */
    private final String m_name;
    
    /**
     * Is the job status
     */
    private JobStatus m_status;
    
    /**
     * Cancelable edit session
     */
    private final CancelabeEditSession m_cEditSession;
    
    /**
     * The player name
     */
    private final PlayerEntry m_player;

    /**
     * Is the async task done
     */
    private boolean m_taskDone;
    
    /**
     * SettlerCraft StructureAPI task ID
     */
    private long m_taskId;
    
    private boolean m_canceled = false;
    
    private boolean m_demolishing = false;

    /**
     * All job state changed events
     */
    private final List<IJobEntryListener> m_jobStateChanged;
    


    
    @Override
    public boolean isDemanding() {
        return false;
    }
    
    @Override
    public JobStatus getStatus() {
        return m_status;
    }

        /**
     * Set the job state
     * @param newStatus 
     */
    @Override
    public void setStatus(JobStatus newStatus) {
        int newS = newStatus.getSeqNumber();
        int oldS = m_status.getSeqNumber();

        if (newS < oldS) {
            return;
        }
        m_status = newStatus;
        callStateChangedEvents();
    }

    @Override
    public void addStateChangedListener(IJobEntryListener listener) {
        if (listener == null) {
            return;
        }

        synchronized (m_jobStateChanged) {
            if (!m_jobStateChanged.contains(listener)) {
                m_jobStateChanged.add(listener);
            }
        }
    }
    
    public void addStateChangeListener(SCIJobListener listener) {
        addStateChangedListener(listener);
    }
    
    
        

    /**
     * Create new instance of the class
     * @param player player uuid
     * @param jobId job id
     * @param name operation name
     * @param taskID
     */
    public SCJobEntry(PlayerEntry player, int jobId, String name, long taskID) {
        super(player, jobId, name);
        m_player = player;
        m_name = name;
        m_status = JobStatus.Initializing;
        m_cEditSession = null;
        m_jobStateChanged = new ArrayList<IJobEntryListener>();
        m_taskId = taskID;
        
        
        
    }

    
    /**     
     * Create new instance of the class
     * @param player player uuid
     * @param jobId job id
     * @param name operation name
     * @param cEditSession the cancelable edit session
     * @param taskID
     */
    public SCJobEntry(PlayerEntry player, 
            CancelabeEditSession cEditSession,
            int jobId, String name, long taskID) {
        super(player, cEditSession, jobId, name);

        m_player = player;
        m_name = name;
        m_status = JobStatus.Initializing;
        m_cEditSession = cEditSession;
        m_jobStateChanged = new ArrayList<IJobEntryListener>();
        m_taskId = taskID;
        
        
        
    }

    /**
     * Gets the StructureAPI taskID
     * @return 
     */
    public long getTaskID() {
        return m_taskId;
    }
    
    public boolean isCanceled() {
        return m_canceled;
    }
    
    void setCanceled(boolean canceled) {
        m_canceled = canceled;
    }
    
    void setDemolishing(boolean demolishing) {
        m_demolishing = demolishing;
    }

    public boolean isDemolishing() {
        return m_demolishing;
    }
    
    
    
    
    @Override
    public String toString() {
        return  "[" + getJobId() + "] " + getName();
    }

    @Override
    public boolean process(IBlockPlacer bp) {
        final PlayerEntry player = m_player;
        
        
        switch (m_status) {
            case Done:
                bp.removeJob(player, this);
                return true;
            case PlacingBlocks:
                setStatus(JobStatus.Done);
                bp.removeJob(player, this);
//                AsyncEventManager.getInstance().post(new StructureJobCompleteEvent(m_taskId, getJobId()));
                break;
            case Initializing:
            case Preparing:
            case Waiting:
                
                setStatus(JobStatus.PlacingBlocks);
                AsyncEventManager.getInstance().post(new StructureJobStartedEvent(m_taskId, getJobId()));
                break;
        }

        
        return true;
    }

    
    /**
     * Inform the listener of state changed
     */
    private void callStateChangedEvents() {
        synchronized (m_jobStateChanged) {
            for (IJobEntryListener listener : m_jobStateChanged) {
                listener.jobStateChanged(this);
            }
        }
    }
}