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
package com.chingo247.settlercraft.structureapi.event.handlers;

import com.chingo247.xplatform.core.AInventory;
import com.chingo247.xplatform.core.AItemStack;
import com.chingo247.xplatform.core.IPlayer;
import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.structureapi.exception.StructureException;
import com.chingo247.settlercraft.core.util.KeyPool;
import com.chingo247.settlercraft.structureapi.util.WorldUtil;
import com.chingo247.settlercraft.core.Direction;
import com.chingo247.settlercraft.core.persistence.dao.world.WorldNode;
import com.chingo247.settlercraft.structureapi.structure.plan.StructurePlan;
import com.chingo247.settlercraft.core.platforms.services.IEconomyProvider;
import com.chingo247.settlercraft.structureapi.persistence.dao.StructureDAO;
import com.chingo247.settlercraft.structureapi.persistence.entities.structure.StructureNode;
import com.chingo247.settlercraft.structureapi.persistence.entities.structure.StructureRelTypes;
import com.chingo247.settlercraft.structureapi.selection.CUISelectionManager;
import com.chingo247.settlercraft.structureapi.selection.ISelectionManager;
import com.chingo247.settlercraft.structureapi.selection.NoneSelectionManager;
import com.chingo247.settlercraft.structureapi.structure.DefaultStructureFactory;
import com.chingo247.settlercraft.structureapi.structure.IStructureAPI;
import com.chingo247.settlercraft.structureapi.structure.Structure;
import com.chingo247.settlercraft.structureapi.structure.StructureAPI;
import com.chingo247.settlercraft.structureapi.structure.options.PlaceOptions;
import com.chingo247.settlercraft.structureapi.structure.plan.StructurePlanManager;
import com.chingo247.settlercraft.structureapi.util.PlacementUtil;
import com.chingo247.xplatform.core.IColors;
import com.chingo247.xplatform.core.IWorld;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.blocks.ItemType;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.world.World;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.util.com.google.common.collect.Maps;
import org.bukkit.ChatColor;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author Chingo
 */
public class StructurePlaceHandler {

    private final KeyPool<UUID> playerPool;
    private final IEconomyProvider economyProvider;
    private final IStructureAPI structureAPI;
    private final IColors color;
    private final StructureDAO structureDAO;

    public StructurePlaceHandler(IEconomyProvider economyProvider) {
        this.playerPool = new KeyPool<>(SettlerCraft.getInstance().getExecutor());
        this.economyProvider = economyProvider;
        this.structureAPI = StructureAPI.getInstance();
        this.color = structureAPI.getPlatform().getChatColors();
        this.structureDAO = new StructureDAO(SettlerCraft.getInstance().getNeo4j());
    }

    public void handle(final AItemStack planItem, final Player player, final World world, final Vector pos) {
        handle(planItem, player, world, pos, null);
    }

