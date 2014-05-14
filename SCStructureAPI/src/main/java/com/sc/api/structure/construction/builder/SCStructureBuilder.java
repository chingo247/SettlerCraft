/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure.construction.builder;

import com.sc.api.structure.construction.builder.async.SCAsyncCuboidBuilder;
import com.sc.api.structure.model.structure.Structure;
import com.sc.api.structure.model.structure.plan.StructurePlan;
import com.sc.api.structure.model.structure.world.SimpleCardinal;
import com.sc.api.structure.persistence.StructureService;
import com.sc.api.structure.util.AsyncWorldEditUtil;
import com.sc.api.structure.util.WorldEditUtil;
import com.sc.api.structure.util.WorldUtil;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
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
public class SCStructureBuilder {

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

    public static boolean placeStructure(Player player, final Location location, final SimpleCardinal direction, StructurePlan plan) {
        StructureService service = new StructureService();

        if (overlaps(location, direction, plan)) {
            return false;
        } else {
            Structure structure = new Structure(player, location, direction, plan);
//            service.save(structure);
            
            AsyncEditSession session = AsyncWorldEditUtil.createAsyncEditSession(player, -1); // -1 = infinite
//            SCCuboidBuilder.place(structure.getPlan().getSchematic(), location, direction);
            final CuboidClipboard cc = structure.getPlan().getSchematic();

            try {
                SCAsyncCuboidBuilder.placeLayered(
                        session, 
                        cc, 
                        location, 
                        direction, 
                        plan.getDisplayName());
            }
            catch (MaxChangedBlocksException ex) {
                Logger.getLogger(SCStructureBuilder.class.getName()).log(Level.SEVERE, null, ex);
            }

//            service.save(structure);
            return true;

        }
    }

}
