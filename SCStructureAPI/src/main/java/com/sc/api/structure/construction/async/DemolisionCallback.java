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

import com.sc.api.structure.SCStructureAPI;
import com.sc.api.structure.entity.Structure;
import com.sc.api.structure.entity.progress.ConstructionTask;
import com.sc.api.structure.persistence.service.TaskService;
import com.sk89q.worldedit.EditSession;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.primesoft.asyncworldedit.blockPlacer.BlockPlacerJobEntry;
import org.primesoft.asyncworldedit.blockPlacer.IJobEntryListener;

/**
 *
 * @author Chingo
 */
public class DemolisionCallback implements SCJobCallback {

    private final String issuer;
    private final Structure structure;
    private final EditSession session;
    private ConstructionTask task;

    public DemolisionCallback(String issuer, Structure structure, ConstructionTask task, EditSession session) {
        this.issuer = issuer;
        this.structure = structure;
        this.session = session;
        this.task = task;
    }

    @Override
    public void onJobAdded(BlockPlacerJobEntry entry) {
        final TaskService constructionService = new TaskService();
        task = constructionService.setJobId(task, entry.getJobId());

        entry.addStateChangedListener(new IJobEntryListener() {

            @Override
            public void jobStateChanged(BlockPlacerJobEntry bpje) {
                if (bpje.getStatus() == BlockPlacerJobEntry.JobStatus.PlacingBlocks) {
                    Player ply = Bukkit.getPlayer(issuer);
                    if (ply != null && ply.isOnline()) {
                        ply.sendMessage(ChatColor.YELLOW + "Demolision started:  " + ChatColor.BLUE + structure.getPlan().getDisplayName());
                    }
                    task.setHasPlacedBlocks(true);
                    task = constructionService.updateStatus(task, ConstructionTask.State.PROGRESSING);
                } else if (bpje.getStatus() == BlockPlacerJobEntry.JobStatus.Done) {
                    Player ply = Bukkit.getPlayer(issuer);
                    if (ply != null && ply.isOnline() && task.getState() != ConstructionTask.State.REMOVED) {
                        ply.sendMessage(ChatColor.YELLOW + "Demolision complete: " + ChatColor.BLUE + structure.getPlan().getDisplayName());
                        ply.playSound(ply.getLocation(), Sound.NOTE_SNARE_DRUM, 2, 0);
                    }
//                    Bukkit.getPluginManager().callEvent(new StructureCompleteEvent(structure));
                    SCStructureAPI.removeRegion(structure);
                    task = constructionService.setJobId(task, -1);
                    task = constructionService.updateStatus(task, ConstructionTask.State.REMOVED);
                    
                }
            }
        });
    }

    @Override
    public void onJobCanceled(BlockPlacerJobEntry entry) {
        Player ply = Bukkit.getPlayer(issuer);
        if (ply != null && ply.isOnline()) {
            ply.sendMessage(ChatColor.RED + "Demolision canceled: " + ChatColor.BLUE + structure.getPlan().getDisplayName());
        }
    }

}
