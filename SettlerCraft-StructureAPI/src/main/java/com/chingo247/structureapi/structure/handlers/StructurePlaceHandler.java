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
package com.chingo247.structureapi.structure.handlers;

import com.chingo247.xplatform.core.AInventory;
import com.chingo247.xplatform.core.AItemStack;
import com.chingo247.xplatform.core.IPlayer;
import com.chingo247.xplatform.util.ChatColors;
import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.structureapi.structure.exception.StructureException;
import com.chingo247.settlercraft.core.util.KeyPool;
import com.chingo247.structureapi.util.WorldUtil;
import com.chingo247.settlercraft.core.Direction;
import com.chingo247.structureapi.structure.plan.StructurePlan;
import com.chingo247.settlercraft.core.services.IEconomyProvider;
import com.chingo247.structureapi.selection.CUISelectionManager;
import com.chingo247.structureapi.selection.ISelectionManager;
import com.chingo247.structureapi.selection.NoneSelectionManager;
import com.chingo247.structureapi.structure.StructureAPI;
import com.chingo247.structureapi.structure.Structure;
import com.chingo247.structureapi.structure.plan.placement.PlaceOptions;
import com.chingo247.structureapi.util.PlacementUtil;
import com.chingo247.xplatform.core.IColor;
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

/**
 *
 * @author Chingo
 */
public class StructurePlaceHandler {

    private final KeyPool<UUID> playerPool;
    private final IEconomyProvider economyProvider;
    private final StructureAPI structureAPI;
    private final IColor COLOR;

    public StructurePlaceHandler(IEconomyProvider economyProvider) {
        this.playerPool = new KeyPool<>(SettlerCraft.getInstance().getExecutor());
        this.economyProvider = economyProvider;
        this.structureAPI = StructureAPI.getInstance();
        this.COLOR = structureAPI.getPlatform().getChatColors();
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
                System.out.println("HAS CUI SUPPORT");
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

                StructurePlan plan = structureAPI.getPlanById(planId);

                if (plan == null) {
                    if (structureAPI.isLoadingPlans()) {
                        player.print(COLOR.red() + "Plans are not loaded yet... please wait...");
                        return;
                    }
                    player.print(COLOR.red() + "Plan: " + planId + "doesn't exist!");
                    int amount = inventory.getAmount(planItem);
                    double value = getValue(planItem);
                    if (value > 0.0d) {
                        economyProvider.give(player.getUniqueId(), value * amount);
                        iPlayer.getInventory().removeItem(planItem);
                        
                        player.print(COLOR.red() + "Invalid StructurePlans have been removed and you've been refunded: " + (value * amount));
                    }

                    return;
                }
                
                
                handlePlace(plan, planItem, player, world, pos, slm);
                
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
        

        // If player has NOT selected anything yet... make a new selection
        if(!selectionManager.hasSelection(player)) {
            selectionManager.select(player,  pos1, pos2);
            player.print(COLOR.yellow() + "Left-Click " + COLOR.reset() + " in the " + COLOR.green() + " green " + COLOR.reset() + "square to " + COLOR.yellow() + "confirm");
            player.print(COLOR.yellow() + "Right-Click " + COLOR.reset() + "to" + COLOR.yellow() + " deselect");
        } else if(selectionManager.matchesCurrentSelection(player, pos1, pos2)){
            
            if (toLeft) {
                // Fix WTF HOW?!!1?
                pos1 = WorldUtil.translateLocation(pos1, direction, (-(plan.getPlacement().getCuboidRegion().getMaximumPoint().getBlockX()- 1)), 0, 0);
            }
            
            if (canPlace(player, world, pos1, direction, plan)) {
                Structure structure;
                try {
                    // Create Structure using the SettlerCraft restrictions
                    CuboidRegion region = new CuboidRegion(pos1, pos2);
                    System.out.println("before: " + region.getMinimumPoint() + ", " + region.getMaximumPoint());
                    
                    structure = structureAPI.createStructure(world, plan, pos1, direction, player);
                    if (structure != null) {
//                        AItemStack clone = item.clone();
//                        clone.setAmount(1);
//                        iPlayer.getInventory().removeItem(clone);
//                        iPlayer.updateInventory();
                        structure.build(player, new PlaceOptions(), false);
                    }
                } catch (StructureException ex) {
                    player.print(ChatColors.RED + ex.getMessage());
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }

                selectionManager.deselect(player);

            }
        } else {
            selectionManager.deselect(player);
            selectionManager.select(player,  pos1, pos2);
            player.print(ChatColors.YELLOW + "Left-Click " + ChatColors.RESET + " in the " + ChatColors.GREEN + " green " + ChatColors.RESET + "square to " + ChatColors.YELLOW + "confirm");
            player.print(ChatColors.YELLOW + "Right-Click " + ChatColors.RESET + "to" + ChatColors.YELLOW + " deselect");
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
   

    private boolean canPlace(Player player, World world, Vector pos1, Direction direction, StructurePlan plan) {
        System.out.println("Still have to implement canPlace() in " + this.getClass().getName());
        return true;
    }

}