    public void handle(final AItemStack planItem, final Player player, final World world, final Vector pos, ISelectionManager selectionManager) {
        if (!isStructurePlan(planItem)) {
            return;
        }

        LocalSession session = WorldEdit.getInstance().getSession(player);

        final ISelectionManager slm;
        // Set the SelectionManager if null...
        if (selectionManager == null) {
            if (session.hasCUISupport()) {
                slm = CUISelectionManager.getInstance();
            } else {
                slm = NoneSelectionManager.getInstance();
            }
        } else {
            slm = selectionManager;
        }

        playerPool.execute(player.getUniqueId(), new Runnable() {

            @Override
            public void run() {
                try {

                    IPlayer iPlayer = SettlerCraft.getInstance().getPlatform().getPlayer(player.getUniqueId());
                    AInventory inventory = iPlayer.getInventory();
                    if (!inventory.hasItem(planItem)) {
                        return;
                    }

                    String planId = getPlanID(planItem);
                    StructurePlan plan = StructurePlanManager.getInstance().getPlan(planId);

                    if (plan == null) {
                        if (structureAPI.isLoading()) {
                            player.print(color.red() + "Plans are not loaded yet... please wait...");
                            return;
                        }
//                    player.print(COLOR.red() + "The plan has become invalid, reason: data was not found");
//                    int amount = inventory.getAmount(planItem);
//                    double value = getValue(planItem);
//                    if (value > 0.0d) {
//                        economyProvider.give(player.getUniqueId(), value * amount);
//                        iPlayer.getInventory().removeItem(planItem);
//                        
//                        player.print(COLOR.red() + "Invalid StructurePlans have been removed and you've been refunded: " + (value * amount));
//                    }

                        return;
                    }
                    long start = System.currentTimeMillis();
                    handlePlace(plan, planItem, player, world, pos, slm);
                    System.out.println("Place() handled in " + (System.currentTimeMillis() - start) + " ms");

                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
        });

    }

    private void handlePlace(StructurePlan plan, AItemStack item, Player player, World world, Vector pos1, ISelectionManager selectionManager) {
        
        IPlayer iPlayer = SettlerCraft.getInstance().getPlatform().getPlayer(player.getUniqueId());

        Direction direction = WorldUtil.getDirection(iPlayer.getYaw());

        Vector pos2;

        boolean toLeft = iPlayer.isSneaking();

        if (toLeft) {
            pos2 = PlacementUtil.getPoint2Left(pos1, direction, plan.getPlacement().getCuboidRegion().getMaximumPoint());
        } else {
            pos2 = PlacementUtil.getPoint2Right(pos1, direction, plan.getPlacement().getCuboidRegion().getMaximumPoint());
        }
        
        
//        if(possibleParentStructure != null) {
////            pos1 = pos1.add(0, 1, 0); // Move one up by default?
//        }

        // If player has NOT selected anything yet... make a new selection
        if (!selectionManager.hasSelection(player)) {
            selectionManager.select(player, pos1, pos2);
            player.print(color.yellow() + "Left-Click " + color.reset() + " in the " + color.green() + " green " + color.reset() + "square to " + color.yellow() + "confirm");
            player.print(color.yellow() + "Right-Click " + color.reset() + "to" + color.yellow() + " deselect");
        } else if (selectionManager.matchesCurrentSelection(player, pos1, pos2)) {

            if (toLeft) {
                // Fix WTF HOW?!!1?
                pos1 = WorldUtil.translateLocation(pos1, direction, (-(plan.getPlacement().getCuboidRegion().getMaximumPoint().getBlockZ() - 1)), 0, 0);
            }

            if (canPlace(player, world, pos1, direction, plan)) {
                Structure structure;
                try {
                    // Create Structure using the SettlerCraft restrictions
                    Structure possibleParentStructure;
                    try {
                    possibleParentStructure = getAndCheckSmallestOverlappingStructure(player,world, pos1);
                    } catch (StructureException ex) {
                        player.printError(ex.getMessage());
                        selectionManager.deselect(player);
                        return;
                    }
                    
                    long start = System.currentTimeMillis();
                    
                    if(possibleParentStructure != null) {
                        structure = structureAPI.createSubstructure(possibleParentStructure, plan, world, pos1, direction, player);
                        System.out.println("Substructure placed in " + (System.currentTimeMillis() - start) + " ms");
                    } else {
                        structure = structureAPI.createStructure(plan, world, pos1, direction, player);
                        System.out.println("Structure placed in " + (System.currentTimeMillis() - start)  + " ms");
                    }
                    
                    if (structure != null) {
//                        AItemStack clone = item.clone();
//                        clone.setAmount(1);
//                        iPlayer.getInventory().removeItem(clone);
//                        iPlayer.updateInventory();
                        structure.build(player, new PlaceOptions(), false);
                    }
                } catch (StructureException ex) {
                    player.print(color.red() + ex.getMessage());
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }

                selectionManager.deselect(player);

            }
        } else {
            selectionManager.deselect(player);
            selectionManager.select(player, pos1, pos2);
            player.print(color.yellow() + "Left-Click " + color.reset() + " in the " + color.green() + " green " + color.reset() + "square to " + color.yellow() + "confirm");
            player.print(color.yellow() + "Right-Click " + color.reset() + "to" + color.yellow() + " deselect");
        }

    }

    private Structure getAndCheckSmallestOverlappingStructure(Player player, World world, Vector position) throws StructureException {

        GraphDatabaseService graph = SettlerCraft.getInstance().getNeo4j();
        Structure structure = null;
        boolean isOwner = false;
        try (Transaction tx = graph.beginTx()) {

            IWorld w = SettlerCraft.getInstance().getPlatform().getServer().getWorld(world.getName());
            long start = System.currentTimeMillis();

            Map<String, Object> params = Maps.newHashMap();
            params.put("worldId", w.getUUID().toString());

            String query
                    = "MATCH (world:" + WorldNode.LABEL.name() + " { " + WorldNode.ID_PROPERTY + ": {worldId} })"
                    + " WITH world "
                    + " MATCH (world)<-[:" + StructureRelTypes.RELATION_WITHIN + "]-(s:" + StructureNode.LABEL.name() + ")"
                    + " WHERE s." + StructureNode.DELETED_AT_PROPERTY + " IS NULL"
                    + " AND s." + StructureNode.MAX_X_PROPERTY + " >= " + position.getBlockX() + " AND s." + StructureNode.MIN_X_PROPERTY + " <= " + position.getBlockX()
                    + " AND s." + StructureNode.MAX_Y_PROPERTY + " >= " + position.getBlockY() + " AND s." + StructureNode.MIN_Y_PROPERTY + " <= " + position.getBlockY()
                    + " AND s." + StructureNode.MAX_Z_PROPERTY + " >= " + position.getBlockZ() + " AND s." + StructureNode.MIN_Z_PROPERTY + " <= " + position.getBlockZ()
                    + " RETURN s as structure"
                    + " ORDER BY s." + StructureNode.SIZE_PROPERTY + " ASC "
                    + " LIMIT 1";

            Result result = graph.execute(query, params);
            System.out.println("getSmallestOverlapping() in " + (System.currentTimeMillis() - start) + " ms");
            while (result.hasNext()) {
                Map<String, Object> map = result.next();
                Node n = (Node) map.get("structure");
                structure = DefaultStructureFactory.getInstance().makeStructure(new StructureNode(n));
            }
            
            if(structure != null) {
                isOwner = structureDAO.isOwnerOfStructure(structure.getId(), player);
            }
            
            tx.success();
        }
        
        if(structure != null && !isOwner) {
            throw new StructureException("Structure will overlap another structure you don't own!");
        }
        
        return structure;
    }

    public static boolean isStructurePlan(AItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }

        if (itemStack.getMaterial() != ItemType.PAPER.getID()) {
            return false;
        }

        List<String> lore = itemStack.getLore();
        if (lore == null || lore.isEmpty()) {
            return false;
        } else {
            for (String s : lore) {
                if (s.contains("Type") && s.contains("StructurePlan")) {
                    return true;
                }
            }
            return false;
        }
    }

    private String getPlanID(AItemStack itemStack) {
        if (isStructurePlan(itemStack)) {
            List<String> lore = itemStack.getLore();
            for (String s : lore) {
                if (s.contains("UniqueId")) {
                    s = s.substring(s.indexOf(":") + 1);
                    s = ChatColor.stripColor(s);
                    return s.trim();
                }
            }
        }
        return null;
    }

    public double getValue(AItemStack itemStack) {
        double price = 0;
        if (isStructurePlan(itemStack)) {

            List<String> lore = itemStack.getLore();
            for (String s : lore) {
                if (s.contains("Price")) {
                    s = s.substring(s.indexOf(":") + 1);
                    s = ChatColor.stripColor(s);
                    if (s.contains("FREE")) {
                        return 0;
                    }

                    try {
                        price = Double.parseDouble(s.trim());
                    } catch (NumberFormatException nfe) {
                        return 0;
                    }
                    return price;
                }
            }
        }
        return price;
    }

    private boolean canPlace(Player player, World world, Vector pos1, Direction direction, StructurePlan plan) {
        System.out.println("Still have to implement canPlace() in " + this.getClass().getName());
        return true;
    }

}
