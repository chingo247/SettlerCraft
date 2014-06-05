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

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import org.bukkit.scheduler.BukkitScheduler;
import org.primesoft.asyncworldedit.ConfigProvider;
import org.primesoft.asyncworldedit.PlayerWrapper;
import org.primesoft.asyncworldedit.PluginMain;
import org.primesoft.asyncworldedit.blockPlacer.BlockPlacer;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSession;
import org.primesoft.asyncworldedit.worldedit.CancelabeEditSession;
import org.primesoft.asyncworldedit.worldedit.CuboidClipboardWrapper;
import org.primesoft.asyncworldedit.worldedit.ProxyCuboidClipboard;
import org.primesoft.asyncworldedit.worldedit.WorldeditOperations;

/**
 * Modfied version of AsyncClipboard
 *
 * @author Chingo
 */
public class SCAsyncCuboidClipboard extends ProxyCuboidClipboard {

    /**
     * The player
     */
    private final String m_player;

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
    private final PluginMain m_plugin;

    public SCAsyncCuboidClipboard(String player, SmartClipBoard parrent) {
        super(new SCCuboidClipboardWrapper(player, parrent));

        m_plugin = PluginMain.getInstance();
        m_schedule = m_plugin.getServer().getScheduler();
        m_clipboard = parrent;
        m_blockPlacer = m_plugin.getBlockPlacer();
        m_player = player;
        m_wrapper = m_plugin.getPlayerManager().getPlayer(player);
    }

    public void place(final EditSession editSession, final Vector pos,
            final boolean noAir, SCJobCallback callback)
            throws MaxChangedBlocksException {
        boolean isAsync = checkAsync(WorldeditOperations.paste);
        if (!isAsync) {
            super.place(editSession, pos, noAir);
            return;
        }

        final int jobId = getJobId();
        final EditSession session;
        final CuboidClipboardWrapper cc = new CuboidClipboardWrapper(m_player, m_clipboard, jobId);
        final SCBlockPlacerJobEntry job;

        if (editSession instanceof AsyncEditSession) {
            AsyncEditSession aSession = (AsyncEditSession) editSession;
            session = new CancelabeEditSession(aSession, aSession.getAsyncMask(), jobId);
            job = new SCBlockPlacerJobEntry(m_player, (CancelabeEditSession) session, jobId, "place");
        } else {
            session = editSession;
            job = new SCBlockPlacerJobEntry(m_player, jobId, "place");
        }

        m_blockPlacer.addJob(m_player, job);

        m_schedule.runTaskAsynchronously(m_plugin, new SCClipBoardAsyncTask(cc, editSession, m_player, "place",
                m_blockPlacer, job, callback) {
                    @Override
                    public void task(CuboidClipboard cc)
                    throws MaxChangedBlocksException {
                        cc.place(session, pos, noAir);
                    }
                });
    }

    
    

    /**
     * This function checks if async mode is enabled for specific command
     *
     * @param operation
     */
    private boolean checkAsync(WorldeditOperations operation) {
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
