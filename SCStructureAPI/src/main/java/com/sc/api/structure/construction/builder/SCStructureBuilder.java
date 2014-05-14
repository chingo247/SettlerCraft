/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure.construction.builder;

import com.google.common.collect.Maps;
import com.sc.api.structure.construction.builder.async.SCAsyncCuboidBuilder;
import com.sc.api.structure.construction.builder.async.SCJobCallback;
import com.sc.api.structure.model.structure.Structure;
import com.sc.api.structure.model.structure.StructureJob;
import com.sc.api.structure.model.structure.plan.StructurePlan;
import com.sc.api.structure.model.structure.world.SimpleCardinal;
import com.sc.api.structure.persistence.StructureService;
import com.sc.api.structure.util.WorldUtil;
import com.sc.api.structure.util.plugins.AsyncWorldEditUtil;
import com.sc.api.structure.util.plugins.WorldEditUtil;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Location;
import com.sk89q.worldedit.MaxChangedBlocksException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
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
    
    private static final ConcurrentMap<String, List<StructureJob>> jobs = Maps.newConcurrentMap();

    public enum BuildDirection {

        UP,
        DOWN
    }

    public static void select(Player player, SimpleCardinal direction, Location location, StructurePlan plan) {
        CuboidClipboard clipboard = plan.getSchematic();
        Location pos2 = WorldUtil.calculateEndLocation(location, direction, clipboard);
        WorldEditUtil.selectClipboardArea(player, location, pos2);
    }

    public static void placeInstant(EditSession session, SimpleCardinal direction, Location location, StructurePlan plan) {
        CuboidClipboard clipboard = plan.getSchematic();
        SCCuboidBuilder.place(clipboard, location, direction);
    }

    public static boolean overlaps(Location location, SimpleCardinal direction, StructurePlan plan) {
        Structure structure = new Structure(null, location, direction, plan);
        StructureService service = new StructureService();
        return service.overlaps(structure);
    }

    public static boolean placeStructure(final Player player, final Location location, final SimpleCardinal direction, StructurePlan plan) {
        final StructureService service = new StructureService();

        if (overlaps(location, direction, plan)) {
            return false;
        } else {
            final Structure structure = new Structure(player, location, direction, plan);
//            service.save(structure);
            
            final AsyncEditSession asyncSession = AsyncWorldEditUtil.createAsyncEditSession(player, -1); // -1 = infinite
            final EditSession session = new EditSession(location.getWorld(), -1);
            final CuboidClipboard cc = structure.getPlan().getSchematic();
            final String playerName = player.getName();
            final String structureName = structure.getPlan().getDisplayName();
          
            
            if(jobs.get(playerName) == null) {
                jobs.put(playerName, new ArrayList<StructureJob>());
            }
            
            if(!jobs.get(playerName).isEmpty()) {
                SCFoundationBuilder.placeDefault(session, structure, Material.COBBLESTONE, true);
            }

            SCJobCallback callback = new SCJobCallback() {

                @Override
                public void onJobAdded(BlockPlacerJobEntry entry) {
                    final StructureJob job = new StructureJob(entry.getJobId(), structure);
                    jobs.get(playerName).add(job);
                    entry.addStateChangedListener(new IJobEntryListener() {

                        @Override
                        public void jobStateChanged(BlockPlacerJobEntry bpje) {
                            if(bpje.getStatus() == BlockPlacerJobEntry.JobStatus.PlacingBlocks) {
                                player.sendMessage(ChatColor.YELLOW + "Building:  " + ChatColor.BLUE + structureName);
//                                player.playSound(player.getLocation(), Sound.NOTE_SNARE_DRUM, 5, 0);
                            } else if(bpje.getStatus() == BlockPlacerJobEntry.JobStatus.Done) {
                                player.sendMessage(ChatColor.YELLOW + "Construction complete: " + ChatColor.BLUE + structureName);
                                remove(playerName, bpje.getJobId());
                                player.playSound(player.getLocation(), Sound.NOTE_SNARE_DRUM, 2, 0);
                                
                            }
                        }
                    });
                }

   
         
                @Override
                public void onJobCanceled(BlockPlacerJobEntry entry) {
                    player.sendMessage(ChatColor.RED + "Construction canceled: " + ChatColor.BLUE + structureName);
                    entry.getEditSession().undo(entry.getEditSession());
                    session.undo(session);
                    remove(playerName, entry.getJobId());
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

//            service.save(structure);
            return true;

        }
    }
    

    
    private static void remove(String player, int jobId) {
        Iterator<StructureJob> it = jobs.get(player).iterator();
        while(it.hasNext()) {
            StructureJob j = it.next();
            if(j.getId() == jobId) {
                it.remove();
                break;
            }
        }
    }

    

}
