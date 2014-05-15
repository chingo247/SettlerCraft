/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure.construction.builder;

import com.google.common.base.Preconditions;
import com.sc.api.structure.construction.builder.strategies.FoundationStrategy;
import com.sc.api.structure.model.structure.Structure;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Location;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Material;

/**
 *
 * @author Chingo
 */
public class SCFoundationBuilder {

    /**
     * A foundation will be created beneath the structure. A foundation is
     * doesnt have any functionality its just there to give the player some
     * feedback. And also clears the construction site from any blocks
     *
     * @param player The player
     * @param structure The structure
     * @param strategy The strategy
     * @param material The material to be used
     */
    static void placeFoundation(EditSession session, Structure structure, FoundationStrategy strategy, Material material, boolean autoflush) {
        switch (strategy) {
            case DEFAULT:
                placeDefault(session, structure, material, autoflush);
                break;
            case FANCY:
                placeFancyFoundation(structure, material);
                break;
            default:
                throw new AssertionError("No action known for: " + strategy);
        }
    }

    static void placeDefault(EditSession session, Structure structure, Material material, boolean autoflush) {
        Location pos1 = structure.getDimension().getStart();
        Location pos2 = new Location(pos1.getWorld(), new BlockVector(structure.getDimension().getEndX(), 1, structure.getDimension().getEndZ()));

        try {
            CuboidRegion region = new CuboidRegion(pos1.getWorld(), pos1.getPosition(), pos2.getPosition());
            System.out.println(region.getCenter());
            session.makeCuboidFaces(region, new BaseBlock(material.getId()));
            if (autoflush) {
                session.flushQueue();
            }
        }
        catch (MaxChangedBlocksException ex) {
            Logger.getLogger(SCFoundationBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static void placeEnclosure(EditSession session, Structure structure, int material, int height) {
        Preconditions.checkArgument(height > 0);
        CuboidClipboard clipboard = structure.getPlan().getSchematic();
        CuboidClipboard cc = new CuboidClipboard(new BlockVector(clipboard.getWidth(), height + 1, clipboard.getLength()));

        for (int z = 0; z < cc.getLength(); z++) {
            for (int x = 0; x < cc.getWidth(); x ++) {
                for (int y = 1; y < cc.getHeight(); y++) {
                    if((z % 3 == 0 && x % 3 == 0) && (z == 0 || z == cc.getLength() - 1 || x == 0 || x == cc.getWidth() - 1)) {
                        cc.setBlock(new BlockVector(x, y, z), new BaseBlock(material));
                    }
                }
            }
        }
        try {
            Location target = SCCuboidBuilder.align(cc, structure.getLocation(), structure.getDirection());
            cc.place(session, target.getPosition(), true);
        }
        catch (MaxChangedBlocksException ex) {
            Logger.getLogger(SCFoundationBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static void placeFancyFoundation(Structure structure, Material material) {
        //throw new UnsupportedOperationException("This feature is not supported yet");
    }

}
