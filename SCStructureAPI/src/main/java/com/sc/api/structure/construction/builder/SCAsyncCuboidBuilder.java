/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure.construction.builder;

import com.sc.api.structure.model.structure.world.SimpleCardinal;
import com.sc.api.structure.util.AsyncWorldEditUtil;
import com.sc.api.structure.util.CuboidUtil;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Location;
import com.sk89q.worldedit.MaxChangedBlocksException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.primesoft.asyncworldedit.blockPlacer.BlockPlacerJobEntry;
import org.primesoft.asyncworldedit.blockPlacer.IJobEntryListener;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSession;
import org.primesoft.asyncworldedit.worldedit.CancelabeEditSession;

/**
 *
 * @author Chingo
 */
public class SCAsyncCuboidBuilder {

    public static void clear(AsyncEditSession asyncEditSession, Location target, SimpleCardinal cardinal, CuboidClipboard clipboard) throws MaxChangedBlocksException {
        SCCuboidBuilder.clear(asyncEditSession, target, cardinal, clipboard);
    }

    public static void clear(AsyncEditSession asyncEditSession, Location pos1, Location pos2) throws MaxChangedBlocksException {
        SCCuboidBuilder.clear(asyncEditSession, pos1, pos2);
    }

    public static void place(AsyncEditSession editSession, CuboidClipboard cuboidClipboard, Location target, SimpleCardinal cardinal, String jobName) throws MaxChangedBlocksException {
        SCCuboidBuilder.align(cuboidClipboard, target, cardinal);
        cuboidClipboard.place(editSession, target.getPosition(), true);
        int jobId = AsyncWorldEditUtil.getAsyncWorldEditPlugin().getBlockPlacer().getJobId(editSession.getPlayer());
        BlockPlacerJobEntry blockPlacerJobEntry = new BlockPlacerJobEntry(editSession, new CancelabeEditSession(editSession, editSession.getAsyncMask(), jobId), jobId, jobName);
        AsyncWorldEditUtil.getAsyncWorldEditPlugin().getBlockPlacer().addJob(editSession.getPlayer(), blockPlacerJobEntry);
    }

    public static void place(Player player, CuboidClipboard cuboidClipboard, Location target, SimpleCardinal cardinal, String jobName) {
        try {
            place(AsyncWorldEditUtil.createAsyncEditSession(player, -1), cuboidClipboard, target, cardinal, jobName);
        }
        catch (MaxChangedBlocksException ex) {
            Logger.getLogger(SCAsyncCuboidBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void placeLayer(Player player, CuboidClipboard clipboard, int layer, Location location, SimpleCardinal direction) throws MaxChangedBlocksException {
        SCCuboidBuilder.place(AsyncWorldEditUtil.createAsyncEditSession(player, -1), clipboard, location, direction);
    }

    public static void placeLayer(AsyncEditSession asyncEditSession, CuboidClipboard clipboard, int layer, Location location, SimpleCardinal direction) throws MaxChangedBlocksException {
        SCCuboidBuilder.placeLayer(asyncEditSession, clipboard, layer, location, direction);
    }

    public static void placeLayered(AsyncEditSession editSession, CuboidClipboard whole, Location location, SimpleCardinal cardinal, String jobName) throws MaxChangedBlocksException {
        Location target = SCCuboidBuilder.align(whole, location, cardinal);
        List<CuboidClipboard> layers = CuboidUtil.getLayers(whole);
        
        for(int i = 0; i < layers.size(); i++) {
                layers.get(i).place(editSession, target.getPosition(), true);   
        }
        
        final String ply = editSession.getPlayer();
        int jobId = AsyncWorldEditUtil.getAsyncWorldEditPlugin().getBlockPlacer().getJobId(ply);
        BlockPlacerJobEntry blockPlacerJobEntry = new BlockPlacerJobEntry(editSession, new CancelabeEditSession(editSession, editSession.getAsyncMask(), jobId), jobId, jobName);
        
        IJobEntryListener entryListener = new IJobEntryListener() {

            @Override
            public void jobStateChanged(BlockPlacerJobEntry bpje) {
                System.out.println("ply: " + ply);
                Player player = Bukkit.getPlayer(ply);
               
                if (player != null && player.isOnline()) {
                    switch (bpje.getStatus()) {
                        case Done:
                            player.sendMessage("Construction complete: " + ChatColor.BLUE + bpje.getName());
                            
                            break;
                        case PlacingBlocks:
                            player.sendMessage(("Building: " + ChatColor.BLUE + bpje.getName()));
                            break;
                        case Waiting:
                            player.sendMessage("Waiting: " + ChatColor.BLUE + bpje.getName());
                            break;
                        default:
                            break;
                    }
                }

            }
        };
        blockPlacerJobEntry.addStateChangedListener(entryListener);
        
        
        AsyncWorldEditUtil.getBlockPlacer().addJob(ply, blockPlacerJobEntry);
        
    }
}
