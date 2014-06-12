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
package com.sc.construction.async;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.MaxChangedBlocksException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.primesoft.asyncworldedit.blockPlacer.BlockPlacer;
import org.primesoft.asyncworldedit.blockPlacer.BlockPlacerJobEntry;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSession;
import org.primesoft.asyncworldedit.worldedit.CancelabeEditSession;

/**
 * Silent non-standard-talkative version of the original see ClipboardAsyncTask
 *
 * @author Chingo
 */
public abstract class SCClipBoardAsyncTask extends BukkitRunnable {

    /**
     * Command name
     */
    private final String command;
    /**
     * Clipboard
     */
    private final CuboidClipboard clipboard;
    /**
     * The player
     */
    private final String player;
    private final BlockPlacer blockPlacer;
    private final SCBlockPlacerJobEntry job;
    private final AsyncEditSession editSession;
    private final JobCallback callback;

    public SCClipBoardAsyncTask(final CuboidClipboard clipboard, final AsyncEditSession editSession,
            final String player, final String commandName, BlockPlacer blocksPlacer,
            SCBlockPlacerJobEntry job, JobCallback callback) {
        Preconditions.checkNotNull(editSession);
        this.clipboard = clipboard;
        this.player = player;
        this.command = commandName;
        this.blockPlacer = blocksPlacer;
        this.job = job;
        this.callback = callback;
        this.editSession = editSession;
        this.editSession.addAsync(job);
    }

    @Override
    public void run() {
        try {
            job.setStatus(BlockPlacerJobEntry.JobStatus.Preparing);
//            if (ConfigProvider.isTalkative()) {
//                PluginMain.say(m_player, ChatColor.LIGHT_PURPLE + "Running " + ChatColor.WHITE
//                        + m_command + ChatColor.LIGHT_PURPLE + " in full async mode.");
//            }
            blockPlacer.addTasks(player, job);
            if (callback != null) {
                callback.onJobAdded(job);
            }
            task(clipboard);

            if (editSession != null && editSession.isQueueEnabled()) {
                editSession.flushQueue();
            }
            job.setStatus(BlockPlacerJobEntry.JobStatus.Waiting);
            blockPlacer.addTasks(player, job);
        } catch (MaxChangedBlocksException ex) {
            Bukkit.getPlayer(player).sendMessage(ChatColor.RED + "Maximum block change limit has been reached");
        } catch (IllegalArgumentException ex) {
            if (ex.getCause() instanceof CancelabeEditSession.SessionCanceled) {
//                PluginMain.say(m_player, ChatColor.LIGHT_PURPLE + "Job canceled.");
                if (callback != null) {
                    callback.onJobCanceled(job);
                }
            }
        }

        job.taskDone();
        editSession.removeAsync(job);
    }

    /**
     * Task to run
     *
     * @param clipboard
     * @throws com.sk89q.worldedit.MaxChangedBlocksException
     */
    public abstract void task(CuboidClipboard clipboard)
            throws MaxChangedBlocksException;
}
