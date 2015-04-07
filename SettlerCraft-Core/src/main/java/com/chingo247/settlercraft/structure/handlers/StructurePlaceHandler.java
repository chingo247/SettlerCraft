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
package com.chingo247.settlercraft.structure.handlers;

import com.chingo247.settlercraft.SettlerCraft;
import com.chingo247.settlercraft.structure.construction.options.Options;
import com.chingo247.settlercraft.structure.exception.StructureException;
import com.chingo247.settlercraft.util.KeyPool;
import com.chingo247.settlercraft.util.WorldUtil;
import com.chingo247.settlercraft.world.Direction;
import com.chingo247.settlercraft.structure.plan.StructurePlan;
import com.chingo247.settlercraft.plugin.IEconomyProvider;
import com.chingo247.settlercraft.selection.CUISelectionManager;
import com.chingo247.settlercraft.selection.ISelectionManager;
import com.chingo247.settlercraft.selection.NoneSelectionManager;
import com.chingo247.settlercraft.structure.StructureAPI;
import com.chingo247.settlercraft.structure.Structure;
import com.chingo247.settlercraft.util.PlacementUtil;
import com.chingo247.settlercraft.world.SettlerCraftWorld;
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
    private final IEconomyProvider economyProvider;
    private final SettlerCraft settlerCraft;
    private final StructureAPI structureAPI;

    public StructurePlaceHandler(ExecutorService service, IEconomyProvider economyProvider, SettlerCraft settlerCraft) {
        this.playerPool = new KeyPool<>(service);
        this.economyProvider = economyProvider;
        this.settlerCraft = settlerCraft;
        this.structureAPI = settlerCraft.getStructureAPI();
    }

    public void handle(final AItemStack planItem, final Player player, final SettlerCraftWorld world, final Vector pos) {
        handle(planItem, player, world, pos, null);
    }

    public void handle(final AItemStack planItem, final Player player, final SettlerCraftWorld world, final Vector pos, ISelectionManager selectionManager) {
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
                
                IPlayer iPlayer = settlerCraft.getPlatform().getPlayer(player.getUniqueId());
                AInventory inventory = iPlayer.getInventory();
                if (!inventory.hasItem(planItem)) {
                    System.out.println("Player doesnt have the item!");
                    return;
                }


                String planId = getPlanID(planItem);

                StructurePlan plan = structureAPI.getPlan(planId);

                if (plan == null) {
                    System.out.println("Plan was null...");
                    if (structureAPI.isLoadingPlans()) {
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
                
                
                handlePlace(plan, planItem, player, world, pos, slm);
                
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
        });

    }
    
    private void handlePlace(StructurePlan plan, AItemStack item, Player player, SettlerCraftWorld world, Vector pos1, ISelectionManager selectionManager) {
        System.out.println("Handling placement...");
        IPlayer iPlayer = settlerCraft.getPlatform().getPlayer(player.getUniqueId());
        
        Direction direction = WorldUtil.getDirection(iPlayer.getYaw());
        
        Vector pos2;
        
        boolean toLeft = iPlayer.isSneaking();
        
        System.out.println("plan size: " + plan.getPlacement().getDimension().getMaxPosition());
        
        if (toLeft) {
            System.out.println("Player was sneaking... pointing left!");
            pos2 = PlacementUtil.getPoint2Left(pos1, direction, plan.getPlacement().getDimension().getMaxPosition());
        } else {
            System.out.println("Player was NOT sneaking... pointing right!");
            pos2 = PlacementUtil.getPoint2Right(pos1, direction, plan.getPlacement().getDimension().getMaxPosition());
        }
        

        // If player has NOT selected anything yet... make a new selection
        if(!selectionManager.hasSelection(player)) {
            selectionManager.select(player,  pos1, pos2);
            player.print(ChatColors.YELLOW + "Left-Click " + ChatColors.RESET + " in the " + ChatColors.GREEN + " green " + ChatColors.RESET + "square to " + ChatColors.YELLOW + "confirm");
            player.print(ChatColors.YELLOW + "Right-Click " + ChatColors.RESET + "to" + ChatColors.YELLOW + " deselect");
        } else if(selectionManager.matchesCurrentSelection(player, pos1, pos2)){
            
            System.out.println("pos1: " + pos1);
            System.out.println("pos2: " + pos2);
            if (toLeft) {
                // Fix WTF HOW?!!1?
                pos1 = WorldUtil.translateLocation(pos1, direction, (-(plan.getPlacement().getDimension().getMaxPosition().getBlockX()- 1)), 0, 0);
            }
            
            
            
            if (canPlace(player, world, pos1, direction, plan)) {
                Structure structure;
                try {
                    // Create Structure using the SettlerCraft restrictions
                    structure = structureAPI.createStructure(world, plan, pos1, direction);
                    
                    if (structure != null) {
//                        AItemStack clone = item.clone();
//                        clone.setAmount(1);
//                        iPlayer.getInventory().removeItem(clone);
//                        iPlayer.updateInventory();
                        
                        structure.build(player, Options.defaultOptions(), false);
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
