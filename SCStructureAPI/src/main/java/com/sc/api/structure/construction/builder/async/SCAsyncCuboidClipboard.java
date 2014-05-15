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
package com.sc.api.structure.construction.builder.async;

import com.sc.api.structure.construction.builder.SCLayeredCuboidClipBoard;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalEntity;
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
 * Modfied version of AsyncClipboard, layers are placed vertically
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

    public SCAsyncCuboidClipboard(String player, SCLayeredCuboidClipBoard parrent) {
        super(new SCCuboidClipboardWrapper(player, parrent));

        m_plugin = PluginMain.getInstance();
        m_schedule = m_plugin.getServer().getScheduler();
        m_clipboard = parrent;
        m_blockPlacer = m_plugin.getBlockPlacer();
        m_player = player;
        m_wrapper = m_plugin.getPlayerManager().getPlayer(player);
    }

    public LocalEntity[] pasteEntities(final Vector pos, SCJobCallback callback) {
        boolean isAsync = checkAsync(WorldeditOperations.paste);
        if (!isAsync) {
            return super.pasteEntities(pos);
        }

        final int jobId = getJobId();
        final CuboidClipboardWrapper cc = new CuboidClipboardWrapper(m_player, m_clipboard, jobId);
        final SCBlockPlacerJobEntry job = new SCBlockPlacerJobEntry(m_player, jobId, "pasteEntities");
        m_blockPlacer.addJob(m_player, job);


        m_schedule.runTaskAsynchronously(m_plugin, new SCClipBoardAsyncTask(cc, null, m_player, "pasteEntities",
                m_blockPlacer, job, callback) {
            @Override
            public void task(CuboidClipboard cc)
                    throws MaxChangedBlocksException {
                cc.pasteEntities(pos);
            }
        });

        return new LocalEntity[0];
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

    
    public void paste(final EditSession editSession, final Vector newOrigin,
                      final boolean noAir, SCJobCallback callback)
            throws MaxChangedBlocksException {
        boolean isAsync = checkAsync(WorldeditOperations.paste);
        if (!isAsync) {
            super.paste(editSession, newOrigin, noAir);
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

        m_schedule.runTaskAsynchronously(m_plugin, new SCClipBoardAsyncTask(cc, editSession, m_player, "paste",
                m_blockPlacer, job, callback) {
            @Override
            public void task(CuboidClipboard cc)
                    throws MaxChangedBlocksException {
                cc.paste(session, newOrigin, noAir);
                System.out.println("paste!");
            }
        });
    }

    @Override
    public void paste(EditSession editSession, Vector newOrigin, boolean noAir) throws MaxChangedBlocksException {
        paste(editSession, newOrigin, noAir, null);
    }

    public void paste(final EditSession editSession, final Vector newOrigin,
                      final boolean noAir, final boolean entities, SCJobCallback callback)
            throws MaxChangedBlocksException {
        boolean isAsync = checkAsync(WorldeditOperations.paste);
        if (!isAsync) {
            super.paste(editSession, newOrigin, noAir, entities);
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


        m_schedule.runTaskAsynchronously(m_plugin, new SCClipBoardAsyncTask(cc, editSession, m_player, "paste",
                m_blockPlacer, job, callback) {
            @Override
            public void task(CuboidClipboard cc)
                    throws MaxChangedBlocksException {
                System.out.println("paste!");
                cc.paste(session, newOrigin, noAir, entities);
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
