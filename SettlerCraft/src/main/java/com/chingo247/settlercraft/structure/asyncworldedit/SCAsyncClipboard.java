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
package com.chingo247.settlercraft.structure.asyncworldedit;

import com.chingo247.settlercraft.structure.construction.ConstructionCallback;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import org.bukkit.scheduler.BukkitScheduler;
import org.primesoft.asyncworldedit.AsyncWorldEditMain;
import org.primesoft.asyncworldedit.PlayerEntry;
import org.primesoft.asyncworldedit.blockPlacer.BlockPlacer;
import org.primesoft.asyncworldedit.configuration.ConfigProvider;
import org.primesoft.asyncworldedit.utils.WaitFor;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSession;
import org.primesoft.asyncworldedit.worldedit.CancelabeEditSession;
import org.primesoft.asyncworldedit.worldedit.CuboidClipboardWrapper;
import org.primesoft.asyncworldedit.worldedit.ProxyCuboidClipboard;
import org.primesoft.asyncworldedit.worldedit.WorldeditOperations;

/**
 *
 * @author Chingo
 */
public class SCAsyncClipboard extends ProxyCuboidClipboard {

    /**
     * The player
     */
    private final PlayerEntry m_player;

    /**
     * The blocks placer
     */
    private final BlockPlacer m_blockPlacer;

    /**
     * Parent clipboard
     */
    private final CuboidClipboard m_clipboard;

    /**
     * Bukkit schedule
     */
    private final BukkitScheduler m_schedule;

    /**
     * The plugin
     */
    private final AsyncWorldEditMain m_plugin;

    public SCAsyncClipboard(PlayerEntry player, CuboidClipboard parrent) {
        super(new ProxyCuboidClipboard(parrent));

        m_plugin = AsyncWorldEditMain.getInstance();
        m_schedule = m_plugin.getServer().getScheduler();
        m_clipboard = parrent;
        m_blockPlacer = m_plugin.getBlockPlacer();
        m_player = player;
    }

    public void place(final EditSession editSession, final Vector pos,
            final boolean noAir, ConstructionCallback callback)
            throws MaxChangedBlocksException {
        

        final int jobId = getJobId();
        final EditSession session;
        final CuboidClipboardWrapper cc = new CuboidClipboardWrapper(m_player, m_clipboard, jobId);
        final SCJobEntry job;
        final WaitFor wait;

        if (editSession instanceof AsyncEditSession) {
            AsyncEditSession aSession = (AsyncEditSession) editSession;
            wait = aSession.getWait();
            session = new CancelabeEditSession(aSession, aSession.getMask(), jobId);
            job = new SCJobEntry(m_player, (CancelabeEditSession) session, jobId, "place");
        } else {
            session = editSession;
            wait = null;
            job = new SCJobEntry(m_player, jobId, "place");
        }

        m_blockPlacer.addJob(m_player, job);
        callback.onJobAdded(job);

        m_schedule.runTaskAsynchronously(m_plugin, new SCAsyncClipboardTask(cc, session, m_player, "place",
                m_blockPlacer, job, callback) {
                    @Override
                    public void task(CuboidClipboard cc)
                    throws MaxChangedBlocksException {
                        if (wait != null) {
                            wait.checkAndWait(null);
                        }
                        cc.place(session, pos, noAir);
                    }
                });

    }

    /**
     * This function checks if async mode is enabled for specific command
     *
     * @param operation
     */
    private boolean checkAsync(WorldeditOperations operation, EditSession session) {
        if (session != null && session instanceof AsyncEditSession) {
            return ((AsyncEditSession) session).checkAsync(operation);
        }
        return ConfigProvider.isAsyncAllowed(operation) && m_player.getMode();
    }

    /**
     * Get next job id for current player
     *
     * @return Job id
     */
    private int getJobId() {
        return m_blockPlacer.getJobId(m_player);
    }
}