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
package com.chingo247.settlercraft.handlers;

import com.chingo247.settlercraft.SettlerCraft;
import com.chingo247.structureapi.construction.options.Options;
import com.chingo247.structureapi.exception.StructureException;
import com.chingo247.structureapi.plan.StructurePlan;
import com.chingo247.settlercraft.provider.EconomyProvider;
import com.chingo247.settlercraft.selection.CUISelectionManager;
import com.chingo247.settlercraft.selection.ISelectionManager;
import com.chingo247.settlercraft.selection.NoneSelectionManager;
import com.chingo247.settlercraft.common.util.KeyPool;
import com.chingo247.settlercraft.common.util.WorldUtil;
import com.chingo247.settlercraft.common.world.Direction;
import static com.chingo247.settlercraft.common.world.Direction.EAST;
import static com.chingo247.settlercraft.common.world.Direction.NORTH;
import static com.chingo247.settlercraft.common.world.Direction.SOUTH;
import static com.chingo247.settlercraft.common.world.Direction.WEST;
import com.chingo247.structureapi.structure.StructureAPI;
import com.chingo247.structureapi.structure.Structure;
import com.chingo247.xcore.core.AInventory;
import com.chingo247.xcore.core.AItemStack;
import com.chingo247.xcore.core.IPlayer;
import com.chingo247.xcore.util.ChatColors;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.blocks.ItemType;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.world.World;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;

/**
 *
 * @author Chingo
 */
public class StructurePlaceHandler {

    private final KeyPool<UUID> playerPool;
    private final EconomyProvider economyProvider;
    private final SettlerCraft settlerCraft;
    private final StructureAPI structureAPI;

