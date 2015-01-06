
/*
 * The MIT License
 *
 * Copyright 2015 Chingo.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.chingo247.settlercraft.structureapi.structure.old;

import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.world.World;

/**
 *
 * @author Chingo
 */
public class StructureAPI extends AbstractStructureAPI<Player, World> {
    
//    private final SchematicDataDAO schematicDataDAO;
//
//    public StructureAPI(ExecutorService executor, APlatform platform, IConfigProvider configProvider, IPlugin plugin) {
//        super(executor, platform, configProvider, plugin);
//        this.schematicDataDAO = new SchematicDataDAO();
//    }
//
//    @Override
//    public Structure create(StructurePlan plan, World world, Vector pos, Direction direction) throws StructureException {
//        return create(null, plan, world, pos, direction);
//    }
//
//    @Override
//    public Structure create(Player player, StructurePlan plan, World world, Vector pos, Direction direction) throws StructureException {
//        // Retrieve schematic, should never return null as schematic data is stored in database on start up
//        SchematicData schematicData = schematicDataDAO.find(plan.getChecksum());
//
//        // Check if it is a valid location
//        CuboidDimension dimension = SchematicUtil.calculateDimension(schematicData, pos, direction);
//        if (dimension.getMinY() <= 1) {
//            throw new StructureException("Structures must be placed above y:1");
//        } else if (dimension.getMaxY() > world.getMaxY()) {
//            throw new StructureException("Can't place structurs above " + world.getMaxY() + " (World max height)");
//        }
//
//        // Check if structure overlaps another structure
//        if (overlaps(world.getName(), dimension)) {
//            throw new StructureException("Structure overlaps another structure");
//        }
//
//        // Create structure
//        Structure structure = new Structure(world, pos, direction, schematicData);
//        structure.setName(plan.getName() == null ? "Structure #" + structure.getId() : plan.getName());
//        structure.setRefundValue(plan.getPrice());
//
//        // Save structure & retrieve stored instance which has a generated id
//        structure = structureDAO.save(structure);
//
//        try {
//            final File STRUCTURE_DIR = getFolder(structure);
//            if (!STRUCTURE_DIR.exists()) {
//                STRUCTURE_DIR.mkdirs();
//            }
//
//            File config = plan.getConfig();
//            File schematicFile = plan.getSchematic();
//
//            FileUtils.copyFile(config, new File(STRUCTURE_DIR, "StructurePlan.xml"));
//            FileUtils.copyFile(schematicFile, new File(STRUCTURE_DIR, schematicFile.getName()));
//        } catch (IOException ex) {   
//            throw new AssertionError(ex);
//        }
//
//        
//        getStructureDocumentManager().register(structure);
//
//
//        if (player != null) {
//            makeOwner(player, PlayerOwnership.Type.FULL, structure);
//        }
//        setState(structure, Structure.State.QUEUED);
//        structure = structureDAO.save(structure);
//        
////        getEventBus().post(new StructureCreateEvent(structure));
//        return structure;
//    }
//
//    @Override
//    public boolean build(Player player, Structure structure, BuildOptions options, boolean force) {
//        if (structure == null) {
//            throw new AssertionError("Null structure");
//        }
//        if (player == null) {
//            throw new AssertionError("Null player");
//        }
//        if (structure.getId() == null) {
//            throw new AssertionError("structure is not a saved instance");
//        }
//        if (!structure.isOwner(player, PlayerOwnership.Type.FULL)) {
//            player.print(ChatColors.RED + "You don't have FULL ownership of this structure");
//            return false;
//        }
//
//        structureTaskHandler.build(player, structure, options, force);
//        return true;
//    }
//
//    @Override
//    public boolean demolish(Player player, Structure structure, DemolitionOptions options, boolean force) {
//        if (structure == null) {
//            throw new AssertionError("Null structure");
//        }
//        if (player == null) {
//            throw new AssertionError("Null player");
//        }
//        if (structure.getId() == null) {
//            throw new AssertionError("structure is not a saved instance");
//        }
//        if (!structure.isOwner(player, PlayerOwnership.Type.FULL)) {
//            player.print(ChatColors.RED + "You don't have FULL ownership of this structure");
//            return false;
//        }
//
//        structureTaskHandler.demolish(player, structure, options, force);
//        return true;
//    }
//
//    @Override
//    public boolean stop(Player player, Structure structure) {
//        if (structure == null) {
//            throw new AssertionError("Null structure");
//        }
//
//        if (structure.getId() == null) {
//            throw new AssertionError("structure is not a saved instance");
//        }
//
//        if (!structure.isOwner(player, PlayerOwnership.Type.FULL)) {
//            player.print(ChatColors.RED + "You don't have FULL ownership of this structure");
//            return false;
//        }
//
//        return structureTaskHandler.stop(structure);
//    }
//    
//    @Override
//    public boolean makeOwner(Player player, PlayerOwnership.Type type, Structure structure) {
//        if (player == null) {
//            throw new AssertionError("Null player");
//        }
//        if (structure == null) {
//            throw new AssertionError("Null structure");
//        }
//
//        if (!structure.addOwner(player, type)) {
//            return false;
//        }
//        structureDAO.save(structure);
//        return true;
//    }
//
//    @Override
//    public boolean makeMember(Player player, Structure structure) {
//        if (player == null) {
//            throw new AssertionError("Null player");
//        }
//        if (structure == null) {
//            throw new AssertionError("Null structure");
//        }
//
//        if (!structure.addMember(player)) {
//            return false;
//        }
//        structureDAO.save(structure);
//        return true;
//    }
//
//    @Override
//    public boolean removeOwner(Player player, Structure structure) {
//        if (player == null) {
//            throw new AssertionError("Null player");
//        }
//        if (structure == null) {
//            throw new AssertionError("Null structure");
//        }
//
//        if (!structure.removeOwner(player)) {
//            return false;
//        }
//
//        structureDAO.save(structure);
//        return true;
//    }
//
//    @Override
//    public boolean removeMember(Player player, Structure structure) {
//        if (player == null) {
//            throw new AssertionError("Null player");
//        }
//        if (structure == null) {
//            throw new AssertionError("Null structure");
//        }
//        if (!structure.addMember(player)) {
//            return false;
//        }
//        structureDAO.save(structure);
//        return true;
//    }

    

    

}
