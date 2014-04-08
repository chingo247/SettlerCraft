/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.listener;

import com.settlercraft.model.entity.structure.StructureChest;
import com.settlercraft.model.entity.structure.StructureProgress;
import com.settlercraft.persistence.StructureChestService;
import com.settlercraft.plugin.SettlerCraft;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Chingo
 */
public class StructureChestListener implements Listener {

    private final SettlerCraft sc;
    private final StructureChestService scs;

    public StructureChestListener(SettlerCraft sc) {
        this.sc = sc;
        this.scs = new StructureChestService();
    }

    @EventHandler
    public void onBuildChestBreak(BlockBreakEvent bbe) {
        System.out.println("Inventory Close event");
        if (bbe.getBlock().getType() == Material.CHEST) {
            Location chest = bbe.getBlock().getLocation();
            final StructureChest stc = scs.getStructureChest(chest.getWorld().getName(), chest.getBlockX(), chest.getBlockY(), chest.getBlockZ());
            if (stc != null) {
                bbe.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryChangeEvent(InventoryClickEvent ice) {
        System.out.println("Inventory Click Event: " + ice.getAction());

        if (ice.getInventory().getType() == InventoryType.CHEST
                && (ice.getAction() == InventoryAction.PLACE_ALL
                || ice.getAction() == InventoryAction.PLACE_ONE
                || ice.getAction() == InventoryAction.PLACE_SOME
                || ice.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY
                || ice.getAction() == InventoryAction.SWAP_WITH_CURSOR)) {

            final Chest chest = (Chest) ice.getInventory().getHolder();
            final StructureChest stc = scs.getStructureChest(chest.getWorld().getName(), chest.getX(), chest.getY(), chest.getZ());
            if (stc != null) {
                System.out.println("StructureChest: " + stc);
                
                Bukkit.getScheduler().runTaskLater(sc, new Runnable() {

                    @Override
                    public void run() {
                        processChest(stc);
                    }
                }, 20);
            }
        }
    }

    private void processChest(StructureChest stc) {
        final StructureProgress progress = stc.getStructure().getProgress();
            if (!progress.isInProgress()) {
                progress.setInProgress(true);
                for (ItemStack is : stc.getChest().getBlockInventory().getContents()) {
                    if (is != null && is.getAmount() > 0 && progress.isNeeded(is)) {
                        
                        processStack(progress, is, stc);
                        break;
                    }
                }
                progress.setInProgress(false);
            }
    }

    private void processStack(final StructureProgress progress, final ItemStack stack, final StructureChest chest) {
        if (progress.commit(stack, 5)) {
            if ((stack != null && (!progress.isNeeded(stack) || stack.getAmount() == 0)) || stack == null) {
                processChest(chest); // Check if there are other material that need to be gathered
            }
            Bukkit.getScheduler().runTaskLater(sc, new Runnable() {
                @Override
                public void run() {
                    processStack(progress, stack, chest);
                }
            }, 50);
        }
    }

}