    public StructurePlaceHandler(ExecutorService service, EconomyProvider economyProvider, SettlerCraft settlerCraft) {
        this.playerPool = new KeyPool<>(service);
        this.economyProvider = economyProvider;
        this.settlerCraft = settlerCraft;
        this.structureAPI = settlerCraft.getStructureAPI();
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
                
                IPlayer iPlayer = settlerCraft.getPlatform().getPlayer(player.getUniqueId());
                AInventory inventory = iPlayer.getInventory();
                if (!inventory.hasItem(planItem)) {
                    System.out.println("Player doesnt have the item!");
                    return;
                }

                System.out.println("Player has the item: " + planItem.getName());

                String planId = getPlanID(planItem);
                System.out.println("PlanID: " + planId);

                StructurePlan plan = structureAPI.getPlan(planId);

                if (plan == null) {
                    System.out.println("Plan was null...");
                    if (settlerCraft.isLoadingPlans()) {
                        player.print(ChatColors.RED + "Plans are not loaded yet... please wait...");
                        return;
                    }
                    player.print(ChatColors.RED + "Plan: " + planId + "doesn't exist!");
                    int amount = inventory.getAmount(planItem);
                    System.out.println("Total amount of invalid plans: " + amount);
                    double value = getValue(planItem);
                    if (value > 0.0d) {
                        economyProvider.give(player.getUniqueId(), value * amount);
                        iPlayer.getInventory().removeItem(planItem);
                        player.print(ChatColor.RED + "StructurePlan was invalid...");
                        player.print(ChatColor.RED + "Invalid StructurePlans have been removed and you've been refunded: " + (value * amount));
                    }

                    return;
                }
                System.out.println("Plan: " + plan.getName() + " ");
                handlePlace(plan, planItem, player, world, pos, slm);
                
                } catch (Exception ex) {
                    System.out.println("Error:");
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
        });

    }
    
    private void handlePlace(StructurePlan plan, AItemStack item, Player player, World world, Vector pos1, ISelectionManager selectionManager) {
        System.out.println("Handling placement...");
        IPlayer iPlayer = settlerCraft.getPlatform().getPlayer(player.getUniqueId());
        
        Direction direction = WorldUtil.getDirection(iPlayer.getYaw());
        System.out.println("Direction: " + direction);
        
        Vector pos2;
        
        boolean toLeft = iPlayer.isSneaking();
        if (toLeft) {
            System.out.println("Player was sneaking... pointing left!");
            pos2 = getPoint2Left(pos1, direction, plan.getPlacement().getMaxPosition());
        } else {
            System.out.println("Player was NOT sneaking... pointing right!");
            pos2 = getPoint2Right(pos1, direction, plan.getPlacement().getMaxPosition());
        }
        
        System.out.println("SelectionManager: " + selectionManager);

        // If player has NOT selected anything yet... make a new selection
        if(!selectionManager.hasSelection(player)) {
            selectionManager.select(player,  pos1, pos2);
            player.print(ChatColors.YELLOW + "Left-Click " + ChatColors.RESET + " in the " + ChatColors.GREEN + " green " + ChatColors.RESET + "square to " + ChatColors.YELLOW + "confirm");
            player.print(ChatColors.YELLOW + "Right-Click " + ChatColors.RESET + "to" + ChatColors.YELLOW + " deselect");
        } else if(selectionManager.matchesCurrentSelection(player, pos1, pos2)){
            if (toLeft) {
                // Fix WTF HOW?!!1?
                pos1 = WorldUtil.translateLocation(pos1, direction, (-(plan.getPlacement().getMaxPosition().getBlockX()- 1)), 0, 0);
            }
            
            System.out.println("Can place?");
            if (canPlace(player, world, pos1, direction, plan)) {
                com.chingo247.settlercraft.SCWorld scWorld = settlerCraft.getWorld(world.getName());
                Structure structure;
                try {
                    System.out.println("Creating structure");
                    structure = scWorld.getStructureManager().createStructure(plan, pos1, direction);
                    
                    System.out.println("Structure: " + structure);
                    if (structure != null) {
//                        AItemStack clone = item.clone();
//                        clone.setAmount(1);
//                        iPlayer.getInventory().removeItem(clone);
//                        iPlayer.updateInventory();
                        System.out.println("Build structure!");
                        structure.build(player.getUniqueId(), Options.defaultOptions(), false);
                    }
                } catch (StructureException ex) {
                    player.print(ChatColors.RED + ex.getMessage());
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }

                selectionManager.deselect(player);

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
        if (lore.isEmpty()) {
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
                if (s.contains("Path")) {
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
    
        /**
     * Used to to getPlan the secondary position when selecting. So that the
     * green square is always at the same place as the clicked block and the
     * secondary always across.
     *
     * @param point1
     * @param direction
     * @param size
     * @return point2
     */
    private Vector getPoint2Right(Vector point1, Direction direction, Vector size) {
        switch (direction) {
            case EAST:
                return point1.add(size.subtract(1, 1, 1));
            case SOUTH:
                return point1.add(-(size.getBlockZ() - 1), size.getBlockY() - 1, (size.getBlockX() - 1));
            case WEST:
                return point1.add(-(size.getBlockX() - 1), size.getBlockY() - 1, -(size.getBlockZ() - 1));
            case NORTH:
                return point1.add((size.getBlockZ() - 1), size.getBlockY() - 1, -(size.getBlockX() - 1));
            default:
                throw new AssertionError("unreachable");
        }
    }

    /**
     * Used to to getPlan the secondary position when selecting. So that the
     * green square is always at the same place as the clicked block and the
     * secondary always across.
     *
     * @param point1
     * @param direction
     * @param size
     * @return point2
     */
    private Vector getPoint2Left(Vector point1, Direction direction, Vector size) {
        switch (direction) {
            case EAST:
                return point1.add((size.getBlockX() - 1), size.getBlockY() - 1, -(size.getBlockZ() - 1));
            case SOUTH:
                return point1.add((size.getBlockZ() - 1), size.getBlockY() - 1, (size.getBlockX() - 1));
            case WEST:
                return point1.add(-(size.getBlockX() - 1), size.getBlockY() - 1, (size.getBlockZ() - 1));
            case NORTH:
                return point1.add(-(size.getBlockZ() - 1), size.getBlockY() - 1, -(size.getBlockX() - 1));
            default:
                throw new AssertionError("unreachable");
        }
    }

    private boolean canPlace(Player player, World world, Vector pos1, Direction direction, StructurePlan plan) {
        System.out.println("Still have to implement canPlace() in " + this.getClass().getName());
        return true;
    }

}
