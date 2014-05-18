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

import com.sc.api.structure.construction.builder.SCConstructionManager;
import com.sc.api.structure.construction.builder.async.SCJobCallback;
import com.sc.api.structure.event.structure.StructureCompleteEvent;
import com.sc.api.structure.model.Structure;
import com.sc.api.structure.model.StructureJob;
import com.sk89q.worldedit.EditSession;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.primesoft.asyncworldedit.blockPlacer.BlockPlacerJobEntry;
import org.primesoft.asyncworldedit.blockPlacer.IJobEntryListener;


/**
 * The default action to for
 * @author Chingo
 */
public class SCDefaultCallbackAction implements SCJobCallback {
    private final Player placer;
    private final Structure structure;
    private final EditSession session;
    private final boolean talkative;
    

    public SCDefaultCallbackAction(Player placer, Structure structure, EditSession session, boolean talkative) {
        this.placer = placer;
        this.structure = structure;
        this.session = session;
        this.talkative = talkative;
    }

    @Override
    public void onJobAdded(BlockPlacerJobEntry entry) {
        final StructureJob job = new StructureJob(entry.getJobId(), structure);
        SCConstructionManager.getInstance().addJob(placer.getName(), job);
        entry.addStateChangedListener(new IJobEntryListener() {

            @Override
            public void jobStateChanged(BlockPlacerJobEntry bpje) {
                if (bpje.getStatus() == BlockPlacerJobEntry.JobStatus.PlacingBlocks) {
                    if(talkative) {
                    placer.sendMessage(ChatColor.YELLOW + "Building:  " + ChatColor.BLUE + structure.getPlan().getDisplayName());
                    }
                } else if (bpje.getStatus() == BlockPlacerJobEntry.JobStatus.Done) {
                    Bukkit.getPluginManager().callEvent(new StructureCompleteEvent(structure));
                    if(talkative) {
                        placer.sendMessage(ChatColor.YELLOW + "Construction complete: " + ChatColor.BLUE + structure.getPlan().getDisplayName());
                        placer.playSound(placer.getLocation(), Sound.NOTE_SNARE_DRUM, 2, 0);
                    }
                }
            }
        });
    }

    @Override
    public void onJobCanceled(BlockPlacerJobEntry entry) {
        placer.sendMessage(ChatColor.RED + "Construction canceled: " + ChatColor.BLUE + structure.getPlan().getDisplayName());
        entry.getEditSession().undo(entry.getEditSession());
        session.undo(session);
        SCConstructionManager.getInstance().removeJob(placer.getName(), entry.getJobId());
    }

}
