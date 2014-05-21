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
package com.sc.api.structure.construction.builder.strategies;

import com.sc.api.structure.construction.builder.async.SCJobCallback;
import com.sc.api.structure.construction.progress.ConstructionState;
import com.sc.api.structure.construction.progress.ConstructionTask;
import com.sc.api.structure.event.structure.StructureCompleteEvent;
import com.sc.api.structure.model.Structure;
import com.sc.api.structure.persistence.ConstructionService;
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

    private final Player placer;
    private final Structure structure;
    private final EditSession session;
    private ConstructionTask task;

    public SCDefaultCallbackAction(Player placer, Structure structure, ConstructionTask task, EditSession session) {
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
                    placer.sendMessage(ChatColor.YELLOW + "Building:  " + ChatColor.BLUE + structure.getPlan().getDisplayName());
                    task.setState(ConstructionState.CONSTRUCTION_IN_PROGRESS);
                    task = constructionService.save(task);
                } else if (bpje.getStatus() == BlockPlacerJobEntry.JobStatus.Done) {
                    Bukkit.getPluginManager().callEvent(new StructureCompleteEvent(structure));
                    placer.sendMessage(ChatColor.YELLOW + "Construction complete: " + ChatColor.BLUE + structure.getPlan().getDisplayName());
                    placer.playSound(placer.getLocation(), Sound.NOTE_SNARE_DRUM, 2, 0);
                    task.setState(ConstructionState.FINISHED);
                    task = constructionService.save(task);
                }
            }
        });
    }

    @Override
    public void onJobCanceled(BlockPlacerJobEntry entry) {
        placer.sendMessage(ChatColor.RED + "Construction canceled: " + ChatColor.BLUE + structure.getPlan().getDisplayName());
        entry.getEditSession().undo(entry.getEditSession());
        session.undo(session);

        final ConstructionService constructionService = new ConstructionService();
        task.setState(ConstructionState.CANCELED);
        task = constructionService.save(task);
    }

}
