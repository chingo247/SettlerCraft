/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure.construction.builder;

import com.sc.api.structure.model.structure.world.SimpleCardinal;
import com.sc.api.structure.util.CuboidUtil;
import com.sc.api.structure.util.WorldUtil;
import com.sc.api.structure.util.plugins.WorldEditUtil;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Location;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.entity.Player;

/**
 *
 * @author Chingo
 */
public class SCCuboidBuilder {

    /**
     * Selects a region between two positions
     *
     * @param player The player to create an editSession
     * @param cardinal The cardinal direction
     * @param target The target location
     * @param cuboidClipboard The cuboidClipboard
     */
    public static void select(Player player, SimpleCardinal cardinal, Location target, CuboidClipboard cuboidClipboard) {
        Location pos2 = WorldUtil.calculateEndLocation(target, cardinal, cuboidClipboard);
        select(player, target, pos2);
    }

    /**
     * Selects a region between two points
     *
     * @param player The player to create an editsession
     * @param pos1 The first position
     * @param pos2 The secondary position
     */
    public static void select(Player player, Location pos1, Location pos2) {
        WorldEditUtil.selectClipboardArea(player, pos1, pos2);
    }

    /**
     * Clears an area within the cliboards target area
     *
     * @param editSession The editSession
     * @param cardinal The cardinal
     * @param target The target location
     * @param cuboidClipboard The cuboidClipboard
     * @throws MaxChangedBlocksException
     */
    public static void clear(EditSession editSession, Location target, SimpleCardinal cardinal, CuboidClipboard cuboidClipboard) throws MaxChangedBlocksException {
        Location pos2 = WorldUtil.calculateEndLocation(target, cardinal, cuboidClipboard);
        clear(editSession, pos2, pos2);
    }

    /**
     * Clears an area between two points
     *
     * @param editSession The editsession
     * @param pos1 The first location
     * @param pos2 The secondary location
     * @throws MaxChangedBlocksException
     */
    public static void clear(EditSession editSession, Location pos1, Location pos2) throws MaxChangedBlocksException {
        editSession.setBlocks(new CuboidRegion(pos1.getPosition(), pos2.getPosition()), new BaseBlock(0));
        editSession.flushQueue();
    }

    /**
     * Alignes target clipboard to speficied direction, assuming that the
     * initial state is pointed to EAST
     *
     * @param clipboard
     * @param location
     * @param direction
     * @return
     */
    public static Location align(CuboidClipboard clipboard, Location location, SimpleCardinal direction) {
        switch (direction) {
            case EAST:
                return location;
            case SOUTH:
                clipboard.rotate2D(90);
                return location.add(-(clipboard.getWidth() - 1), 0, 0);
            case WEST:
                clipboard.rotate2D(180);
                return location.add(-(clipboard.getWidth() - 1), 0, -(clipboard.getLength() - 1));
            case NORTH:
                clipboard.rotate2D(270);
                return location.add(0, 0, -(clipboard.getLength() - 1));
            default:
                throw new AssertionError("unreachable");
        }
    }

    /**
     * Aligns clipboard to direction and pastes it on target location
     *
     * @param editSession The editSession
     * @param clipboard The clipboard
     * @param target The target location
     * @param cardinal The cardinal
     */
    public static void place(EditSession editSession, CuboidClipboard clipboard, Location target, SimpleCardinal cardinal) throws MaxChangedBlocksException {
        align(clipboard, target, cardinal);
        clipboard.paste(editSession, target.getPosition(), true);
    }

