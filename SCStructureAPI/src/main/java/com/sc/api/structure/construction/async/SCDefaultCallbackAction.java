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

import com.sc.api.structure.entity.progress.ConstructionTask;
import com.sc.api.structure.entity.progress.ConstructionTask.State;
import com.sc.api.structure.entity.Structure;
import com.sc.api.structure.event.structure.StructureCompleteEvent;
import com.sc.api.structure.persistence.service.ConstructionService;
import com.sk89q.worldedit.EditSession;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.primesoft.asyncworldedit.blockPlacer.BlockPlacerJobEntry;
import org.primesoft.asyncworldedit.blockPlacer.IJobEntryListener;

/**
 * The default action to for
 *
 * @author Chingo
 */
public class SCDefaultCallbackAction implements SCJobCallback {

    private final String placer;
    private final Structure structure;
    private final EditSession session;
    private ConstructionTask task;

    public SCDefaultCallbackAction(String placer, Structure structure, ConstructionTask task, EditSession session) {
        this.placer = placer;
        this.structure = structure;
        this.session = session;
        this.task = task;
    }

    @Override
    public void onJobAdded(BlockPlacerJobEntry entry) {
        final ConstructionService constructionService = new ConstructionService();

        entry.addStateChangedListener(new IJobEntryListener() {

            @Override
            public void jobStateChanged(BlockPlacerJobEntry bpje) {
                if (bpje.getStatus() == BlockPlacerJobEntry.JobStatus.PlacingBlocks) {
                    Player ply = Bukkit.getPlayer(placer);
                    if (ply != null && ply.isOnline()) {
                        ply.sendMessage(ChatColor.YELLOW + "Building:  " + ChatColor.BLUE + structure.getPlan().getDisplayName());
                    }
                    task = constructionService.updateStatus(task, State.QUEUED);
                } else if (bpje.getStatus() == BlockPlacerJobEntry.JobStatus.Done) {
                    Player ply = Bukkit.getPlayer(placer);
                    if (ply != null && ply.isOnline()) {
                        ply.sendMessage(ChatColor.YELLOW + "Construction complete: " + ChatColor.BLUE + structure.getPlan().getDisplayName());
                        ply.playSound(ply.getLocation(), Sound.NOTE_SNARE_DRUM, 2, 0);
                    }
                    Bukkit.getPluginManager().callEvent(new StructureCompleteEvent(structure));
                    task = constructionService.updateStatus(task, State.COMPLETE);
                }
            }
        });
    }

    @Override
    public void onJobCanceled(BlockPlacerJobEntry entry) {
        Player ply = Bukkit.getPlayer(placer);
        if (ply != null && ply.isOnline()) {
            ply.sendMessage(ChatColor.RED + "Construction canceled: " + ChatColor.BLUE + structure.getPlan().getDisplayName());
        }

        entry.getEditSession().undo(entry.getEditSession());
        session.undo(session);

        final ConstructionService constructionService = new ConstructionService();
        constructionService.updateStatus(task, State.CANCELED);
    }

}
