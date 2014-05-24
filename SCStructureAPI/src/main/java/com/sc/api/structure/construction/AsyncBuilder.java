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

package com.sc.api.structure.construction;

import com.sc.api.structure.construction.async.SCAsyncCuboidClipboard;
import com.sc.api.structure.construction.async.SCDefaultCallbackAction;
import com.sc.api.structure.construction.async.SCJobCallback;
import com.sc.api.structure.construction.progress.ConstructionEntry;
import com.sc.api.structure.construction.progress.ConstructionException;
import com.sc.api.structure.construction.progress.ConstructionStrategyType;
import com.sc.api.structure.construction.progress.ConstructionTask;
import com.sc.api.structure.construction.progress.ConstructionTaskException;
import com.sc.api.structure.model.Structure;
import com.sc.api.structure.model.plan.StructurePlan;
import com.sc.api.structure.model.world.SimpleCardinal;
import com.sc.api.structure.persistence.service.ConstructionService;
import com.sc.api.structure.util.WorldUtil;
import com.sc.api.structure.util.plugins.AsyncWorldEditUtil;
import com.sc.api.structure.util.plugins.WorldGuardUtil;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Location;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.protection.managers.RegionManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSession;

/**
 *
 * @author Chingo
 */
public class AsyncBuilder {
    
    public static void clear(AsyncEditSession asyncEditSession, Location target, SimpleCardinal cardinal, CuboidClipboard clipboard) throws MaxChangedBlocksException {
        SyncBuilder.clear(asyncEditSession, target, cardinal, clipboard);
    }

    public static void clear(AsyncEditSession asyncEditSession, Location pos1, Location pos2) throws MaxChangedBlocksException {
        SyncBuilder.clear(asyncEditSession, pos1, pos2);
    }

    public static void place(AsyncEditSession editSession, CuboidClipboard cuboidClipboard, Location target, SimpleCardinal cardinal, String jobName) throws MaxChangedBlocksException {
        Location t = SyncBuilder.align(cuboidClipboard, target, cardinal);
        cuboidClipboard.place(editSession, t.getPosition(), true);
    }

    public static void place(Player player, CuboidClipboard cuboidClipboard, Location target, SimpleCardinal cardinal, String jobName) {
        try {
            place(AsyncWorldEditUtil.createAsyncEditSession(player, -1), cuboidClipboard, target, cardinal, jobName);
        }
        catch (MaxChangedBlocksException ex) {
            Logger.getLogger(AsyncBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void placeLayer(Player player, CuboidClipboard clipboard, int layer, Location location, SimpleCardinal direction) throws MaxChangedBlocksException {
        SyncBuilder.place(AsyncWorldEditUtil.createAsyncEditSession(player, -1), clipboard, location, direction);
    }

    public static void placeLayer(AsyncEditSession asyncEditSession, CuboidClipboard clipboard, int layer, Location location, SimpleCardinal direction) throws MaxChangedBlocksException {
        SyncBuilder.placeLayer(asyncEditSession, clipboard, layer, location, direction);
    }
    
     public static void placeLayered(AsyncEditSession asyncEditSession, CuboidClipboard whole, Location location, SimpleCardinal cardinal, String jobName, SCJobCallback callback) throws MaxChangedBlocksException {
        Location t = SyncBuilder.align(whole, location, cardinal);
        SmartClipBoard smartClipboard = new SmartClipBoard(whole, ConstructionStrategyType.LAYERED, false);
        SCAsyncCuboidClipboard asyncCuboidClipboard = new SCAsyncCuboidClipboard(asyncEditSession.getPlayer(), smartClipboard);
        asyncCuboidClipboard.place(asyncEditSession, t.getPosition(), false, callback);
    }
     
      public static void placeStructure(String placer, Structure structure) throws ConstructionException {
        final ConstructionService service = new ConstructionService();
        if (service.hasConstructionTask(structure)) {
            throw new ConstructionTaskException("Already have a task reserved for structure" + structure.getId());
        }
        final RegionManager mgr = WorldGuardUtil.getWorldGuard().getGlobalRegionManager().get(Bukkit.getWorld(structure.getLocation().getWorld().getName()));

        if (structure.getStructureRegion() == null || !mgr.hasRegion(structure.getStructureRegion())) {
            throw new ConstructionException("Tried to place a structure without a region");
        }

        final ConstructionEntry entry = service.hasEntry(placer) ? service.getEntry(placer) : service.createEntry(placer);
        final AsyncEditSession asyncSession = AsyncWorldEditUtil.createAsyncEditSession(placer, structure.getLocation().getWorld(), -1); // -1 = infinite

        ConstructionTask task = new ConstructionTask(entry, structure, ConstructionTask.ConstructionType.BUILDING_AUTO, ConstructionStrategyType.LAYERED);

        // WORKAROUND / HACK...
        Sign sign = WorldUtil.createSign(structure.getLocation(), structure.getCardinal());
        sign.setLine(0, String.valueOf(structure.getId()));
        sign.setLine(1, String.valueOf(structure.getPlan().getDisplayName()));
        sign.setLine(2, task.getState().name());
        sign.update(true);

//        TODO Place enclosure
        task = service.save(task); // first save, retrieve id, etc...
        SCDefaultCallbackAction dca = new SCDefaultCallbackAction(placer, structure, task, asyncSession);
//
        CuboidClipboard schematic = structure.getPlan().getSchematic();
        Location t = SyncBuilder.align(schematic, structure.getLocation(), structure.getCardinal());
        Vector signVec = structure.getLocation().getPosition().subtract(t.getPosition()).add(0, 1, 0);
        SmartClipBoard smartClipboard = new SmartClipBoard(schematic, signVec, ConstructionStrategyType.LAYERED, false);
        SCAsyncCuboidClipboard asyncCuboidClipboard = new SCAsyncCuboidClipboard(asyncSession.getPlayer(), smartClipboard);

        try {
            asyncCuboidClipboard.place(asyncSession, t.getPosition(), false, dca);
        }
        catch (MaxChangedBlocksException ex) {
            Logger.getLogger(SyncBuilder.class.getName()).log(Level.SEVERE, null, ex); // Won't happen
        }

    }

    public static void placeStructure(Player player, Structure structure) throws ConstructionException {
        placeStructure(player.getName(), structure);
    }

    public static void placeStructure(Player player, StructurePlan plan, Location location, SimpleCardinal cardinal) throws ConstructionException {
        placeStructure(player, new Structure(player.getName(), location, cardinal, plan));
    }
}
