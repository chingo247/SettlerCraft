/*
 * Copyright (C) 2015 Chingo
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
package com.chingo247.structureapi.construction;

import com.chingo247.structureapi.construction.asyncworldedit.AWEPlacementTask;
import com.chingo247.structureapi.construction.backup.BackupTask;
import com.chingo247.structureapi.construction.backup.IBackupAPI;
import com.chingo247.structureapi.construction.backup.IWorldPartSnapshot;
import com.chingo247.structureapi.exception.ConstructionException;
import com.chingo247.structureapi.exception.StructureTaskException;
import com.chingo247.structureapi.model.structure.ConstructionStatus;
import com.chingo247.structureapi.model.structure.Structure;
import com.chingo247.structureapi.model.structure.StructureNode;
import com.chingo247.structureapi.structure.StructureAPI;
import com.chingo247.structureapi.structure.plan.placement.BlockPlacement;
import com.chingo247.structureapi.structure.plan.placement.DemolishingPlacement;
import com.chingo247.structureapi.structure.plan.placement.Placement;
import com.chingo247.structureapi.structure.plan.placement.RestoringPlacement;
import com.chingo247.structureapi.structure.plan.placement.RotationalPlacement;
import com.chingo247.structureapi.structure.plan.placement.options.BlockPredicate;
import com.chingo247.structureapi.structure.plan.placement.options.BuildOptions;
import com.chingo247.structureapi.structure.plan.placement.options.DemolitionOptions;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import org.neo4j.graphdb.Transaction;
import org.primesoft.asyncworldedit.AsyncWorldEditMain;
import org.primesoft.asyncworldedit.playerManager.PlayerEntry;

/**
 *
 * @author Chingo
 */
public class DefaultStructureTaskFactory implements IStructureTaskFactory {

    private static final String FIRST_BACKUP = "restore.snapshot";
    
    @Override
    public StructureTask build(EditSession session, UUID player, Structure structure) throws ConstructionException {
        return build(session, player, structure, new BuildOptions());
    }

    @Override
    public StructureTask build(EditSession session, UUID player, Structure structure, BuildOptions options) throws ConstructionException {
        StructureNode sn = new StructureNode(structure.getNode());

        // Get the placement and rotate
        Placement p = structure.getStructurePlan().getPlacement();
        if (p instanceof RotationalPlacement) {
            RotationalPlacement rt = (RotationalPlacement) p;
            rt.rotate(structure.getDirection().getRotation());
        }
        
        // Get the AWE playerEntry
        PlayerEntry playerEntry = AsyncWorldEditMain.getInstance().getPlayerManager().getPlayer(player);
        ConstructionEntry ce = ConstructionManager.getInstance().getEntry(structure);
        Vector pos = structure.getCuboidRegion().getMinimumPoint();
        
        // Make sure substructures are skipped during build
        for (StructureNode s : sn.getSubstructures()) {
            final CuboidRegion region = s.getCuboidRegion();

            options.addIgnore(new BlockPredicate() {

                @Override
                public boolean evaluate(Vector position, Vector worldPosition, BaseBlock block) {
                    return region.contains(worldPosition);
                }
            });
        }
        
        AWEPlacementTask task = new AWEPlacementTask(ConstructionStatus.BUILDING.name(), ce, p, playerEntry, session, pos, options);
        
        return task;

    }

    @Override
    public StructureTask demolish(EditSession session, UUID player, Structure structure) throws ConstructionException {
        return demolish(session, player, structure, new DemolitionOptions());
    }

    /**
     * Creates a task that will demolish a structure. <b>NOTE:</b> requires to be executed within a {@link Transaction}
     * @param session
     * @param player
     * @param structure
     * @param options
     * @return The StructureTask
     */
    @Override
    public StructureTask demolish(EditSession session, UUID player, Structure structure, DemolitionOptions options) throws ConstructionException {
        StructureNode sn = new StructureNode(structure.getNode());
        StructureNode parent = sn.getParent();
        DemolishingPlacement dp;
        CuboidRegion region = structure.getCuboidRegion();
        
        // If there is no parent or the structure doesn't have a placement inheriting from BlockPlacement
        // Then use the default removal method, which will clear the whole area
        if(parent == null || (!(parent.getStructurePlan().getPlacement() instanceof BlockPlacement))) {
            Vector size = region.getMaximumPoint().subtract(region.getMinimumPoint()).add(Vector.ONE);
            dp = new DemolishingPlacement(size);
        } else {
            // Otherwise use the parent to restore the area
            Placement parentPlacement = parent.getStructurePlan().getPlacement();
            if (parentPlacement instanceof RotationalPlacement) {
                RotationalPlacement rt = (RotationalPlacement) parentPlacement;
                rt.rotate(parent.getDirection().getRotation());
            }
            dp = new RestoringPlacement((BlockPlacement) parentPlacement);
        }
        
        // Get the AWE playerEntry
        PlayerEntry playerEntry = AsyncWorldEditMain.getInstance().getPlayerManager().getPlayer(player);
        ConstructionEntry ce = ConstructionManager.getInstance().getEntry(structure);
        Vector pos = structure.getCuboidRegion().getMinimumPoint();
        
        // Make sure the areas of substructures are skipped during block placement/removal
        for (StructureNode s : sn.getSubstructures()) {
            final CuboidRegion childRegion = s.getCuboidRegion();
            options.addIgnore(new BlockPredicate() {

                @Override
                public boolean evaluate(Vector position, Vector worldPosition, BaseBlock block) {
                    return childRegion.contains(worldPosition);
                }
            });
        }
        
        AWEPlacementTask task = new AWEPlacementTask(ConstructionStatus.DEMOLISHING.name(), ce, dp, playerEntry, session, pos, options);
        
        return task;
    }

    @Override
    public StructureTask backup(Structure structure, String backup) throws StructureTaskException {
        return new BackupTask(ConstructionManager.getInstance().getEntry(structure), backup);
    }

    @Override
    public StructureTask rollback(EditSession session, UUID player, Structure structure) throws StructureTaskException, IOException {
        CuboidRegion region = structure.getCuboidRegion();
        Vector pos = region.getMinimumPoint();
        ConstructionEntry entry = ConstructionManager.getInstance().getEntry(structure);
        PlayerEntry playerEntry = AsyncWorldEditMain.getInstance().getPlayerManager().getPlayer(player);
        
        if(!hasBackup(structure, FIRST_BACKUP)) {
            throw new StructureTaskException("Backup '"+FIRST_BACKUP+"' does not exist!");
        }
        
        File backupFile = getBackup(structure, FIRST_BACKUP);
        BackupRestoringPlacment brp = getPlacement(backupFile, region);
        
        AWEPlacementTask task = new AWEPlacementTask(ConstructionStatus.ROLLING_BACK.name(), entry, brp, playerEntry, session, pos, new BuildOptions());
        return task;
    }
    
    public boolean hasBackup(Structure structure, String backup) {
        return getBackup(structure, backup).exists();
    }
    
    private File getBackup(Structure structure, String backup) {
        File backupDir = new File(structure.getStructureDirectory(), "//backups");
        return new File(backupDir, backup);
    }
    
    private BackupRestoringPlacment getPlacement(File f, CuboidRegion region) throws IOException  {
        IBackupAPI backupAPI = StructureAPI.getInstance().getBackupAPI();
        IWorldPartSnapshot worldPartSnapshot = backupAPI.readBackup(f);
        
        return new BackupRestoringPlacment(worldPartSnapshot, region);
    }
    
    @Override
    public StructureTask restoreTo(EditSession session, UUID player, Structure structure, String backup) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

   

}
