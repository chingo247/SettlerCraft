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
package com.chingo247.structureapi.handlers;

import com.chingo247.settlercraft.core.Direction;
import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.core.model.WorldNode;
import com.chingo247.settlercraft.core.concurrent.KeyPool;
import com.chingo247.settlercraft.core.platforms.services.IEconomyProvider;
import com.chingo247.structureapi.ConstructionWorld;
import com.chingo247.structureapi.exception.StructureException;
import com.chingo247.structureapi.exception.StructureRestrictionException;
import com.chingo247.structureapi.model.structure.StructureRelations;
import com.chingo247.structureapi.platform.bukkit.selection.HologramSelectionManager;
import com.chingo247.structureapi.platform.permission.PermissionManager;
import com.chingo247.structureapi.selection.CUISelectionManager;
import com.chingo247.structureapi.selection.ISelectionManager;
import com.chingo247.structureapi.selection.NoneSelectionManager;
import com.chingo247.structureapi.model.structure.ConstructionStatus;
import com.chingo247.structureapi.model.world.StructureWorldRepository;
import com.chingo247.structureapi.IStructureAPI;
import com.chingo247.structureapi.model.structure.StructureNode;
import com.chingo247.structureapi.model.world.IStructureWorld;
import com.chingo247.structureapi.model.world.IStructureWorldRepository;
import com.chingo247.structureapi.StructureAPI;
import com.chingo247.structureapi.StructureCreator;
import com.chingo247.structureapi.plan.IStructurePlan;
import com.chingo247.structureapi.plan.StructurePlanManager;
import com.chingo247.structureapi.construction.options.BuildOptions;
import com.chingo247.structureapi.model.RelTypes;
import com.chingo247.structureapi.model.structure.IStructureRepository;
import com.chingo247.structureapi.model.structure.Structure;
import com.chingo247.structureapi.model.structure.StructureRepository;
import com.chingo247.structureapi.util.PlacementUtil;
import com.chingo247.structureapi.util.WorldUtil;
import com.chingo247.xplatform.core.AInventory;
import com.chingo247.xplatform.core.AItemStack;
import com.chingo247.xplatform.core.APlatform;
import com.chingo247.xplatform.core.IColors;
import com.chingo247.xplatform.core.IPlayer;
import com.chingo247.xplatform.core.IWorld;
import com.google.common.collect.Maps;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.blocks.ItemType;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author Chingo
 */
public class PlayerPlaceStructureHandler {

    private final KeyPool<UUID> playerPool;
    private final IEconomyProvider economyProvider;
    private final IStructureAPI structureAPI;
    private final IColors color;
    private final IStructureWorldRepository structureWorldRepository;
    private final IStructureRepository structureRepository;
    private final APlatform platform;

    public PlayerPlaceStructureHandler(IEconomyProvider economyProvider) {
        this.playerPool = new KeyPool<>(SettlerCraft.getInstance().getExecutor());
        this.economyProvider = economyProvider;
        this.structureAPI = StructureAPI.getInstance();
        this.platform = structureAPI.getPlatform();
        this.color = platform.getChatColors();
        
        // Setup repositories
        GraphDatabaseService graph = SettlerCraft.getInstance().getNeo4j();
        this.structureWorldRepository = new StructureWorldRepository(graph);
        this.structureRepository = new StructureRepository(graph);
    }

    public void handleDeselect(Player player) {

        if (CUISelectionManager.getInstance().hasSelection(player)) {
            CUISelectionManager.getInstance().deselect(player);
        }
        if (HologramSelectionManager.getInstance().hasHologramsProvider() && HologramSelectionManager.getInstance().hasSelection(player)) {
            HologramSelectionManager.getInstance().deselect(player);
        }
        if (NoneSelectionManager.getInstance().hasSelection(player)) {
            NoneSelectionManager.getInstance().deselect(player);
        }

    }

    public void handle(final AItemStack planItem, final Player player, final World world, final Vector pos) {
        handle(planItem, player, world, pos, null);
    }

