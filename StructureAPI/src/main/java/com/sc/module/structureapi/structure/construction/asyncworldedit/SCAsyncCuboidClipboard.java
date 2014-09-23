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

import com.sc.module.structureapi.structure.construction.ConstructionCallback;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import java.util.UUID;
import org.bukkit.scheduler.BukkitScheduler;
import org.primesoft.asyncworldedit.AsyncWorldEditMain;
import org.primesoft.asyncworldedit.ConfigProvider;
import org.primesoft.asyncworldedit.PlayerWrapper;
import org.primesoft.asyncworldedit.blockPlacer.BlockPlacer;
import org.primesoft.asyncworldedit.utils.WaitFor;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSession;
import org.primesoft.asyncworldedit.worldedit.CancelabeEditSession;
import org.primesoft.asyncworldedit.worldedit.CuboidClipboardWrapper;
import org.primesoft.asyncworldedit.worldedit.ProxyCuboidClipboard;
import org.primesoft.asyncworldedit.worldedit.WorldeditOperations;

/**
 * This is a modified version of AsyncWorldEdit's AsyncClipboard.
 *
 * @author SBPrime
 */
public class SCAsyncCuboidClipboard extends ProxyCuboidClipboard {

    /**
     * The player
     */
    private final UUID m_player;

    /**
     * Player wraper
     */
    private final PlayerWrapper m_wrapper;

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

    public SCAsyncCuboidClipboard(UUID player, CuboidClipboard parrent) {
        super(new ProxyCuboidClipboard(parrent));

        m_plugin = AsyncWorldEditMain.getInstance();
        m_schedule = m_plugin.getServer().getScheduler();
        m_clipboard = parrent;
        m_blockPlacer = m_plugin.getBlockPlacer();
        m_player = player;
        m_wrapper = m_plugin.getPlayerManager().getPlayer(player);
    }

    public void place(final EditSession editSession, final Vector pos,
            final boolean noAir, ConstructionCallback callback)
            throws MaxChangedBlocksException {
        boolean isAsync = checkAsync(WorldeditOperations.paste, editSession);
        if (!isAsync) {
            super.place(editSession, pos, noAir);
            return;
        }

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
        // MODIFIED START
        callback.onJobAdded(job);
        
        m_schedule.runTaskAsynchronously(m_plugin, new SCClipboardAsyncTask(cc, session, m_player, "place",
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
        // MODIFIED END
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
        return ConfigProvider.isAsyncAllowed(operation) && (m_wrapper == null || m_wrapper.getMode());
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
