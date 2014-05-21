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
package com.sc.api.structure.construction.progress;

import com.sc.api.structure.construction.ConstructionValidator;
import com.sc.api.structure.construction.builder.SCCuboidBuilder;
import com.sc.api.structure.construction.builder.async.SCAsyncCuboidBuilder;
import com.sc.api.structure.construction.builder.flag.SCFlags;
import com.sc.api.structure.construction.builder.strategies.SCDefaultCallbackAction;
import com.sc.api.structure.model.Structure;
import com.sc.api.structure.model.plan.StructurePlan;
import com.sc.api.structure.model.world.SimpleCardinal;
import com.sc.api.structure.model.world.WorldDimension;
import com.sc.api.structure.persistence.ConstructionService;
import com.sc.api.structure.util.WorldUtil;
import com.sc.api.structure.util.plugins.AsyncWorldEditUtil;
import com.sc.api.structure.util.plugins.WorldGuardUtil;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Location;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.RegionPermissionModel;
import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSession;

/**
 * @author Chingo
 */
public class StructureBuilder {
    
    public static ProtectedRegion claimGround(final Player player, final Structure structure) {
        if(structure.getId() == null) {
            throw new AssertionError("Save the structure instance first! (e.g. structure = structureService.save(structure)");
        }
        if(!ConstructionValidator.canClaim(player) 
                || !ConstructionValidator.mayClaim(player) 
                || ConstructionValidator.overlapsStructure(structure)
                || ConstructionValidator.overlapsUnowned(player, structure)
                ) {
            return null;
        }
        
        RegionManager mgr = WorldGuardUtil.getWorldGuard().getGlobalRegionManager().get(Bukkit.getWorld(structure.getLocation().getWorld().getName()));
        WorldDimension dim = structure.getDimension();
        Vector p1 = dim.getStart().getPosition();
        Vector p2 = dim.getEnd().getPosition();
        String id = getIdForStructure(structure);
        ProtectedCuboidRegion region = new ProtectedCuboidRegion(id, new BlockVector(p1.getBlockX(), p1.getBlockY(), p1.getBlockZ()), new BlockVector(p2.getBlockX(), p2.getBlockY(), p2.getBlockZ()));
        
        
        // Set Flag
        region.setFlag(SCFlags.STRUCTURE, structure.getPlan().getDisplayName());
        region.getOwners().addPlayer(player.getName());
        try {
            mgr.addRegion(region);
            mgr.save();
        }
        catch (ProtectionDatabaseException ex) {
            Logger.getLogger(StructureBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }

        
        return region;
    }
    
    public static void place(String placer, Structure structure) throws ConstructionException {
        final ConstructionService service = new ConstructionService();
        if(service.hasConstructionTask(structure)) {
            throw new ConstructionTaskException("Already have a task reserved for structure" + structure.getId());
        }
        final RegionManager mgr = WorldGuardUtil.getWorldGuard().getGlobalRegionManager().get(Bukkit.getWorld(structure.getLocation().getWorld().getName()));
        System.out.println("Region: " + structure.getStructureRegion());
        
        if(structure.getStructureRegion() == null || !mgr.hasRegion(structure.getStructureRegion())) {
            throw new ConstructionException("Tried to place a structure without a region");
        }
        
        final ConstructionEntry entry = service.hasEntry(placer) ? service.getEntry(placer) : service.createEntry(placer);
        final AsyncEditSession asyncSession = AsyncWorldEditUtil.createAsyncEditSession(placer, structure.getLocation().getWorld(), -1); // -1 = infinite
        if(asyncSession == null) {
            System.out.println("asyncsession");
        }
        
        ConstructionTask task = new ConstructionTask(entry, structure, ConstructionTask.ConstructionType.BUILDING_AUTO, ConstructionStrategyType.LAYERED);
        
        //TODO Place enclosure
        task = service.save(task); // first save retrieve id, etc...
        SCDefaultCallbackAction dca = new SCDefaultCallbackAction(placer, structure, task, asyncSession);

        try {
            SCAsyncCuboidBuilder.placeLayered(
                    asyncSession, 
                    structure.getPlan().getSchematic(), 
                    structure.getLocation(), 
                    structure.getDirection(), 
                    structure.getPlan().getDisplayName(), 
                    dca
            );
        }
        catch (MaxChangedBlocksException ex) {
            Logger.getLogger(StructureBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public static void place(Player player, Structure structure) throws ConstructionException {
        place(player.getName(), structure);
    }
    
    public static void place(Player player, StructurePlan plan, Location location, SimpleCardinal cardinal) throws ConstructionException {
        place(player, new Structure(player.getName(), location, cardinal, plan));
    }
    
    public static void select(Player player, Structure structure) {
        Location pos2 = WorldUtil.calculateEndLocation(structure.getLocation(), structure.getDirection(), structure.getPlan().getSchematic());
        SCCuboidBuilder.select(player, structure.getLocation(), pos2);
    }
    
    public static void select(Player player, StructurePlan plan, Location location, SimpleCardinal cardinal){
        Structure structure = new Structure("", location, cardinal, plan);
        select(player, structure);
    }
    
    public static void demolish(Structure structure) {
        // Is canceled?
        // Set canceled
        // Demolish
    }
    
    private static String getIdForStructure(Structure structure) {
        String s = String.valueOf("sc_s_" + (structure.getPlan().getDisplayName().replaceAll("\\s", "") + "_" + structure.getId())).toLowerCase();
        return s;
    }

    public boolean createConstructionSite(String id, Player placer, World world, BlockVector pos1, BlockVector pos2, boolean feedback, boolean addSelf) {
        ProtectedCuboidRegion region = new ProtectedCuboidRegion(id, pos1, pos2);
        RegionPermissionModel permModel = WorldGuardUtil.getRegionPermissionModel(placer);
        // Can't replace existing regions
        RegionManager mgr = WorldGuardUtil.getWorldGuard().getGlobalRegionManager().get(world);
        if (mgr.hasRegion(id)) {
            if (feedback) {
                placer.sendMessage(ChatColor.RED + "That region already exists. Please choose a different name.");
            }
            return false;
        }

        region.getOwners().addPlayer(placer.getName());

        mgr.addRegion(region);
        try {
            mgr.save();
        }
        catch (ProtectionDatabaseException ex) {
            Logger.getLogger(StructureBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;

    }

}
