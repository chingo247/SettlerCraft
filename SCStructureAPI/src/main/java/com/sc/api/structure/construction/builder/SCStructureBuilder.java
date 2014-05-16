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

package com.sc.api.structure.construction.builder;

import com.sc.api.structure.construction.builder.async.SCAsyncCuboidBuilder;
import com.sc.api.structure.construction.builder.async.SCJobCallback;
import com.sc.api.structure.model.structure.Structure;
import com.sc.api.structure.model.structure.StructureJob;
import com.sc.api.structure.model.structure.plan.StructurePlan;
import com.sc.api.structure.model.structure.world.SimpleCardinal;
import com.sc.api.structure.persistence.StructureService;
import com.sc.api.structure.util.WorldUtil;
import com.sc.api.structure.util.plugins.AsyncWorldEditUtil;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Location;
import com.sk89q.worldedit.MaxChangedBlocksException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.primesoft.asyncworldedit.blockPlacer.BlockPlacerJobEntry;
import org.primesoft.asyncworldedit.blockPlacer.IJobEntryListener;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSession;

/**
 *
 * @author Chingo
 */
public class SCStructureBuilder {

    public enum BuildDirection {

        UP,
        DOWN
    }

    public static void select(Player player, Structure structure) {
        SCCuboidBuilder.select(player, structure.getLocation(), WorldUtil.calculateEndLocation(structure.getLocation(), structure.getDirection(), structure.getPlan().getSchematic()));
    }

    public static void place(EditSession session, Structure structure) {
        CuboidClipboard clipboard = structure.getPlan().getSchematic();
        SCCuboidBuilder.place(clipboard, structure.getLocation(), structure.getDirection());
    }

    public static boolean overlaps(Location location, SimpleCardinal direction, StructurePlan plan) {
        Structure structure = new Structure(null, location, direction, plan);
        StructureService service = new StructureService();
        return service.overlaps(structure);
    }

    public static boolean placeStructure(final Player player, final Location location, final SimpleCardinal direction, final StructurePlan plan) {
        final StructureService service = new StructureService();

        if (overlaps(location, direction, plan)) {
            return false;
        } else {
            final Structure structure = new Structure(player.getName(), location, direction, plan);
//            service.save(structure);

            final AsyncEditSession asyncSession = AsyncWorldEditUtil.createAsyncEditSession(player, -1); // -1 = infinite
            final EditSession session = new EditSession(location.getWorld(), -1);
            final CuboidClipboard cc = structure.getPlan().getSchematic();
            final String playerName = player.getName();
            final String structureName = structure.getPlan().getDisplayName();
            if(SCConstructionManager.getInstance().hasJob(playerName)) {
                SCFoundationBuilder.placeDefault(session, structure, Material.COBBLESTONE, true);
            }
            
            SCFrameBuilder.generateFancyFrame(structure, 4, 4);
            

            final SCJobCallback callback = new SCJobCallback() {

                @Override
                public void onJobAdded(BlockPlacerJobEntry entry) {
                    final StructureJob job = new StructureJob(entry.getJobId(), structure);
                    SCConstructionManager.getInstance().addJob(playerName, job);
                    entry.addStateChangedListener(new IJobEntryListener() {

                        @Override
                        public void jobStateChanged(BlockPlacerJobEntry bpje) {
                            if (bpje.getStatus() == BlockPlacerJobEntry.JobStatus.PlacingBlocks) {
                                
                                player.sendMessage(ChatColor.YELLOW + "Building:  " + ChatColor.BLUE + structureName);
//                                player.playSound(player.getLocation(), Sound.NOTE_SNARE_DRUM, 5, 0);
                            } else if (bpje.getStatus() == BlockPlacerJobEntry.JobStatus.Done) {
                                player.sendMessage(ChatColor.YELLOW + "Construction complete: " + ChatColor.BLUE + structureName);
                                SCConstructionManager.getInstance().removeJob(playerName, bpje.getJobId());
                                player.playSound(player.getLocation(), Sound.NOTE_SNARE_DRUM, 2, 0);
                                session.undo(session);
                            }
                        }
                    });
                }

                @Override
                public void onJobCanceled(BlockPlacerJobEntry entry) {
                    player.sendMessage(ChatColor.RED + "Construction canceled: " + ChatColor.BLUE + structureName);
                    entry.getEditSession().undo(entry.getEditSession());
                    session.undo(session);
                    SCConstructionManager.getInstance().removeJob(playerName, entry.getJobId());
                }
            };

            
           
            
            try {
                
                SCAsyncCuboidBuilder.placeLayered(
                        asyncSession,
                        cc,
                        location,
                        direction,
                        plan.getDisplayName(),
                        callback);
            }
            catch (MaxChangedBlocksException ex) {
                Logger.getLogger(SCStructureBuilder.class.getName()).log(Level.SEVERE, null, ex);
            }
            service.save(structure);
        }

            
        return true;

    }
}


