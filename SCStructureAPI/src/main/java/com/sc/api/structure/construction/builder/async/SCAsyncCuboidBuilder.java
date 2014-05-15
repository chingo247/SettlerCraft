/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure.construction.builder.async;

import com.sc.api.structure.construction.builder.SCLayeredCuboidClipBoard;
import com.sc.api.structure.construction.builder.SCCuboidBuilder;
import com.sc.api.structure.model.structure.world.SimpleCardinal;
import com.sc.api.structure.util.plugins.AsyncWorldEditUtil;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Location;
import com.sk89q.worldedit.MaxChangedBlocksException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSession;

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

    public static void placeLayered(AsyncEditSession asyncEditSession, CuboidClipboard whole, Location location, SimpleCardinal cardinal, String jobName, SCJobCallback callback) throws MaxChangedBlocksException {
        Location target = SCCuboidBuilder.align(whole, location, cardinal);
        SCLayeredCuboidClipBoard clipBoard = new SCLayeredCuboidClipBoard(whole);
        SCAsyncCuboidClipboard asyncCuboidClipboard = new SCAsyncCuboidClipboard(asyncEditSession.getPlayer(), clipBoard);
        asyncCuboidClipboard.place(asyncEditSession, target.getPosition(), true, callback);
    }
}
