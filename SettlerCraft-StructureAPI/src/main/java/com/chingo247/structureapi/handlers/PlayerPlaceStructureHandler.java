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
import com.chingo247.settlercraft.core.concurrent.KeyPool;
import com.chingo247.settlercraft.core.platforms.services.IEconomyProvider;
import com.chingo247.structureapi.ConstructionWorld;
import com.chingo247.structureapi.StructureException;
import com.chingo247.structureapi.platform.bukkit.selection.HologramSelectionManager;
import com.chingo247.structureapi.platform.permission.PermissionManager;
import com.chingo247.structureapi.selection.CUISelectionManager;
import com.chingo247.structureapi.selection.ISelectionManager;
import com.chingo247.structureapi.selection.NoneSelectionManager;
import com.chingo247.structureapi.model.structure.ConstructionStatus;
import com.chingo247.structureapi.model.world.StructureWorldRepository;
import com.chingo247.structureapi.IStructureAPI;
import com.chingo247.structureapi.IStructureManager;
import com.chingo247.structureapi.model.structure.StructureNode;
import com.chingo247.structureapi.model.world.IStructureWorldRepository;
import com.chingo247.structureapi.StructureAPI;
import com.chingo247.structureapi.plan.IStructurePlan;
import com.chingo247.structureapi.plan.StructurePlanManager;
import com.chingo247.structureapi.construction.options.BuildOptions;
import com.chingo247.structureapi.RestrictionException;
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
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.blocks.ItemType;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.neo4j.graphdb.GraphDatabaseService;
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

    public void handle(final AItemStack planItem, final Player player, final ConstructionWorld world, final Vector pos) {
        handle(planItem, player, world, pos, null);
    }

    public void handle(final AItemStack planItem, final Player player, final ConstructionWorld world, final Vector pos, ISelectionManager selectionManager) {
        if (!isStructurePlan(planItem)) {
            return;
        }

        if (!PermissionManager.getInstance().isAllowed(player, PermissionManager.Perms.SETTLER_STRUCTURE_PLACE)) {
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

    private void handlePlace(IStructurePlan plan, AItemStack item, Player player, ConstructionWorld world, Vector pos1, ISelectionManager selectionManager) {
        IPlayer iPlayer = SettlerCraft.getInstance().getPlatform().getPlayer(player.getUniqueId());

        Direction direction = WorldUtil.getDirection(iPlayer.getYaw());
        Vector pos2;

        boolean toLeft = iPlayer.isSneaking();

        if (toLeft) {
            pos2 = PlacementUtil.getPoint2Left(pos1, direction, plan.getPlacement().getCuboidRegion().getMaximumPoint());
        } else {
            pos2 = PlacementUtil.getPoint2Right(pos1, direction, plan.getPlacement().getCuboidRegion().getMaximumPoint());
        }

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
            PlacingResult placingResult = canPlace(player, world, pos1, direction, plan);
            if (placingResult.canPlace()) {
                Structure structure;
                try {
                    IStructureManager sc = world.getStructureHandler();

                    if (placingResult.hasParentStructure()) {
                        Structure parentStructure = placingResult.getParentStructure();
                        if (parentStructure.getStatus() != ConstructionStatus.COMPLETED) {
                            player.printError("Status of #" + parentStructure.getId() + " must not be in progress before substructures can be placed inside");
                            return;
                        }
                        structure = sc.createSubstructure(parentStructure, plan, pos1, direction, player);
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

    private PlacingResult canPlace(Player player, ConstructionWorld world, Vector pos1, Direction direction, IStructurePlan plan) {
        // Check for overlap with other structures
        Vector min = pos1;
        Vector max = PlacementUtil.getPoint2Right(min, direction, plan.getPlacement().getCuboidRegion().getMaximumPoint());
        
        CuboidRegion affectedArea = new CuboidRegion(min, max);

        try {
            world.getStructureHandler().checkWorldRestrictions(world, affectedArea);
            world.getStructureHandler().checkStructureRestrictions(player, world, affectedArea);
        } catch (RestrictionException ex) {
            player.printError(ex.getMessage());
            return new PlacingResult(false);
        }

        GraphDatabaseService graph = SettlerCraft.getInstance().getNeo4j();
        IWorld iw = platform.getServer().getWorld(world.getName());
        Transaction tx = null;
        Structure parentStructure = null;
        PlacingResult placingResult = null;
        try {
            tx = graph.beginTx();

            world.getStructureHandler().checkStructurePlacingRestrictions(player, world, affectedArea, min);

            StructureNode structureNode = structureRepository.findStructureOnPosition(iw.getUUID(), pos1);
            if (structureNode != null) {
                parentStructure = new Structure(structureNode);
            }
            placingResult = new PlacingResult(true);
            placingResult.setParentStructure(parentStructure);
            
            tx.success();
        } catch (RestrictionException ex) {
            if (tx != null) {
                tx.failure();
            }
            placingResult = new PlacingResult(false);
            player.print(ex.getMessage());
        } finally {
            if (tx != null) {
                tx.close();
            }
        }

        return placingResult;
    }

}
