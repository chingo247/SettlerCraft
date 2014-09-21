/*
 * The MIT License
 *
 * Copyright 2013 SBPrime.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sc.module.structureapi.structure.construction.asyncworldedit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.primesoft.asyncworldedit.blockPlacer.BlockPlacer;
import org.primesoft.asyncworldedit.blockPlacer.IJobEntryListener;
import org.primesoft.asyncworldedit.blockPlacer.entries.JobEntry;
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
    private final UUID m_player;

    /**
     * Is the async task done
     */
    private boolean m_taskDone;

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
    
    
        

    /**
     * Create new instance of the class
     * @param player player uuid
     * @param jobId job id
     * @param name operation name
     */
    public SCJobEntry(UUID player, int jobId, String name) {
        super(player, jobId, name);
        m_player = player;
        m_name = name;
        m_status = JobStatus.Initializing;
        m_cEditSession = null;
        m_jobStateChanged = new ArrayList<IJobEntryListener>();
    }

    
    /**     
     * Create new instance of the class
     * @param player player uuid
     * @param jobId job id
     * @param name operation name
     * @param cEditSession the cancelable edit session
     */
    public SCJobEntry(UUID player, 
            CancelabeEditSession cEditSession,
            int jobId, String name) {
        super(player, cEditSession, jobId, name);

        m_player = player;
        m_name = name;
        m_status = JobStatus.Initializing;
        m_cEditSession = cEditSession;
        m_jobStateChanged = new ArrayList<IJobEntryListener>();
    }

    @Override
    public String toString() {
        return ChatColor.WHITE + "[" + getJobId() + "] " + getName();
    }

    @Override
    public boolean Process(BlockPlacer bp) {
        final UUID player = m_player;
        
        
        switch (m_status) {
            case Done:
                
                bp.removeJob(player, this);
                return true;
            case PlacingBlocks:
                setStatus(JobStatus.Done);
                bp.removeJob(player, this);
                break;
            case Initializing:
            case Preparing:
            case Waiting:
                setStatus(JobStatus.PlacingBlocks);
                
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