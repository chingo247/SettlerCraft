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
import com.sc.api.structure.construction.builder.strategies.SCDefaultCallbackAction;
import com.sc.api.structure.model.structure.Structure;
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
import org.bukkit.Material;
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

    /**
     * Selects the area where the structure will be placed
     * @param player
     * @param structure 
     */
    public static void select(Player player, Structure structure) {
        SCCuboidBuilder.select(player, structure.getLocation(), WorldUtil.calculateEndLocation(structure.getLocation(), structure.getDirection(), structure.getPlan().getSchematic()));
    }

    /**
     * Places the structure at target location
     * @param session
     * @param structure 
     */
    public static void place(EditSession session, Structure structure) {
        CuboidClipboard clipboard = structure.getPlan().getSchematic();
        SCCuboidBuilder.place(clipboard, structure.getLocation(), structure.getDirection());
    }

    /**
     * Checks wheter this structure overlaps another structure
     * @param target The location
     * @param direction The direction
     * @param plan The structure plan
     * @return true if structure doesnt overlap another
     * @deprecated Doesnt perform check through worldguard
     */
    public static boolean overlaps(Location target, SimpleCardinal direction, StructurePlan plan) {
        Structure structure = new Structure(null, target, direction, plan);
        StructureService service = new StructureService();
        return service.overlaps(structure);
    }

    
    public static boolean placeStructure(final Player player, final Location location, final SimpleCardinal direction, final StructurePlan plan, boolean defaultFeedback) {
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
            
            // Only generate a foundation if there is another job in progress
            if(SCConstructionManager.getInstance().hasJob(playerName)) {
                SCFoundationBuilder.placeDefault(session, structure, Material.COBBLESTONE, true);
            }
            
            
            SCDefaultCallbackAction dca = new SCDefaultCallbackAction(player, structure, asyncSession, defaultFeedback);
            
            try {
                
                SCAsyncCuboidBuilder.placeLayered(
                        asyncSession,
                        cc,
                        location,
                        direction,
                        plan.getDisplayName(),
                        dca);
            }
            catch (MaxChangedBlocksException ex) {
                Logger.getLogger(SCStructureBuilder.class.getName()).log(Level.SEVERE, null, ex);
            }
            service.save(structure);
        }

            
        return true;

    }
}