    /**
     * Creates a session for infinite blocks and places a CuboidClipBoard
     * instantly aligned to direction
     *
     * @param cuboidClipboard The cuboidclipboard
     * @param target The target location
     * @param direction The direction
     */
    public static void place(CuboidClipboard cuboidClipboard, Location target, SimpleCardinal direction) {
        Location t = align(cuboidClipboard, target, direction);
        try {
            SCCuboidBuilder.place(WorldEditUtil.getEditSession(t.getWorld(), -1), cuboidClipboard, t, direction);
        }
        catch (MaxChangedBlocksException ex) {
            Logger.getLogger(SCCuboidBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Creates a session for infinite blocks and places a specified layer of a
     * cuboid at target location,
     *
     * @param whole The whole cuboidClipBoard
     * @param layer The layer, must be between 0 and height
     * @param location The target location
     * @param cardinal The cardinal
     */
    public static void placeLayer(CuboidClipboard whole, int layer, Location location, SimpleCardinal cardinal) {
        try {
            placeLayer(WorldEditUtil.getEditSession(location.getWorld(), -1), whole, layer, location, cardinal);
        }
        catch (MaxChangedBlocksException ex) {
            Logger.getLogger(SCCuboidBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Uses the given session to place a specified layer of a cuboid at target
     * location
     *
     * @param editSession
     * @param whole The whole cuboidClipBoard
     * @param layer The layer, must be between 0 and height
     * @param target The target location
     * @param cardinal The cardinal
     * @throws com.sk89q.worldedit.MaxChangedBlocksException
     */
    public static void placeLayer(EditSession editSession, CuboidClipboard whole, int layer, Location target, SimpleCardinal cardinal) throws MaxChangedBlocksException {
        CuboidClipboard layerClip = CuboidUtil.getLayer(whole, layer);
        Location t = align(layerClip, target, cardinal);
        SCCuboidBuilder.place(editSession, layerClip, t, cardinal);
    }

    /**
     * Places a cuboidClipBoard in layers at target location at a specified
     * interval
     *
     * @param editSession
     * @param whole
     * @param location
     * @param cardinal
     * @param interval
     *
     */
    public static void placeLayered(EditSession editSession, CuboidClipboard whole, Location location, SimpleCardinal cardinal, int interval) {
        Location target = align(whole, location, cardinal);
        placeLayered(editSession, whole, CuboidUtil.getLayers(whole), target, interval, 0);
    }

    private static void placeLayered(final EditSession editSession, final CuboidClipboard whole, final List<CuboidClipboard> all, final Location location, final int delayBetweenLayers, final int index) {
        try {
            all.get(index).paste(editSession, location.getPosition().add(new BlockVector(0, 1, 0)), true);

        }
        catch (MaxChangedBlocksException ex) {
            Logger.getLogger(SCCuboidBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
        final int next = index + 1;
        if (next < all.size()) {
            placeLayered(editSession, whole, all, location, delayBetweenLayers, next);
        }
    }

//    public static boolean placeIfFree(Player player, CuboidClipboard clip, Location location, SimpleCardinal cardinal) {
//        Location target = align(clip, location, cardinal);
//        if(SCStructureAPI.getSCStructureAPI().isRestrictZonesEnabled()) {
//            WorldGuardUtil.getRegionManager(player.getWorld()).getApplicableRegions(null)
//            
//            
//        }
//        
//    }
//    public static void placeBuffered(final EditSession editSession, final CuboidClipboard whole, final int bufferSize, final Location location, final SimpleCardinal direction, final int delayBetweenLayers, final BufferCallback callback) {
//        final Location target = align(whole, location, direction);
//        new BukkitRunnable() {
//
//            @Override
//            public void run() {
//                placeBuffered(editSession, Collections.synchronizedList(getAsList(whole, direction)), whole, bufferSize, target, direction, 0, delayBetweenLayers, callback);
//            }
//        }.runTask(SCStructureAPI.getSCStructureAPI());
//
//    }
//
//    private static void placeBuffered(final EditSession editSession, final List<BlockVector> vectors, final CuboidClipboard whole, final int bufferSize, final Location location, final SimpleCardinal direction, final int index, final int delayBetweenLayers, final BufferCallback callback) {
//        int indexCounter = index;
//        int bufferCounter = 0;
//        System.out.println("size: " + vectors.size());
//        System.out.println("index: " + index);
//        if(index == vectors.size() - 1) {
//            callback.onComplete();
//            System.out.println("On Complete");
//            return;
//        }
//        
//        for (int i = index; i < vectors.size(); i++) {
//            Vector v = vectors.get(i);
//            BaseBlock b = whole.getBlock(v);
//            if (b != null && !b.isAir()) {
//                try {
//
//                    editSession.setBlock(location.add(v).getPosition(), b);
//                    indexCounter++;
//                    bufferCounter++;
//                    if (bufferCounter == bufferSize) {
//                        editSession.flushQueue();
//                        final int nIndex = indexCounter;
//
//                        new BukkitRunnable() {
//                            @Override
//                            public void run() {
//                                placeBuffered(editSession, vectors, whole, bufferSize, location, direction, nIndex, delayBetweenLayers, callback);
//                            }
//                        }.runTaskLater(SCStructureAPI.getSCStructureAPI(), delayBetweenLayers);
//                        
//                        
//                    }
//                }
//                catch (MaxChangedBlocksException ex) {
//                    Logger.getLogger(SCCuboidBuilder.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//            
//        }
//        
////        callback.onComplete();
//        editSession.flushQueue();
//
//    }
//
//    private static List<BlockVector> getAsList(CuboidClipboard whole, SimpleCardinal cardinal) {
////        Location target = align(whole, location, direction);
//
//        List<BlockVector> vectors = new ArrayList<>();
//
//        for (int y = 0; y < whole.getHeight(); y++) {
//            for (int z = 0; z < whole.getLength(); z++) {
//                for (int x = 0; x < whole.getWidth(); x++) {
//                    BlockVector vector = new BlockVector(x, y, z);
//                    BaseBlock b = whole.getBlock(vector);
//                            vectors.add(new BlockVector(x, y, z));
//                }
//            }
//        }
//
//        return vectors;
//    }
//
//    private static final Comparator<Vector> heightComperator = new Comparator<Vector>() {
//
//        @Override
//        public int compare(Vector o1, Vector o2) {
//            if (o1.getBlockY() > o2.getBlockY()) {
//                return -1;
//            } else {
//                return 1;
//            }
//        }
//    };
//    
//    public interface BufferCallback {
//        void onComplete();
//    }

}