    public void handle(final AItemStack planItem, final Player player, final World world, final Vector pos, ISelectionManager selectionManager) {
        if (!isStructurePlan(planItem)) {
            return;
        }

        if (!PermissionManager.getInstance().isAllowed(player, PermissionManager.Perms.PLACE_STRUCTURE)) {
            player.printError("You have no permission to place structures");
            return;
        }

        LocalSession session = WorldEdit.getInstance().getSession(player);

        final ISelectionManager slm;
        // Set the SelectionManager if null...
        if (selectionManager == null) {
            if (session.hasCUISupport()) {
                slm = CUISelectionManager.getInstance();
//            } else if (HologramSelectionManager.getInstance().hasHologramsProvider()) {
//                slm = HologramSelectionManager.getInstance();
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
                    IStructurePlan plan = StructurePlanManager.getInstance().getPlan(planId);

                    if (plan == null) {
                        if (structureAPI.isLoading()) {
                            player.print(color.red() + "Plans are not loaded yet... please wait...");
                            return;
                        }
                        player.print(color.red() + "The plan has become invalid, reason: data was not found");
                        int amount = planItem.getAmount();
                        double value = getValue(planItem);
                        if (economyProvider != null && value > 0.0d) {
                            economyProvider.give(player.getUniqueId(), value * amount);
                            player.print(color.red() + "Invalid StructurePlans have been removed and you've been refunded: " + (value * amount));
                        } else {
                            player.print(color.red() + "Removed invalid structure plans from your inventory");
                        }

                        iPlayer.getInventory().removeItem(planItem);

                        return;
                    }
                    handlePlace(plan, planItem, player, world, pos, slm);
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
        });

    }

    private void handlePlace(IStructurePlan plan, AItemStack item, Player player, World world, Vector pos1, ISelectionManager selectionManager) {
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
            if (!(selectionManager instanceof NoneSelectionManager)) {
                player.print(color.yellow() + "Left-Click " + color.reset() + " in the " + color.green() + " green " + color.reset() + "square to " + color.yellow() + "confirm");
                player.print(color.yellow() + "Right-Click " + color.reset() + "to" + color.yellow() + " deselect");
            }
        } else if (selectionManager.matchesCurrentSelection(player, pos1, pos2)) {

            if (toLeft) {
                // Fix WTF HOW?!!1?
                pos1 = WorldUtil.translateLocation(pos1, direction, (-(plan.getPlacement().getCuboidRegion().getMaximumPoint().getBlockZ() - 1)), 0, 0);
            }

            Structure possibleParentStructure;
            try {
                possibleParentStructure = getAndCheckSmallestOverlappingStructure(player, world, pos1);
            } catch (StructureException ex) {
                player.printError(ex.getMessage());
                selectionManager.deselect(player);
                return;
            }

            if (canPlace(possibleParentStructure, player, world, pos1, direction, plan)) {
                Structure structure;
                try {
                    // Create Structure using the SettlerCraft restrictions
                    
                    ConstructionWorld cw = structureAPI.getConstructionWorld(world);
                    StructureCreator sc = cw.getStructureCreator();
                    
                    if (possibleParentStructure != null) {
                        if (possibleParentStructure.getStatus() != ConstructionStatus.COMPLETED) {
                            player.printError("Status of #" + possibleParentStructure.getId() + " must not be in progress before substructures can be placed inside");
                            return;
                        }
                        structure = sc.createSubstructure(possibleParentStructure, plan, pos1, direction, player);
                    } else {
                        structure = sc.createStructure(plan, pos1, direction, player);
                    }

                    if (structure != null) {
                        AItemStack clone = item.clone();
                        clone.setAmount(1);
                        iPlayer.getInventory().removeItem(clone);
                        iPlayer.updateInventory();

                        if (!structureAPI.isQueueLocked(player.getUniqueId())) {
                            structureAPI.build(iPlayer.getUniqueId(), structure, new BuildOptions());
                        } else {
                            player.printError("Your queue is locked at the moment, try '/stt build " + structure.getId() + "' when your queue is unlocked");
                        }
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
            if (!(selectionManager instanceof NoneSelectionManager)) {
                player.print(color.yellow() + "Left-Click " + color.reset() + " in the " + color.green() + " green " + color.reset() + "square to " + color.yellow() + "confirm");
                player.print(color.yellow() + "Right-Click " + color.reset() + "to" + color.yellow() + " deselect");
            }
        }

    }

    private Structure getAndCheckSmallestOverlappingStructure(Player player, World world, Vector position) throws StructureException {

        GraphDatabaseService graph = SettlerCraft.getInstance().getNeo4j();
        Structure structure = null;
        boolean isOwner = false;
        try (Transaction tx = graph.beginTx()) {

            IWorld w = SettlerCraft.getInstance().getPlatform().getServer().getWorld(world.getName());

            Map<String, Object> params = Maps.newHashMap();
            params.put("worldId", w.getUUID().toString());

            String query
                    = "MATCH ( world: " + WorldNode.LABEL + " { " + WorldNode.ID_PROPERTY + ": {worldId} })"
                    + " WITH world "
                    + " MATCH (world)<-[:" + RelTypes.WITHIN + "]-(s:" + StructureNode.LABEL + ")"
                    + " WHERE s." + StructureNode.DELETED_AT_PROPERTY + " IS NULL"
                    + " AND s." + StructureNode.MAX_X_PROPERTY + " >= " + position.getBlockX() + " AND s." + StructureNode.MIN_X_PROPERTY + " <= " + position.getBlockX()
                    + " AND s." + StructureNode.MAX_Y_PROPERTY + " >= " + position.getBlockY() + " AND s." + StructureNode.MIN_Y_PROPERTY + " <= " + position.getBlockY()
                    + " AND s." + StructureNode.MAX_Z_PROPERTY + " >= " + position.getBlockZ() + " AND s." + StructureNode.MIN_Z_PROPERTY + " <= " + position.getBlockZ()
                    + " RETURN s as structure"
                    + " ORDER BY s." + StructureNode.SIZE_PROPERTY + " ASC "
                    + " LIMIT 1";
            System.out.println("QUERY: " + query);

            Result result = graph.execute(query, params);
            StructureNode structureNode = null;
            while (result.hasNext()) {
                Map<String, Object> map = result.next();
                Node n = (Node) map.get("structure");
                structureNode = new StructureNode(n);
                structure = new Structure(n);
            }

            if (structureNode != null) {
                isOwner = structureNode.getOwnerDomain().isOwner(player.getUniqueId());
            }

            tx.success();
        }

        if (structure != null && !structureAPI.getConfig().isSubstructuresAllowed()) {
            throw new StructureException("Placing substructures is disabled");
        }

        if (structure != null && !isOwner) {
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

                    int modifier = 1;
                    try {
                        if (s.contains("M")) {
                            s = s.substring(0, s.indexOf("M"));
                            modifier = 1_000_000;
                        } else if (s.contains("K")) {
                            s = s.substring(0, s.indexOf("K"));
                            modifier = 1_000;
                        }
                        price = Double.parseDouble(s.trim());
                        price *= modifier;
                    } catch (NumberFormatException nfe) {
                        return 0;
                    }
                    return price;
                }
            }
        }
        return price;
    }

