/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure.construction.builder;

import com.sc.api.structure.construction.strategies.FoundationStrategy;
import com.sc.api.structure.model.structure.Structure;
import com.sc.api.structure.model.structure.plan.StructurePlan;
import com.sc.api.structure.model.structure.world.Direction;
import com.sc.api.structure.persistence.StructureService;
import com.sc.api.structure.util.WorldEditUtil;
import com.sc.api.structure.util.WorldUtil;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Location;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 *
 * @author Chingo
 */
public class SCStructureBuilder {

    public static void selectStructureArea(Player player, Direction direction, Location pos1, StructurePlan plan) {
        CuboidClipboard clipboard = plan.getSchematic();
        Location pos2 = WorldUtil.calculateEndLocation(pos1, direction, clipboard);
        WorldEditUtil.selectClipboardArea(player, pos1, pos2);
    }

    /**
     * Clears all blocks at the location of the structure.
     *
     * @param session
     * @param location
     * @param clipboard
     * @param direction
     */
    public static void clear(EditSession session, Location location, Direction direction, CuboidClipboard clipboard, boolean autoflush) {
        try {
            Location start = WorldUtil.getWorldDimension(location, direction, clipboard).getStart();
            Location end = WorldUtil.getWorldDimension(location, direction, clipboard).getEnd();
            session.makeCuboidFaces(new CuboidRegion(start.getWorld(), start.getPosition(), end.getPosition()), new BaseBlock(0));
            if (autoflush) {
                session.flushQueue();
            }
        }
        catch (MaxChangedBlocksException ex) { // shoulnd't happen
            Logger.getLogger(SCStructureBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void align(Direction direction, CuboidClipboard cuboidClipboard) {
        switch (direction) {
            case EAST:
                return;
            case SOUTH:
                cuboidClipboard.rotate2D(90);
                break;
            case WEST:
                cuboidClipboard.rotate2D(180);
                break;
            case NORTH:
                cuboidClipboard.rotate2D(270);
                break;
            default:
                throw new AssertionError("unreachable");
        }
    }

    public static void buildAligned(EditSession session, Location location, Direction direction, CuboidClipboard clipboard, boolean autoflush) {
        switch (direction) {
            case EAST:
                break;
            case SOUTH:
                clipboard.rotate2D(90);
                location = location.add(-(clipboard.getWidth() - 1), 0, 0);
                break;
            case WEST:
                clipboard.rotate2D(180);
                location = location.add(-(clipboard.getWidth() - 1), 0, -(clipboard.getLength() - 1));
                break;
            case NORTH:
                clipboard.rotate2D(270);
                location = location.add(0, 0, -(clipboard.getLength() - 1));
                break;
            default:
                throw new AssertionError("unreachable");
        }
        WorldEditUtil.place(session, clipboard, location, autoflush);
    }

    public static void buildInstantly(EditSession session, Direction direction, Location location, StructurePlan plan, boolean autoflush) {
        CuboidClipboard clipboard = plan.getSchematic();
        buildAligned(session, location, direction, clipboard, autoflush);
    }

    public static boolean overlaps(Location location, Direction direction, StructurePlan plan) {
        Structure structure = new Structure(null, location, direction, plan);
        StructureService service = new StructureService();
        return service.overlaps(structure);
    }

    public static boolean placeStructure(Player player, Location location, Direction direction, StructurePlan plan) {
//        StructureService service = new StructureService();

        if (overlaps(location, direction, plan)) {
            return false;
        } else {
            Structure structure = new Structure(player, location, direction, plan);
            EditSession session = WorldEditUtil.getEditSession(location.getWorld(), -1);
            clear(session, location, direction, plan.getSchematic(), true);
            SCFoundationBuilder.placeFoundation(session, structure, FoundationStrategy.DEFAULT, Material.COBBLESTONE, true);
//            buildInstantly(session, direction, location, plan, true);
//            service.save(structure);
            return true;

        }
    }

}
