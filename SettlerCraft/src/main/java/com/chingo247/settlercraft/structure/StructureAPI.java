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
package com.chingo247.settlercraft.structure;

import com.chingo247.settlercraft.structure.construction.BuildOptions;
import com.chingo247.settlercraft.structure.construction.DemolitionOptions;
import com.chingo247.settlercraft.structure.exception.StructureDataException;
import com.chingo247.settlercraft.structure.exception.StructureException;
import com.chingo247.settlercraft.structure.persistence.hibernate.SchematicDataDAO;
import com.chingo247.settlercraft.structure.plan.StructurePlan;
import com.chingo247.settlercraft.structure.schematic.SchematicData;
import com.chingo247.settlercraft.structure.util.SchematicUtil;
import com.chingo247.settlercraft.structure.world.Dimension;
import com.chingo247.settlercraft.structure.world.Direction;
import com.chingo247.xcore.core.APlatform;
import com.chingo247.xcore.core.IPlugin;
import com.chingo247.xcore.util.ChatColors;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.world.World;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Chingo
 */
public class StructureAPI extends AbstractStructureAPI<Player, World> {
    
    private final SchematicDataDAO schematicDataDAO;

    public StructureAPI(ExecutorService executor, APlatform platform, IConfigProvider configProvider, IPlugin plugin) {
        super(executor, platform, configProvider, plugin);
        this.schematicDataDAO = new SchematicDataDAO();
    }

    @Override
    public Structure create(StructurePlan plan, World world, Vector pos, Direction direction) throws StructureException {
        return create(null, plan, world, pos, direction);
    }

    @Override
    public Structure create(Player player, StructurePlan plan, World world, Vector pos, Direction direction) throws StructureException {
         // Retrieve schematic, should never return null as schematic data is stored in database on start up
        SchematicData schematicData = schematicDataDAO.find(plan.getChecksum());

        // Check if it is a valid location
        Dimension dimension = SchematicUtil.calculateDimension(schematicData, pos, direction);
        if (dimension.getMinY() < 0) {
            throw new StructureException("Can't place structures below y:0");
        } else if (dimension.getMaxY() > world.getMaxY()) {
            throw new StructureException("Can't place structurs above " + world.getMaxY() + " (World max height)");
        }

        // Check if structure overlaps another structure
        if (overlaps(world.getName(), dimension)) {
            throw new StructureException("Structure overlaps another structure");
        }

        // Create structure
        Structure structure = new Structure(world, pos, direction, schematicData);
        structure.setName(plan.getName() == null ? "Structure #" + structure.getId() : plan.getName());
        structure.setRefundValue(plan.getPrice());

        // Save structure & retrieve stored instance which has a generated id
        structure = structureDAO.save(structure);

        try {
            final File STRUCTURE_DIR = getFolder(structure);
            if (!STRUCTURE_DIR.exists()) {
                STRUCTURE_DIR.mkdirs();
            }

            File config = plan.getConfig();
            File schematicFile = plan.getSchematic();

            FileUtils.copyFile(config, new File(STRUCTURE_DIR, "StructurePlan.xml"));
            FileUtils.copyFile(schematicFile, new File(STRUCTURE_DIR, schematicFile.getName()));
        } catch (IOException ex) {

            java.util.logging.Logger.getLogger(StructureAPI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            throw new StructureDataException(ChatColors.RED + "Couldn't copy data for structure");
        }

        
        getStructureDocumentManager().register(structure);


        if (player != null) {
            makeOwner(player, PlayerOwnership.Type.FULL, structure);
        }
        setState(structure, Structure.State.QUEUED);
        structure = structureDAO.save(structure);
        
//        getEventBus().post(new StructureCreateEvent(structure));
        return structure;
    }

    @Override
    public boolean build(Player player, Structure structure, BuildOptions options, boolean force) {
        if (structure == null) {
            throw new AssertionError("Null structure");
        }
        if (player == null) {
            throw new AssertionError("Null player");
        }
        if (structure.getId() == null) {
            throw new AssertionError("structure is not a saved instance");
        }
        if (!structure.isOwner(player, PlayerOwnership.Type.FULL)) {
            player.print(ChatColors.RED + "You don't have FULL ownership of this structure");
            return false;
        }

        structureTaskHandler.build(player, structure, options, force);
        return true;
    }

    @Override
    public boolean demolish(Player player, Structure structure, DemolitionOptions options, boolean force) {
        if (structure == null) {
            throw new AssertionError("Null structure");
        }
        if (player == null) {
            throw new AssertionError("Null player");
        }
        if (structure.getId() == null) {
            throw new AssertionError("structure is not a saved instance");
        }
        if (!structure.isOwner(player, PlayerOwnership.Type.FULL)) {
            player.print(ChatColors.RED + "You don't have FULL ownership of this structure");
            return false;
        }

        structureTaskHandler.demolish(player, structure, options, force);
        return true;
    }

    @Override
    public boolean stop(Player player, Structure structure) {
        if (structure == null) {
            throw new AssertionError("Null structure");
        }

        if (structure.getId() == null) {
            throw new AssertionError("structure is not a saved instance");
        }

        if (!structure.isOwner(player, PlayerOwnership.Type.FULL)) {
            player.print(ChatColors.RED + "You don't have FULL ownership of this structure");
            return false;
        }

        return structureTaskHandler.stop(structure);
    }
    
    @Override
    public boolean makeOwner(Player player, PlayerOwnership.Type type, Structure structure) {
        if (player == null) {
            throw new AssertionError("Null player");
        }
        if (structure == null) {
            throw new AssertionError("Null structure");
        }

        if (!structure.addOwner(player, type)) {
            return false;
        }
        structureDAO.save(structure);
        return true;
    }

    @Override
    public boolean makeMember(Player player, Structure structure) {
        if (player == null) {
            throw new AssertionError("Null player");
        }
        if (structure == null) {
            throw new AssertionError("Null structure");
        }

        if (!structure.addMember(player)) {
            return false;
        }
        structureDAO.save(structure);
        return true;
    }

    @Override
    public boolean removeOwner(Player player, Structure structure) {
        if (player == null) {
            throw new AssertionError("Null player");
        }
        if (structure == null) {
            throw new AssertionError("Null structure");
        }

        if (!structure.removeOwner(player)) {
            return false;
        }

        structureDAO.save(structure);
        return true;
    }

    @Override
    public boolean removeMember(Player player, Structure structure) {
        if (player == null) {
            throw new AssertionError("Null player");
        }
        if (structure == null) {
            throw new AssertionError("Null structure");
        }
        if (!structure.addMember(player)) {
            return false;
        }
        structureDAO.save(structure);
        return true;
    }

    

    

}