    private boolean canPlace(Structure possibleParent, Player player, World world, Vector pos1, Direction direction, IStructurePlan plan) {
        // Check for overlap with other structures
        Vector min = pos1;
        Vector max = PlacementUtil.getPoint2Right(min, direction, plan.getPlacement().getCuboidRegion().getMaximumPoint());
        GraphDatabaseService graph = SettlerCraft.getInstance().getNeo4j();

        StructureAPI sapi = (StructureAPI) StructureAPI.getInstance();
        try {
            sapi.checkRestrictions(player, world, new CuboidRegion(min, max));
        } catch (StructureRestrictionException ex) {
            player.printError(ex.getMessage());
            return false;
        }

        try (Transaction tx = graph.beginTx()) {
            StructureNode overlappingStructure = null;
            if (possibleParent != null) {
                Node n = possibleParent.getNode();
                StructureNode sn = new StructureNode(n);
                Iterator<StructureNode> subIt = sn.getSubStructuresWithin(new CuboidRegion(min, max)).iterator();
                overlappingStructure = subIt.hasNext() ? subIt.next() : null;
            } else {
                IWorld iw = platform.getServer().getWorld(world.getName());
                Iterator<StructureNode> subIt = structureRepository.findStructuresWithin(iw.getUUID(), new CuboidRegion(min, max), 1).iterator();
                overlappingStructure = subIt.hasNext() ? subIt.next() : null;
            }

            if (overlappingStructure != null) {
                CuboidRegion overlappingArea = overlappingStructure.getCuboidRegion();
                player.printError("Can't place structure, structure would overlap structure #" + overlappingStructure.getId() + " - " + overlappingStructure.getName() + "\n"
                        + "Located at min: " + overlappingArea.getMinimumPoint() + ", max: " + overlappingArea.getMaximumPoint());
                tx.success();
                return false;
            }

            tx.success();
        }

        return true;
    }

}
